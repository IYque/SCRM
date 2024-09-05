package cn.iyque.interceptor;

import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if(!JwtUtils.validateToken(token)){
                sendJsonErrorResponse(response,HttpStatus.RE_LOGIN, "token失效重新登录");
                return false;
            }

        } else {
            sendJsonErrorResponse(response,HttpStatus.RE_LOGIN, "token错误重新登录");
            return false;
        }



        if(SpringUtil.getBean(IYqueParamConfig.class).getDemo()){
            String method = request.getMethod();
            if (!"GET".equalsIgnoreCase(method)) {
                sendJsonErrorResponse(response,HttpStatus.ERROR,"演示环境数据无法修改,如需使用请自行部署");
                return false;
            }
        }


        return true;
    }

    private void sendJsonErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(),
                ResponseResult.builder()
                        .msg(message)
                        .code(status)
                        .build());
    }
}
