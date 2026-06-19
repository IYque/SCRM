package cn.iyque.wxjava.bean;


import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.io.Serializable;


/**
 * 截屏参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxScreenshotInfo implements Serializable {
    /*
     * 开始时间 秒
     */
    @SerializedName("start_time")
    private Long startTime;

    /**
     * 结束时间，开始时间到结束时间的范围不能超过14天 秒
     */
    @SerializedName("end_time")
    private Long endTime;


    /**
     * 由企业微信后台返回，第一次调用可不填
     */
    private String cursor;


    /**
     * 	限制返回的条数，最多设置为1000
     */
    private Integer limit;

    public String toJson() {
        return WxCpGsonBuilder.create().toJson(this);
    }
}
