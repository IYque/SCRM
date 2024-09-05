package cn.iyque.service;

import cn.iyque.entity.IYqueMsgAnnex;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

import java.util.List;

public interface IYqueMsgAnnexService {

    List<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(Long msgId);

    void deleteIYqueMsgAnnexByMsgId(Long msgId);

    void saveAll( List<IYqueMsgAnnex> annexLists);
    List<Attachment> msgAnnexToAttachment(List<IYqueMsgAnnex> annexList);
}
