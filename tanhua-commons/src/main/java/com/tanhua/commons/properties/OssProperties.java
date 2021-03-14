package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data

@ConfigurationProperties(prefix = "tanhua.oss")
public class OssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String url;//sztanhua.oss-cn-shenzhen.aliyuncs.com    https://tanhuaitcst.oss-cn-beijing.aliyuncs.com/1.png
}
