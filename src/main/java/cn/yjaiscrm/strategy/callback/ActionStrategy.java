package cn.iyque.strategy.callback;

import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueUserCode;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;

public interface ActionStrategy {
    void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail);
}
