export interface DocumentEntry {
	id: string;
	filename: string;
	content: string;
	indexedAt: number;
}

export async function uploadDocument(
	content: string,
	filename: string,
): Promise<void> {
	const res = await fetch("/api/knowledge/documents", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ content, filename }),
	});
	if (!res.ok) throw new Error(`Failed to upload document: ${res.status}`);
}

export async function searchDocuments(
	keyword: string,
): Promise<DocumentEntry[]> {
	const res = await fetch(
		`/api/knowledge/documents/search?keyword=${encodeURIComponent(keyword)}`,
	);
	if (!res.ok)
		throw new Error(`Failed to search documents: ${res.status}`);
	return res.json();
}

export async function listDocuments(): Promise<DocumentEntry[]> {
	const res = await fetch("/api/knowledge/documents");
	if (!res.ok)
		throw new Error(`Failed to list documents: ${res.status}`);
	return res.json();
}

export async function deleteDocument(id: string): Promise<void> {
	const res = await fetch(
		`/api/knowledge/documents/${encodeURIComponent(id)}`,
		{ method: "DELETE" },
	);
	if (!res.ok) throw new Error(`Failed to delete document: ${res.status}`);
}
