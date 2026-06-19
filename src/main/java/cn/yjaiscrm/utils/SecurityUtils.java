package cn.iyque.utils;

public class SecurityUtils {


    /**
     * 企业微信移动端，当前则代表userId
     * @return
     */
    public static String getCurrentUserName(){

       return JwtUtils.getUsernameFromToken(ServletUtils.getToken());
    }
}
