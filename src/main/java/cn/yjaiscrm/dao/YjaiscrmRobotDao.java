package cn.iyque.dao;

import cn.iyque.entity.IYqueRobot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYQueRobotDao extends JpaRepository<IYqueRobot,Long> , JpaSpecificationExecutor<IYqueRobot> {
}
