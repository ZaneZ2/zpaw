package cn.zane.bootstrap;

import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class ZPawBootstrap {

    private HarnessAgent defaultAgent;

    @Value("${ZPAW_AGENT_NAME:zpaw}")
    private String agentName;

    @Value(
            "${ZPAW_SYS_PROMPT:You are a helpful local assistant named zpaw. Answer accurately and"
                    + " concisely.}")
    private String sysPrompt;

    @Value("${ZPAW_DEFAULT_MODEL:dashscope:qwen-plus}")
    private String defaultModel;

    @PostConstruct
    public void init() {
        log.info("Initializing HarnessAgent with model: {}", defaultModel);
        defaultAgent =
                HarnessAgent.builder()
                        .name(agentName)
                        .sysPrompt(sysPrompt)
                        .model(defaultModel)
                        .build();
        log.info("HarnessAgent '{}' initialized", agentName);
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down HarnessAgent");
    }
}
