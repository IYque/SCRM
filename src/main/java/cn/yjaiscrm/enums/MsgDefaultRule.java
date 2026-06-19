package cn.iyque.enums;


import cn.iyque.entity.IYqueMsgRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
public enum MsgDefaultRule {
    A("通过不正当手段要求客户下单或完成交易。",true,1),

    B("使用侮辱性、攻击性语言对待客户。",true,1),
    C("员工向客户索要红包或其他利益。",true,1),
    D("故意隐瞒客户已有对接人的信息,误导客户与自己交易。",true,1),
    E("使用侮辱性、攻击性语言对待群成员。",true,2),

    F("群内发送反动,政治等敏感词。",true,2),

    G("咨询产品相关价格,服务等相关信息",true,3),

    H("咨询产品相关价格,服务等相关信息",true,4);


    private String ruleContent;

    private Boolean status;

    //规则类型1:客户规则；2:客群规则
    private Integer ruleType;

    MsgDefaultRule(String ruleContent,Boolean status,Integer ruleType){
        this.ruleContent=ruleContent;
        this.status=status;
        this.ruleType=ruleType;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public static  List<IYqueMsgRule> getAllRules(Integer ruleType) {
        List<IYqueMsgRule> msgRules=new ArrayList<>();
        Arrays.asList(MsgDefaultRule.values()).stream().filter(item->item.getRuleType().equals(ruleType)).forEach(k->{
            msgRules.add(
                    IYqueMsgRule.builder()
                            .defaultRule(true)
                            .ruleType(k.getRuleType())
                            .ruleStatus(k.getStatus())
                            .createTime(new Date())
                            .ruleContent(k.getRuleContent())
                            .build()
            );


        });


        return msgRules;
    }
}
