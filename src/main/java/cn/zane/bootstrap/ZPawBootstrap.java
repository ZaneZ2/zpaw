package cn.zane.bootstrap;

import cn.zane.bootstrap.config.AgentProperties;
import cn.zane.bootstrap.config.ModelProperties;
import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * ZPaw 应用引导组件。
 *
 * <p>负责在应用启动时初始化默认的 {@link HarnessAgent}，并在应用关闭时执行清理。</p>
 *
 * @author Zane
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
@DependsOn("modelConfig")
public class ZPawBootstrap {

    private final ModelProperties modelConfig;
    private final AgentProperties agentProperties;

    private HarnessAgent defaultAgent;

    /**
     * Initialize the default HarnessAgent after bean construction.
     *
     * <p>Reads model and agent configuration from properties, builds and assigns
     * the default {@link HarnessAgent} instance.</p>
     */
    @PostConstruct
    public void init() {
        String modelStr = modelConfig.getDefaultModel();
        String agentName = agentProperties.getName();
        String sysPrompt = agentProperties.getSysPrompt();
        log.info("Initializing HarnessAgent '{}' with model: {}", agentName, modelStr);
        defaultAgent =
                HarnessAgent.builder().name(agentName).sysPrompt(sysPrompt).model(modelStr).build();
        log.info("HarnessAgent '{}' initialized with model '{}'", agentName, modelStr);
    }

    /**
     * Clean up resources before bean destruction.
     */
    @PreDestroy
    public void destroy() {
        log.info("Shutting down HarnessAgent");
    }

    /**
     * Get the configured agent name.
     *
     * @return the agent name from properties
     */
    public String getAgentName() {
        return agentProperties.getName();
    }
}
