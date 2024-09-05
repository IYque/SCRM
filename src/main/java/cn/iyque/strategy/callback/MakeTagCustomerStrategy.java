package cn.iyque.strategy.callback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;


/**
 * 自动打标签
 */
@Slf4j
public class MakeTagCustomerStrategy implements ActionStrategy {
    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail) {
        if(null != iyQueCallbackQuery && StrUtil.isNotEmpty(iyQueCallbackQuery.getTagId())){
            try {
                SpringUtil.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService()
                        .markTag(callBackBaseMsg.getUserID(),callBackBaseMsg.getExternalUserID(),iyQueCallbackQuery.getTagId().split(","),null);
            }catch (Exception e){
                log.error("未客户打标签失败:"+e.getMessage());
            }

        }

    }
}
