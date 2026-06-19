package cn.iyque.dao;

import cn.iyque.entity.IYqueGroupMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueGroupMsgDao extends JpaRepository<IYqueGroupMsg,Long>, JpaSpecificationExecutor<IYqueGroupMsg> {
}
