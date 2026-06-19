package cn.iyque.wxjava.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;


/**
 * 截屏返回
 */
@Data
public class WxScreenshotResult extends WxCpBaseResp {
    //是否还有下一页
    @SerializedName("has_more")
    private boolean hasMore;

    //下一页的分页游标。不同过滤条件的的cursor不能混用
    @SerializedName("next_cursor")
    private String nextCursor;

    @SerializedName("record_list")
    private List<Record> recordList;

    @Data
    public static class Record{

        //操作时间
        private long time;

        //操作人id
        private String userid;

        @SerializedName("department_id")
        private Long departmentId;


        @SerializedName("screen_shot_type")
        private Integer screenShotType;


        @SerializedName("screen_shot_content")
        private String screenShotContent;



        private String system;


    }

    public static WxScreenshotResult fromJson(String json) {
        return (WxScreenshotResult) WxCpGsonBuilder.create().fromJson(json, WxScreenshotResult.class);
    }

}
