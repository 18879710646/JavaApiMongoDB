package com.tanhua.dubbo.api;


import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserInfoApiImpl implements UserInfoApi {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public void save(UserInfo userInfo) {
//        保存用户信息
        userInfoMapper.insert(userInfo);
    }
    // 更新用户信息
    @Override
    public void update(UserInfo userInfo) {

        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        System.out.println("id======="+id);
        return userInfoMapper.selectById(id);
    }
}
