package cn.iyque.service;

import cn.iyque.domain.*;
import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IYqueCustomerInfoService {
    void addCustomerCallBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
    void updateCustomerInfoStatus(String externalUserid,String userId,Integer status);

    IYqueUserCodeCountVo countTotalTab(IYQueCountQuery queCountQuery,boolean codeOrLink);

    IYQueTrendCount countTrend(IYQueCountQuery queCountQuery,boolean codeOrLink);

    List<IYQueCustomerInfo> saveCustomer(String externalUserid);


    IYQueCustomerInfo findCustomerInfoByExternalUserId(String externalUserid);


    /**
     * 客户列表
     * @param iyQueCustomerInfo
     * @param pageable
     * @return
     */
    Page<IYQueCustomerInfo> findAll(IYQueCustomerInfo iyQueCustomerInfo, Pageable pageable);

    /**
     * 同步客户
     */
    void synchCustomer();

    /**
     * 根据外部客户ID查找客户信息列表
     * @param externalUserid 外部客户ID
     * @return 客户信息列表
     */
    List<IYQueCustomerInfo> findByExternalUserid(String externalUserid);

    /**
     * 批量根据外部客户ID查找客户信息列表
     * @param externalUserids 外部客户ID列表
     * @return 客户信息列表
     */
    List<IYQueCustomerInfo> findByExternalUseridIn(List<String> externalUserids);


    /**
     * 客户打标签
     * @param customerDto
     */
    void makeTag(IYQueCustomerDto customerDto);

}
