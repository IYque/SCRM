package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Link;

import java.io.File;


/**
 * 图文转化器
 */
@Slf4j
public class LinkAttachmentConverter implements AttachmentConverter {
    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        cn.iyque.domain.fileType.Link link = annex.getLink();
        if(null != link && StrUtil.isNotEmpty(link.getTitle())
                && StrUtil.isNotEmpty(link.getUrl())){
            Attachment attachment=new Attachment();
            Link wLink=new Link();
            wLink.setTitle(link.getTitle());
            wLink.setDesc(link.getDesc());
            if(StrUtil.isNotEmpty(link.getPicUrl())){
                if (link.getPicUrl().startsWith("http://") || link.getPicUrl().startsWith("https://")){
                    wLink.setPicUrl(link.getPicUrl());
                }else{
                    wLink.setPicUrl( SpringUtil.getBean(IYqueParamConfig.class).getFileViewUrl()+link.getPicUrl());
                }
            }
            wLink.setUrl(link.getUrl());
            attachment.setLink(wLink);
            return attachment;
        }else{
            log.error("图文消息标题或链接不可为空");
        }
        return null;
    }

    @Override
    public Attachment attachmentConvert(IYqueMsgAnnex annex, Integer attachmentType) {
        cn.iyque.domain.fileType.Link link = annex.getLink();
        if(null != link && StrUtil.isNotEmpty(link.getTitle())
                && StrUtil.isNotEmpty(link.getUrl())){
            Attachment attachment=new Attachment();
            Link wLink=new Link();
            wLink.setTitle(link.getTitle());
            File file = FileUtils.downloadImage(annex.getImage().getPicUrl());
            if (null != file) {
                try {
                    WxMediaUploadResult uploadResult = SpringUtils.getBean(IYqueConfigService.class)
                            .findWxcpservice().getExternalContactService().uploadAttachment(annex.getMsgtype(), attachmentType, file);
                    if (null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())) {
                        wLink.setMediaId(uploadResult.getMediaId());
                    }
                }catch (Exception e){
                    log.error("链接封面转化异常:"+e.getMessage());
                }

            }

            wLink.setUrl(link.getUrl());
            attachment.setLink(wLink);
            return attachment;
        }else{
            log.error("图文消息标题或链接不可为空");
        }
        return null;
    }
}
