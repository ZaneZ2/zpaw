package cn.zane.bootstrap.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "zpaw.model")
public class ModelConfig {
    private String defaultModel = "dashscope:qwen-plus";
    private List<ModelEntry> providers = new ArrayList<>();

    @Setter
    @Getter
    public static class ModelEntry {
        private String id;
        private String apiKey;
        private String baseUrl;
        private String modelName;
        private boolean stream = true;
    }
}
