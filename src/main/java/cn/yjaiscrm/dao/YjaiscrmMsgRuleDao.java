package cn.iyque.dao;

import cn.iyque.entity.IYqueMsgAudit;
import cn.iyque.entity.IYqueMsgRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYqueMsgRuleDao extends JpaRepository<IYqueMsgRule,Long>, JpaSpecificationExecutor<IYqueMsgRule> {

    /**
     * 获取启用或者停用的列表数据
     * @param ruleStatus
     * @return
     */
    List<IYqueMsgRule>  findByRuleStatusAndRuleType(boolean ruleStatus,Integer ruleType);


    /**
     *
     * @param ruleType
     * @param defaultRule
     * @return
     */
    long countByRuleTypeAndDefaultRule(Integer ruleType,Boolean defaultRule);
}
