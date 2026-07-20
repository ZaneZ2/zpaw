package cn.zane.bootstrap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Agent 配置属性。
 *
 * <p>映射 {@code zpaw.agent.*} 配置前缀，用于定义 Agent 的名称和系统提示词。
 * 属性值可在 {@code application.yaml} 或环境变量中覆盖。
 *
 * @author Zane
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zpaw.agent")
public class AgentProperties {
    /** Agent 名称，默认 "zpaw" */
    private String name = "zpaw";

    /** 系统提示词，用于设定 Agent 的行为和角色 */
    private String sysPrompt =
            "You are a helpful local assistant named zpaw. " + "Answer accurately and concisely.";
}
