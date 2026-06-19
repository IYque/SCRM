package cn.iyque.service;


import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYquePeriodMsgAnnex;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

import java.util.List;

public interface IYquePeriodMsgAnnexService {
    void saveAll( List<IYquePeriodMsgAnnex> annexLists);

    void deleteAllByAnnexPeroidIdIn(List<Long> annexPeroidIds);

    /**
     * 附件转化为企业微信api所需的附件
     * @param annexList
     * @return
     */
    List<Attachment> msgAnnexToAttachment(List<IYquePeriodMsgAnnex> annexList);

}
