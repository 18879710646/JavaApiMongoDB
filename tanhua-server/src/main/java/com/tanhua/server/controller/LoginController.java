package com.tanhua.server.controller;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
@Autowired
    private UserService userService;
    @GetMapping("/findUser")
    public ResponseEntity<User> findUser(String mobile){
        //通过电话号码来查询
        User all = userService.findAll(mobile);
        // 响应
        return ResponseEntity.ok(all);
    }
    @PostMapping("/saveUser")
    public ResponseEntity SaveUser(@RequestBody Map<String,String> parm){
        String mobile = parm.get("mobile");
        String password = parm.get("password");
        userService.saveUser(mobile,password);
        return ResponseEntity.ok("保存成功！");
    }

    /**
     * 登陆时发送验证码（apk端）
     * @param param
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity sendValidateCode(@RequestBody Map<String,String> param){
        // 获取手机号码
        String phone = param.get("phone");
        System.out.println("获取到的手机号：========"+phone);
        // 调用业务service发送
        userService.sendvalidateCode(phone);
        // 响应结果
        return ResponseEntity.ok(null);
    }

    /*
    * 登录验证码校验
    *@PostMapping("/loginVerification")  中的/有和没有是一样的，没有将自动补全
     */

    @PostMapping("loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        String verificationCode = param.get("verificationCode");
        Map<String,Object> map=userService.loginVerification(phone,verificationCode);
        return ResponseEntity.ok(map);
    }


    /**
     * 查询用户信息
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfoVo userInfoVo, @RequestHeader("Authorization" ) String token){
        // 转成userInfo
        UserInfo userInfo = new UserInfo();
        // copyProperties 复制属性值(属性性名一样就会复制，不一样就忽略)
        // 第一个参数，来源，userInfoVo 复制到 userInfo, 来源就是userInfoVo
        // 第二个参数，目标对象。userInfo
        BeanUtils.copyProperties(userInfoVo,userInfo);
        // 调用UserService 完成注册，
        userService.saveUserInfo(userInfo,token);
        return ResponseEntity.ok(null);

    }


    @PostMapping("/loginReginfo/head")
    public  ResponseEntity uploadAvatar(MultipartFile headPhoto, @RequestHeader("Authorization") String token){
        userService.updateUserAvatar(headPhoto,token);
        return ResponseEntity.ok(null);

    }
}
