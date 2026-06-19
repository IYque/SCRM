package cn.iyque.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


@Configuration
public class MybatisPlusConfig {

//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        // 添加分页插件，并指定数据库类型（这里以MySQL为例）
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }


    // 不添加分页拦截器，即可禁用MyBatis-Plus的分页功能
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 注释掉下面这行，不添加分页插件
        // interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    //pagehelper 分页配置
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(MybatisConfiguration configuration) {
                PageInterceptor pageInterceptor = new PageInterceptor();
                Properties properties = new Properties();
                properties.setProperty("helperDialect", "mysql");
                properties.setProperty("reasonable", "true"); // 分页合理化
                pageInterceptor.setProperties(properties);
                configuration.addInterceptor(pageInterceptor);
            }
        };
    }

}
