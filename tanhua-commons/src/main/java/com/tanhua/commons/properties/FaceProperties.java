package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data

// 要在CommonsAutoconfiguration中添加配置类
@ConfigurationProperties(prefix = "tanhua.face")
public class FaceProperties {
    private  String appId;
    private String  akikey;
     private String secretKey ;

}
