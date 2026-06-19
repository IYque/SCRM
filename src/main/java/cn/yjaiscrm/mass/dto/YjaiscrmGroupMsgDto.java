package cn.iyque.mass.dto;


import cn.iyque.entity.IYqueGroupMsg;
import lombok.Data;
import me.chanjar.weixin.cp.bean.external.WxCpMsgTemplate;

import java.util.List;

@Data
public class IYqueGroupMsgDto {
    //群发基础信息
    private IYqueGroupMsg iYqueGroupMsg;

    //群发任务模版
    private List<WxCpMsgTemplate> wxCpMsgTemplate;
}
