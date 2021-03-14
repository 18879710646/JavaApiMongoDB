package com.tanhua.server.service;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.GetAgeUtil;
import com.tanhua.server.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserService {
    // @Reference 也是注入在dubbo中的注入的是分布式远程服务的对象,表示的是订阅的意思
    @Reference
    private UserApi userApi;
    @Autowired
    private  UserService userService;
    @Autowired
    private FaceTemplate faceTemplate;
    @Reference
    private UserInfoApi userInfoApi;
    @Autowired
    private  OssTemplate ossTemplate;

    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    @Autowired
    private RedisTemplate redisTemplate;
    //mobile
    public User findAll(String mobile){
        User byMobile = userApi.findByMobile(mobile);
        return byMobile;
    }
    public void saveUser(String mobile, String password) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        user.setCreated(new Date());
        userApi.save(user);
    }

    /**
     * 保存用户信息
     * @param userInfo
     * @param token
     */
    public void saveUserInfo(UserInfo userInfo,String token) {
        //1. 从token中来获取登陆用户信息, 保存 tb_userInfo.id=登陆用户的id
//        User user = getUserByToken(token);
//        if(null == user){
//            throw new TanHuaException("登陆超时，请重新登陆");
//        }
        //2. 设置用户的id
        userInfo.setId(UserHolder.getUserId());
        // 通过出生日期来获取年龄
        userInfo.setAge(GetAgeUtil.getAge(userInfo.getBirthday()));
        //3. 调用服务保存用户信息
        userInfoApi.save(userInfo);
    }

    // 发送验证码
    public  void sendvalidateCode(String phone){
            // 1:redis存入验证码的key
        String key= redisValidateCodeKeyPrefix + phone;
        //2:redis中的验证码
        String codeInRedis = (String) redisTemplate.opsForValue().get(key);
        log.info("codeInRedis:{}",codeInRedis);
        //3:如果存在提示上一次的验证码还生效
        if (StringUtils.isNoneEmpty(codeInRedis)){
            //有值，验证码未失效则返回自定义异常
                throw new TanHuaException(ErrorResult.duplicate());
        }else {
            //否则没有
            //生成验证码
            String validateCode = RandomStringUtils.randomNumeric(6);
            log.info("=============验证码:{},{}",phone, validateCode);
            //6. 发送验证码
            Map<String, String> smsRs = smsTemplate.sendvalidateCode(phone,validateCode);
            if(null != smsRs){
                // 验证码发送失败，报错
                throw new TanHuaException(ErrorResult.fail());
            }
            redisTemplate.opsForValue().set(key,validateCode, Duration.ofMinutes(10));


        }
    }

    /*
    * 登录或者验证
    *
    *
    * */

    public  Map<String ,Object> loginVerification(String phone,String verificationCode){
        //redis中存入验证码的key
        String key = redisValidateCodeKeyPrefix + phone;
        System.out.println("redis中的phone=========="+phone);
        //redis中的验证码
        String codeInredis = (String) redisTemplate.opsForValue().get(key);
        System.out.println("redis中的验证码=======："+codeInredis);
        log.debug("========校验 验证码：{}，{}，{}",phone,codeInredis,verificationCode);
        if (StringUtils.isEmpty(codeInredis)){
            throw  new TanHuaException(ErrorResult.loginError());
        }
        if (!codeInredis.equals(verificationCode)){
            throw  new TanHuaException(ErrorResult.validateCodeError());
        }
         //防止重复提交
        redisTemplate.delete(key);
        // 查看用户是否存在
        User user = userApi.findByMobile(phone);
        log.info("用户信息: {}", user==null?"不存在":"存在");
        boolean isNew=false;
        if (null ==user){
            //不存在则添加用户
             user = new User();
             user.setCreated(new Date());
            user.setUpdated(new Date());
             user.setMobile(phone);
             //手机号后6位为默认密码
            user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length()-6)));
            log.info("============添加新用户{}",phone);
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew=true;

        }
        //签发token令牌
        String token = jwtUtils.createJWT(phone, user.getId());
        // 用户信息存入redis，方便后期获取，有效期为1天
        String userstring = JSON.toJSONString(user);
        redisTemplate.opsForValue().set("TOKEN_"+token,userstring,1, TimeUnit.DAYS);
        log.debug("=========================签发token:{}",token);
        //返回结果
        Map<String, Object> resultMap = new HashMap<String,Object>();
        resultMap.put("isNew",isNew);
        resultMap.put("token",token);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
        return resultMap;
    }


    /**
     *通过tonken获取用户信息
     * @param token
     * @return
     */

    public User getUserByToken(String token){
        String key="TOKEN_"+token;
        String userString = (String) redisTemplate.opsForValue().get(key);
        System.out.println("token中获取到的用户ID============"+(userString == null ));
        System.out.println(StringUtils.isNoneEmpty(userString));
        if (StringUtils.isEmpty(userString)){
            //用户超时了
            return  null;
        }
            // 解析Json字符串为登入信息
        User user = JSON.parseObject(userString, User.class);
        // 重置有效期为七天
        redisTemplate.expire(key,7, TimeUnit.DAYS);
        return user;

    }

    /**
     *通过用户Id，查询用户详情
     * @param id
     * @return
     */
    public UserInfoVo findById(Long id){
        UserInfo userinfo = userInfoApi.findById(id);
        System.out.println("userinfo====" + userinfo);
        System.out.println(userinfo!=null);
        //转成vo
        UserInfoVo vo = new UserInfoVo();
        //复制属性
        BeanUtils.copyProperties(userinfo, vo);
        //年龄处理，如果数据库是空值
        Integer age = userinfo.getAge();
        if (age==null){
            vo.setAge("18");
        }else {
            vo.setAge(age.toString());
        }
        return vo;

    }

//  更新个人信息
    public void updateUserInfo(String token, UserInfoVo vo) {
//        User user = getUserByToken(token);
//        if (null==user){
//            throw new TanHuaException("登录超时，请重新登录！");
//        }
        // 把vo转成userInfo
        UserInfo userInfo = new UserInfo();
        // 复制属性值
        BeanUtils.copyProperties(vo,userInfo);
        // 设置用户id
        userInfo.setId(UserHolder.getUserId());
        // 设置年龄
        userInfo.setAge(GetAgeUtil.getAge(vo.getBirthday()));
        // 更新
        userInfoApi.update(userInfo);
        log.info("更新用户信息成功,{}",userInfo);
    }

    /**
     * 新用户上传头像
     * @param headPhoto
     * @param token
     */
    public void updateUserAvatar(MultipartFile headPhoto, String token) {
        System.out.println("开始上传头像！======");
        log.info("开始上传头像处理...............");
        //获取用户登录信息
        User user = getUserByToken(token);
        //用户失效判断
        if (null==user){
            throw  new TanHuaException("登录超时，请重新登录");
        }
        try {
            //头像的人脸检测  faceTemplate
            byte[] bytes = headPhoto.getBytes();

//            byte[] bytes = Files.readAllBytes(new File("C:\\Users\\HP\\Desktop\\xbxj.jpg").toPath());
            boolean detect = faceTemplate.detect(bytes);
            if (!detect){
                throw  new TanHuaException("没有检测到人脸，请重新上传！");
            }
            log.info("人脸识别通过，准备上传到oss");
            //通过则上传到阿里的oss
//            String avatarUrl = ossTemplate.upload("xbxj.jpg", new FileInputStream("C:\\Users\\HP\\Desktop\\xbxj.jpg"));
            String avatarUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            //构建userinfo对象
            log.info("上传用户头像成功:{},{}",avatarUrl, user.getId());
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setAvatar(avatarUrl);
            //调用userInfoApi更新数据
            userInfoApi.update(userInfo);
        } catch (IOException e) {
            log.error("上传头像失败",e);
            throw  new TanHuaException("上传头像失败！请稍后重试");
        }


    }


    // 查询用户详情,作用在APK中的头像展示
    public UserInfoVo findUserInfoById(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);
        UserInfoVo vo = new UserInfoVo();
        // 拷贝属性
        BeanUtils.copyProperties(userInfo,vo);
        if (null!=userInfo.getAge()){
            vo.setAge(String.valueOf(userInfo.getAge().intValue()));
        }
        return vo;
    }

    //更新头像
    public void updatePhoto(MultipartFile headPhoto, String token) {
        try {
            byte[] bytes = headPhoto.getBytes();
            boolean detect = faceTemplate.detect(bytes);
            if (!detect){
                //不通过没有检测到人脸请重新登录
                throw  new TanHuaException("没有检测到人脸请重新检测！！");
            }
            log.info("人脸检测通过，正在上传中！");
            String avatarUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            log.info(String.format("头像上传成功！{}，{}", avatarUrl, UserHolder.getUserId()));
            UserInfo userInfo4Avatar = userInfoApi.findById(UserHolder.getUserId());
            String oldavatar = userInfo4Avatar.getAvatar();
            log.info("获取到旧头像信息成功！{}",oldavatar);
            UserInfo userInfo = new UserInfo();
            userInfo.setId(UserHolder.getUserId());
            userInfo.setAvatar(avatarUrl);
            userInfoApi.update(userInfo);
            log.info("更新头像成功！");
            ossTemplate.deleteFile(oldavatar);
            log.info("删除旧头像图片成功........{}",oldavatar);
        } catch (IOException e) {
            throw new TanHuaException("更新头像失败！"+e);
        }


    }
}
