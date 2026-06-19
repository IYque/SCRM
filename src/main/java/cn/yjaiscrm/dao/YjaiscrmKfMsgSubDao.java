package cn.iyque.dao;

import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IYqueKfMsgSubDao extends JpaRepository<IYqueKfMsgSub,Long>, JpaSpecificationExecutor<IYqueKfMsgSub> {

    List<IYqueKfMsgSub> findByExternalUserIdIn(List<String> externalUserIds);


}
