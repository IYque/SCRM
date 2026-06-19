package cn.iyque.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.domain.GiteeTokenResponse;
import cn.iyque.domain.JwtResponse;
import cn.iyque.service.IYqueGiteeOAuthService;
import cn.iyque.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


@Slf4j
@Service
public class IYqueGiteeOAuthServiceImpl implements IYqueGiteeOAuthService {


    @Autowired
    private IYqueParamConfig yqueParamConfig;



    /**
     * 获取 AccessToken 并转换为实体类
     */
    @Override
    public  GiteeTokenResponse getAccessToken(String code,String redirectUri) {

        IYqueParamConfig.GiteeLoginParam giteeLoginParam
                = yqueParamConfig.getThreeLoginParam().getGiteeLoginParam();


        HttpResponse response = HttpRequest.post(giteeLoginParam.getGetTokenUrl())
                .form("grant_type", "authorization_code")
                .form("code", code)
                .form("client_id", giteeLoginParam.getClientId())
                .form("redirect_uri",StringUtils.isEmpty(redirectUri)? giteeLoginParam.getRedirectUri():redirectUri)
                .form("client_secret", giteeLoginParam.getClientSecret())
                .execute();


        if (!response.isOk()) {
            log.error("gitee获取token请求失败:" + response.getStatus());
            return null;
        }

        log.info("响应体:"+response.body());



        return JSONUtil.toBean(response.body(), GiteeTokenResponse.class);
    }

    @Override
    public String getIYqueLoginToken(String code,String redirectUri) {



        GiteeTokenResponse response = this.getAccessToken(code,redirectUri);


        log.info("token:"+response);


        if(null != response){

           if(StringUtils.isNotEmpty(response.getAccessToken())){

               if(!this.isRepoStarred(response.getAccessToken())){
                   //未star则执行star操作
                   if(!this.starRepository(response.getAccessToken())){
                      return null;
                   }
               }

               return JwtUtils.generateToken(yqueParamConfig.getUserName());

           }
        }

        return null;
    }

    @Override
    public boolean isRepoStarred(String accessToken) {

        // 发送 GET 请求
        HttpResponse response = HttpRequest.get(
                MessageFormat.format( yqueParamConfig.getThreeLoginParam().getGiteeLoginParam().getStarUlr(),accessToken)
        ).execute();

        // 解析响应
        if (response.getStatus() == 204) {
            return true; // 已 Star
        } else if (response.getStatus() == 404) {
            return false; // 未 Star
        } else {
            throw new RuntimeException(
                    "请求失败: HTTP " + response.getStatus() + ", " + response.body()
            );
        }
    }

    @Override
    public boolean starRepository(String accessToken) {
        // 2. 构建JSON Body（包含access_token）
        JSONObject jsonBody = new JSONObject();
        jsonBody.set("access_token", accessToken);

        // 3. 发送PUT请求（JSON格式）
        HttpResponse response = HttpRequest.put( yqueParamConfig.getThreeLoginParam().getGiteeLoginParam().getStarRepositoryUrl())
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(jsonBody.toString())
                .timeout(5000)
                .execute();

        // 4. 解析响应
        if (response.getStatus() == 204) {
           log.info("Star 仓库成功！");
            return true;
        } else {
          log.error("操作失败: " + response.getStatus() + ", " + response.body());
            return false;
        }
    }
}
