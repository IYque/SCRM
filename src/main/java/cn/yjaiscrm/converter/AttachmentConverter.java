package cn.iyque.converter;

import cn.iyque.entity.IYqueMsgAnnex;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

public interface AttachmentConverter {

    /**
     * 临时素材转换器
     * @param annex
     * @return
     */
    Attachment convert(IYqueMsgAnnex annex);


    /**
     * 附件转换器
     * @param annex
     * @param attachmentType 1：朋友圈；2:商品图册
     * @return
     */
    Attachment attachmentConvert(IYqueMsgAnnex annex,Integer attachmentType);


}
