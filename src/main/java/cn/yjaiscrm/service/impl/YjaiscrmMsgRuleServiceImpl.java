package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.iyque.dao.IYqueMsgRuleDao;
import cn.iyque.entity.IYqueMsgRule;
import cn.iyque.enums.MsgDefaultRule;
import cn.iyque.service.IYqueMsgRuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;


@Slf4j
@Service
public class IYqueMsgRuleServiceImpl implements IYqueMsgRuleService {

    @Autowired
    private IYqueMsgRuleDao iYqueMsgRuleDao;

    //系统启动是如果不存在规则插入默认规则
    @PostConstruct
    public void init() {
        //初始化客户会话预审规则
       if(iYqueMsgRuleDao.countByRuleTypeAndDefaultRule(1,true)<=0){
           iYqueMsgRuleDao.saveAll(
                   MsgDefaultRule.getAllRules(1)
           );
       }


        //初始化客群会话预审规则
        if(iYqueMsgRuleDao.countByRuleTypeAndDefaultRule(2,true)<=0){
            iYqueMsgRuleDao.saveAll(
                    MsgDefaultRule.getAllRules(2)
            );
        }

        //初始化意向客户分析规则
        if(iYqueMsgRuleDao.countByRuleTypeAndDefaultRule(3,true)<=0){
            iYqueMsgRuleDao.saveAll(
                    MsgDefaultRule.getAllRules(3)
            );
        }


        //初始化意向群友分析规则
        if(iYqueMsgRuleDao.countByRuleTypeAndDefaultRule(4,true)<=0){
            iYqueMsgRuleDao.saveAll(
                    MsgDefaultRule.getAllRules(4)
            );
        }
    }

    @Override
    public Page<IYqueMsgRule> findAll(IYqueMsgRule iYqueMsgRule, Pageable pageable) {
        Specification<IYqueMsgRule> spec = Specification.where(null);
        if(iYqueMsgRule.getDefaultRule() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("defaultRule"),iYqueMsgRule.getDefaultRule()));
        }
        if(iYqueMsgRule.getRuleStatus() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ruleStatus"), iYqueMsgRule.getRuleStatus()));
        }

        if(iYqueMsgRule.getRuleType() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ruleType"), iYqueMsgRule.getRuleType()));
        }
        return   iYqueMsgRuleDao.findAll(spec, pageable);
    }

    @Override
    public void saveOrUpdateMsgRule(IYqueMsgRule iYqueMsgRule) {
        if(iYqueMsgRule.getId()==null){
            iYqueMsgRule.setCreateTime(new Date());
            iYqueMsgRule.setDefaultRule(false);
            iYqueMsgRule.setRuleStatus(false);
        }else{
            Optional<IYqueMsgRule> optional = iYqueMsgRuleDao.findById(iYqueMsgRule.getId());
            if(optional.isPresent()){
                IYqueMsgRule oldIYqueMsgRule = optional.get();
                iYqueMsgRule.setRuleContent(StringUtils.isNotEmpty(iYqueMsgRule.getRuleContent())?
                        iYqueMsgRule.getRuleContent()
                        :oldIYqueMsgRule.getRuleContent());
                iYqueMsgRule.setId(oldIYqueMsgRule.getId());
                iYqueMsgRule.setDefaultRule(oldIYqueMsgRule.getDefaultRule());
                iYqueMsgRule.setCreateTime(oldIYqueMsgRule.getCreateTime());

            }

        }

        iYqueMsgRuleDao.saveAndFlush(iYqueMsgRule);
    }

    @Override
    public void batchDeleteAiMsgRule(Long[] ids) {
        iYqueMsgRuleDao.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public void batchStartOrStop(Long[] ids) {
        List<IYqueMsgRule> iYqueMsgRules = iYqueMsgRuleDao.findAllById(Arrays.asList(ids));
        if(CollectionUtil.isNotEmpty(iYqueMsgRules)){
            iYqueMsgRules.stream().forEach(k->{

                k.setRuleStatus(
                        !k.getRuleStatus()
                );
            });
            iYqueMsgRuleDao.saveAllAndFlush(iYqueMsgRules);

        }

    }

    @Override
    public List<IYqueMsgRule> findByStartOrStop(boolean startOrStop,Integer ruleType) {
        return iYqueMsgRuleDao.findByRuleStatusAndRuleType(startOrStop,ruleType);
    }
}
