package cn.iyque.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;

@Data
public class NewContactWay extends WxCpContactWayInfo.ContactWay{
    @SerializedName("is_exclusive")
    private Boolean isExclusive;
}
