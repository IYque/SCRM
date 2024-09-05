package cn.iyque.dao;

import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IYQueCustomerInfoDao  extends JpaRepository<IYQueCustomerInfo,Integer> {
    IYQueCustomerInfo findByExternalUseridAndUserId(String externalUserid, String userId);
}
