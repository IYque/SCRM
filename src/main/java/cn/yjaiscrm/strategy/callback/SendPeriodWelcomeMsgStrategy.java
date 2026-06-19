package cn.iyque.strategy.callback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYquePeriodMsgAnnex;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueAnnexPeriodService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYquePeriodMsgAnnexService;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发送欢迎语(时段欢迎语)
 */
@Slf4j
public class SendPeriodWelcomeMsgStrategy implements ActionStrategy{
    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail) {

           if(iyQueCallbackQuery.isStartPeriodAnnex()){
               WxCpWelcomeMsg wxCpWelcomeMsg=new WxCpWelcomeMsg();
               Text text = new Text();
               //是否发送默认欢迎语
               boolean sendDefaultMsg=true;


               List<IYqueAnnexPeriod> iYqueAnnexPeriods
                       = SpringUtil.getBean(IYqueAnnexPeriodService.class).findIYqueAnnexPeriodByMsgId(iyQueCallbackQuery.getBusinessId());
               if(CollectionUtil.isNotEmpty(iYqueAnnexPeriods)){

                   // 使用filter来筛选符合条件的周期
                   List<IYqueAnnexPeriod> filteredPeriods = iYqueAnnexPeriods.stream()
                           .filter(k -> StrUtil.isNotEmpty(k.getWorkCycle()) &&
                                   StrUtil.isNotEmpty(k.getBeginTime()) &&
                                   StrUtil.isNotEmpty(k.getEndTime()) &&
                                   DateUtils.isWorkCycle(k.getWorkCycle()) &&
                                   DateUtils.isCurrentTimeInRange(k.getBeginTime(), k.getEndTime()))
                           .collect(Collectors.toList());

                   if(CollectionUtil.isNotEmpty(filteredPeriods)){
                       sendDefaultMsg=false;
                       IYqueAnnexPeriod iYqueAnnexPeriod = filteredPeriods.stream().findFirst().get();
                       //文字欢迎语
                       if(StrUtil.isNotEmpty(iYqueAnnexPeriod.getWeclomeMsg())){
                           text.setContent(iYqueAnnexPeriod.getWeclomeMsg());
                       }
                       //欢迎语附件
                       List<IYquePeriodMsgAnnex> periodMsgAnnexList = iYqueAnnexPeriod.getPeriodMsgAnnexList();
                       if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                           wxCpWelcomeMsg.setAttachments(SpringUtil.getBean(IYquePeriodMsgAnnexService.class)
                                   .msgAnnexToAttachment(periodMsgAnnexList));
                       }
                   }

               }


               //默认欢迎语
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
