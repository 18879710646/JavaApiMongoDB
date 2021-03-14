package com.tanhua.commons.templates;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.tanhua.commons.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SmsTemplate {
    /** SMSRESPONSE_CODE SMSRESPONSE_MESSAGE 用来返回给调用者，短信发送结果的key */
    public static  final  String SMSRESPONSE_CODE="code";
    public  static  final  String  SMSRESPONSE_MESSAGE="message";
    private SmsProperties smsProperties;

    private IAcsClient acsClient;

    public SmsTemplate(SmsProperties smsProperties){
        this.smsProperties=smsProperties;

    }
    // 初始化阿里云短信
    public void init(){
        //设置超时时间，可以自己调整
        System.setProperty("sun.net.client.defaultConnectTimeout","10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascclient需要的几个参数
        final  String product="Dysmsapi";  // 短信API产品名称（短信产品固定）
        final  String domain="dysmsapi.aliyuncs.com";  //短信产品域名（接口地址固定，无需修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
        try {
            DefaultProfile.addEndpoint("cn-hangzhou","cn-hangzhou",product,domain);
            acsClient = new DefaultAcsClient(profile);
        } catch (ClientException e) {
            log.error("初始化阿里云短信错误",e);
            e.printStackTrace();
        }


    }

    //发送验证码

    public Map<String,String> sendvalidateCode(String phoneNumber,String validateCode){
            return sendSms(smsProperties.getValidateCodeTemplateCode(),phoneNumber,validateCode);
    }


    //发送短信
    public Map<String ,String> sendSms(String templateCode,String phoneNumber,String param){
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post请求
        request.setMethod(MethodType.POST);
        // 必填待发送手机号，支持以逗号的形式批量调用，批量上线为1000个手机号码，批量调用相对于单挑调用有延迟，验证码类型的短信推荐单条d调用方式
        request.setPhoneNumbers(phoneNumber);
        //必填短信签名-可在短信控制台中找到
        request.setSignName(smsProperties.getSignName());
        //必填短信模板
        request.setTemplateCode(templateCode);
        //可选：模板中的变量替换json串，如模板内容为“亲爱的${name}”，您的验证码为${code}时，此处的值
        request.setTemplateParam(String.format("{\"%s\":\"%s\"}",smsProperties.getParameterName(),param));
        //将发送的结果装到map里
        Map<String, String> result = new HashMap<>();
//        System.out.println("Phone========="+request.getPhoneNumbers());
//        System.out.println("sigName===="+request.getSignName());
        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println("响应结果："+sendSmsResponse.getCode());
            if (("OK").equals(sendSmsResponse.getCode())){
                //请求成功
                return null;
            }
            result.put(SmsTemplate.SMSRESPONSE_CODE,sendSmsResponse.getCode());
            result.put(SmsTemplate.SMSRESPONSE_MESSAGE,sendSmsResponse.getMessage());
        } catch (ClientException e) {
            log.error("发送短信失败！",e);
            System.out.println("短信发送失败错误信息：="+e);
           result.put(SmsTemplate.SMSRESPONSE_CODE,"FAIT");
        }
        return  result;
    }






}
