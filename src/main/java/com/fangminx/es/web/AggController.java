package com.fangminx.es.web;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/api/agg")
public class AggController {

    @Autowired
    private TransportClient esClient;

    /**
     * 聚合分析操作：
     *（1）首先按照country国家来进行分组
     *（2）然后在每个country分组内，再按照入职年限进行分组
     *（3）最后计算每个分组内的平均薪资
     */
    @GetMapping
    public String agg(){
        SearchResponse searchResponse = esClient.prepareSearch("company")
                .addAggregation(AggregationBuilders.terms("group_by_country").field("country")
                        .subAggregation(AggregationBuilders
                                .dateHistogram("group_by_join_date")
                                .field("join_date")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                                .subAggregation(AggregationBuilders.avg("avg_salary").field("salary")))
                )
                .execute().actionGet();
        StringBuffer res = new StringBuffer();

        Map<String, Aggregation> aggrMap = searchResponse.getAggregations().asMap();

        StringTerms groupByCountry = (StringTerms) aggrMap.get("group_by_country");
        Iterator<Bucket> groupByCountryBucketIterator = groupByCountry.getBuckets().iterator();
        while(groupByCountryBucketIterator.hasNext()) {
            Bucket groupByCountryBucket = groupByCountryBucketIterator.next();
            res.append("\n").append(groupByCountryBucket.getKey() + ":" + groupByCountryBucket.getDocCount()).append("\n");

            Histogram groupByJoinDate = (Histogram) groupByCountryBucket.getAggregations().asMap().get("group_by_join_date");
            Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByJoinDateBucketIterator = groupByJoinDate.getBuckets().iterator();
            while(groupByJoinDateBucketIterator.hasNext()) {
                org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket groupByJoinDateBucket = groupByJoinDateBucketIterator.next();
                res.append(groupByJoinDateBucket.getKey() + ":" +groupByJoinDateBucket.getDocCount()).append("\n");

                Avg avg = (Avg) groupByJoinDateBucket.getAggregations().asMap().get("avg_salary");
                res.append(avg.getValue()).append("\n");
            }
        }
        return res.toString();
    }
}
