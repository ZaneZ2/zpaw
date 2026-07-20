import { useEffect, useState, useCallback } from "react";
import { Button, Input, List, Typography, Popconfirm, Empty, Space, message } from "antd";
import { UploadOutlined, SearchOutlined, DeleteOutlined, FileTextOutlined } from "@ant-design/icons";
import {
	listDocuments,
	uploadDocument,
	searchDocuments,
	deleteDocument,
	type DocumentEntry,
} from "../api/knowledge";

export default function KnowledgePage() {
	const [documents, setDocuments] = useState<DocumentEntry[]>([]);
	const [loading, setLoading] = useState(true);
	const [uploading, setUploading] = useState(false);
	const [content, setContent] = useState("");
	const [filename, setFilename] = useState("");
	const [searchKeyword, setSearchKeyword] = useState("");

	const loadDocuments = useCallback(async () => {
		try {
			setLoading(true);
			const docs = await listDocuments();
			setDocuments(docs);
		} catch {
			// backend may not be ready
		} finally {
			setLoading(false);
		}
	}, []);

	useEffect(() => {
		loadDocuments();
	}, [loadDocuments]);

	const handleUpload = async () => {
		if (!content.trim() || !filename.trim()) {
			message.warning("请输入文件名和内容");
			return;
		}
		try {
			setUploading(true);
			await uploadDocument(content, filename);
			message.success("上传成功");
			setContent("");
			setFilename("");
			await loadDocuments();
		} catch {
			message.error("上传失败");
		} finally {
			setUploading(false);
		}
	};

	const handleSearch = async () => {
		if (!searchKeyword.trim()) {
			await loadDocuments();
			return;
		}
		try {
			setLoading(true);
			const results = await searchDocuments(searchKeyword);
			setDocuments(results);
		} catch {
			message.error("搜索失败");
		} finally {
			setLoading(false);
		}
	};

	const handleDelete = async (id: string) => {
		try {
			await deleteDocument(id);
			message.success("删除成功");
			await loadDocuments();
		} catch {
			message.error("删除失败");
		}
	};

	return (
		<div style={{ padding: 24 }}>
			<Typography.Title level={4} style={{ marginBottom: 16 }}>
				知识库
			</Typography.Title>

			{/* Upload section */}
			<div
				style={{
					marginBottom: 24,
					padding: 16,
					border: "1px solid #f0f0f0",
					borderRadius: 8,
					background: "#fafafa",
				}}
			>
				<Typography.Text strong style={{ display: "block", marginBottom: 12 }}>
					上传文档
				</Typography.Text>
				<Space direction="vertical" style={{ width: "100%" }}>
					<Input
						placeholder="文件名（如 readme.txt）"
						value={filename}
						onChange={(e) => setFilename(e.target.value)}
					/>
					<Input.TextArea
						placeholder="输入文档内容..."
						value={content}
						onChange={(e) => setContent(e.target.value)}
						rows={4}
					/>
					<Button
						type="primary"
						icon={<UploadOutlined />}
						onClick={handleUpload}
						loading={uploading}
					>
						上传
					</Button>
				</Space>
			</div>

			{/* Search section */}
			<div style={{ marginBottom: 16 }}>
				<Space>
					<Input
						placeholder="搜索关键词..."
						prefix={<SearchOutlined />}
						value={searchKeyword}
						onChange={(e) => setSearchKeyword(e.target.value)}
						onPressEnter={handleSearch}
						style={{ width: 300 }}
					/>
					<Button onClick={handleSearch}>搜索</Button>
					{searchKeyword && (
						<Button onClick={() => { setSearchKeyword(""); loadDocuments(); }}>
							清除
						</Button>
					)}
				</Space>
			</div>

			{/* Document list */}
			<Typography.Text
				type="secondary"
				style={{ display: "block", marginBottom: 8 }}
			>
				共 {documents.length} 个文档
			</Typography.Text>

			<List
				loading={loading}
				dataSource={documents}
				locale={{ emptyText: <Empty description="暂无文档" /> }}
				renderItem={(item) => (
					<List.Item
						actions={[
							<Popconfirm
								key="delete"
								title="确定删除此文档？"
								onConfirm={() => handleDelete(item.id)}
							>
								<Button
									type="link"
									danger
									icon={<DeleteOutlined />}
								>
									删除
								</Button>
							</Popconfirm>,
						]}
					>
						<List.Item.Meta
							avatar={<FileTextOutlined style={{ fontSize: 24, color: "#1677ff" }} />}
							title={item.filename}
							description={
								<>
									<div
										style={{
											color: "#666",
											maxHeight: 40,
											overflow: "hidden",
											textOverflow: "ellipsis",
										}}
									>
										{item.content.slice(0, 200)}
									</div>
									<Typography.Text type="secondary" style={{ fontSize: 12 }}>
										{new Date(item.indexedAt).toLocaleString()}
									</Typography.Text>
								</>
							}
						/>
					</List.Item>
				)}
			/>
		</div>
	);
}
