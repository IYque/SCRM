package cn.iyque.dao;

import cn.iyque.entity.IYqueCustomerSeas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueCustomerSeasDao  extends JpaRepository<IYqueCustomerSeas,Long>, JpaSpecificationExecutor<IYqueCustomerSeas> {
}
