package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.dao.IYqueAiAnalysisMsgAuditDao;
import cn.iyque.dao.IYqueMsgAuditDao;
import cn.iyque.domain.CustomerChatGroup;
import cn.iyque.domain.EmployeeChatGroup;
import cn.iyque.entity.*;
import cn.iyque.service.*;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpMsgAuditService;
import me.chanjar.weixin.cp.bean.msgaudit.WxCpChatDatas;
import me.chanjar.weixin.cp.bean.msgaudit.WxCpChatModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 会话存档相关
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class IYqueMsgAuditServiceImpl implements IYqueMsgAuditService {

    @Autowired
    private IYqueConfigService yqueConfigService;


    @Autowired
    private IYqueMsgAuditDao yqueMsgAuditDao;

    @Autowired
    private IYqueCustomerInfoService infoService;

    @Autowired
    private IYqueUserService iYqueUserService;

    @Autowired
    private IYqueAiAnalysisMsgAuditDao yqueAiAnalysisMsgAuditDao;

    @Autowired
    private IYqueParamConfig yqueParamConfig;

    @Autowired
    private IYqueChatService iYqueChatService;


    //ai预审提示词模版
    private final String promptTpl = "任务描述：\n" +
            "分析以下聊天内容，判断是否存在以下违规行为：\n" +
            "%s\n" +
            "聊天内容：\n" +
            "%s\n\n" +
            "分析要求：\n" +
            "1.逐条分析聊天内容，判断是否存在违规行为。\n" +
            "2.如果存在违规行为，需明确违规类型，并在 `msg` 字段中描述具体违规行为。\n" +
            "3.最终输出结果必须严格为以下格式结构化输出之一着条返回员工与客户是否违规,不可包含其他内容：\n" +
            "- 存在违规行为：[{\"warning\":true, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"具体违规行为描述\"}]\n" +
            "- 不存在违规行为：[{\"warning\":false, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"未发现违规行为\"}]";



    //ai意向分析提示词模版
    private final String intentionPromptTpl = "任务描述：\n" +
            "分析以下聊天内容，判断是否存在意向客户：\n" +
            "%s\n" +
            "聊天内容：\n" +
            "%s\n\n" +
            "分析要求：\n" +
            "1.逐条分析聊天内容，判断是否存在意向客户。\n" +
            "2.如果存在意向客户，需明确意向客户类型，其中`warning`为true表示为意向客户,false为非意向客户 并在 `msg` 字段中描述具体意向行为。\n" +
            "3.最终输出结果必须严格为以下格式结构化输出之一着条返回员工与客户是否违规,不可包含其他内容：\n" +
            "- 存在意向客户行为：[{\"warning\":true, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"具体意向行为描述\"}]\n" +
            "- 不存在意向客户行为：[{\"warning\":false, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"未发现意向行为\"}]";



    /**
     * 超时时间，单位秒
     */
    private final long timeout = 5 * 60;


    @Autowired
    private IYqueAiService aiService;


    @Override
    public Page<IYqueMsgAudit> findAll(IYqueMsgAudit iYqueMsgAudit, Pageable pageable) {
        Specification<IYqueMsgAudit> spec = Specification.where(null);

        if(iYqueMsgAudit.getAcceptType() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("acceptType"), iYqueMsgAudit.getAcceptType()));
        }

        //发送人
        if(StringUtils.isNotEmpty(iYqueMsgAudit.getFromName())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("fromName"), "%" + iYqueMsgAudit.getFromName() + "%"));
        }

        //接收人
        if(StringUtils.isNotEmpty(iYqueMsgAudit.getAcceptName())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("acceptName"), "%" + iYqueMsgAudit.getAcceptName() + "%"));
        }


        //按照时间查询
        if (iYqueMsgAudit.getStartTime() != null && iYqueMsgAudit.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("msgTime"),DateUtils.setTimeToStartOfDay( iYqueMsgAudit.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueMsgAudit.getEndTime())));
        }



        return   yqueMsgAuditDao.findAll(spec, pageable);
    }

    @Override
    public Page<IYqueAiAnalysisMsgAudit> findAiAnalysisMsgAudits(IYqueAiAnalysisMsgAudit analysisMsgAudit, Pageable pageable) {
        Specification<IYqueAiAnalysisMsgAudit> spec = Specification.where(null);
        //是否有异常行为
        if(analysisMsgAudit.getWarning() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("warning"), analysisMsgAudit.getWarning()));
        }

        //员工名称
        if(StringUtils.isNotEmpty(analysisMsgAudit.getEmployeeName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("employeeName")), "%" + analysisMsgAudit.getEmployeeName().trim()  + "%"));
        }

        //客户名称
        if(StringUtils.isNotEmpty(analysisMsgAudit.getCustomerName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerName")), "%" + analysisMsgAudit.getCustomerName().trim() + "%"));
        }

        //按照时间查询
        if (analysisMsgAudit.getStartTime() != null && analysisMsgAudit.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("createTime"), DateUtils.setTimeToStartOfDay(analysisMsgAudit.getStartTime()), DateUtils.setTimeToEndOfDay(analysisMsgAudit.getEndTime()) ));
        }

        //会话预审类型
        if(analysisMsgAudit.getMsgAuditType() !=null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("msgAuditType"), analysisMsgAudit.getMsgAuditType()));
        }

        return yqueAiAnalysisMsgAuditDao.findAll(spec,pageable);
    }

    @Override
    public void synchMsg(){

        try {
            WxCpMsgAuditService msgAuditService = yqueConfigService.findWxcpservice()
                    .getMsgAuditService();

            if(null != msgAuditService){
                Long dataSeq = 0L;

                IYqueMsgAudit iYqueMsgAudit = yqueMsgAuditDao.findTopByOrderByDataSeqDesc();
                if(iYqueMsgAudit != null){
                    dataSeq= iYqueMsgAudit.getDataSeq();
                }


                WxCpChatDatas chatDatas = msgAuditService.getChatDatas(dataSeq, new Long(1000), null, null, timeout);
                if(null != chatDatas){
                    List<WxCpChatDatas.WxCpChatData> chatData =
                            chatDatas.getChatData();
                    if(CollectionUtil.isNotEmpty(chatData)){
                        //解密聊天数据
                        chatData.stream().forEach(k->{
                            try {
                                log.info("解密前的信息:"+k);
                                WxCpChatModel decryptData = msgAuditService.getDecryptData(chatDatas.getSdk(), k, new Integer(1));
                                log.info("解密后的信息:"+decryptData);
                                if(null != decryptData){
                                    //目前只处理员工至客户文字类型,拉取存储
                                    if(!StringUtils.isNotEmpty(decryptData.getRoomId())){
                                        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(decryptData.getMsgType())){
                                            String content = decryptData.getText().getContent();
                                            handleMsg(IYqueMsgAudit.builder()
                                                    .msgId(decryptData.getMsgId())
                                                    .fromId(decryptData.getFrom())
                                                    .acceptType(1)
                                                    .acceptId(Arrays.stream(decryptData.getTolist()).findFirst().get())
                                                    .msgType(decryptData.getMsgType())
                                                    .content(content)
                                                    .dataSeq(k.getSeq())
                                                    .msgTime( new Date(decryptData.getMsgTime()))
                                                    .createTime(new Date())
                                                    .build());
                                            log.info("文字类数据获取:"+content);
                                        }
                                    }else{//群聊消息

                                        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(decryptData.getMsgType())){
                                            String content = decryptData.getText().getContent();
                                            handleMsg(IYqueMsgAudit.builder()
                                                    .msgId(decryptData.getMsgId())
                                                    .fromId(decryptData.getFrom())
                                                    .acceptType(2)
                                                    .acceptId(decryptData.getRoomId())
                                                    .msgType(decryptData.getMsgType())
                                                    .content(content)
                                                    .dataSeq(k.getSeq())
                                                    .msgTime( new Date(decryptData.getMsgTime()))
                                                    .createTime(new Date())
                                                    .build());
                                            log.info("文字类数据获取:"+content);
                                        }

                                    }



                                }
                            } catch (Exception e) {
                                log.error("聊天会话数据解密失败:"+e.getMessage());
                            }


                        });
                    }
                }
            }



        }catch (Exception e){
            log.error("拉取会话数据报错:"+e.getMessage());
        }


    }



    @Override
    public void handleMsg(IYqueMsgAudit yqueMsgAudit) {

        ThreadUtil.execute(()->{

            log.info("入库数据:"+yqueMsgAudit);
            //发送人姓名处理
            if(StringUtils.isNotEmpty(yqueMsgAudit.getFromId())){
                //表示为客户
                if(yqueMsgAudit.getFromId().startsWith("wm")||yqueMsgAudit.getFromId().startsWith("wo")){
                    yqueMsgAudit.setFromName(
                            infoService.findCustomerInfoByExternalUserId(yqueMsgAudit.getFromId()).getCustomerName()
                    );
                }else{ //员工名称处理
                    yqueMsgAudit.setFromName(
                            iYqueUserService.findOrSaveUser(yqueMsgAudit.getFromId()).getName()
                    );


                }

            }


            if(yqueMsgAudit.getAcceptType().equals(new Integer(1))){//接收人为客户或成员
                //接收人姓名处理
                if(StringUtils.isNotEmpty(yqueMsgAudit.getAcceptId())){
                    //表示为客户
                    if(yqueMsgAudit.getAcceptId().startsWith("wm")||yqueMsgAudit.getAcceptId().startsWith("wo")){
                        yqueMsgAudit.setAcceptName(
                                infoService.findCustomerInfoByExternalUserId(yqueMsgAudit.getAcceptId()).getCustomerName()
                        );
                    }else{//员工名称处理

                        yqueMsgAudit.setAcceptName(
                                iYqueUserService.findOrSaveUser(yqueMsgAudit.getAcceptId()).getName()
                        );

                    }

                }

            }else{//接收人为客群

                if(StringUtils.isNotEmpty(yqueMsgAudit.getAcceptId())){
                    yqueMsgAudit.setAcceptName(
                            iYqueChatService.findOrSaveChat(yqueMsgAudit.getAcceptId()).getChatName()
                    );
                }


            }


            //聊天数据入库
            yqueMsgAuditDao.saveAndFlush(yqueMsgAudit);


        });



    }

    @Override
    @Async
    @Transactional
    public void aISessionWarning(List<IYqueMsgRule> iYqueMsgRules, BaseEntity baseEntity) {


        if(CollectionUtil.isNotEmpty(iYqueMsgRules)){




            String nowUserInquiryMsgData = this.findNowUserInquiryMsgData(baseEntity);


            if(StringUtils.isNotEmpty(nowUserInquiryMsgData)){
                String prompt = String.format(promptTpl,IYqueMsgRule.formatRules(iYqueMsgRules), nowUserInquiryMsgData);

                log.info("当前聊天内容分析提示词:"+prompt);

                String result = aiService.aiHandleCommonContentToJson(prompt);

                log.info("大模型输出原生结果:"+result);

                if(StringUtils.isNotEmpty(result)){

                        List<IYqueAiAnalysisMsgAudit> msgAuditResults
                                = JSONUtil.toList(result, IYqueAiAnalysisMsgAudit.class);


                        if(CollectionUtil.isNotEmpty(msgAuditResults)){
                            msgAuditResults.stream().forEach(k->{
                                k.setMsgAuditType(baseEntity.getMsgAuditType());
                                k.setStartTime(baseEntity.getStartTime());
                                k.setEndTime(baseEntity.getEndTime());
                            });
//                            //清空当天已生成的记录,避免重复
//                            yqueAiAnalysisMsgAuditDao.deleteByCreateTimeToday(DateUtils.earlyMorning(
//                            ),new Date());
                            //记录生成完成后给员工发送通知
                            yqueAiAnalysisMsgAuditDao.saveAll(msgAuditResults);

                        }
                    }
            }


        }








    }

    @Override
    @Async
    @Transactional
    public void aiIntentionAssay(List<IYqueMsgRule> iYqueMsgRules, BaseEntity baseEntity) {
        if(CollectionUtil.isNotEmpty(iYqueMsgRules)){
            //获取客户聊天内容
            String customerMsgData = this.findCustomerMsgData(baseEntity);
            if(StringUtils.isNotEmpty(customerMsgData)){


                String prompt = String.format(intentionPromptTpl,IYqueMsgRule.formatRules(iYqueMsgRules), customerMsgData);

                log.info("当前聊天内容分析提示词:"+prompt);

                String result = aiService.aiHandleCommonContentToJson(prompt);

                log.info("大模型输出原生结果:"+result);

                if(StringUtils.isNotEmpty(result)){
                        List<IYqueAiAnalysisMsgAudit> msgAuditResults
                                = JSONUtil.toList(result, IYqueAiAnalysisMsgAudit.class);

                        if(CollectionUtil.isNotEmpty(msgAuditResults)){
                            msgAuditResults.stream().forEach(k->{
                                k.setMsgAuditType(baseEntity.getMsgAuditType());
                                k.setStartTime(baseEntity.getStartTime());
                                k.setEndTime(baseEntity.getEndTime());
                            });
//                            //清空当天已生成的记录,避免重复
//                            yqueAiAnalysisMsgAuditDao.deleteByCreateTimeToday(DateUtils.earlyMorning(
//                            ),new Date());
                            //记录生成完成后给员工发送通知
                            yqueAiAnalysisMsgAuditDao.saveAll(msgAuditResults);

                        }
                }

            }
        }
    }

    @Override
    public String findNowUserInquiryMsgData(BaseEntity baseEntity) {
        StringBuilder sb=new StringBuilder();

        List<IYqueMsgAudit> msgAuditList = yqueMsgAuditDao.findByMsgTimeBetweenAndAcceptType(DateUtils.setTimeToStartOfDay(baseEntity.getStartTime()),
                DateUtils.setTimeToEndOfDay(baseEntity.getEndTime()),baseEntity.getMsgAuditType());

        if(CollectionUtil.isNotEmpty(msgAuditList)){
            List<IYqueMsgAudit> filteredList = msgAuditList.stream()
                    .filter(msg -> !msg.getFromId().startsWith("wm") && !msg.getFromId().startsWith("wo"))
                    .collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(filteredList)){
                List<EmployeeChatGroup> employeeChatGroups = this.groupByEmployeeAndCustomer(filteredList);
                if(CollectionUtil.isNotEmpty(employeeChatGroups)){
                    sb.append(
                            JSONUtil.toJsonStr(employeeChatGroups)
                    );
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String findCustomerMsgData(BaseEntity baseEntity) {
        StringBuilder sb=new StringBuilder();
        List<IYqueMsgAudit> msgAuditList = yqueMsgAuditDao.findByMsgTimeBetweenAndAcceptType(DateUtils.setTimeToStartOfDay(baseEntity.getStartTime()),
                DateUtils.setTimeToEndOfDay(baseEntity.getEndTime()),baseEntity.getMsgAuditType()==3?1:2);

        if(CollectionUtil.isNotEmpty(msgAuditList)){

            List<IYqueMsgAudit> filteredList = msgAuditList.stream()
                    .filter(msg -> msg.getFromId().startsWith("wm") || msg.getFromId().startsWith("wo"))
                    .collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(filteredList)){
                List<EmployeeChatGroup> employeeChatGroups = this.groupByEmployeeAndCustomer(filteredList);
                if(CollectionUtil.isNotEmpty(employeeChatGroups)){
                    sb.append(
                            JSONUtil.toJsonStr(employeeChatGroups)
                    );
                }
            }

        }

        return sb.toString();
    }

    @Override
    public List<EmployeeChatGroup> groupByEmployeeAndCustomer(List<IYqueMsgAudit> msgAuditList) {
        // 按员工 ID 分组
        Map<String, List<IYqueMsgAudit>> employeeGroupMap = msgAuditList.stream()
                .collect(Collectors.groupingBy(IYqueMsgAudit::getFromId));

        // 将分组结果转换为自定义数据结构
        return employeeGroupMap.entrySet().stream()
                .map(entry -> {
                    String employeeId = entry.getKey();
                    List<IYqueMsgAudit> employeeMessages = entry.getValue();

                    // 获取员工名称（假设第一条记录的员工名称就是该员工的名称）
                    String employeeName = employeeMessages.get(0).getFromName();

                    // 按客户 ID 分组
                    Map<String, List<IYqueMsgAudit>> customerGroupMap = employeeMessages.stream()
                            .collect(Collectors.groupingBy(IYqueMsgAudit::getAcceptId));

                    // 将客户分组结果转换为自定义数据结构
                    List<CustomerChatGroup> customerChatGroups = customerGroupMap.entrySet().stream()
                            .map(customerEntry -> {
                                String customerId = customerEntry.getKey();
                                List<IYqueMsgAudit> customerMessages = customerEntry.getValue();

                                // 获取客户名称（假设第一条记录的客户名称就是该客户的名称）
                                String customerName = customerMessages.get(0).getAcceptName();

                                // 提取聊天内容
                                List<String> contents = customerMessages.stream()
                                        .map(IYqueMsgAudit::getContent)
                                        .collect(Collectors.toList());

                                return new CustomerChatGroup(customerId, customerName, contents);
                            })
                            .collect(Collectors.toList());

                    return new EmployeeChatGroup(employeeId, employeeName, customerChatGroups);
                })
                .collect(Collectors.toList());


    }

    @Override
    public String findByMsgTimeBetweenAndAcceptType(Date startTime, Date endTime, Integer AcceptType) {
        StringBuilder sb=new StringBuilder();
        List<IYqueMsgAudit> msgAuditList = yqueMsgAuditDao.findByMsgTimeBetweenAndAcceptType(DateUtils.setTimeToStartOfDay(startTime),   DateUtils.setTimeToEndOfDay(endTime), AcceptType);

        if(CollectionUtil.isNotEmpty(msgAuditList)){
            List<IYqueMsgAudit> filteredList = msgAuditList.stream()
                    .filter(msg -> msg.getFromId().startsWith("wm") || msg.getFromId().startsWith("wo"))
                    .collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(filteredList)){
                sb.append(
                        JSONUtil.toJsonStr(filteredList)
                );
            }
        }
        return sb.toString();
    }
}
