package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Agent 管理 REST 控制器。
 *
 * <p>提供 Agent 的列表查询和详情获取接口。</p>
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final ZPawBootstrap bootstrap;

    /**
     * 获取所有 Agent 列表。
     *
     * @return a list of agent summaries, each containing id, name and builtin flag
     */
    @GetMapping
    public Mono<List<Map<String, Object>>> list() {
        var agent = bootstrap.getDefaultAgent();
        if (agent == null) {
            return Mono.just(List.of());
        }
        return Mono.just(
                List.of(
                        Map.of(
                                "id",
                                "default",
                                "name",
                                bootstrap.getAgentName(),
                                "builtin",
                                true)));
    }

    /**
     * 根据 ID 获取 Agent。
     *
     * @param id the agent identifier
     * @return agent detail containing id, name and builtin flag
     */
    @GetMapping("/{id}")
    public Mono<Map<String, Object>> get(@PathVariable("id") String id) {
        return Mono.just(Map.of("id", id, "name", bootstrap.getAgentName(), "builtin", true));
    }
}
