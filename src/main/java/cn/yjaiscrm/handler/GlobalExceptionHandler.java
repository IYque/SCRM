package cn.iyque.handler;

import cn.iyque.domain.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<String> handleRateLimitException(RuntimeException e) {
        if (e.getMessage().contains("请求过于频繁") || e.getMessage().contains("IP已被临时锁定")) {
            return  new ResponseResult(429,e.getMessage(),null);
        }


        return  new ResponseResult(500,"登录异常:"+e.getMessage(),null);
    }
}
