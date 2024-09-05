package cn.iyque.converter;

import cn.iyque.entity.IYqueMsgAnnex;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

public interface AttachmentConverter {
    Attachment convert(IYqueMsgAnnex annex);
}
