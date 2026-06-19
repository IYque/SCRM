package cn.iyque.dao;

import cn.iyque.entity.IYqueAgentSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueAgentSubDao  extends JpaRepository<IYqueAgentSub,Long>, JpaSpecificationExecutor<IYqueAgentSub> {
}
