package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;

public interface UserInfoApi {
    void save(UserInfo userInfo);
    void update(UserInfo userInfo);
    UserInfo findById(Long userInfo);
}
