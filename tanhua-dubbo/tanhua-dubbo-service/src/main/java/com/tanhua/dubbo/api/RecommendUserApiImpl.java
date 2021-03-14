package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;


/**
 * <p>
 * 好友推荐服务实现,
 * 查询是mongodb
 * </p>
 *
 * @author: Eric
 * @since: 2021/3/9
 */
@Service
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询佳人，缘分值最高的
     * @param loginUserId
     * @return
     */
    @Override
    public RecommendUser queryWithMaxScore(Long loginUserId) {
        System.out.println("来到了queryWithMaxScore方法中");
        Query query = new Query();
        // 推荐给登陆用户的id
        query.addCriteria(Criteria.where("toUserId").is(loginUserId));
        // 按分数降序, 只取1个
        query.with(Sort.by(Sort.Order.desc("score"))).limit(1);
        RecommendUser jiaren = mongoTemplate.findOne(query, RecommendUser.class);
        System.out.println("jiaren========="+jiaren);
        return jiaren;
    }

    /**
     * 首页的分页查询的
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    @Override
    public PageResult findPage(Integer page, Integer pagesize, Long userId) {
        // 操作的是mongodb
        Query query=new Query();
        // toUserId为你传进来的userId
        query.addCriteria(Criteria.where("toUserId").is(userId));
        // 获取总记录数
        long total = mongoTemplate.count(query, RecommendUser.class);
        //  创建一个空的集合
        List<RecommendUser> list = Collections.emptyList();
        // 分页结果集
        if (total>0){
            // 有记录，所以分页查询。按desc降序排序
            query.with(PageRequest.of(page-1,pagesize)).with(Sort.by(Sort.Order.desc("score")));
            // 查询记录
           list = mongoTemplate.find(query, RecommendUser.class);

        }
        PageResult pageResult = new PageResult<>();
        pageResult.setItems(list);
        pageResult.setPage(page.longValue());
        pageResult.setPagesize(pagesize.longValue());
        //总页数
        Long pages=total/pagesize;
        pages+=total%pagesize>0?1:0;
        pageResult.setPages(pages);
        pageResult.setCounts(total);
        return pageResult;
    }


}