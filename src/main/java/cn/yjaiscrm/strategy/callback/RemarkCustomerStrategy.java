package cn.iyque.strategy.callback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.enums.RemarksType;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.WxCpUpdateRemarkRequest;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.util.Date;
import cn.iyque.entity.IYqueUserCode;
/**
 * 自动备注
 */
@Slf4j
public class RemarkCustomerStrategy implements ActionStrategy {
    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail) {

            log.info("自动备注excute");
        try {
            Integer remarkType = iyQueCallbackQuery.getRemarkType();
            WxCpUpdateRemarkRequest remarkRequest=new WxCpUpdateRemarkRequest();
            remarkRequest.setExternalUserId(callBackBaseMsg.getExternalUserID());
            remarkRequest.setUserId(callBackBaseMsg.getUserID());

            if(RemarksType.REMARKS_TYPE_STATENAME
                    .getCode().equals(remarkType)){
                remarkRequest.setRemark(contactDetail.getExternalContact().getName()+"-"+iyQueCallbackQuery.getRemarkName());
            }else if(RemarksType.REMARKS_TYPE_TAGS
                    .getCode().equals(remarkType)){
                remarkRequest.setRemark(contactDetail.getExternalContact().getName()+"-"+iyQueCallbackQuery.getTagName());
            }else if(RemarksType.REMARKS_TYPE_ADDTIME
                    .getCode().equals(remarkType)){
                Long createTime = contactDetail.getFollowedUsers().stream()
                        .filter(user -> user.getUserId().equals(callBackBaseMsg.getUserID()))
                        .findFirst().get().getCreateTime();
                remarkRequest.setRemark(contactDetail.getExternalContact().getName()+"-"+ DateFormatUtils.format(new Date(createTime * 1000L), "yyyyMMdd"));
            }

            if(StrUtil.isNotEmpty(remarkRequest.getRemark())){
                //企微规定备注字符串不可超过20个，超过20个则自动截取
                if ( remarkRequest.getRemark().length() > 20) {
                    remarkRequest.setRemark(
                            remarkRequest.getRemark().substring(0,20)
                    );
                }
                SpringUtil.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService()
                        .updateRemark(remarkRequest);
            }
        }catch (Exception e){
            log.error("客户自动备注失败:"+e.getMessage());
        }

    }
}
