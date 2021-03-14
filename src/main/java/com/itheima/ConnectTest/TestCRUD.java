package com.itheima.ConnectTest;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

public class TestCRUD {

    private MongoCollection<Document> mongoCollection;

    @Before
    public void init() {
        // 建立连接
        MongoClient mongoClient =
                MongoClients.create("mongodb://192.168.145.160:27017");
        System.out.println("==================="+mongoClient);

        // 选择数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("testdb");

        // 选择表
        this.mongoCollection = mongoDatabase.getCollection("person");
    }

    @Test
    public void insert() {
        Document document = new Document()
                .append("id", 10002)
                .append("name", "zhangsan")
                .append("address", "北京顺义")
                .append("age", 20);
        mongoCollection.insertOne(document);
    }

    @Test
    public void find() {
        FindIterable<Document> documents = mongoCollection.find();
        documents.forEach((Consumer<? super Document>) document -> {
            System.out.println(document);
        });
    }

    //根据条件查询 age < 13 or age > 40
    @Test
    public void findByAge() {
        FindIterable<Document> documents =  mongoCollection.find(Filters.or(Filters.lt("age",13),Filters.gt("age",40)));
        documents.forEach((Consumer<? super Document>) document -> {
            System.out.println(document);
        });
    }

    @Test
    public void update() {

        mongoCollection.updateOne(Filters.eq("id",10001), Updates.set("age", 12));
    }

    @Test
    public void delete() {

        mongoCollection.deleteOne(Filters.eq("id",10001));
    }
}