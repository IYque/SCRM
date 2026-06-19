package cn.iyque.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

public class TicketUtils {
    public static Map<String, Object> getSignature(String ticket, String url) {
        String nonceStr = RandomStringUtils.randomAlphanumeric(10);
        Long timestamp = System.currentTimeMillis() / 1000;
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("jsapi_ticket=").append(ticket)
                .append("&noncestr=").append(nonceStr)
                .append("&timestamp=").append(timestamp)
                .append("&url=").append(url);

        String signature = DigestUtils.sha1Hex(strBuild.toString());
        return new HashMap(16) {{
            put("nonceStr", nonceStr);
            put("timestamp", timestamp);
            put("signature", signature);
        }};
    }

}
