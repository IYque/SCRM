package cn.iyque.wxjava.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxOperLogInfo implements Serializable {

    /*
     * 开始时间取值范围：不早于180天前
     */
    @SerializedName("start_time")
    private Long startTime;

    /**
     * 结束时间
     * 取值范围：大于start_time，小于当前时间。开始时间和结束时间之间的跨度不能超过7天。
     */
    @SerializedName("end_time")
    private Long endTime;


    /**
     * 操作类型。不填表示全部
     */
    @SerializedName("oper_type")
    private Integer operType;


    /**
     * 操作者userid过滤，需要在应用可见范围内。可不填
     */
    private String userid;

    /**
     * 分页游标。不填表示首页
     */
    private String cursor;

    /**
     * 最大记录数。不填默认最多获取400个记录
     * 取值范围：1 ~ 400
     * 注意：不保证每次返回的数据刚好为指定limit，必须用返回的 has_more 判断是否继续请求
     */
    private Integer limit;


    public String toJson() {
        return WxCpGsonBuilder.create().toJson(this);
    }
}
