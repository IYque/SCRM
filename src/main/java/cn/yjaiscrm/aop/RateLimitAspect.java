package cn.iyque.aop;

import cn.hutool.core.util.StrUtil;
import cn.iyque.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        try {
            // 1. 获取请求IP
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = request.getRemoteAddr();

            // 2. 构建Redis key
            String key = "rate_limit:" + ip + ":" + joinPoint.getSignature().toShortString();

            // 3. 检查是否被锁定
            String lockKey = "rate_limit_lock:" + ip;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
                throw new RuntimeException("请求过于频繁，请5分钟后再试");
            }

            // 4. 统计请求次数
            Long count = redisTemplate.opsForValue().increment(key);
            if (count == 1) {
                redisTemplate.expire(key, 1, TimeUnit.MINUTES); // 计数窗口1分钟
            }

            // 5. 超过阈值则锁定
            if (count != null && count > rateLimit.attempts()) {
                redisTemplate.opsForValue().set(lockKey, "1", rateLimit.lockTime(), TimeUnit.SECONDS);
                throw new RuntimeException("请求次数过多，IP已被临时锁定");
            }
        }catch (Exception e){
            log.error("异常信息:"+e);
            throw e;
        }


        // 6. 执行原方法
        return joinPoint.proceed();
    }
}
