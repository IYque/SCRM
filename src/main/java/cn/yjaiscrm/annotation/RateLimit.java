package cn.iyque.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int attempts() default 5;     // 默认允许5次尝试
    long lockTime() default 300;   // 锁定时间（秒），默认5分钟
}
