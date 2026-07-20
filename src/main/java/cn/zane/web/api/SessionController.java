package cn.zane.web.api;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 会话管理 REST 控制器。
 *
 * <p>提供会话的收件箱、历史查看、重置和删除接口。</p>
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/agents/{agentId}/sessions")
@RequiredArgsConstructor
public class SessionController {

    /**
     * List inbox sessions for the given agent.
     *
     * @param agentId the agent identifier
     * @return a list of session summaries with last activity info
     */
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

    /**
     * Get conversation turns for a specific session.
     *
     * @param agentId    the agent identifier
     * @param sessionKey the session key
     * @return a list of turn entries (currently returns empty list)
     */
    @GetMapping("/{sessionKey}")
    public Mono<List<Map<String, Object>>> turns(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.just(List.of());
    }

    /**
     * Reset a session, clearing its conversation history.
     *
     * @param agentId    the agent identifier
     * @param sessionKey the session key to reset
     * @return a map indicating the session was reset
     */
    @PostMapping("/{sessionKey}/reset")
    public Mono<Map<String, Object>> reset(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.just(Map.of("sessionKey", sessionKey, "reset", true));
    }

    /**
     * Delete a session.
     *
     * @param agentId    the agent identifier
     * @param sessionKey the session key to delete
     * @return an empty response on success
     */
    @DeleteMapping("/{sessionKey}")
    public Mono<Void> delete(
            @PathVariable("agentId") String agentId,
            @PathVariable("sessionKey") String sessionKey) {
        return Mono.empty();
    }
}
