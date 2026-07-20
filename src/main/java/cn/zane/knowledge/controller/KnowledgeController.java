package cn.zane.knowledge.controller;

import cn.zane.knowledge.model.DocumentEntry;
import cn.zane.knowledge.service.KnowledgeService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 知识库管理 REST 控制器。
 *
 * <p>提供知识文档的上传、搜索、列表和删除接口。
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 上传知识库文档。
     *
     * @param req 上传请求体，包含 filename 和 content
     * @return 已索引的文档条目
     */
    @PostMapping("/upload")
    public Mono<DocumentEntry> upload(@RequestBody UploadRequest req) {
        DocumentEntry entry = knowledgeService.uploadDocument(req.filename(), req.content());
        return Mono.just(entry);
    }

    /**
     * 搜索知识库。
     *
     * @param req 搜索请求体，包含可选的 keyword
     * @return 匹配的文档列表
     */
    @PostMapping("/search")
    public Mono<List<DocumentEntry>> search(@RequestBody SearchRequest req) {
        List<DocumentEntry> results = knowledgeService.search(req.keyword());
        return Mono.just(results);
    }

    /**
     * 列出所有文档。
     *
     * @return 所有文档列表
     */
    @GetMapping
    public Mono<List<DocumentEntry>> list() {
        return Mono.just(knowledgeService.listDocuments());
    }

    /**
     * 删除指定文档。
     *
     * @param id 文档唯一标识
     * @return 操作结果，包含 deleted 状态
     */
    @DeleteMapping("/{id}")
    public Mono<Map<String, Object>> delete(@PathVariable("id") String id) {
        boolean deleted = knowledgeService.deleteDocument(id);
        return Mono.just(Map.of("deleted", deleted));
    }

    // ========== 请求体 Records ==========

    /**
     * 文档上传请求。
     *
     * @param filename 文件名或来源标识
     * @param content  文档文本内容
     */
    public record UploadRequest(String filename, String content) {}

    /**
     * 文档搜索请求。
     *
     * @param keyword 搜索关键词（可选，为空则返回全部文档）
     */
    public record SearchRequest(String keyword) {}
}
