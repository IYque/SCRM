package cn.iyque.dao;

import cn.iyque.entity.IYqueAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueAgentDao  extends JpaRepository<IYqueAgent,Long>, JpaSpecificationExecutor<IYqueAgent> {
    IYqueAgent findByAgentId(Integer agentId);
}
