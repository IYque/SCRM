package cn.iyque.service;

import cn.iyque.entity.IYqueMsgAnnex;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

import java.util.List;

public interface IYqueMsgAnnexService {

    List<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(Long msgId);

    void deleteIYqueMsgAnnexByMsgId(Long msgId);

    void saveAll( List<IYqueMsgAnnex> annexLists);
    List<Attachment> msgAnnexToAttachment(List<IYqueMsgAnnex> annexList);

    /**
     * 朋友圈，商品图片附件转化
     * @param annexList
     * @param attachmentType 1：朋友圈；2:商品图册
     * @return
     */
    List<Attachment> msgAnnexToAttachment(List<IYqueMsgAnnex> annexList, Integer attachmentType);
}
