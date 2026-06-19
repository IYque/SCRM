package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.entity.IYqueSessionInterceptRule;
import cn.iyque.exception.IYqueException;
import cn.iyque.mapper.IYqueSessionInterceptRuleMapper;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueSessionInterceptRuleService;
import cn.iyque.utils.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.interceptrule.ApplicableRange;
import me.chanjar.weixin.cp.bean.external.interceptrule.WxCpInterceptRule;
import me.chanjar.weixin.cp.bean.external.interceptrule.WxCpInterceptRuleAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueSessionInterceptRuleServiceImpl extends ServiceImpl<IYqueSessionInterceptRuleMapper, IYqueSessionInterceptRule> implements IYqueSessionInterceptRuleService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(IYqueSessionInterceptRule sessionInterceptRule) {
        List<String> removeDiffStaffIds=new ArrayList<>();
        if (sessionInterceptRule.getId() == null) {
            sessionInterceptRule.setCreateTime(new Date());
        }else{
            IYqueSessionInterceptRule oldInterceptRule = this.getById(sessionInterceptRule.getId());
            if(null != oldInterceptRule){
                sessionInterceptRule.setRuleId(oldInterceptRule.getRuleId());
                removeDiffStaffIds = IYqueSessionInterceptRule.
                        getDiffStaffIds(sessionInterceptRule.getStaffIds(), oldInterceptRule.getStaffIds());
            }

        }
        sessionInterceptRule.setUpdateTime(new Date());
        if(this.saveOrUpdate(sessionInterceptRule)){

            try {
                WxCpExternalContactService externalContactService = iYqueConfigService.findWxcpservice().getExternalContactService();

                WxCpInterceptRuleAddRequest request=new WxCpInterceptRuleAddRequest();
                request.setRuleId(sessionInterceptRule.getRuleId());
                request.setRuleName(sessionInterceptRule.getRuleName());
                request.setWordList(Arrays.asList(sessionInterceptRule.getSensitiveWords().split(",")));
                request.setInterceptType(sessionInterceptRule.getInterceptType());
                ApplicableRange applicableRange=new ApplicableRange();
                applicableRange.setUserList(Arrays.asList(sessionInterceptRule.getStaffIds().split(",")));
                request.setApplicableRange(applicableRange);
                request.setSemanticsList(IYqueSessionInterceptRule.parseSemanticsStrict(sessionInterceptRule.getSemantics()));

                if(StringUtils.isNotEmpty(request.getRuleId())){
                    WxCpInterceptRule interceptRule = WxCpInterceptRule.builder().ruleId(request.getRuleId()).ruleName(request.getRuleName())
                            .wordList(request.getWordList())
                            .interceptType(request.getInterceptType())
                            .addApplicableRange(applicableRange)
                            .build();

                    WxCpInterceptRule.ExtraRule extraRule=new WxCpInterceptRule.ExtraRule();
                    extraRule.setSemanticsList(IYqueSessionInterceptRule.parseSemanticsStrict(sessionInterceptRule.getSemantics()));
                    interceptRule.setExtraRule(extraRule);
                    if(CollectionUtil.isNotEmpty(removeDiffStaffIds)){
                        ApplicableRange removeApplicableRange=new ApplicableRange();
                        removeApplicableRange.setUserList(removeDiffStaffIds);
                        interceptRule.setRemoveApplicableRange(removeApplicableRange);
                    }

                    externalContactService.updateInterceptRule(
                            interceptRule
                    );
                }else{
                    String ruleId = externalContactService.addInterceptRule(request);
                    if(StringUtils.isNotEmpty(ruleId)){
                        sessionInterceptRule.setRuleId(ruleId);
                        this.updateById(sessionInterceptRule);
                    }
                }
            } catch (Exception e) {
                  log.error("规则新增或编辑错误:"+e.getMessage());
                  throw new IYqueException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInterceptRuleById(Long id) {

        try {

            IYqueSessionInterceptRule rule = this.getById(id);
            if(null != rule){
                this.removeById(id);

                if(StringUtils.isNotEmpty(rule.getRuleId())){
                    iYqueConfigService.findWxcpservice().getExternalContactService().delInterceptRule(rule.getRuleId());
                }
            }

        }catch (Exception e){
            log.error("规则删除错误:"+e.getMessage());
            throw new IYqueException(e.getMessage());
        }


    }


}
