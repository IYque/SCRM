package cn.iyque.service;

import cn.iyque.domain.GiteeTokenResponse;

public interface IYqueGiteeOAuthService {

    /**
     * 获取请求token
     *
     * @param code
     * @return
     */
    GiteeTokenResponse getAccessToken(String code,String redirectUri);


    /**
     * 获取登录token
     *
     * @param code
     * @return
     */
    String getIYqueLoginToken(String code,String redirectUri);


    /**
     * 判断指定仓库是否进行了star
     * @return
     */
    boolean isRepoStarred(String accessToken);


    /**
     * star仓库
     * @param accessToken
     * @return
     */
    boolean starRepository(String accessToken);


}
