package cn.iyque.interceptor;

import cn.iyque.config.IYqueParamConfig;
import cn.iyque.domain.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ReadOnlyInterceptor implements HandlerInterceptor {




    @Autowired
    private IYqueParamConfig yqueParamConfig;

    // 允许的GET请求URL白名单
    private static final List<String> NOT_ALLOWED_GET_URLS = Arrays.asList(
            "/msg/synchMsg",
            "/msg/buildAISessionWarning",
            "/msg/aiIntentionAssay"
    );


    // 允许的非GET请求URL白名单
    private static final List<String> NOT_ALLOWED_OTHER_URLS = Arrays.asList(
            "/iYqueSys/login",
            "/iYqueSys/threeLogin/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {



        if (yqueParamConfig.getDemo()) {
            String method = request.getMethod();
            String requestURI = request.getRequestURI();

            // 只允许白名单中的URL使用GET请求
            if ("GET".equalsIgnoreCase(method)) {
                boolean isNotAllowed = NOT_ALLOWED_GET_URLS.stream()
                        .anyMatch(pattern -> new AntPathMatcher().match(pattern, requestURI));

                if (isNotAllowed) {
                    return handleErrorResponse(response);
                }
            }



            if (!"GET".equalsIgnoreCase(method) &&
                    !"HEAD".equalsIgnoreCase(method) &&
                    !"OPTIONS".equalsIgnoreCase(method)) {

                boolean isAllowed = NOT_ALLOWED_OTHER_URLS.stream()
                        .anyMatch(pattern -> new AntPathMatcher().match(pattern, requestURI));

                if(!isAllowed){
                    return handleErrorResponse(response);
                }
            }
        }
        return true;
    }

    private boolean handleErrorResponse(HttpServletResponse response) throws IOException {

        // 设置响应内容类型
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 写入响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                new ResponseResult( HttpStatus.INTERNAL_SERVER_ERROR.value(),"演示环境数据无法修改,如需体验完整功能,可自行部署",null)

        ));

        return false;
    }
}


