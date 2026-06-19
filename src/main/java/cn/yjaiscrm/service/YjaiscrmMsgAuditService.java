package cn.iyque.service;

import cn.iyque.domain.EmployeeChatGroup;
import cn.iyque.entity.*;
import cn.iyque.utils.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface IYqueMsgAuditService {


    /**
     * 分页获取会话列表
     * @param iYqueMsgAudit
     * @param pageable
     * @return
     */
    Page<IYqueMsgAudit> findAll(IYqueMsgAudit iYqueMsgAudit, Pageable pageable);


    /**
     * ai生成报告查询
     * @param analysisMsgAudit
     * @param pageable
     * @return
     */
    Page<IYqueAiAnalysisMsgAudit> findAiAnalysisMsgAudits(IYqueAiAnalysisMsgAudit analysisMsgAudit, Pageable pageable);

    /**
     * 同步会话数据
     */
    void synchMsg() throws Exception;


    /**
     * 数据处理,入库等处理
     * @param yqueMsgAudit
     */
    void handleMsg(IYqueMsgAudit yqueMsgAudit);


    /**
     * ai会话预审
     */
    void aISessionWarning(List<IYqueMsgRule> iYqueMsgRules, BaseEntity baseEntity );


    /**
     * ai意向分析
     * @param iYqueMsgRules
     * @param baseEntity
     */
    void aiIntentionAssay(List<IYqueMsgRule> iYqueMsgRules, BaseEntity baseEntity);


    /**
     *  获取指定时间,员工发送给客户的聊天数据
     * @return
     */
    String findNowUserInquiryMsgData(BaseEntity baseEntity);


    /**
     * 获取指定时间，客户发送的聊天信息
     * @param baseEntity
     * @return
     */
    String findCustomerMsgData(BaseEntity baseEntity);


    /**
     * 将聊天记录按员工和客户分组，并封装到自定义数据结构中
     * @param msgAuditList 聊天记录列表
     * @return 分组后的结果
     */
     List<EmployeeChatGroup> groupByEmployeeAndCustomer(List<IYqueMsgAudit> msgAuditList);


    /**
     * 获取指定时间段内客户的聊天内容
     * @param startTime
     * @param endTime
     * @param AcceptType
     * @return
     */
     String findByMsgTimeBetweenAndAcceptType(Date startTime,
                                                           Date endTime, Integer AcceptType);
}
