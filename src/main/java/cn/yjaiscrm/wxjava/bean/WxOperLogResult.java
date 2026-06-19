package cn.iyque.wxjava.bean;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;
import java.util.List;

@Data
public class WxOperLogResult extends WxCpBaseResp {

    //是否还有下一页
    @SerializedName("has_more")
    private boolean hasMore;

    //下一页的分页游标。不同过滤条件的的cursor不能混用
    @SerializedName("next_cursor")
    private String nextCursor;

    //记录列表
    @SerializedName("record_list")
    private List<Record> recordList;



    @Data
    public static class Record{

        //操作时间
        private long time;
        //操作者userid
        private String userid;
        //操作类型
        @SerializedName("oper_type")
        private Integer operType;
        //相关数据
        @SerializedName("detail_info")
        private String detailInfo;
        //操作者ip
        private String ip;

    }

    public static WxOperLogResult fromJson(String json) {
        return (WxOperLogResult) WxCpGsonBuilder.create().fromJson(json, WxOperLogResult.class);
    }

}
