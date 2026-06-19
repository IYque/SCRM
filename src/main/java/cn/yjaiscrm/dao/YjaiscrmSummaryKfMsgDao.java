package cn.iyque.dao;

import cn.iyque.entity.IYqueSummaryKfMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IYqueSummaryKfMsgDao extends JpaRepository<IYqueSummaryKfMsg,Long>, JpaSpecificationExecutor<IYqueSummaryKfMsg> {

    @Transactional
    void deleteByExternalUserIdIn(List<String> externalUserId);
}
