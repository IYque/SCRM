package cn.iyque.converter;

import cn.iyque.entity.IYqueMsgAnnex;

public class AttachmentConverterFactory {
    public static AttachmentConverter getConverter(String msgType) {
        switch (msgType) {
            case IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE:
                return new ImageAttachmentConverter();
            case IYqueMsgAnnex.MsgType.MSG_TYPE_LINK:
                return new LinkAttachmentConverter();
            case IYqueMsgAnnex.MsgType.MSG_TYPE_MINIPROGRAM:
                return new MiniprogramAttachmentConverter();
            case IYqueMsgAnnex.MsgType.MSG_TYPE_VIDES:
                return new VideoAttachmentConverter();
            case IYqueMsgAnnex.MsgType.MSG_TYPE_FILE:
                return new FileAttachmentConverter();
            default:
                throw new IllegalArgumentException("Unknown message type: " + msgType);
        }
    }
}
