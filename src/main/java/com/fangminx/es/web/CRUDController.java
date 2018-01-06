package com.fangminx.es.web;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * elasticsearch CRUD 操作
 */
@RestController
@RequestMapping("/api")
public class CRUDController {

    @Autowired
    private TransportClient esClient;

    //创建员工信息
    @GetMapping("/insert")
    public ResponseEntity insert(@RequestParam(value = "id",defaultValue = "")String id) throws IOException {
        IndexResponse response = esClient.prepareIndex("company", "employee", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "jack")
                        .field("age", 27)
                        .field("position", "technique")
                        .field("country", "china")
                        .field("join_date", "2017-01-01")
                        .field("salary", 10000)
                        .endObject())
                .get();
        return ResponseEntity.ok(response.getResult());
    }

    //获取员工信息
    @GetMapping("/get")
    public ResponseEntity get(){
        try {
            GetResponse response = esClient.prepareGet("company", "employee", "1").get();
            return ResponseEntity.ok(response.getSourceAsString());
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    //修改该员工信息
    @GetMapping("/update")
    public ResponseEntity update() throws IOException {
        UpdateResponse response = esClient.prepareUpdate("company","employee","1")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                            .field("position","technique manager")
                        .endObject())
                .get();
        return ResponseEntity.ok(response.getResult());
    }

    //删除员工信息
    @GetMapping("/delete")
    public ResponseEntity delete(){
        DeleteResponse response = esClient.prepareDelete("company","employee","1").get();
        return ResponseEntity.ok(response.getResult());
    }
}
