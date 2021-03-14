package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 表示的是以class的Bean这是用在类中的，！@Bean这是在方法中
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private  UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token)){
            User user = userService.getUserByToken(token);
            log.info("拦截器中获取到的token是==="+user);
            if (null!=user){
                  //  存入ThreadLocal 存入到本地线程中
                UserHolder.setUser(user);
                return true;
            }
        }
            log.info("在拦截器中没有获取到Token的值");
            return false;

    }
}
