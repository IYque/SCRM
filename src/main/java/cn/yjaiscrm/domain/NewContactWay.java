package cn.iyque.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewContactWay extends WxCpContactWayInfo.ContactWay{

}
