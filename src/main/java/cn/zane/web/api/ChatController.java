package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEndEvent;
import io.agentscope.core.event.AgentStartEvent;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.event.ToolResultEndEvent;
import io.agentscope.core.event.ToolResultStartEvent;
import io.agentscope.core.event.ToolResultTextDeltaEvent;
import io.agentscope.core.message.UserMessage;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 智能体对话 API。
 *
 * <p>提供 SSE 流式聊天端点，将 {@link io.agentscope.harness.agent.HarnessAgent} 的
 * AgentEvent 流转换为前端可消费的 SSE 事件流。
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class ChatController {

    private final ZPawBootstrap bootstrap;

    /**
     * SSE 流式聊天接口。
     *
     * <p>接收用户消息，通过 {@link io.agentscope.harness.agent.HarnessAgent#streamEvents}
     * 获取 AgentEvent 事件流，映射为 {@link StreamEvent} 后以 SSE 格式推送。
     *
     * <p>事件类型说明：
     * <pre>
     *   AGENT_START       → Agent 开始处理
     *     TOKEN           → LLM 文本增量
     *     TOOL_CALL       → LLM 请求调工具
     *     TOOL_RESULT_*   → 工具执行过程
     *   AGENT_END         → Agent 处理完成（不做任何输出）
     *   done              → SSE 流结束标志
     * </pre>
     *
     * @param agentId Agent 标识（当前固定为 "default"）
     * @param req     请求体，包含 message 和可选的 sessionKey
     * @return SSE 事件流
     */
    @PostMapping(value = "/{agentId}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @PathVariable("agentId") String agentId, @RequestBody ChatRequest req) {
        // 前置检查：Agent 是否已初始化
        var agent = bootstrap.getDefaultAgent();
        if (agent == null) {
            return Flux.just(StreamEvent.error("Agent not initialized").toSse());
        }

        return agent.streamEvents(new UserMessage(req.message()), RuntimeContext.empty())
                // 将阻塞操作切换到 boundedElastic 线程池，避免阻塞 Netty event loop
                .subscribeOn(Schedulers.boundedElastic())
                // 单次对话超时 120 秒，防止请求卡死
                .timeout(Duration.ofSeconds(120))
                // 将 AgentEvent 映射为 StreamEvent → SSE 字符串
                .map(
                        event -> {
                            if (event instanceof AgentStartEvent e) {
                                return StreamEvent.agentStart(e.getName());
                            } else if (event instanceof TextBlockDeltaEvent e) {
                                return StreamEvent.token(e.getDelta());
                            } else if (event instanceof ToolCallStartEvent e) {
                                return StreamEvent.toolCall(e.getToolCallName());
                            } else if (event instanceof ToolResultStartEvent e) {
                                return StreamEvent.toolResultStart(e.getToolCallName());
                            } else if (event instanceof ToolResultTextDeltaEvent e) {
                                return StreamEvent.toolResultDelta(e.getDelta());
                            } else if (event instanceof ToolResultEndEvent e) {
                                String state =
                                        e.getState() != null ? e.getState().name() : "SUCCESS";
                                return StreamEvent.toolResultEnd(state);
                            } else if (event instanceof AgentEndEvent) {
                                // Agent 正常结束，不做任何输出，流由 filter + concatWithValues 闭合
                                return null;
                            }
                            return null;
                        })
                // 过滤掉 null（AGENT_END 等不需要输出的事件）
                .filter(e -> e != null)
                // 转 SSE 格式
                .map(StreamEvent::toSse)
                // 流正常结束时追加 done 事件
                .concatWithValues(StreamEvent.done().toSse())
                // 异常时推送 error 事件
                .onErrorResume(e -> Flux.just(StreamEvent.error(e.getMessage()).toSse()));
    }

    /**
     * 获取当前会话信息。
     *
     * @param agentId Agent 标识
     * @return 当前会话 key 和是否存在标识
     */
    @GetMapping("/{agentId}/chat/session")
    public Mono<Map<String, Object>> currentSession(@PathVariable("agentId") String agentId) {
        return Mono.just(Map.of("sessionKey", "default", "exists", false));
    }

    /**
     * 聊天请求体。
     *
     * @param message    用户消息文本
     * @param sessionKey 会话标识（可选，不传则创建新会话）
     */
    public record ChatRequest(String message, String sessionKey) {
        public ChatRequest(String message) {
            this(message, null);
        }
    }
}
