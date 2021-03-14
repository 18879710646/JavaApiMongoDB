package com.tanhua.server.service;

import com.sun.xml.bind.v2.model.core.ID;
import com.tanhua.commons.RedisConstant.RedisKeyConst;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.*;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;

import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SettingsService {
    @Autowired
    private  UserService userService;
    @Reference
    private UserApi userApi;
    @Reference
    private SettingApi settingApi;
    @Reference
    private BlackListApi blackListApi;
    @Reference
    private QuestionApi questionApi;

    @Autowired
    private  SmsTemplate smsTemplate;
    @Reference
    private RedisKeyConst redisKeyConst;
    @Autowired
    private RedisTemplate redisTemplate;



    public SettingsVo getSettings() {
        // 获取到id
        User user = UserHolder.getUser();
        System.out.println("通用设置中获取到的ID====="+user.getId());
        System.out.println("=============");
        //设置通用设置api
        System.out.println(settingApi);
        // 他这是利用user.getId来获取所有settings中的所有值
        Settings settings = settingApi.findByUserId(user.getId());
        System.out.println("settings==="+settings);
        //settings===Settings(id=1, userId=10048, likeNotification=true, pinglunNotification=true, gonggaoNotification=true)
        // 设置陌生人问题api
        Question question = questionApi.findByUserId(user.getId());
        System.out.println("question====="+question);

        //构建vo中的值
        SettingsVo vo = new SettingsVo();
        if (null != settings) {
            BeanUtils.copyProperties(settings, vo);
            System.out.println("vo====="+vo);
            //vo=====SettingsVo(id=1, strangerQuestion=null, phone=null, likeNotification=true, pinglunNotification=true, gonggaoNotification=true)
        }
        // 设置陌生人问题
        if (null != question) {
            vo.setStrangerQuestion(question.getTxt());
        }
        //设置手机号
        vo.setPhone(user.getMobile());
        // 返回
        return vo;

    }

    // 保存通用设置中的推送按钮是否被点击
    public void updateNotification(SettingsVo vo) {
        Long userId = UserHolder.getUserId();
        Settings settings = settingApi.findByUserId(userId);
        System.out.println("在保存和更新通用设置中的推送按钮是否被点击中获取到的用户ID" + settings);
        log.info("在数据库中的settings中的数据===" + settings);
        if (null != settings) {
            BeanUtils.copyProperties(vo, settings, "id");
            settingApi.updateNotification(settings);
        } else {
            settings = new Settings();
            BeanUtils.copyProperties(vo, settings);
            settings.setUserId(userId);
            settingApi.save(settings);


        }


    }

    // 陌生人问题保存
    public void updateQuestions(String content) {
        Long userId = UserHolder.getUserId();
        Question question = questionApi.findByUserId(userId);
        if (null!=question){
            question.setTxt(content);
            questionApi.update(question);
        }else {
             question = new Question();
             question.setTxt(content);
             question.setUserId(userId);
             questionApi.save(question);

        }

    }

    /**
     * 黑名单分页查询
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<UserInfoVo> findBlackList(long page, long pagesize) {
        // 获取用户id
        Long userId = UserHolder.getUserId();
        // 调用api分页查询
        PageResult pageResult = blackListApi.findBlackList(page,pagesize,userId);
        // 把UserInfo转成vo, 可以转也可不转成vo，如果是要按规范（企业）走
        // 分页的结果集
        List<UserInfo> items = (List<UserInfo>)pageResult.getItems();
        if(CollectionUtils.isNotEmpty(items)){
            // 接收的集合，转在的目标集合
            List<UserInfoVo> list = new ArrayList<UserInfoVo>();
            // 遍历查询到的结果集
            for (UserInfo item : items) {
                // 创建 vo对象
                UserInfoVo vo = new UserInfoVo();
                // 复制属性值
                BeanUtils.copyProperties(item, vo);
                vo.setAge(item.getAge().toString());
                // 添加到新集合里
                list.add(vo);
            }
            /*List<UserInfoVo> list = items.stream().map(userInfo -> {
                UserInfoVo vo = new UserInfoVo();
                BeanUtils.copyProperties(userInfo, vo);
                return vo;
            }).collect(Collectors.toList());*/
            pageResult.setItems(list);
        }
        // 再返回给
        return pageResult;
    }

    // 移除黑名单
    public ResponseEntity delBlacklist(long deleteUserId) {
        Long userId = UserHolder.getUserId();
        blackListApi.delete(userId,deleteUserId);
        return ResponseEntity.ok(null);
    }

    //  给旧的手机号发送验证码
    public void sendValidateCode() {
        String mobile = UserHolder.getUser().getMobile();
        // reids中存入的验证码的Key
        String key = redisKeyConst.LOGIN_VALIDATE_CODE + mobile;
        // redis中存入的验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(key);
        log.debug("redis中的手机号{}验证码{}", mobile, codeInRedis);
        if (StringUtils.isNotEmpty(codeInRedis)){
            throw new TanHuaException(ErrorResult.duplicate());

        }
        // 生成验证码
        String validateCode = RandomStringUtils.randomAlphabetic(6);
        System.out.println("给旧手机发送的验证码是======="+validateCode);
        // 发送验证码
        Map<String, String> smsResult = smsTemplate.sendvalidateCode(mobile, validateCode);
        if (null!=smsResult){
            throw new TanHuaException(ErrorResult.fail());
        }
        log.info("===== 验证码存入到redis中");
        // 存入到redsis有效期为5分钟
        redisTemplate.opsForValue().set(key,validateCode,5,TimeUnit.MINUTES);

    }

    /**修改手机号，校验验证码
     *
     * @param verificationCode
     * @return
     */

    public boolean checkValidateCode(String verificationCode) {
        String phone = UserHolder.getUser().getMobile();
        String key = redisKeyConst.CHANGE_MOBILE_VALIDATE_CODE + phone;
        //redis中的验证码
        String CodeInRedis = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(CodeInRedis)){
            throw  new TanHuaException(ErrorResult.loginError());
        }
        if (!CodeInRedis.equals(verificationCode)){
            return false;
        }
        return true;

    }
// 更新保存的手机号
    public void changeMobile(String phone, String token) {
        Long userId = UserHolder.getUserId();
        userApi.updateMobile(userId,phone);
        String key = RedisKeyConst.TOKEN + token;
        redisTemplate.delete(key);
    }
}
