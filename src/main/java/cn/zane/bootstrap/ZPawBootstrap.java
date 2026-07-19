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

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
@DependsOn("modelConfig")
public class ZPawBootstrap {

    private final ModelProperties modelConfig;
    private final AgentProperties agentProperties;

    private HarnessAgent defaultAgent;

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

    @PreDestroy
    public void destroy() {
        log.info("Shutting down HarnessAgent");
    }

    public String getAgentName() {
        return agentProperties.getName();
    }
}
