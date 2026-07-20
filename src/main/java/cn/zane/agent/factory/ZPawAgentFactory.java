package cn.zane.agent.factory;

import org.springframework.stereotype.Component;

/**
 * Agent 实例工厂。
 *
 * <p>负责创建 {@link io.agentscope.harness.agent.HarnessAgent} 实例，
 * 封装 Agent 的初始化、配置注入及生命周期管理。Phase 2 将在此实现
 * 多 Agent 实例的生产逻辑。
 *
 * @author Zane
 */
@Component
public class ZPawAgentFactory {
    // Phase 2: Create HarnessAgent instances
}
