package cn.iyque.dao;

import cn.iyque.entity.IYqueAiAnalysisMsgAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;

public interface IYqueAiAnalysisMsgAuditDao extends JpaRepository<IYqueAiAnalysisMsgAudit,Long> , JpaSpecificationExecutor<IYqueAiAnalysisMsgAudit> {

    @Modifying
    @Transactional
    @Query("DELETE FROM iyque_ai_analysis_msg_audit a WHERE a.createTime BETWEEN :startOfDay AND :endOfDay")
    void deleteByCreateTimeToday(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);
}
