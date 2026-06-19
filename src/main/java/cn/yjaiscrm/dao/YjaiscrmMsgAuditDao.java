package cn.iyque.dao;

import cn.iyque.entity.IYQueComplain;
import cn.iyque.entity.IYqueMsgAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface IYqueMsgAuditDao extends JpaRepository<IYqueMsgAudit,String> , JpaSpecificationExecutor<IYqueMsgAudit> {

    /**
     * 获取最新的分页下标
     * @return
     */
    IYqueMsgAudit findTopByOrderByDataSeqDesc();


    /**
     * 查询当天凌晨到当前时间的数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param acceptType
     * @return 符合条件的消息列表
     */
    List<IYqueMsgAudit> findByMsgTimeBetweenAndAcceptType(Date startTime, Date endTime,Integer acceptType);

}
