import { useState, useRef, useCallback, useEffect } from "react";
import { Bubble, Sender } from "@ant-design/x";
import { streamChat } from "../api/chat";

interface ToolEntry {
	name: string;
	input?: string;
	result?: string;
}

interface Message {
	key: string;
	role: "user" | "assistant";
	content: string;
	tools?: ToolEntry[];
}

let msgId = 0;

export default function ChatPage() {
	const [messages, setMessages] = useState<Message[]>([]);
	const [input, setInput] = useState("");
	const busyRef = useRef(false);
	const threadRef = useRef<HTMLDivElement>(null);

	// Auto-scroll when messages change
	useEffect(() => {
		if (threadRef.current) {
			threadRef.current.scrollTo({
				top: threadRef.current.scrollHeight,
				behavior: "smooth",
			});
		}
	}, [messages]);

	const handleSend = useCallback(async (text: string) => {
		if (!text.trim() || busyRef.current) return;
		busyRef.current = true;
		const userMsg: Message = {
			key: `u-${msgId++}`,
			role: "user",
			content: text,
		};
		const replyMsg: Message = {
			key: `a-${msgId++}`,
			role: "assistant",
			content: "",
			tools: [],
		};
		setMessages((prev) => [...prev, userMsg, replyMsg]);
		setInput("");

		try {
			for await (const evt of streamChat("default", text)) {
				if (evt.type === "token") {
					setMessages((prev) =>
						prev.map((m) =>
							m.key === replyMsg.key
								? { ...m, content: m.content + (evt.data ?? "") }
								: m,
						),
					);
				} else if (evt.type === "tool_call") {
					setMessages((prev) =>
						prev.map((m) =>
							m.key === replyMsg.key
								? {
										...m,
										tools: [
											...(m.tools ?? []),
											{
												name: evt.toolName ?? "unknown",
												input: evt.toolInput,
											},
										],
									}
								: m,
						),
					);
				} else if (evt.type === "tool_result") {
					setMessages((prev) =>
						prev.map((m) => {
							if (m.key !== replyMsg.key) return m;
							const tools = [...(m.tools ?? [])];
							for (let i = tools.length - 1; i >= 0; i--) {
								if (tools[i].name === evt.toolName && !tools[i].result) {
									tools[i] = { ...tools[i], result: evt.toolResult };
									return { ...m, tools };
								}
							}
							return m;
						}),
					);
				} else if (evt.type === "error") {
					setMessages((prev) =>
						prev.map((m) =>
							m.key === replyMsg.key
								? {
										...m,
										content:
											m.content + `\n\n[Error: ${evt.error ?? "unknown"}]`,
									}
								: m,
						),
					);
				}
			}
		} catch {
			setMessages((prev) =>
				prev.map((m) =>
					m.key === replyMsg.key
						? { ...m, content: m.content + "\n\n[Error: Connection failed]" }
						: m,
				),
			);
		} finally {
			busyRef.current = false;
		}
	}, []);

	return (
		<div
			style={{
				display: "flex",
				flexDirection: "column",
				height: "100%",
			}}
		>
			<div
				ref={threadRef}
				style={{
					flex: 1,
					overflow: "auto",
					padding: "16px 24px",
				}}
			>
				{messages.length === 0 && (
					<div
						style={{
							textAlign: "center",
							color: "#999",
							marginTop: 80,
							fontSize: 16,
						}}
					>
						开始与新会话
					</div>
				)}
				{messages.map((m) => (
					<Bubble
						key={m.key}
						placement={m.role === "user" ? "end" : "start"}
						content={m.content}
						avatar={m.role === "assistant" ? "Z" : undefined}
						footer={
							m.tools && m.tools.length > 0
								? m.tools.map((t, i) => (
										<div
											key={i}
											style={{
												fontSize: 12,
												color: "#666",
												marginTop: 4,
												padding: "4px 8px",
												background: "#f5f5f5",
												borderRadius: 6,
											}}
										>
											🔧 {t.name}
											{t.result ? " ✓" : " ..."}
										</div>
									))
								: undefined
						}
					/>
				))}
			</div>
			<div style={{ padding: "0 24px 24px", flexShrink: 0 }}>
				<Sender
					value={input}
					onChange={(v) => setInput(v)}
					onSubmit={handleSend}
					loading={busyRef.current}
					placeholder="输入消息..."
				/>
			</div>
		</div>
	);
}
