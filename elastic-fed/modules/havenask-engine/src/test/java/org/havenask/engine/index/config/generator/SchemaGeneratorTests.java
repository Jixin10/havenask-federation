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

package org.havenask.engine.index.config.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.havenask.common.settings.Settings;
import org.havenask.engine.HavenaskEnginePlugin;
import org.havenask.engine.index.config.Schema;
import org.havenask.index.mapper.MapperService;
import org.havenask.index.mapper.MapperServiceTestCase;
import org.havenask.plugins.Plugin;

import static java.util.Collections.singletonList;

public class SchemaGeneratorTests extends MapperServiceTestCase {

    @Override
    protected Collection<? extends Plugin> getPlugins() {
        return singletonList(new HavenaskEnginePlugin(Settings.EMPTY));
    }

    private String indexName = randomAlphaOfLength(5);

    public void testSchemaGenerate() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {
            {
                b.startObject("name");
                {
                    b.field("type", "text");
                }
                b.endObject();
                b.startObject("age");
                {
                    b.field("type", "integer");
                }
                b.endObject();
                b.startObject("price");
                {
                    b.field("type", "float");
                }
                b.endObject();
                b.startObject("date");
                {
                    b.field("type", "date");
                }
                b.endObject();
                b.startObject("bool");
                {
                    b.field("type", "boolean");
                }
                b.endObject();
                b.startObject("byte");
                {
                    b.field("type", "byte");
                }
                b.endObject();
                b.startObject("short");
                {
                    b.field("type", "short");
                }
                b.endObject();
                b.startObject("long");
                {
                    b.field("type", "long");
                }
                b.endObject();
                b.startObject("double");
                {
                    b.field("type", "double");
                }
                b.endObject();
                b.startObject("keyword");
                {
                    b.field("type", "keyword");
                }
                b.endObject();
                b.startObject("text");
                {
                    b.field("type", "text");
                }
                b.endObject();
                // object type
                b.startObject("object");
                {
                    b.field("type", "object");
                    b.startObject("properties");
                    {
                        b.startObject("name");
                        {
                            b.field("type", "text");
                        }
                        b.endObject();
                        b.startObject("age");
                        {
                            b.field("type", "integer");
                        }
                        b.endObject();
                    }
                    b.endObject();
                }
                b.endObject();
            }
        }));
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        Schema schema = schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService);
        String actual = schema.toString();
        String expect = String.format(
            Locale.ROOT,
            "{\n"
                + "\t\"attributes\":[\"date\",\"bool\",\"byte\",\"double\",\"long\",\"_seq_no\",\"object_age\","
                + "\"price\","
                + "\"short\",\"_id\",\"keyword\",\"_version\",\"age\",\"_primary_term\"],\n"
                + "\t\"fields\":[{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_routing\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"date\",\n"
                + "\t\t\"field_type\":\"UINT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"bool\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"byte\",\n"
                + "\t\t\"field_type\":\"INT8\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"double\",\n"
                + "\t\t\"field_type\":\"DOUBLE\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"long\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"analyzer\":\"simple_analyzer\",\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"object_name\",\n"
                + "\t\t\"field_type\":\"TEXT\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_seq_no\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"object_age\",\n"
                + "\t\t\"field_type\":\"INTEGER\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"price\",\n"
                + "\t\t\"field_type\":\"FLOAT\"\n"
                + "\t},{\n"
                + "\t\t\"analyzer\":\"simple_analyzer\",\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"name\",\n"
                + "\t\t\"field_type\":\"TEXT\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"short\",\n"
                + "\t\t\"field_type\":\"INT16\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_source\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_id\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"analyzer\":\"simple_analyzer\",\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"text\",\n"
                + "\t\t\"field_type\":\"TEXT\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"keyword\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_version\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"age\",\n"
                + "\t\t\"field_type\":\"INTEGER\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_primary_term\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t}],\n"
                + "\t\"indexs\":[{\n"
                + "\t\t\"index_fields\":\"_routing\",\n"
                + "\t\t\"index_name\":\"_routing\",\n"
                + "\t\t\"index_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"date\",\n"
                + "\t\t\"index_name\":\"date\",\n"
                + "\t\t\"index_type\":\"DATE\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"bool\",\n"
                + "\t\t\"index_name\":\"bool\",\n"
                + "\t\t\"index_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"byte\",\n"
                + "\t\t\"index_name\":\"byte\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"long\",\n"
                + "\t\t\"index_name\":\"long\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"doc_payload_flag\":1,\n"
                + "\t\t\"index_fields\":\"object_name\",\n"
                + "\t\t\"index_name\":\"object_name\",\n"
                + "\t\t\"index_type\":\"TEXT\",\n"
                + "\t\t\"position_list_flag\":1,\n"
                + "\t\t\"position_payload_flag\":1,\n"
                + "\t\t\"term_frequency_flag\":1\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"_seq_no\",\n"
                + "\t\t\"index_name\":\"_seq_no\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"object_age\",\n"
                + "\t\t\"index_name\":\"object_age\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"doc_payload_flag\":1,\n"
                + "\t\t\"index_fields\":\"name\",\n"
                + "\t\t\"index_name\":\"name\",\n"
                + "\t\t\"index_type\":\"TEXT\",\n"
                + "\t\t\"position_list_flag\":1,\n"
                + "\t\t\"position_payload_flag\":1,\n"
                + "\t\t\"term_frequency_flag\":1\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"short\",\n"
                + "\t\t\"index_name\":\"short\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"has_primary_key_attribute\":true,\n"
                + "\t\t\"index_fields\":\"_id\",\n"
                + "\t\t\"index_name\":\"_id\",\n"
                + "\t\t\"index_type\":\"PRIMARYKEY64\",\n"
                + "\t\t\"is_primary_key_sorted\":false\n"
                + "\t},{\n"
                + "\t\t\"doc_payload_flag\":1,\n"
                + "\t\t\"index_fields\":\"text\",\n"
                + "\t\t\"index_name\":\"text\",\n"
                + "\t\t\"index_type\":\"TEXT\",\n"
                + "\t\t\"position_list_flag\":1,\n"
                + "\t\t\"position_payload_flag\":1,\n"
                + "\t\t\"term_frequency_flag\":1\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"keyword\",\n"
                + "\t\t\"index_name\":\"keyword\",\n"
                + "\t\t\"index_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"age\",\n"
                + "\t\t\"index_name\":\"age\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t}],\n"
                + "\t\"summarys\":{\n"
                + "\t\t\"summary_fields\":[\"_routing\",\"_source\",\"_id\"]\n"
                + "\t},\n"
                + "\t\"table_name\":\"%s\",\n"
                + "\t\"table_type\":\"normal\"\n"
                + "}",
            indexName
        );
        assertEquals(expect, actual);
    }

    // test not support nested type
    public void testNestedType() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {
            {
                // nested type
                b.startObject("nested_field");
                b.field("type", "nested");
                b.startObject("properties");
                {
                    b.startObject("name");
                    b.field("type", "text");
                    b.endObject();
                    b.startObject("age");
                    b.field("type", "integer");
                    b.endObject();
                }
                b.endObject();
                b.endObject();
            }
        }));
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        // java.lang.UnsupportedOperationException: nested field not support
        UnsupportedOperationException e = expectThrows(
            UnsupportedOperationException.class,
            () -> schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService)
        );
        assertEquals("nested field not support", e.getMessage());
    }

    // test not support geo_point type
    public void testGeoPointType() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {
            {
                // geo_point type
                b.startObject("geo_point_field");
                b.field("type", "geo_point");
                b.endObject();
            }
        }));
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        // java.lang.UnsupportedOperationException: geo_point field not support
        UnsupportedOperationException e = expectThrows(
            UnsupportedOperationException.class,
            () -> schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService)
        );
        assertEquals("no support mapping type (geo_point) for field geo_point_field", e.getMessage());
    }

    // test default schema
    public void testDefaultSchema() throws IOException {
        MapperService mapperService = null;
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        Schema schema = schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService);
        String actual = schema.toString();
        String expect = String.format(
            Locale.ROOT,
            "{\n"
                + "\t\"attributes\":[\n"
                + "\t\t\"_seq_no\",\n"
                + "\t\t\"_id\",\n"
                + "\t\t\"_version\",\n"
                + "\t\t\"_primary_term\"\n"
                + "\t],\n"
                + "\t\"fields\":[\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_routing\",\n"
                + "\t\t\t\"field_type\":\"STRING\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_seq_no\",\n"
                + "\t\t\t\"field_type\":\"INT64\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_source\",\n"
                + "\t\t\t\"field_type\":\"STRING\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_id\",\n"
                + "\t\t\t\"field_type\":\"STRING\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_version\",\n"
                + "\t\t\t\"field_type\":\"INT64\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"binary_field\":false,\n"
                + "\t\t\t\"field_name\":\"_primary_term\",\n"
                + "\t\t\t\"field_type\":\"INT64\"\n"
                + "\t\t}\n"
                + "\t],\n"
                + "\t\"indexs\":[\n"
                + "\t\t{\n"
                + "\t\t\t\"index_fields\":\"_routing\",\n"
                + "\t\t\t\"index_name\":\"_routing\",\n"
                + "\t\t\t\"index_type\":\"STRING\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"index_fields\":\"_seq_no\",\n"
                + "\t\t\t\"index_name\":\"_seq_no\",\n"
                + "\t\t\t\"index_type\":\"NUMBER\"\n"
                + "\t\t},\n"
                + "\t\t{\n"
                + "\t\t\t\"has_primary_key_attribute\":true,\n"
                + "\t\t\t\"index_fields\":\"_id\",\n"
                + "\t\t\t\"index_name\":\"_id\",\n"
                + "\t\t\t\"index_type\":\"PRIMARYKEY64\",\n"
                + "\t\t\t\"is_primary_key_sorted\":false\n"
                + "\t\t}\n"
                + "\t],\n"
                + "\t\"summarys\":{\n"
                + "\t\t\"summary_fields\":[\n"
                + "\t\t\t\"_routing\",\n"
                + "\t\t\t\"_source\",\n"
                + "\t\t\t\"_id\"\n"
                + "\t\t]\n"
                + "\t},\n"
                + "\t\"table_name\":\"%s\",\n"
                + "\t\"table_type\":\"normal\"\n"
                + "}",
            indexName
        );
        assertEquals(expect, actual);
    }

    // test index vector
    public void testIndexVector() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {
            {
                b.startObject("field");
                {
                    b.field("type", "dense_vector");
                    b.field("dims", 128);
                }
                b.endObject();
            }
        }));
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        Schema schema = schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService);
        String actual = schema.toString();
        String expected = String.format(
            Locale.ROOT,
            "{\n"
                + "\t\"attributes\":[\"_seq_no\",\"_id\",\"_version\",\"_primary_term\"],\n"
                + "\t\"fields\":[{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_routing\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_seq_no\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"field\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_source\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_id\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_version\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_id\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_field\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_primary_term\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t}],\n"
                + "\t\"indexs\":[{\n"
                + "\t\t\"index_fields\":\"_routing\",\n"
                + "\t\t\"index_name\":\"_routing\",\n"
                + "\t\t\"index_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"_seq_no\",\n"
                + "\t\t\"index_name\":\"_seq_no\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":[\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_id\"\n"
                + "\t\t\t},\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_field\"\n"
                + "\t\t\t}\n"
                + "\t\t],\n"
                + "\t\t\"index_name\":\"field\",\n"
                + "\t\t\"index_type\":\"CUSTOMIZED\",\n"
                + "\t\t\"indexer\":\"aitheta_indexer\",\n"
                + "\t\t\"parameters\":{\n"
                + "\t\t\t\"dimension\":\"128\",\n"
                + "\t\t\t\"build_metric_type\":\"l2\",\n"
                + "\t\t\t\"search_metric_type\":\"l2\",\n"
                + "\t\t\t\"index_type\":\"graph\",\n"
                + "\t\t\t\"proxima.graph.common.graph_type\":\"hnsw\"\n"
                + "\t\t}\n"
                + "\t},{\n"
                + "\t\t\"has_primary_key_attribute\":true,\n"
                + "\t\t\"index_fields\":\"_id\",\n"
                + "\t\t\"index_name\":\"_id\",\n"
                + "\t\t\"index_type\":\"PRIMARYKEY64\",\n"
                + "\t\t\"is_primary_key_sorted\":false\n"
                + "\t}],\n"
                + "\t\"summarys\":{\n"
                + "\t\t\"summary_fields\":[\"_routing\",\"_source\",\"_id\"]\n"
                + "\t},\n"
                + "\t\"table_name\":\"%s\",\n"
                + "\t\"table_type\":\"normal\"\n"
                + "}",
            indexName
        );
        assertEquals(expected, actual);
    }

    // test index vector with all parameters
    public void testIndexVectorWithAllParameters() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {
            {
                b.startObject("linear_field");
                {
                    b.field("type", "dense_vector");
                    b.field("dims", 128);
                    b.startObject("index_options");
                    b.field("type", "linear");
                    b.endObject();
                }
                b.endObject();
                b.startObject("hc_field");
                {
                    b.field("type", "dense_vector");
                    b.field("dims", 128);
                    b.startObject("index_options");
                    b.field("type", "hc");
                    b.field("proxima.hc.builder.num_in_level_1", 1000);
                    b.field("proxima.hc.builder.num_in_level_2", 100);
                    b.field("proxima.hc.common.leaf_centroid_num", 100000);
                    b.field("proxima.general.builder.train_sample_count", 0);
                    b.field("proxima.general.builder.train_sample_ratio", 0.0);
                    b.field("proxima.hc.searcher.scan_num_in_level_1", 60);
                    b.field("proxima.hc.searcher.scan_num_in_level_2", 6000);
                    b.field("proxima.hc.searcher.max_scan_num", 50000);
                    b.field("use_linear_threshold", 10000);
                    b.field("use_dynamic_params", 1);
                    b.endObject();
                }
                b.endObject();
                b.startObject("hnsw_field");
                {
                    b.field("type", "dense_vector");
                    b.field("dims", 128);
                    b.startObject("index_options");
                    b.field("type", "hnsw");
                    b.field("proxima.graph.common.max_doc_cnt", 50000000);
                    b.field("proxima.graph.common.max_scan_num", 25000);
                    b.field("proxima.general.builder.memory_quota", 0);
                    b.field("proxima.hnsw.builder.efconstruction", 400);
                    b.field("proxima.hnsw.builder.max_level", 6);
                    b.field("proxima.hnsw.builder.scaling_factor", 50);
                    b.field("proxima.hnsw.builder.upper_neighbor_cnt", 50);
                    b.field("proxima.hnsw.searcher.ef", 200);
                    b.field("proxima.hnsw.searcher.max_scan_cnt", 15000);
                    b.endObject();
                }
                b.endObject();
            }
        }));
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        Schema schema = schemaGenerator.getSchema(indexName, Settings.EMPTY, mapperService);
        String actual = schema.toString();
        String expected = String.format(
            Locale.ROOT,
            "{\n"
                + "\t\"attributes\":[\"_seq_no\",\"_id\",\"_version\",\"_primary_term\"],\n"
                + "\t\"fields\":[{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_routing\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"linear_field\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"hnsw_field\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_seq_no\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"hc_field\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_source\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_id\",\n"
                + "\t\t\"field_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_version\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_id\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_linear_field\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_hnsw_field\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"DUP_hc_field\",\n"
                + "\t\t\"field_type\":\"RAW\"\n"
                + "\t},{\n"
                + "\t\t\"binary_field\":false,\n"
                + "\t\t\"field_name\":\"_primary_term\",\n"
                + "\t\t\"field_type\":\"INT64\"\n"
                + "\t}],\n"
                + "\t\"indexs\":[{\n"
                + "\t\t\"index_fields\":\"_routing\",\n"
                + "\t\t\"index_name\":\"_routing\",\n"
                + "\t\t\"index_type\":\"STRING\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":[\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_id\"\n"
                + "\t\t\t},\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_linear_field\"\n"
                + "\t\t\t}\n"
                + "\t\t],\n"
                + "\t\t\"index_name\":\"linear_field\",\n"
                + "\t\t\"index_type\":\"CUSTOMIZED\",\n"
                + "\t\t\"indexer\":\"aitheta_indexer\",\n"
                + "\t\t\"parameters\":{\n"
                + "\t\t\t\"dimension\":\"128\",\n"
                + "\t\t\t\"build_metric_type\":\"l2\",\n"
                + "\t\t\t\"search_metric_type\":\"l2\",\n"
                + "\t\t\t\"index_type\":\"linear\"\n"
                + "\t\t}\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":[\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_id\"\n"
                + "\t\t\t},\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_hnsw_field\"\n"
                + "\t\t\t}\n"
                + "\t\t],\n"
                + "\t\t\"index_name\":\"hnsw_field\",\n"
                + "\t\t\"index_type\":\"CUSTOMIZED\",\n"
                + "\t\t\"indexer\":\"aitheta_indexer\",\n"
                + "\t\t\"parameters\":{\n"
                + "\t\t\t\"dimension\":\"128\",\n"
                + "\t\t\t\"build_metric_type\":\"l2\",\n"
                + "\t\t\t\"search_metric_type\":\"l2\",\n"
                + "\t\t\t\"index_type\":\"graph\",\n"
                + "\t\t\t\"proxima.graph.common.graph_type\":\"hnsw\",\n"
                + "\t\t\t\"proxima.graph.common.max_doc_cnt\":\"50000000\",\n"
                + "\t\t\t\"proxima.graph.common.max_scan_num\":\"25000\",\n"
                + "\t\t\t\"proxima.general.builder.memory_quota\":\"0\",\n"
                + "\t\t\t\"proxima.hnsw.builder.efconstruction\":\"400\",\n"
                + "\t\t\t\"proxima.hnsw.builder.max_level\":\"6\",\n"
                + "\t\t\t\"proxima.hnsw.builder.scaling_factor\":\"50\",\n"
                + "\t\t\t\"proxima.hnsw.builder.upper_neighbor_cnt\":\"50\",\n"
                + "\t\t\t\"proxima.hnsw.searcher.ef\":\"200\",\n"
                + "\t\t\t\"proxima.hnsw.searcher.max_scan_cnt\":\"15000\"\n"
                + "\t\t}\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":\"_seq_no\",\n"
                + "\t\t\"index_name\":\"_seq_no\",\n"
                + "\t\t\"index_type\":\"NUMBER\"\n"
                + "\t},{\n"
                + "\t\t\"index_fields\":[\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_id\"\n"
                + "\t\t\t},\n"
                + "\t\t\t{\n"
                + "\t\t\t\t\"boost\":1,\n"
                + "\t\t\t\t\"field_name\":\"DUP_hc_field\"\n"
                + "\t\t\t}\n"
                + "\t\t],\n"
                + "\t\t\"index_name\":\"hc_field\",\n"
                + "\t\t\"index_type\":\"CUSTOMIZED\",\n"
                + "\t\t\"indexer\":\"aitheta_indexer\",\n"
                + "\t\t\"parameters\":{\n"
                + "\t\t\t\"dimension\":\"128\",\n"
                + "\t\t\t\"build_metric_type\":\"l2\",\n"
                + "\t\t\t\"search_metric_type\":\"l2\",\n"
                + "\t\t\t\"index_type\":\"hc\",\n"
                + "\t\t\t\"proxima.hc.builder.num_in_level_1\":\"1000\",\n"
                + "\t\t\t\"proxima.hc.builder.num_in_level_2\":\"100\",\n"
                + "\t\t\t\"proxima.hc.common.leaf_centroid_num\":\"100000\",\n"
                + "\t\t\t\"proxima.hc.builder.train_sample_count\":\"0\",\n"
                + "\t\t\t\"proxima.hc.builder.train_sample_ratio\":\"0.0\",\n"
                + "\t\t\t\"proxima.hc.builder.scan_num_in_level_1\":\"60\",\n"
                + "\t\t\t\"proxima.hc.builder.scan_num_in_level_2\":\"6000\",\n"
                + "\t\t\t\"proxima.hc.searcher.max_scan_num\":\"50000\",\n"
                + "\t\t\t\"use_linear_threshold\":\"10000\",\n"
                + "\t\t\t\"use_dynamic_params\":\"1\"\n"
                + "\t\t}\n"
                + "\t},{\n"
                + "\t\t\"has_primary_key_attribute\":true,\n"
                + "\t\t\"index_fields\":\"_id\",\n"
                + "\t\t\"index_name\":\"_id\",\n"
                + "\t\t\"index_type\":\"PRIMARYKEY64\",\n"
                + "\t\t\"is_primary_key_sorted\":false\n"
                + "\t}],\n"
                + "\t\"summarys\":{\n"
                + "\t\t\"summary_fields\":[\"_routing\",\"_source\",\"_id\"]\n"
                + "\t},\n"
                + "\t\"table_name\":\"%s\",\n"
                + "\t\"table_type\":\"normal\"\n"
                + "}",
            indexName
        );
        assertEquals(expected, actual);
    }

}
