package cn.iyque.service;

import cn.iyque.domain.IYQueCountQuery;
import cn.iyque.domain.IYQueTrendCount;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueUserCodeCountVo;

public interface IYqueCustomerInfoService {
    void addCustomerCallBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
    void updateCustomerInfoStatus(String externalUserid,String userId,Integer status);

    IYqueUserCodeCountVo countTotalTab(IYQueCountQuery queCountQuery,boolean codeOrLink);

    IYQueTrendCount countTrend(IYQueCountQuery queCountQuery,boolean codeOrLink);

}
