package cn.iyque.dao;

import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYQueCustomerInfoDao  extends JpaRepository<IYQueCustomerInfo,Long>, JpaSpecificationExecutor<IYQueCustomerInfo> {



    IYQueCustomerInfo findByExternalUseridAndUserId(String externalUserid, String userId);


    List<IYQueCustomerInfo> findByExternalUserid(String externalUserid);

    /**
     * 批量根据外部客户ID查找客户信息列表
     * @param externalUserids 外部客户ID列表
     * @return 客户信息列表
     */
    List<IYQueCustomerInfo> findByExternalUseridIn(List<String> externalUserids);
}
