package com.tanhua.commons;
import com.tanhua.commons.properties.FaceProperties;
import com.tanhua.commons.properties.OssProperties;
import com.tanhua.commons.properties.SmsProperties;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.OssTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tanhua.commons.templates.SmsTemplate;



@Configuration
@EnableConfigurationProperties({SmsProperties.class,OssProperties.class,FaceProperties.class})
public class CommonsAutoConfiguration {
    //短信服务
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        SmsTemplate smsTemplate = new SmsTemplate(smsProperties);
        smsTemplate.init();
        return smsTemplate;
    }


    // 阿里云存储的配置类
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        OssTemplate ossTemplate = new OssTemplate(ossProperties);
        return ossTemplate;
    }

    //百度云人脸识别、
    @Bean
    public FaceTemplate faceTemplate(FaceProperties faceProperties){
        return new FaceTemplate(faceProperties);
    }
}


