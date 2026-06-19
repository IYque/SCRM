package cn.iyque.strategy.callback;

import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.dao.IYQueCustomerInfoDao;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.enums.CustomerStatusType;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


/**
 * 客户相关信息入库
 */
@Slf4j
public class SaveCustomerStrategy implements ActionStrategy{



    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYQueCallbackQuery iyQueCallbackQuery, WxCpExternalContactInfo contactDetail) {


        try {
            log.info("客户信息:"+contactDetail);


            SpringUtil.getBean(IYqueCustomerInfoService.class).saveCustomer(callBackBaseMsg.getExternalUserID());

        }catch (Exception e){
            log.error("回调客户入库:"+e.getMessage());

        }



    }
}
