package cn.zane.knowledge.model;

/**
 * 知识库文档条目。
 *
 * <p>表示一条被索引的知识文档，包含唯一标识、文件名、文本内容和索引时间戳。
 *
 * @param id        文档唯一标识（如 "doc-1"）
 * @param filename  原始文件名或来源标识
 * @param content   文档文本内容
 * @param indexedAt 索引时间戳（毫秒，由 {@link System#currentTimeMillis()} 生成）
 *
 * @author Zane
 */
public record DocumentEntry(String id, String filename, String content, long indexedAt) {}
