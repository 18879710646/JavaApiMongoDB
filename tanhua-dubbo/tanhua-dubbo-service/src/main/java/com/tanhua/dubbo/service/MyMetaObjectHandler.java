package com.tanhua.dubbo.service;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/*
* 公共填充类
* */
public class MyMetaObjectHandler  implements MetaObjectHandler {

    /*
    * 插入时自动填充 created updated
    * */
    @Override
    public void insertFill(MetaObject metaObject) {
        setFieldValByName("created",new Date(),metaObject);
        setFieldValByName("updated",new Date(),metaObject);
    }
    /*
    *
    * 更新时自动填充,updated
    *
    * */
    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updated",new Date(),metaObject);
    }
}
