package cn.iyque.service;

import cn.iyque.entity.IYqueMsgRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueMsgRuleService {

    /**
     * 分页查询规则列表
     * @param iYqueMsgRule
     * @param pageable
     * @return
     */
    Page<IYqueMsgRule> findAll(IYqueMsgRule iYqueMsgRule, Pageable pageable);


    /**
     * 新增或编辑预审规则
     * @param iYqueMsgRule
     */
    void saveOrUpdateMsgRule(IYqueMsgRule iYqueMsgRule);


    /**
     * 批量删除预审规则
     * @param ids
     */
    void batchDeleteAiMsgRule(Long[] ids);


    /**
     * 批量启用或停用预审规则
     * @param ids
     */
    void batchStartOrStop(Long[] ids);


    /**
     * 获取启用或者停用的列表数据
     * @param startOrStop
     * @return
     */
    List<IYqueMsgRule> findByStartOrStop(boolean startOrStop,Integer ruleType);
}
