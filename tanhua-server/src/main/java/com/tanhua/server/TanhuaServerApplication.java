package com.tanhua.server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 消费者启动类
 */
@SpringBootApplication(exclude = {
        // 在项目中，添加了mongo的依赖的话，springboot就会自动去连接本地的mongo，由于他连接不上会导致出错。
        // ，s所以我们要排除mongo的自动配置
        MongoAutoConfiguration.class
})
public class TanhuaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TanhuaServerApplication.class,args);
    }
}