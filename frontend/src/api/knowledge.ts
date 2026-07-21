/**
 * 知识库 API。
 *
 * 后端路由统一为 /api/knowledge，提供文档的上传、搜索、列表和删除能力。
 *
 * @author Zane
 */
export interface DocumentEntry {
	id: string;
	filename: string;
	content: string;
	indexedAt: number;
}

/**
 * 上传文档到知识库。
 *
 * @param content  文档文本内容
 * @param filename 文件名或来源标识
 */
export async function uploadDocument(
	content: string,
	filename: string,
): Promise<void> {
	const res = await fetch("/api/knowledge/upload", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ content, filename }),
	});
	if (!res.ok) throw new Error(`上传文档失败: ${res.status}`);
}

/**
 * 搜索知识库文档。
 *
 * @param keyword 搜索关键词（为空则返回全部文档）
 * @returns 匹配的文档列表
 */
export async function searchDocuments(
	keyword: string,
): Promise<DocumentEntry[]> {
	const res = await fetch("/api/knowledge/search", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ keyword }),
	});
	if (!res.ok) throw new Error(`搜索失败: ${res.status}`);
	return res.json();
}

/**
 * 获取知识库中所有文档。
 *
 * @returns 文档列表
 */
export async function listDocuments(): Promise<DocumentEntry[]> {
	const res = await fetch("/api/knowledge");
	if (!res.ok) throw new Error(`获取文档列表失败: ${res.status}`);
	return res.json();
}

/**
 * 删除指定文档。
 *
 * @param id 文档唯一标识
 */
export async function deleteDocument(id: string): Promise<void> {
	const res = await fetch(`/api/knowledge/${encodeURIComponent(id)}`, {
		method: "DELETE",
	});
	if (!res.ok) throw new Error(`删除文档失败: ${res.status}`);
}
