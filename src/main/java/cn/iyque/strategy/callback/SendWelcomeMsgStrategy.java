package cn.iyque.strategy.callback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import cn.iyque.constant.IYqueContant;
import cn.iyque.entity.IYqueUserCode;

import java.util.List;

/**
 * 发送欢迎语(非时段欢迎语)
 */
@Slf4j
public class SendWelcomeMsgStrategy implements ActionStrategy {


    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail) {

        if(!iyQueCallbackQuery.isStartPeriodAnnex()){


            WxCpWelcomeMsg wxCpWelcomeMsg=new WxCpWelcomeMsg();
            wxCpWelcomeMsg.setWelcomeCode(callBackBaseMsg.getWelcomeCode());
            Text text = new Text();
            //是否发送默认欢迎语
            boolean sendDefaultMsg=true;

            if(StrUtil.isNotEmpty(iyQueCallbackQuery.getWeclomeMsg())){
                text.setContent(iyQueCallbackQuery.getWeclomeMsg());
                List<IYqueMsgAnnex> annexLists = SpringUtil.getBean(IYqueMsgAnnexService.class)
                        .findIYqueMsgAnnexByMsgId(iyQueCallbackQuery.getBusinessId());
                if(CollectionUtil.isNotEmpty(annexLists)){
                    List<Attachment> attachments = SpringUtil.getBean(IYqueMsgAnnexService.class)
                            .msgAnnexToAttachment(annexLists);
                    wxCpWelcomeMsg.setAttachments(attachments);
                }
                sendDefaultMsg=false;
            }

            //默认欢迎语
            if(sendDefaultMsg){
                SpringUtil.getBean(IYqueDefaultMsgService.class).setDefaultMsg(wxCpWelcomeMsg,text);
            }


            //发送欢迎语
            if(StrUtil.isNotEmpty(text.getContent())){

                //替换为真实用户名
                if(text.getContent().contains(IYqueContant.USER_NICKNAME_TPL)){
                    text.setContent(
                            text.getContent().replace(IYqueContant.USER_NICKNAME_TPL,  contactDetail.getExternalContact().getName())
                    );
                }

                wxCpWelcomeMsg.setText(text);

                try {
                    WxCpExternalContactService externalContactService
                            = SpringUtil.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService();
                    externalContactService.sendWelcomeMsg(wxCpWelcomeMsg);
                }catch (Exception e){
                    log.error("欢迎语发送失败:"+e.getMessage());
                }

            }

        }




    }
}
