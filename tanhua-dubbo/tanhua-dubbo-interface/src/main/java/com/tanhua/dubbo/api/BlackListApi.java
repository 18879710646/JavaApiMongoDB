package com.tanhua.dubbo.api;

import com.tanhua.domain.vo.PageResult;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2021/3/7
 */
public interface BlackListApi {
    /**
     * 分页查询
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult findBlackList(long page, long pagesize, Long userId);


    //根据用户id和黑名单用户id，删除
    void delete(Long userId,Long blackUserId);

}
