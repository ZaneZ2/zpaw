package cn.zane;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

@Slf4j
public class Main {
    public static void main(String[] args) {

        HarnessAgent agent = HarnessAgent.builder()
                .name("paw")
                .sysPrompt("你是一个个人助手.")
                .model(OpenAIChatModel.builder()
                        .modelName(System.getenv("AGENT_MODEL_ID"))
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .baseUrl(System.getenv("OPENAI_BASE_URL"))
                        .build())
                .workspace(Paths.get(".agentscope/workspace"))
                .compaction(
                        CompactionConfig.builder()
                                .triggerMessages(30)
                                .keepMessages(10)
                                .build()
                )
                .build();

        RuntimeContext ctx = RuntimeContext.builder()
                .sessionId("demo-session")
                .userId("zane")
                .build();

        Msg msg = agent.call(new UserMessage("我是很烦人, 现在开始学习AgentScope-java."), ctx).block();


        agent.call(new UserMessage("我是谁,我准备干什么?")).block();
    }
}