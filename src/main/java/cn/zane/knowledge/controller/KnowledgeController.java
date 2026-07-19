package cn.zane.knowledge.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    @PostMapping("/upload")
    public Mono<String> upload() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }

    @PostMapping("/search")
    public Mono<String> search() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }
}
