package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.iyque.domain.fileType.Miniprogram;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.MiniProgram;
import java.io.File;


/**
 * 小程序转换器
 */
@Slf4j
public class MiniprogramAttachmentConverter implements AttachmentConverter {

    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        Miniprogram miniprogram = annex.getMiniprogram();

        if(null != miniprogram&& StrUtil.isNotEmpty(miniprogram.getTitle())
                &&StrUtil.isNotEmpty(miniprogram.getPage())&&StrUtil.isNotEmpty(miniprogram.getAppid())
                &&StrUtil.isNotEmpty(miniprogram.getPicUrl())){

            String picurl = miniprogram.getPicUrl();
            if(StrUtil.isNotEmpty(picurl)){
                try {
                    File file = FileUtils.downloadImage(annex.getImage().getPicUrl());
                    if(null != file){
                        WxMediaUploadResult uploadResult =
                                SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(annex.getMsgtype(), file);
                        if(null != uploadResult && StrUtil.isNotEmpty(uploadResult.getMediaId())){
                            Attachment attachment=new Attachment();
                            MiniProgram miniProgram=new MiniProgram();
                            miniProgram.setAppid(miniprogram.getAppid());
                            miniProgram.setTitle(miniprogram.getTitle());
                            miniProgram.setPage(miniprogram.getPage());
                            miniProgram.setPicMediaId(uploadResult.getMediaId());
                            attachment.setMiniProgram(
                                    miniProgram
                            );
                           return attachment;

                        }
                    }
                }catch (Exception e){
                    log.error("小程序转化器异常:"+e.getMessage());
                }

            }else {
                log.error("小程序封面不可为空");
            }

        }else{
            log.error("小程序相关参数不可为空");
        }

        return null;
    }

    @Override
    public Attachment attachmentConvert(IYqueMsgAnnex annex, Integer attachmentType) {
        return null;
    }
}
