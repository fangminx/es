package com.fangminx.es.web;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 搜索操作
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private TransportClient esClient;

//    GET /company/employee/_search
//    {
//        "query": {
//        "bool": {
//            "must": [
//            {
//                "match": {
//                "position": "technique"
//            }
//            }
//      ],
//            "filter": {
//                "range": {
//                    "age": {
//                        "gte": 30,
//                                "lte": 40
//                    }
//                }
//            }
//        }
//    },
//        "from": 0,
//            "size": 1
//    }
    /**
     * 根据搜索条件搜索
     *
     * @return
     */
    @GetMapping("/some")
    public ResponseEntity executeSearch(){
        SearchResponse response = esClient.prepareSearch()
                .setIndices("company")
                .setTypes("employee")
//去掉就查全部
//                .setQuery(QueryBuilders.matchQuery("position","technique"))
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(30).to(40))
//                .setFrom(0).setSize(1)
                .get();

        StringBuffer sb = new StringBuffer("[");
        SearchHit[] searchHit = response.getHits().getHits();
        for(SearchHit hit: searchHit){
            sb.append(hit.getSourceAsString()).append(",");
        }
        sb.delete(sb.length()-1,sb.length()).append("]");
        return ResponseEntity.ok(sb.toString());
    }

    /**
     * 初始化数据
     */
    @GetMapping("/init/data")
    public void initData() throws IOException {
        esClient.prepareIndex("company", "employee", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "jack")
                        .field("age", 27)
                        .field("position", "technique software")
                        .field("country", "china")
                        .field("join_date", "2017-01-01")
                        .field("salary", 10000)
                        .endObject())
                .get();

        esClient.prepareIndex("company", "employee", "2")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "marry")
                        .field("age", 35)
                        .field("position", "technique manager")
                        .field("country", "china")
                        .field("join_date", "2017-01-01")
                        .field("salary", 12000)
                        .endObject())
                .get();

        esClient.prepareIndex("company", "employee", "3")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "tom")
                        .field("age", 32)
                        .field("position", "senior technique software")
                        .field("country", "china")
                        .field("join_date", "2016-01-01")
                        .field("salary", 11000)
                        .endObject())
                .get();

        esClient.prepareIndex("company", "employee", "4")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "jen")
                        .field("age", 25)
                        .field("position", "junior finance")
                        .field("country", "usa")
                        .field("join_date", "2016-01-01")
                        .field("salary", 7000)
                        .endObject())
                .get();

        esClient.prepareIndex("company", "employee", "5")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "mike")
                        .field("age", 37)
                        .field("position", "finance manager")
                        .field("country", "usa")
                        .field("join_date", "2015-01-01")
                        .field("salary", 15000)
                        .endObject())
                .get();
    }
}
