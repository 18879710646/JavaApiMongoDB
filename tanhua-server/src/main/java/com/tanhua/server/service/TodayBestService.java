package com.tanhua.server.service;

import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.RecommendUserApi;

import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TodayBestService {
    @Reference
    private RecommendUserApi recommendUserApi;

    @Reference
    private UserInfoApi userInfoApi;
    // 查询佳人，缘分值最高的
    public TodayBestVo queryTodayBest() {
        Long loginuserId = UserHolder.getUserId();
        log.info("今日佳人查询：登录用户的id={}",loginuserId);

        //调用recommendUserApi查询佳人，缘分值最高的
         RecommendUser recommendUser=  recommendUserApi.queryWithMaxScore(loginuserId);
        log.info("查询到的佳人：{}",recommendUser);
        log.error("没有找到佳人：{}",recommendUser);
        //没值
        if (null==recommendUser){
            //给一个默认用户Id(1)
            recommendUser = new RecommendUser();
            recommendUser.setScore(95d);
            recommendUser.setUserId(5l);
        }
        //有值
        //调用userinfoApi通过查询用户信息
        UserInfo  userInfo=userInfoApi.findById(recommendUser.getUserId());

        log.info("查询到的用户信息: {}",userInfo);
        // 6. 构建vo，把查询佳人, 及用户信息转成vo
        TodayBestVo todayBestVo = new TodayBestVo();
        BeanUtils.copyProperties(userInfo,todayBestVo);
        // tags数组处理
        //todayBestVo.setTags(userInfo.getTags()==null?new String[]{}:userInfo.getTags().split(","));
        // 【注意】：尽量不要出现在空值
        todayBestVo.setTags(StringUtils.split(userInfo.getTags(),","));
        // 缘分值,向下取整
        todayBestVo.setFateValue(recommendUser.getScore().longValue());
        // 7. 返回给controller
        return todayBestVo;
    }

    // 首页列表
    public PageResult<TodayBestVo> recommendList(RecommendUserQueryParam param) {
        // 先获取登录用户的id
        Long userId = UserHolder.getUserId();
        //调用recommendUserApi 分页查询推荐用户
        PageResult pageResult =  recommendUserApi.findPage(param.getPage(),param.getPagesize(),userId);
        //获取到分页结果集
        List<RecommendUser> items =  (List<RecommendUser>) pageResult.getItems();
        if (CollectionUtils.isEmpty(items)){
            //如果没有找到需要使用默认列表
//             pageResult = new PageResult(10l, param.getPagesize().longValue(), 1l, 1l, null);
             // 没有就构建数据给records
            items=defaultRecommend();
        }
        //通过用户Id来查询用户详情
        //遍历用户集合 每个查询下它们的详情信息
        //变成list<todayBestVo> list
        List<TodayBestVo> list=new ArrayList<TodayBestVo>();
        for (RecommendUser item : items) {
            Long uid = item.getUserId();
            UserInfo userInfo = userInfoApi.findById(uid);
            TodayBestVo vo = new TodayBestVo();
            // 复制属性
            BeanUtils.copyProperties(userInfo,vo);
            //tage数组处理 注意不要出现空值
            vo.setTags(StringUtils.split(userInfo.getTags(),","));
            // 向下取整
            vo.setFateValue(item.getScore().longValue());
            list.add(vo);
        }
        // 设置返回的PageResult ,在返回
        pageResult.setItems(list);
        return pageResult;



    }
// 如果没有找到需要的，默认消费者首页，构造默认数据
    private List<RecommendUser> defaultRecommend() {
        String ids = "1,2,3,4,5,6,7,8,9";
        List<RecommendUser> records = new ArrayList<>();
        for(String id :ids.split(",")){
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(Long.valueOf(id));
            recommendUser.setScore(RandomUtils.nextDouble(70,93));
            records.add(recommendUser);
        }
        return records;


    }
}
