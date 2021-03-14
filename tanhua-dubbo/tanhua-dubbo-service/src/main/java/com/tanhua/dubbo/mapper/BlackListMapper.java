package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2021/3/7
 */
public interface BlackListMapper extends BaseMapper<BlackList> {
    /**
     * 查询 黑名单用户信息
     * 需要 tb_user_info 和 tb_black_list两张变联合查询
     * 分页查询
     */

    @Select("select id,avatar,nickname,gender,age from tb_user_Info where id in (" +
        " select black_user_id from tb_black_list where user_id=#{userId})")
    IPage<UserInfo> findBlackList(IPage<UserInfo> userInfoPage, @Param("userId") Long userId);
}
