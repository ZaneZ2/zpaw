package cn.zane.bootstrap.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模型提供者配置属性。
 *
 * <p>映射 {@code zpaw.model.*} 配置前缀，管理多个 LLM 提供者（如 DashScope、OpenAI）
 * 的接入信息。支持运行时动态注册模型实例到 {@link io.agentscope.core.model.ModelRegistry}。
 *
 * @author Zane
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zpaw.model")
public class ModelProperties {
    /** 默认模型的标识符，格式为 {@code providerId:modelName} */
    private String defaultModel = "dashscope:qwen-plus";

    /** LLM 提供者列表，每个条目包含接入凭证和模型参数 */
    private List<ModelEntry> providers = new ArrayList<>();

    /**
     * 单个 LLM 提供者条目。
     *
     * <p>描述一个模型提供者的接入信息，包括提供者标识、API 密钥、
     * 服务基地址及模型名称。
     *
     * @author Zane
     */
    @Setter
    @Getter
    public static class ModelEntry {
        /** 提供者标识，如 {@code openai} 或 {@code dashscope} */
        private String id;

        /** API 密钥 */
        private String apiKey;

        /** 服务基地址，为 null 时使用 SDK 默认值 */
        private String baseUrl;

        /** 模型名称，如 {@code qwen-plus}、{@code gpt-4o} */
        private String modelName;

        /** 是否启用流式响应，默认 true */
        private boolean stream = true;
    }
}
