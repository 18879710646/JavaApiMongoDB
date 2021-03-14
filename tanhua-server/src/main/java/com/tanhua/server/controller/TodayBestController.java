package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.TodayBestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tanhua")
public class TodayBestController {
    @Autowired
    private TodayBestService todayBestService;
    @GetMapping("/todayBest")
    public ResponseEntity todayBest(){
        //今日佳人
        TodayBestVo vo=todayBestService.queryTodayBest();
        System.out.println("今日佳人=============");
        return ResponseEntity.ok(vo);
    }

    // 首页里面的推荐用户
    @GetMapping("/recommendation")
    public ResponseEntity recommendList(RecommendUserQueryParam param){
      PageResult<TodayBestVo> pageResult= todayBestService.recommendList(param);
        return ResponseEntity.ok(pageResult);
    }
}
