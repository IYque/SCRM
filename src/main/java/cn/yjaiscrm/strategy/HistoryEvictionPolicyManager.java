package cn.iyque.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class HistoryEvictionPolicyManager {

    @Autowired
    private List<HistoryEvictionPolicy> policies;

    private final Map<String, HistoryEvictionPolicy> policyMap = new HashMap<>();

    private String defaultPolicyName = "summary";

    @PostConstruct
    public void init() {
        for (HistoryEvictionPolicy policy : policies) {
            policyMap.put(policy.getName(), policy);
            log.info("已加载历史消息淘汰策略: {} - {}", policy.getName(), policy.getDescription());
        }
    }

    public HistoryEvictionPolicy getPolicy(String name) {
        HistoryEvictionPolicy policy = policyMap.get(name);
        if (policy == null) {
            log.warn("未找到策略: {}，使用默认策略: {}", name, defaultPolicyName);
            return policyMap.get(defaultPolicyName);
        }
        return policy;
    }

    public HistoryEvictionPolicy getDefaultPolicy() {
        return policyMap.get(defaultPolicyName);
    }

    public void setDefaultPolicy(String name) {
        if (policyMap.containsKey(name)) {
            this.defaultPolicyName = name;
            log.info("已设置默认策略为: {}", name);
        } else {
            log.warn("策略 {} 不存在，保持默认策略: {}", name, defaultPolicyName);
        }
    }

    public Map<String, String> getAllPolicies() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, HistoryEvictionPolicy> entry : policyMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getDescription());
        }
        return result;
    }

    public List<String> getPolicyNames() {
        return new ArrayList<>(policyMap.keySet());
    }
}
