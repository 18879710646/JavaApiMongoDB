package com.tanhua.server.Test;

import com.tanhua.commons.templates.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OssTest {
    @Autowired
    private OssTemplate ossTemplate;
    @Test
    public void testoss()throws FileNotFoundException{
        FileInputStream is = new FileInputStream("D:\\图片\\背景图\\1.png");
        ossTemplate.upload("1.png",is);
    }
}
