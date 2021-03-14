package com.tanhua.server.controller;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
//上传头像
public class UserInfoController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getUserInfo(Long userId, Long huanxinID, @RequestHeader("Authorization") String token){
        User user = userService.getUserByToken(token);
        if (null==user){
            throw  new TanHuaException("登录超时，请重新登录!");
        }
        // 通过获取id来查询用户详情
        UserInfoVo vo= userService.findUserInfoById(user.getId());
        return ResponseEntity.ok(vo);

    }


    @PutMapping
    //编辑个人信息】
    public ResponseEntity  updateUserInfo(@RequestHeader("Authorization") String token,@RequestBody UserInfoVo vo){
        userService.updateUserInfo(token,vo);
        return ResponseEntity.ok(null);

    }


    @PostMapping("/header")
    public ResponseEntity updatePhoto(@RequestBody MultipartFile headPhoto,@RequestHeader("Authorization")String token){
        User user = userService.getUserByToken(token);
        if (null==user){
            throw new TanHuaException("登录超时，请重新上传!");
        }
        userService.updatePhoto(headPhoto,token);
        return ResponseEntity.ok(null);
    }


}
