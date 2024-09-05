package cn.iyque.strategy.callback;

import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.dao.IYQueCustomerInfoDao;
import cn.iyque.domain.IYQueCallbackQuery;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.enums.CustomerStatusType;
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

        log.info("客户信息:"+contactDetail);

        SpringUtil.getBean(IYQueCustomerInfoDao.class)
                        .save(
                                IYQueCustomerInfo.builder()
                                        .state(callBackBaseMsg.getState())
                                        .externalUserid(callBackBaseMsg.getExternalUserID())
                                        .userId(callBackBaseMsg.getUserID())
                                        .addTime(new Date( contactDetail.getFollowedUsers().stream().findFirst().get().getCreateTime() * 1000L))
                                        .status(CustomerStatusType.CUSTOMER_STATUS_TYPE_COMMON.getCode())
                                        .build()

                        );

    }
}
