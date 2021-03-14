package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.SettingApi;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {
  @Autowired
    private SettingsService  settingsService;

// 利用通用设置来获取到用户Id
  @GetMapping("/settings")
  public ResponseEntity findById(){
    SettingsVo vo = settingsService.getSettings();
    return ResponseEntity.ok(vo);
  }

  // 通用设置中的推送功能的按钮设置
  @PostMapping("/notifications/setting")
  public  ResponseEntity updateNotification(@RequestBody SettingsVo Vo ){
    settingsService.updateNotification(Vo);
    return  ResponseEntity.ok(null);
  }

  /**
   * 陌生人问题 - 保存
   */
  @PostMapping("/questions")
  public ResponseEntity updateQuestions(@RequestBody Map<String,String> param){
    // 调用业务更新
    settingsService.updateQuestions(param.get("content"));
    return ResponseEntity.ok(null);
  }

  /**
   * 分页查询黑名单列表
   *  请求连接：
   *      GET /blacklist
   *  请求参数：
   *      Query参数：
   *          page：当前页
   *          pagesize：每页查询条数
   */
  @GetMapping("/blacklist")
  public ResponseEntity findBlackList(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10")  int pagesize) {

    pagesize = pagesize>50?50:pagesize; // 防止pagesize过大
    // 分页查询
    PageResult<UserInfoVo> pageResult = settingsService.findBlackList(page,pagesize);
    return ResponseEntity.ok(pageResult);
  }
  /**
   * 移除黑名单
   *  请求连接：
   *      DELETE  /blacklist/{uid}
   */
  @DeleteMapping("/blacklist/{uid}")
  public ResponseEntity delBlacklist(@PathVariable("uid") long deleteUserId) {
    return settingsService.delBlacklist(deleteUserId);
  }


  /**
   * 修改手机号码：1.发送验证码
   */
  @PostMapping("/phone/sendVerificationCode")
  public ResponseEntity sendValidateCode(){
    settingsService.sendValidateCode();
    return ResponseEntity.ok(null);
  }

  /**
   * 修改手机号 - 2 校验验证码
   */
  @PostMapping("/phone/checkVerificationCode")
  public ResponseEntity checkValidateCode(@RequestBody Map<String,String> param){
    boolean flag = settingsService.checkValidateCode(param.get("verificationCode"));
    Map<String,Boolean> result = new HashMap<String,Boolean>();
    result.put("verification",flag);
    return ResponseEntity.ok(result);
  }
  /**
   * 修改手机号 - 3 保存
   */
  @PostMapping("/phone")
  public ResponseEntity changeMobile(@RequestBody Map<String,String> param, @RequestHeader("Authorization") String token){
    settingsService.changeMobile(param.get("phone"),token);
    return ResponseEntity.ok(null);
  }
}
