package com.tanhua.server.Test;

import com.tanhua.commons.templates.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {
    @Autowired
    private SmsTemplate smsTemplate;

    //代码短信测试
    @Test
    public void testSms(){
        Map<String, String> map = smsTemplate.sendvalidateCode("18879710646", "666666");
        System.out.println(map);
    }
}
