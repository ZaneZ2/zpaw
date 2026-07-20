package cn.zane.knowledge.service;

import cn.zane.knowledge.model.DocumentEntry;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 本地全文检索服务。
 *
 * <p>基于内存 {@link ConcurrentHashMap} 实现简易的关键词搜索，支持文档的索引、搜索、
 * 列出和删除操作。Phase 4+ 可升级为 Lucene 或向量数据库。
 *
 * @author Zane
 */
@Service
public class IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);

    private final ConcurrentHashMap<String, DocumentEntry> store = new ConcurrentHashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(1);

    /**
     * 索引一条文档。
     *
     * @param filename 原始文件名或来源标识
     * @param content  文档文本内容
     * @return 已索引的 {@link DocumentEntry}
     */
    public DocumentEntry indexDocument(String filename, String content) {
        String id = "doc-" + idGen.getAndIncrement();
        DocumentEntry entry = new DocumentEntry(id, filename, content, System.currentTimeMillis());
        store.put(id, entry);
        log.info("文档已索引: {} ({} 字符)", filename, content.length());
        return entry;
    }

    /**
     * 按关键词搜索文档。
     *
     * <p>关键词为空或空白时返回全部文档。匹配规则：文件名或内容中（不区分大小写）
     * 包含关键词即视为匹配。
     *
     * @param keyword 搜索关键词（大小写不敏感）
     * @return 匹配的文档列表
     */
    public List<DocumentEntry> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.copyOf(store.values());
        }
        String kw = keyword.toLowerCase();
        return store.values().stream()
                .filter(
                        d ->
                                d.filename().toLowerCase().contains(kw)
                                        || d.content().toLowerCase().contains(kw))
                .toList();
    }

    /**
     * 返回所有已索引的文档。
     *
     * @return 所有文档列表（不可修改的快照）
     */
    public List<DocumentEntry> listDocuments() {
        return List.copyOf(store.values());
    }

    /**
     * 删除指定文档。
     *
     * @param id 文档唯一标识
     * @return 是否存在且已删除
     */
    public boolean deleteDocument(String id) {
        return store.remove(id) != null;
    }

    /**
     * 获取当前索引文档数量。
     *
     * @return 文档总数
     */
    public int getIndexCount() {
        return store.size();
    }
}
