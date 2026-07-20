package cn.zane.bootstrap.config;

import io.agentscope.core.model.Model;
import io.agentscope.core.model.ModelRegistry;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 模型实例注册配置。
 *
 * <p>读取 {@link ModelProperties} 中的 providers 列表，为每个 provider 创建
 * {@link Model} 实例并注册到 AgentScope 的 {@link ModelRegistry}，
 * 使得 {@code defaultModel} 字符串可解析为具体的 Model 对象。
 *
 * @author Zane
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ModelProperties.class, AgentProperties.class})
public class ModelConfig {

    private final ModelProperties modelProperties;

    @PostConstruct
    public void registerModels() {
        String defaultModel = modelProperties.getDefaultModel();
        log.info("Registering models, default: {}", defaultModel);

        int registered = 0;
        int skipped = 0;

        for (ModelProperties.ModelEntry entry : modelProperties.getProviders()) {
            Model model = createModel(entry);
            if (model == null) {
                skipped++;
                continue;
            }
            String modelId = entry.getId() + ":" + entry.getModelName();
            ModelRegistry.register(modelId, model);
            registered++;
            log.info(
                    "Registered model: {} (provider={}, baseUrl={})",
                    modelId,
                    entry.getId(),
                    obfuscate(entry.getBaseUrl()));
        }

        log.info(
                "Model registration complete: {} registered, {} skipped, default={}",
                registered,
                skipped,
                defaultModel);
    }

    private Model createModel(ModelProperties.ModelEntry entry) {
        String providerId = entry.getId();
        String apiKey = entry.getApiKey();
        String modelName = entry.getModelName();

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[{}] Skipped: apiKey is empty", providerId);
            return null;
        }
        if (modelName == null || modelName.isBlank()) {
            log.warn("[{}] Skipped: modelName is empty", providerId);
            return null;
        }

        return switch (providerId) {
            case "openai" -> {
                String baseUrl = entry.getBaseUrl();
                if (baseUrl == null || baseUrl.isBlank()) {
                    log.info("[openai] Using default baseUrl");
                }
                yield OpenAIChatModel.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .modelName(modelName)
                        .stream(entry.isStream())
                        .build();
            }
            case "dashscope" ->
                    DashScopeChatModel.builder().apiKey(apiKey).modelName(modelName).stream(
                                    entry.isStream())
                            .build();
            default -> {
                log.warn("Unsupported provider id: {}, skipping", providerId);
                yield null;
            }
        };
    }

    /**
     * 脱敏显示 baseUrl
     */
    private static String obfuscate(String url) {
        if (url == null || url.isEmpty()) {
            return "(default)";
        }
        int idx = url.indexOf("://");
        if (idx < 0) {
            return url;
        }
        String host = url.substring(idx + 3);
        return url.substring(0, idx + 3)
                + host.substring(0, Math.min(host.length(), 20))
                + (host.length() > 20 ? "..." : "");
    }
}
