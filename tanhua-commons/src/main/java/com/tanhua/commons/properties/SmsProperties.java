package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
//  读取配置文件（application.yml）中的sms中的数据
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
    // 模板签名
    private String signName;
    //模板参数
    private  String  parameterName;
    //验证码 短信模板code
    private  String validateCodeTemplateCode;

    private String accessKeyId;
    private  String  accessKeySecret;



}
