package com.tanhua.server.utils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtils {
    @Value("${tanhua.secret}")
    private String secret;


    /*
    * 生成JWT
    * */
    public  String createJWT(String phone,Long userID){
        Map<String,Object> claims=new HashMap<String,Object>();
        claims.put("mobile",phone);
        claims.put("id",userID);
        //获取当前时间
        long niwMillis = System.currentTimeMillis();
        //格式化未date类型保存到map集合中
        Date now = new Date(niwMillis);
        //SignatureAlgorithm.ES256,secret  指定签名的时候使用签名算法，也就是header部分，jwt已经封装好了
        //下面就是未palayLoad添加各种标准的申明，和私有的的申明
        //Jwts.builder()这里其实就是new JwtBuilder 设置jwt的body
        // setClaims(claims)如果有私有申明，那么将使用自定义的，
        //setIssuedAt 设置签发时间
        JwtBuilder builder = Jwts.builder().setClaims(claims).setIssuedAt(now).signWith(SignatureAlgorithm.HS256,secret);
        return builder.compact();




    }
}
