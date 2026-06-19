package cn.iyque.dao;

import cn.iyque.entity.IYuqeOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface IYuqeOperateLogDao extends JpaRepository<IYuqeOperateLog,Long>, JpaSpecificationExecutor<IYuqeOperateLog> {

    /**
     * 派生查询：根据时间范围删除
     */
    @Modifying
    @Transactional
    void deleteByOperateTypeAndCreateTimeBetween(Integer OperateType,Date startTime, Date endTime);
}
