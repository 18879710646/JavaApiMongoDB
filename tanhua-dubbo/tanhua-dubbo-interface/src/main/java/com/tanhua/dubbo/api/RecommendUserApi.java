package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;

public interface RecommendUserApi {
    /*
    * 查询某个用户，推荐值最高的人
    * */
    RecommendUser queryWithMaxScore(Long toUserId);

    // 首页推荐用户分页查询
    PageResult findPage(Integer page, Integer pagesize, Long userId);
}
