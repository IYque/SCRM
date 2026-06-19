package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Image;

import java.io.File;

/**
 * 图片转换器
 */
@Slf4j
public class ImageAttachmentConverter implements AttachmentConverter {

    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        File file = FileUtils.downloadImage(annex.getImage().getPicUrl());
        if (null != file) {

            try {
                WxMediaUploadResult uploadResult =
                        SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(annex.getMsgtype(), file);
                if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                    Attachment attachment = new Attachment();
                    Image image = new Image();
                    image.setMediaId(uploadResult.getMediaId());
                    attachment.setImage(image);
                    return attachment;
                }
            }catch (Exception e){
                log.error("图片转换器异常:"+e.getMessage());
            }

        }
        return null;
    }

    @Override
    public Attachment attachmentConvert(IYqueMsgAnnex annex, Integer attachmentType) {
        File file = FileUtils.downloadImage(annex.getImage().getPicUrl());
        if (null != file) {

            try {
                WxMediaUploadResult uploadResult = SpringUtils.getBean(IYqueConfigService.class)
                        .findWxcpservice().getExternalContactService().uploadAttachment(annex.getMsgtype(), attachmentType, file);
                if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                    Attachment attachment = new Attachment();
                    Image image = new Image();
                    image.setMediaId(uploadResult.getMediaId());
                    attachment.setImage(image);
                    return attachment;
                }
            }catch (Exception e){
                log.error("图片转换器异常:"+e.getMessage());
            }
        }
        return null;
    }
}
