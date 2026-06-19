package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Image;
import me.chanjar.weixin.cp.bean.external.msg.Video;

import java.io.File;


/**
 * 视频转换器
 */
@Slf4j
public class VideoAttachmentConverter implements AttachmentConverter {
    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        File file = FileUtils.downloadImage(annex.getVideo().getVideoUrl());
        if (null != file) {

            try {
                WxMediaUploadResult uploadResult =
                        SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(annex.getMsgtype(), file);
                if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                    Attachment attachment = new Attachment();
                    Video video=new Video();
                    video.setMediaId(uploadResult.getMediaId());
                    attachment.setVideo(video);
                    return attachment;
                }
            }catch (Exception e){
                log.error("视频转换器异常:"+e.getMessage());
            }

        }
        return null;
    }

    @Override
    public Attachment attachmentConvert(IYqueMsgAnnex annex, Integer attachmentType) {
        File file = FileUtils.downloadImage(annex.getImage().getPicUrl());
        if (null != file) {
            try {
                WxMediaUploadResult uploadResult =
                        SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(annex.getMsgtype(), file);
                if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                    Attachment attachment = new Attachment();
                    Video video=new Video();
                    video.setMediaId(uploadResult.getMediaId());
                    attachment.setVideo(video);
                    return attachment;
                }
            }catch (Exception e){
                log.error("视频转换器异常:"+e.getMessage());
            }

        }
        return null;
    }
}
