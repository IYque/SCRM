package cn.iyque.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai.models")
public class AiModelsProperties {

    private List<String> enabled = new ArrayList<>();

    private Map<String, ModelConfig> configs = new HashMap<>();

    public List<String> getEnabled() {
        return enabled;
    }

    public void setEnabled(List<String> enabled) {
        this.enabled = enabled;
    }

    public Map<String, ModelConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, ModelConfig> configs) {
        this.configs = configs;
    }

    // 单个模型的配置
    public static class ModelConfig {
        private String apiKey;
        private String baseUrl;
        private String modelName;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public boolean isValid() {
            return apiKey != null && !apiKey.trim().isEmpty();
        }
    }
}

