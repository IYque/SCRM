package cn.iyque.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueAiAnalysisMsgAudit;
import cn.iyque.entity.IYqueMsgAudit;
import cn.iyque.entity.IYqueMsgRule;
import cn.iyque.service.IYqueMsgAuditService;
import cn.iyque.service.IYqueMsgRuleService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/msg")
@Slf4j
public class IYqueWeMsgAuditController {



    @Autowired
    private IYqueMsgAuditService weMsgAuditService;


    @Autowired
    private IYqueMsgRuleService yqueMsgRuleService;



    /**
     * 获取会话列表
     * @param iYqueMsgAudit
     * @return
     */
    @GetMapping("/findMsgAuditByPage")
    public ResponseResult<IYqueMsgAudit> findMsgAuditByPage(IYqueMsgAudit iYqueMsgAudit){

        Page<IYqueMsgAudit> iYqueMsgAudits = weMsgAuditService.findAll(iYqueMsgAudit,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("msgTime").descending()));
        return new ResponseResult(iYqueMsgAudits.getContent(),iYqueMsgAudits.getTotalElements());
    }


    /**
     * 会话同步
     * @return
     */
    @GetMapping("/synchMsg")
    public ResponseResult synchMsg(){
        ThreadUtil.execute(()->{
            try {
                weMsgAuditService.synchMsg();
            }catch (Exception e){
               log.error("会话同步失败:"+e.getMessage());
            }
         });


        return new ResponseResult("当前会话记录正在同步中,请稍后查看");
    }




    /**
     * ai智能分析生成员工聊天是否违规记录
     */
    @GetMapping("/buildAISessionWarning")
    public ResponseResult buildAISessionWarning(BaseEntity baseEntity){
        List<IYqueMsgRule> iYqueMsgRules = yqueMsgRuleService.findByStartOrStop(true,baseEntity.getMsgAuditType());
        if(CollectionUtil.isEmpty(iYqueMsgRules)){
            return new ResponseResult("请设置AI客户预审规则");
        }
        weMsgAuditService.aISessionWarning(iYqueMsgRules,baseEntity);
        return new ResponseResult("当前记录正在生成中,请稍后查看");
    }


    /**
     * ai意向分析
     */
    @GetMapping("/aiIntentionAssay")
    public ResponseResult aiIntentionAssay(BaseEntity baseEntity){
        List<IYqueMsgRule> iYqueMsgRules = yqueMsgRuleService.findByStartOrStop(true,baseEntity.getMsgAuditType());
        if(CollectionUtil.isEmpty(iYqueMsgRules)){
            return new ResponseResult("请设置AI客户预审规则");
        }
        weMsgAuditService.aiIntentionAssay(iYqueMsgRules,baseEntity);
        return new ResponseResult("当前记录正在生成中,请稍后查看");
    }




    /**
     * ai分析预审报告列表
     * @param analysisMsgAudit
     * @return
     */
    @GetMapping("/findAiAnalysisMsgAudits")
    public ResponseResult<IYqueAiAnalysisMsgAudit> findAiAnalysisMsgAudits(IYqueAiAnalysisMsgAudit analysisMsgAudit){

        Page<IYqueAiAnalysisMsgAudit> msgAudits = weMsgAuditService.findAiAnalysisMsgAudits(analysisMsgAudit,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));

        return new ResponseResult(msgAudits.getContent(),msgAudits.getTotalElements());
    }

    /**
     * 获取ai预审规则列表
     * @param iYqueMsgRule
     * @return
     */
    @GetMapping("/findIYqueMsgRules")
    public ResponseResult<IYqueMsgRule> findIYqueMsgRules(IYqueMsgRule iYqueMsgRule){

        Page<IYqueMsgRule> iYqueMsgRules = yqueMsgRuleService.findAll(iYqueMsgRule,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(),
                        Sort.by(
                                Sort.Order.desc("defaultRule"),
                                Sort.Order.desc("createTime")
                        )
                        ));

        return new ResponseResult(iYqueMsgRules.getContent(),iYqueMsgRules.getTotalElements());
    }


    /**
     * 新增或编辑预审规则
     * @param iYqueMsgRule
     * @return
     */
    @PostMapping("/saveOrUpdateMsgRule")
    public ResponseResult saveOrUpdateMsgRule(@RequestBody IYqueMsgRule iYqueMsgRule){



        yqueMsgRuleService.saveOrUpdateMsgRule(iYqueMsgRule);

        return new ResponseResult();
    }



    /**
     * 批量启用或停用
     * @param ids
     * @return
     */
    @PostMapping(path = "/{ids}")
    public ResponseResult batchStartOrStop(@PathVariable("ids") Long[] ids) {

        yqueMsgRuleService.batchStartOrStop(ids);

        return new ResponseResult();
    }

    /**
     * 规则删除
     * @param ids
     * @return
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueMsgRuleService.batchDeleteAiMsgRule(ids);

        return new ResponseResult();
    }


}
