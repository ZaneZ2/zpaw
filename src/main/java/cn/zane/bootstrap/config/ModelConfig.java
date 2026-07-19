package cn.zane.bootstrap.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zpaw.model")
public class ModelConfig {
    private String defaultModel = "dashscope:qwen-plus";
    private List<ModelEntry> providers = new ArrayList<>();

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String v) {
        this.defaultModel = v;
    }

    public List<ModelEntry> getProviders() {
        return providers;
    }

    public void setProviders(List<ModelEntry> v) {
        this.providers = v;
    }

    public static class ModelEntry {
        private String id;
        private String apiKey;
        private String modelName;
        private boolean stream = true;

        public String getId() {
            return id;
        }

        public void setId(String v) {
            this.id = v;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String v) {
            this.apiKey = v;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String v) {
            this.modelName = v;
        }

        public boolean isStream() {
            return stream;
        }

        public void setStream(boolean v) {
            this.stream = v;
        }
    }
}
