package cn.iyque.dao;

import cn.iyque.entity.IYqueGroupMsgSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYqueGroupMsgSubDao  extends JpaRepository<IYqueGroupMsgSub,Long>, JpaSpecificationExecutor<IYqueGroupMsgSub> {

    List<IYqueGroupMsgSub> findByGroupMsgId(Long groupMsgId);
}
