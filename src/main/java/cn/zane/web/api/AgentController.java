package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final ZPawBootstrap bootstrap;

    public AgentController(ZPawBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

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

    @GetMapping("/{id}")
    public Mono<Map<String, Object>> get(@PathVariable("id") String id) {
        return Mono.just(Map.of("id", id, "name", bootstrap.getAgentName(), "builtin", true));
    }
}
