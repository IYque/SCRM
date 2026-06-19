package cn.iyque.dao;

import cn.iyque.entity.IYqueRobotSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYQueRobotSubDao extends JpaRepository<IYqueRobotSub,Long> , JpaSpecificationExecutor<IYqueRobotSub> {

//    void deleteAllByRobotIdIs(List<Long> robotIds);
}
