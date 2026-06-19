package cn.iyque.wxjava.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;


/**
 * 文件安全返回
 */
@Data
public class WxFileSecurityResult extends WxCpBaseResp {
    //是否还有下一页
    @SerializedName("has_more")
    private boolean hasMore;

    //下一页的分页游标。不同过滤条件的的cursor不能混用
    @SerializedName("next_cursor")
    private String nextCursor;

    //
    @SerializedName("record_list")
    private List<Record> recordList;


    @Data
    public static class Record{

        //操作时间
        private long time;

        //操作人id
        private String userid;


        //文件操作说明
        @SerializedName("file_info")
        private String fileInfo;


        //操作内容
        private Operation operation;


        //企业外部人员账号信息，当操作者为企业外部用户时返回该结构
        private ExternalUser externalUser;


    }


    @Data
    public static class ExternalUser{
        private Integer type;
        private String name;

        @SerializedName("corp_name")
        private String corpName;

    }

    @Data
    public static class Operation{
        //操作类型
        private Integer type;
        //操作来源
        private Integer source;

    }

    public static WxFileSecurityResult fromJson(String json) {
        return (WxFileSecurityResult) WxCpGsonBuilder.create().fromJson(json, WxFileSecurityResult.class);
    }
}
