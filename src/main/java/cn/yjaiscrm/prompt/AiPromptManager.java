package cn.iyque.prompt;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiPromptManager {
    private final Map<String, PromptConfig> prompts = new ConcurrentHashMap<>();

    @PostConstruct
    public void load() {
        loadYaml("prompts/role-prompts.yml", "recommend-reply");
        // 可扩展更多文件
    }

    private void loadYaml(String path, String prefix) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("提示词文件缺失: " + path);
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            for (Map.Entry<String, Object> entry : root.entrySet()) {
                String key = prefix + "." + entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) entry.getValue();
                prompts.put(key, new PromptConfig(map.get("system"), map.get("user")));
            }
        } catch (Exception e) {
            throw new RuntimeException("加载提示词失败: " + path, e);
        }
    }

    public PromptConfig get(String key) {
        PromptConfig config = prompts.get(key);
        if (config == null) throw new IllegalArgumentException("未找到提示词: " + key);
        return config;
    }

    public static class PromptConfig {
        private final String system;
        private final String user;
        public PromptConfig(String system, String user) {
            this.system = system;
            this.user = user;
        }
        // getters...
        public String getSystem() { return system; }
        public String getUser() { return user; }
    }
}
