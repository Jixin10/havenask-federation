/*
 * Copyright (c) 2021, Alibaba Group;
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.havenask.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.SetOnce;
import org.havenask.action.ActionRequest;
import org.havenask.action.ActionResponse;
import org.havenask.client.Client;
import org.havenask.cluster.metadata.IndexNameExpressionResolver;
import org.havenask.cluster.node.DiscoveryNodes;
import org.havenask.cluster.service.ClusterService;
import org.havenask.common.io.stream.NamedWriteableRegistry;
import org.havenask.common.settings.ClusterSettings;
import org.havenask.common.settings.IndexScopedSettings;
import org.havenask.common.settings.Setting;
import org.havenask.common.settings.Setting.Property;
import org.havenask.common.settings.Settings;
import org.havenask.common.settings.SettingsFilter;
import org.havenask.common.unit.TimeValue;
import org.havenask.common.xcontent.NamedXContentRegistry;
import org.havenask.engine.index.HavenaskIndexEventListener;
import org.havenask.engine.index.engine.EngineSettings;
import org.havenask.engine.index.engine.HavenaskEngine;
import org.havenask.engine.index.mapper.DenseVectorFieldMapper;
import org.havenask.engine.rpc.HavenaskClient;
import org.havenask.engine.rpc.QrsClient;
import org.havenask.engine.rpc.SearcherClient;
import org.havenask.engine.rpc.arpc.SearcherArpcClient;
import org.havenask.engine.rpc.http.QrsHttpClient;
import org.havenask.engine.rpc.http.SearcherHttpClient;
import org.havenask.engine.search.action.HavenaskSqlAction;
import org.havenask.engine.search.action.HavenaskSqlClientInfoAction;
import org.havenask.engine.search.action.TransportHavenaskSqlAction;
import org.havenask.engine.search.action.TransportHavenaskSqlClientInfoAction;
import org.havenask.engine.search.rest.RestHavenaskSqlAction;
import org.havenask.engine.search.rest.RestHavenaskSqlClientInfoAction;
import org.havenask.engine.stop.action.HavenaskStopAction;
import org.havenask.engine.stop.action.TransportHavenaskStopAction;
import org.havenask.engine.stop.rest.RestHavenaskStop;
import org.havenask.env.Environment;
import org.havenask.env.NodeEnvironment;
import org.havenask.index.IndexModule;
import org.havenask.index.IndexSettings;
import org.havenask.index.engine.EngineFactory;
import org.havenask.index.mapper.Mapper;
import org.havenask.index.shard.IndexMappingProvider;
import org.havenask.index.shard.IndexSettingProvider;
import org.havenask.plugins.ActionPlugin;
import org.havenask.plugins.AnalysisPlugin;
import org.havenask.plugins.EnginePlugin;
import org.havenask.plugins.MapperPlugin;
import org.havenask.plugins.NodeEnvironmentPlugin;
import org.havenask.plugins.Plugin;
import org.havenask.plugins.SearchPlugin;
import org.havenask.repositories.RepositoriesService;
import org.havenask.rest.RestController;
import org.havenask.rest.RestHandler;
import org.havenask.script.ScriptService;
import org.havenask.threadpool.ExecutorBuilder;
import org.havenask.threadpool.ScalingExecutorBuilder;
import org.havenask.threadpool.ThreadPool;
import org.havenask.watcher.ResourceWatcherService;

import static org.havenask.discovery.DiscoveryModule.DISCOVERY_TYPE_SETTING;
import static org.havenask.discovery.DiscoveryModule.SINGLE_NODE_DISCOVERY_TYPE;

public class HavenaskEnginePlugin extends Plugin
    implements
        EnginePlugin,
        AnalysisPlugin,
        ActionPlugin,
        SearchPlugin,
        NodeEnvironmentPlugin,
        MapperPlugin {
    private static Logger logger = LogManager.getLogger(HavenaskEnginePlugin.class);
    private final SetOnce<HavenaskEngineEnvironment> havenaskEngineEnvironmentSetOnce = new SetOnce<>();
    private final SetOnce<NativeProcessControlService> nativeProcessControlServiceSetOnce = new SetOnce<>();
    private final SetOnce<CheckTargetService> checkTargetServiceSetOnce = new SetOnce<>();
    private final SetOnce<HavenaskClient> searcherClientSetOnce = new SetOnce<>();
    private final SetOnce<QrsClient> qrsClientSetOnce = new SetOnce<>();
    private final SetOnce<SearcherClient> searcherArpcClientSetOnce = new SetOnce<>();
    private final Settings settings;

    public static final String HAVENASK_THREAD_POOL_NAME = "havenask";
    public static final Setting<Boolean> HAVENASK_ENGINE_ENABLED_SETTING = Setting.boolSetting(
        "havenask.engine.enabled",
        false,
        new Setting.Validator<>() {
            @Override
            public void validate(Boolean value) {}

            @Override
            public void validate(Boolean value, Map<Setting<?>, Object> settings) {
                // DISCOVERY_TYPE_SETTING must be single-node when havenask engine is enabled
                if (value) {
                    String discoveryType = (String) settings.get(DISCOVERY_TYPE_SETTING);
                    if (false == SINGLE_NODE_DISCOVERY_TYPE.equals(discoveryType)) {
                        throw new IllegalArgumentException("havenask engine can only be enabled when discovery type is single-node");
                    }
                }
            }

            @Override
            public Iterator<Setting<?>> settings() {
                List<Setting<?>> settings = List.of(DISCOVERY_TYPE_SETTING);
                return settings.iterator();
            }
        },
        Property.NodeScope,
        Setting.Property.Final
    );

    public static final Setting<Boolean> HAVENASK_SET_DEFAULT_ENGINE_SETTING = Setting.boolSetting(
        "havenask.engine.set_default_engine",
        false,
        new Setting.Validator<>() {
            @Override
            public void validate(Boolean value) {}

            @Override
            public void validate(Boolean value, Map<Setting<?>, Object> settings) {
                // HAVENASK_ENGINE_ENABLED_SETTING must be true when havenask engine is enabled
                if (value) {
                    Boolean engineEnabled = (Boolean) settings.get(HAVENASK_ENGINE_ENABLED_SETTING);
                    if (false == engineEnabled) {
                        throw new IllegalArgumentException(
                            "havenask engine can only be set as default engine when havenask engine is enabled"
                        );
                    }
                }
            }

            @Override
            public Iterator<Setting<?>> settings() {
                List<Setting<?>> settings = List.of(HAVENASK_ENGINE_ENABLED_SETTING);
                return settings.iterator();
            }
        },
        Property.NodeScope,
        Setting.Property.Final
    );

    public HavenaskEnginePlugin(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Optional<EngineFactory> getEngineFactory(IndexSettings indexSettings) {
        if (EngineSettings.isHavenaskEngine(indexSettings.getSettings())) {
            return Optional.of(
                engineConfig -> new HavenaskEngine(
                    engineConfig,
                    searcherClientSetOnce.get(),
                    qrsClientSetOnce.get(),
                    searcherArpcClientSetOnce.get(),
                    havenaskEngineEnvironmentSetOnce.get(),
                    nativeProcessControlServiceSetOnce.get()
                )
            );
        }

        return Optional.empty();
    }

    @Override
    public Collection<Object> createComponents(
        Client client,
        ClusterService clusterService,
        ThreadPool threadPool,
        ResourceWatcherService resourceWatcherService,
        ScriptService scriptService,
        NamedXContentRegistry xContentRegistry,
        Environment environment,
        NodeEnvironment nodeEnvironment,
        NamedWriteableRegistry namedWriteableRegistry,
        IndexNameExpressionResolver indexNameExpressionResolver,
        Supplier<RepositoriesService> repositoriesServiceSupplier
    ) {
        NativeProcessControlService nativeProcessControlService = new NativeProcessControlService(
            clusterService,
            threadPool,
            environment,
            nodeEnvironment,
            havenaskEngineEnvironmentSetOnce.get()
        );
        nativeProcessControlServiceSetOnce.set(nativeProcessControlService);
        HavenaskClient havenaskClient = new SearcherHttpClient(nativeProcessControlService.getSearcherHttpPort());
        QrsClient qrsClient = new QrsHttpClient(nativeProcessControlService.getQrsHttpPort());
        SearcherClient searcherClient = new SearcherArpcClient(nativeProcessControlService.getSearcherTcpPort());
        searcherClientSetOnce.set(havenaskClient);
        qrsClientSetOnce.set(qrsClient);
        searcherArpcClientSetOnce.set(searcherClient);
        CheckTargetService checkTargetService = new CheckTargetService(
            clusterService,
            threadPool,
            client,
            nativeProcessControlService,
            havenaskClient
        );
        checkTargetServiceSetOnce.set(checkTargetService);
        return Arrays.asList(
            nativeProcessControlServiceSetOnce.get(),
            havenaskEngineEnvironmentSetOnce.get(),
            searcherClientSetOnce.get(),
            checkTargetServiceSetOnce.get()
        );
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(
            HAVENASK_ENGINE_ENABLED_SETTING,
            HAVENASK_SET_DEFAULT_ENGINE_SETTING,
            HavenaskEngineEnvironment.HAVENASK_PATH_DATA_SETTING,
            EngineSettings.ENGINE_TYPE_SETTING,
            EngineSettings.HA3_FLOAT_MUL_BY10,
            EngineSettings.HAVENASK_REALTIME_ENABLE,
            EngineSettings.HAVENASK_REALTIME_TOPIC_NAME,
            EngineSettings.HAVENASK_REALTIME_BOOTSTRAP_SERVERS,
            EngineSettings.HAVENASK_REALTIME_KAFKA_START_TIMESTAMP,
            EngineSettings.HAVENASK_FLUSH_MAX_DOC_COUNT,
            NativeProcessControlService.HAVENASK_COMMAND_TIMEOUT_SETTING,
            NativeProcessControlService.HAVENASK_SEARCHER_HTTP_PORT_SETTING,
            NativeProcessControlService.HAVENASK_SEARCHER_TCP_PORT_SETTING,
            NativeProcessControlService.HAVENASK_SEARCHER_GRPC_PORT_SETTING,
            NativeProcessControlService.HAVENASK_QRS_HTTP_PORT_SETTING,
            NativeProcessControlService.HAVENASK_QRS_TCP_PORT_SETTING
        );
    }

    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        return Arrays.asList(
            new ActionHandler<>(HavenaskSqlAction.INSTANCE, TransportHavenaskSqlAction.class),
            new ActionHandler<>(HavenaskSqlClientInfoAction.INSTANCE, TransportHavenaskSqlClientInfoAction.class),
            new ActionHandler<>(HavenaskStopAction.INSTANCE, TransportHavenaskStopAction.class)
        );
    }

    @Override
    public List<RestHandler> getRestHandlers(
        Settings settings,
        RestController restController,
        ClusterSettings clusterSettings,
        IndexScopedSettings indexScopedSettings,
        SettingsFilter settingsFilter,
        IndexNameExpressionResolver indexNameExpressionResolver,
        Supplier<DiscoveryNodes> nodesInCluster
    ) {
        return Arrays.asList(new RestHavenaskSqlAction(), new RestHavenaskSqlClientInfoAction(), new RestHavenaskStop());
    }

    @Override
    public Collection<IndexSettingProvider> getAdditionalIndexSettingProviders() {
        return Arrays.asList(new HavenaskIndexSettingProvider(settings));
    }

    @Override
    public Collection<IndexMappingProvider> getAdditionalIndexMappingProviders() {
        return Arrays.asList(new HavenaskIndexMappingProvider());
    }

    @Override
    public CustomEnvironment newEnvironment(final Environment environment, final Settings settings) {
        HavenaskEngineEnvironment havenaskEngineEnvironment = new HavenaskEngineEnvironment(environment, settings);
        havenaskEngineEnvironmentSetOnce.set(havenaskEngineEnvironment);
        return havenaskEngineEnvironment;
    }

    @Override
    public List<ExecutorBuilder<?>> getExecutorBuilders(Settings settings) {
        return Arrays.asList(executorBuilder());
    }

    private static ExecutorBuilder<?> executorBuilder() {
        return new ScalingExecutorBuilder(HAVENASK_THREAD_POOL_NAME, 0, 128, TimeValue.timeValueSeconds(30L));
    }

    @Override
    public void onIndexModule(IndexModule indexModule) {
        if (EngineSettings.isHavenaskEngine(indexModule.getSettings())) {
            indexModule.addIndexEventListener(new HavenaskIndexEventListener(havenaskEngineEnvironmentSetOnce.get()));
        }
    }

    @Override
    public Map<String, Mapper.TypeParser> getMappers() {
        return Collections.singletonMap(DenseVectorFieldMapper.CONTENT_TYPE, new DenseVectorFieldMapper.TypeParser());
    }
}
