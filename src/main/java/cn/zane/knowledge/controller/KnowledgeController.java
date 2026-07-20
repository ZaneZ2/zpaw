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
     * 上传知识库文档。
     *
     * @return 包含上传状态的 JSON 字符串（当前未实现）
     */
    @PostMapping("/upload")
    public Mono<String> upload() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }

    /**
     * 搜索知识库。
     *
     * @return 包含搜索结果的 JSON 字符串（当前未实现）
     */
    @PostMapping("/search")
    public Mono<String> search() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }
}
