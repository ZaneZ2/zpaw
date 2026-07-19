package cn.zane.bootstrap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "zpaw.agent")
public class AgentProperties {
    private String name = "zpaw";
    private String sysPrompt =
            "You are a helpful local assistant named zpaw. " + "Answer accurately and concisely.";
}
