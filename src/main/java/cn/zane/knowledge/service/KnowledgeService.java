package cn.zane.knowledge.service;

import cn.zane.knowledge.model.DocumentEntry;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 知识库服务。
 *
 * <p>提供 RAG（检索增强生成）能力，包括文档导入、文本分块、关键词检索
 * 及文档管理。当前阶段（Phase 3）基于 {@link IndexService} 实现内存全文检索，
 * Phase 4+ 可升级为向量化存储与语义检索。
 *
 * @author Zane
 */
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeService.class);

    /** 允许上传的文本文件扩展名（小写）。 */
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("txt", "md", "json", "xml", "html", "csv", "yaml", "yml", "properties", "log");

    private final IndexService indexService;

    /**
     * 上传并索引一条文档，含文件类型校验。
     *
     * @param filename 文件名或来源标识
     * @param content  文档文本内容
     * @return 已索引的 {@link DocumentEntry}
     * @throws IllegalArgumentException 如果文件类型不被允许
     */
    public DocumentEntry uploadDocument(String filename, String content) {
        validateFileType(filename);
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("文档内容不能为空");
        }
        return indexService.indexDocument(filename, content);
    }

    /**
     * 按关键词搜索文档。
     *
     * @param keyword 搜索关键词
     * @return 匹配的文档列表
     */
    public List<DocumentEntry> search(String keyword) {
        return indexService.search(keyword);
    }

    /**
     * 列出所有文档。
     *
     * @return 所有文档列表
     */
    public List<DocumentEntry> listDocuments() {
        return indexService.listDocuments();
    }

    /**
     * 删除指定文档。
     *
     * @param id 文档唯一标识
     * @return 是否存在且已删除
     */
    public boolean deleteDocument(String id) {
        return indexService.deleteDocument(id);
    }

    /**
     * 校验文件扩展名是否在允许列表中。
     *
     * @param filename 文件名
     * @throws IllegalArgumentException 如果文件类型不被允许
     */
    private void validateFileType(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        int dotIdx = filename.lastIndexOf('.');
        if (dotIdx < 0) {
            throw new IllegalArgumentException("不支持无扩展名的文件: " + filename);
        }
        String ext = filename.substring(dotIdx + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                    "不支持的文件类型: ." + ext + "。允许的类型: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
}
