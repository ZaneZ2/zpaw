package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.message.UserMessage;
import java.time.Duration;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents")
public class ChatController {

    private final ZPawBootstrap bootstrap;

    public ChatController(ZPawBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @PostMapping(value = "/{agentId}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @PathVariable("agentId") String agentId, @RequestBody ChatRequest req) {
        var agent = bootstrap.getDefaultAgent();
        if (agent == null) {
            return Flux.just("data: {\"type\":\"error\",\"error\":\"Agent not initialized\"}\n\n");
        }
        return agent.streamEvents(new UserMessage(req.message()))
                .timeout(Duration.ofSeconds(60))
                .map(
                        event -> {
                            if (event instanceof TextBlockDeltaEvent e) {
                                return "data: {\"type\":\"token\",\"data\":\""
                                        + escape(e.getDelta())
                                        + "\"}\n\n";
                            } else if (event instanceof ToolCallStartEvent e) {
                                return "data: {\"type\":\"tool_call\",\"toolName\":\""
                                        + escape(e.getToolCallName())
                                        + "\"}\n\n";
                            }
                            return "";
                        })
                .filter(s -> !s.isEmpty())
                .concatWithValues("data: {\"type\":\"done\"}\n\n")
                .onErrorResume(
                        e ->
                                Flux.just(
                                        "data: {\"type\":\"error\",\"error\":\""
                                                + escape(e.getMessage())
                                                + "\"}\n\n"));
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @GetMapping("/{agentId}/chat/session")
    public Mono<Map<String, Object>> currentSession(@PathVariable("agentId") String agentId) {
        return Mono.just(Map.of("sessionKey", "default", "exists", false));
    }

    public record ChatRequest(String message, String sessionKey) {
        public ChatRequest(String message) {
            this(message, null);
        }
    }
}
