package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;

import java.io.File;


/**
 * 文件转换器
 */
@Slf4j
public class FileAttachmentConverter implements AttachmentConverter {
    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        File file = FileUtils.downloadImage(annex.getFile().getFileUrl());
        if (null != file) {

            try {
                WxMediaUploadResult uploadResult =
                        SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(annex.getMsgtype(), file);
                if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                    Attachment attachment = new Attachment();
                    me.chanjar.weixin.cp.bean.external.msg.File wfile = new me.chanjar.weixin.cp.bean.external.msg.File();
                    wfile.setMediaId(uploadResult.getMediaId());
                    attachment.setFile(wfile);
                    return attachment;
                }
            }catch (Exception e){
                log.error("文件转换器异常:"+e.getMessage());
            }

        }
        return null;
    }

    @Override
    public Attachment attachmentConvert(IYqueMsgAnnex annex, Integer attachmentType) {
        return null;
    }
}
