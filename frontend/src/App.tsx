import { useNavigate, Routes, Route, Navigate } from "react-router-dom";
import ProLayout from "@ant-design/pro-layout";
import {
	ToolOutlined,
	WechatOutlined,
	SettingOutlined,
	DatabaseOutlined,
} from "@ant-design/icons";
import ChatPage from "./pages/ChatPage";
import ToolsPage from "./pages/ToolsPage";
import KnowledgePage from "./pages/KnowledgePage";

function Placeholder({ title }: { title: string }) {
	return (
		<div
			style={{
				padding: 48,
				textAlign: "center",
				color: "#999",
				fontSize: 18,
			}}
		>
			{title} — 开发中
		</div>
	);
}

export default function App() {
	const navigate = useNavigate();

	return (
		<ProLayout
			title="zPaw"
			logo="https://img.alicdn.com/imgextra/i1/O1CN01nTg6w21NqT5qFKH1u_!!6000000001621-55-tps-550-550.svg"
			route={{
				path: "/",
				routes: [
					{ path: "/agents", name: "聊天", icon: <WechatOutlined /> },
					{ path: "/tools", name: "工具", icon: <ToolOutlined /> },
					{ path: "/knowledge", name: "知识库", icon: <DatabaseOutlined /> },
					{ path: "/settings", name: "设置", icon: <SettingOutlined /> },
				],
			}}
			menuItemRender={(item, dom) => {
				const path = item.path || "/";
				return (
					<span
						onClick={() => navigate(path)}
						style={{ cursor: "pointer", display: "block", width: "100%" }}
					>
						{dom}
					</span>
				);
			}}
		>
			<Routes>
				<Route path="/" element={<Navigate to="/agents" replace />} />
				<Route path="/agents" element={<ChatPage />} />
				<Route path="/tools" element={<ToolsPage />} />
				<Route path="/knowledge" element={<KnowledgePage />} />
				<Route path="/settings" element={<Placeholder title="设置" />} />
			</Routes>
		</ProLayout>
	);
}
