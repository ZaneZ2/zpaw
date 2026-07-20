import { useEffect, useState } from "react";
import { ProTable } from "@ant-design/pro-components";
import { Tag, Typography } from "antd";
import type { ProColumns } from "@ant-design/pro-components";
import { listTools, type ToolDefinition } from "../api/tools";

export default function ToolsPage() {
	const [data, setData] = useState<ToolDefinition[]>([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		(async () => {
			try {
				setLoading(true);
				const tools = await listTools();
				setData(tools);
			} catch {
				// backend may not be ready
			} finally {
				setLoading(false);
			}
		})();
	}, []);

	const columns: ProColumns<ToolDefinition>[] = [
		{
			title: "名称",
			dataIndex: "name",
			key: "name",
			width: 200,
		},
		{
			title: "描述",
			dataIndex: "description",
			key: "description",
			ellipsis: true,
		},
		{
			title: "状态",
			dataIndex: "enabled",
			key: "enabled",
			width: 100,
			render: (_, record) =>
				record.enabled ? (
					<Tag color="green">已启用</Tag>
				) : (
					<Tag color="default">已禁用</Tag>
				),
		},
		{
			title: "操作",
			key: "action",
			width: 120,
			render: () => <Tag color="orange">开发中</Tag>,
		},
	];

	return (
		<div style={{ padding: 24 }}>
			<Typography.Title level={4} style={{ marginBottom: 16 }}>
				工具管理
			</Typography.Title>
			<ProTable<ToolDefinition>
				columns={columns}
				dataSource={data}
				rowKey="name"
				loading={loading}
				pagination={false}
				search={false}
				options={false}
				dateFormatter="string"
				toolBarRender={false}
			/>
		</div>
	);
}
