package cn.zane.web.api;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents/{agentId}/sessions")
public class SessionController {

    @GetMapping("/inbox")
    public Mono<List<Map<String, Object>>> inbox(@PathVariable("agentId") String agentId) {
        return Mono.just(
                List.of(
                        Map.of(
                                "sessionKey", "default",
                                "sessionId", "default",
                                "agentId", agentId,
                                "label", "当前会话",
                                "lastActivityMs", System.currentTimeMillis(),
                                "lastMessage", "",
                                "unread", false)));
    }

    @GetMapping("/{sessionKey}")
    public Mono<List<Map<String, Object>>> turns(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.just(List.of());
    }

    @PostMapping("/{sessionKey}/reset")
    public Mono<Map<String, Object>> reset(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.just(Map.of("sessionKey", sessionKey, "reset", true));
    }

    @DeleteMapping("/{sessionKey}")
    public Mono<Void> delete(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.empty();
    }
}
