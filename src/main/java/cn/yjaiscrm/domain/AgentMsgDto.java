package cn.iyque.domain;


import cn.hutool.core.collection.ListUtil;
import cn.iyque.constant.WxFileType;
import cn.iyque.entity.IYqueAgentSub;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueRobotSub;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.FileUtils;
import cn.iyque.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@Slf4j
public class AgentMsgDto {

    public static WxCpMessage buildAgentMsg(IYqueAgentSub iYqueAgentSub){

        try {

            if(StringUtils.isNotEmpty(iYqueAgentSub.getMsgType())){
                if(iYqueAgentSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){//文本


                   return WxCpMessage.TEXT().content(iYqueAgentSub.getText().getContent()).build();


                }else if(iYqueAgentSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE)){ //图片

                    WxMediaUploadResult upload = SpringUtils.getBean(IYqueConfigService.class).findWxcpservice().getMediaService().upload(WxFileType.IMAGE, FileUtils.downloadImage(
                            iYqueAgentSub.getImage().getPicUrl()
                    ));

                    if(StringUtils.isNotEmpty(upload.getMediaId())) {
                           return WxCpMessage.IMAGE().mediaId( upload.getMediaId()).build();
                    }

                }else if(iYqueAgentSub.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_LINK)) { //图文

                    NewArticle newArticle=new NewArticle();
                    newArticle.setTitle(iYqueAgentSub.getLink().getTitle());
                    newArticle.setDescription(iYqueAgentSub.getLink().getDesc());
                    newArticle.setUrl(iYqueAgentSub.getLink().getUrl());
                    newArticle.setPicUrl(iYqueAgentSub.getLink().getPicUrl());

                   return WxCpMessage.NEWS()
                            .addArticle(newArticle)
                            .build();

                }

            }

        }catch (Exception e){
            log.error("构建应用发送消息失败:"+e.getMessage());
            return null;
        }


        return null;




    }
}
