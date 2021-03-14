package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

@Service
public class UserApiImpl implements UserApi {
@Autowired
private UserMapper userMapper;
// 添加用户
    @Override
    public Long save(User user) {
//        user.setCreated(new Date());
//        user.setUpdate(new Date());
        userMapper.insert(user);
        return user.getId();
    }


//    通过手机号码查询
    @Override
    public User findByMobile(String mobile) {
        System.out.println("======================");
//        构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        //按手机号码查询
        queryWrapper.eq("mobile",mobile);
        System.out.println("手机号码查询到的值===="+userMapper.selectOne(queryWrapper));
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findById(Long id) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id",id);
        System.out.println("用户id查询到的值为====="+userMapper.selectOne(userQueryWrapper));
        return userMapper.selectOne(userQueryWrapper);
    }

    @Override
    public void updateMobile(Long userId, String phone) {
        User user = new User();
        user.setId(userId);
        user.setMobile(phone);
        userMapper.updateById(user);
    }
}
