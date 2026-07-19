package cn.zane.bootstrap;

import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZPawBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ZPawBootstrap.class);
    private HarnessAgent defaultAgent;

    @PostConstruct
    public void init() {
        log.info("ZPawBootstrap initialized — agent scaffolding ready");
    }

    public HarnessAgent getDefaultAgent() {
        return defaultAgent;
    }
}
