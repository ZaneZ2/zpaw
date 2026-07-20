import { useState, useRef, useCallback, useEffect } from "react";
import { Bubble, Sender, ThoughtChain } from "@ant-design/x";
import { Button, Drawer, List, Tag, Typography } from "antd";
import {
  HistoryOutlined,
  PlusOutlined,
  CheckCircleOutlined,
  LoadingOutlined,
} from "@ant-design/icons";
import { streamChat } from "../api/chat";
import { inbox, currentSession, type InboxEntry } from "../api/sessions";

interface ToolEntry {
  name: string;
  status: "loading" | "success" | "error";
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
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [sessionList, setSessionList] = useState<InboxEntry[]>([]);
  const busyRef = useRef(false);
  const threadRef = useRef<HTMLDivElement>(null);

  // Auto-scroll
  useEffect(() => {
    if (threadRef.current) {
      threadRef.current.scrollTo({
        top: threadRef.current.scrollHeight,
        behavior: "smooth",
      });
    }
  }, [messages]);

  // Load session list
  useEffect(() => {
    (async () => {
      try {
        const cur = await currentSession("default");
        if (!cur.exists) return;
        const list = await inbox("default");
        setSessionList(list);
      } catch {
        // first visit, no sessions yet
      }
    })();
  }, []);

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
                      { name: evt.toolName ?? "unknown", status: "loading" },
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
                if (
                  tools[i].name === evt.toolName &&
                  tools[i].status === "loading"
                ) {
                  tools[i] = { ...tools[i], status: "success" };
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

  const handleNewChat = useCallback(async () => {
    setMessages([]);
  }, []);

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100%" }}>
      {/* Header bar */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "8px 24px",
          borderBottom: "1px solid #f0f0f0",
          flexShrink: 0,
        }}
      >
        <Typography.Text type="secondary" style={{ fontSize: 13 }}>
          zpaw · 默认会话
        </Typography.Text>
        <div style={{ display: "flex", gap: 8 }}>
          <Button
            size="small"
            icon={<PlusOutlined />}
            onClick={handleNewChat}
          >
            新对话
          </Button>
          <Button
            size="small"
            icon={<HistoryOutlined />}
            onClick={async () => {
              try {
                const list = await inbox("default");
                setSessionList(list);
              } catch {
                /* ignore */
              }
              setDrawerOpen(true);
            }}
          >
            历史
          </Button>
        </div>
      </div>

      {/* Messages */}
      <div
        ref={threadRef}
        style={{ flex: 1, overflow: "auto", padding: "16px 24px" }}
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
              m.tools && m.tools.length > 0 ? (
                <div style={{ marginTop: 8 }}>
                  <ThoughtChain
                    items={m.tools.map((t) => ({
                      key: t.name,
                      title: t.name,
                      status: t.status,
                      icon:
                        t.status === "success" ? (
                          <CheckCircleOutlined style={{ color: "#52c41a" }} />
                        ) : t.status === "loading" ? (
                          <LoadingOutlined />
                        ) : undefined,
                    }))}
                  />
                </div>
              ) : undefined
            }
          />
        ))}
      </div>

      {/* Input */}
      <div style={{ padding: "0 24px 24px", flexShrink: 0 }}>
        <Sender
          value={input}
          onChange={(v) => setInput(v)}
          onSubmit={handleSend}
          loading={busyRef.current}
          placeholder="输入消息..."
        />
      </div>

      {/* Session history drawer */}
      <Drawer
        title="会话历史"
        placement="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={360}
      >
        {sessionList.length === 0 ? (
          <Typography.Text type="secondary">暂无历史会话</Typography.Text>
        ) : (
          <List
            dataSource={sessionList}
            renderItem={(item) => (
              <List.Item
                style={{ cursor: "pointer" }}
                onClick={() => setDrawerOpen(false)}
              >
                <List.Item.Meta
                  title={
                    <span>
                      {item.label ?? item.sessionKey.slice(0, 16)}
                      {item.unread && (
                        <Tag color="blue" style={{ marginLeft: 8 }}>
                          未读
                        </Tag>
                      )}
                    </span>
                  }
                  description={
                    item.lastMessage ?? new Date(item.lastActivityMs).toLocaleString()
                  }
                />
              </List.Item>
            )}
          />
        )}
      </Drawer>
    </div>
  );
}
