package cn.zane.knowledge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 知识库管理 REST 控制器。
 *
 * <p>提供知识文档的上传和搜索接口（当前为桩实现）。</p>
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    /**
     * Upload a knowledge document.
     *
     * @return a JSON string indicating the upload status (currently not implemented)
     */
    @PostMapping("/upload")
    public Mono<String> upload() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }

    /**
     * Search knowledge base.
     *
     * @return a JSON string with search results (currently not implemented)
     */
    @PostMapping("/search")
    public Mono<String> search() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }
}
