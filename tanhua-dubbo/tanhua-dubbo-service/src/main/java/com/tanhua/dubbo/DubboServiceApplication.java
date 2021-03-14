package com.tanhua.dubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mapper")
/**
 * 服务者启动类
 */
public class DubboServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboServiceApplication.class,args);
    }
}