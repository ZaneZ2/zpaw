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
     * 初始化默认的 HarnessAgent。
     *
     * <p>从配置中读取模型和 Agent 配置，构建并赋值默认的 {@link HarnessAgent} 实例。</p>
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
     * 销毁时释放资源。
     */
    @PreDestroy
    public void destroy() {
        log.info("Shutting down HarnessAgent");
    }

    /**
     * 获取配置的 Agent 名称。
     *
     * @return 配置中的 Agent 名称
     */
    public String getAgentName() {
        return agentProperties.getName();
    }
}
