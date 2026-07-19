# zpaw Phase 2: 核心对话对接 — 实施计划

**Goal:** 实现 HarnessAgent 启动 + SSE 流式聊天 + Ant Design X 对话界面

**Architecture:** ZPawBootstrap 启动 HarnessAgent，ChatController 提供 SSE 流式 API，前端用 Ant Design X 的 Conversation + Bubble + Sender

**Tech Stack:** Java 21, Spring Boot 4.1+ WebFlux, AgentScope 2.0.0, React 19, Ant Design X 2.8.0

## Global Constraints

- HarnessAgent 通过 ModelRegistry 解析 `ZPAW_DEFAULT_MODEL` 环境变量
- SSE 流式端点路径: `POST /api/agents/{agentId}/chat/stream`
- 前端构建输出到 `src/main/resources/static`
- 包基础路径: `cn.zane.*`

---

### Task 1: HarnessAgent 启动对接

**Files:**

- Modify: `src/main/java/cn/zane/bootstrap/ZPawBootstrap.java`
- Modify: `src/main/java/cn/zane/bootstrap/config/ModelConfig.java`
- Create: `src/main/java/cn/zane/web/api/ChatController.java`
- Create: `src/main/java/cn/zane/web/api/AgentController.java`

- [ ] **Step 1: 更新 ZPawBootstrap 启动 HarnessAgent**

```java
package cn.zane.bootstrap;

import cn.zane.bootstrap.config.ModelConfig;
import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class ZPawBootstrap {
    private HarnessAgent defaultAgent;

    @PostConstruct
    public void init() {
        log.info("Initializing HarnessAgent...");
        defaultAgent = HarnessAgent.builder()
                .name("zpaw")
                .sysPrompt("You are a helpful local assistant named zpaw. " +
                           "Answer accurately and concisely.")
                .model("dashscope:qwen-plus")
                .build();
        log.info("HarnessAgent initialized");
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down HarnessAgent");
    }
}
```

- [ ] **Step 2: 创建 ChatController (SSE 流式)**

```java
package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.UserMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/agents")
public class ChatController {
    private final ZPawBootstrap bootstrap;

    public ChatController(ZPawBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @PostMapping(value = "/{agentId}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@PathVariable String agentId, @RequestBody ChatRequest req) {
        var agent = bootstrap.getDefaultAgent();
        return agent.streamEvents(new UserMessage(req.message()))
                .map(event -> {
                    if (event instanceof TextBlockDeltaEvent e) {
                        return "data: {\"type\":\"token\",\"data\":\"" +
                               escape(e.getDelta()) + "\"}\n\n";
                    }
                    return "";
                })
                .concatWithValues("data: {\"type\":\"done\"}\n\n");
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public record ChatRequest(String message) {}
}
```

- [ ] **Step 3: 创建 AgentController (列表/详情)**

```java
package cn.zane.web.api;

import cn.zane.bootstrap.ZPawBootstrap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {
    private final ZPawBootstrap bootstrap;

    public AgentController(ZPawBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @GetMapping
    public Mono<List<Map<String, Object>>> list() {
        var agent = bootstrap.getDefaultAgent();
        return Mono.just(List.of(Map.of(
            "id", "default",
            "name", "zpaw",
            "builtin", true
        )));
    }

    @GetMapping("/{id}")
    public Mono<Map<String, Object>> get(@PathVariable String id) {
        return Mono.just(Map.of(
            "id", id,
            "name", "zpaw",
            "builtin", true
        ));
    }
}
```

- [ ] **Step 4: 编译验证**

```bash
mvn clean compile -DskipTests
```

预期: BUILD SUCCESS

---

### Task 2: 前端聊天面板对接

**Files:**

- Create: `frontend/src/api/chat.ts`
- Create: `frontend/src/api/agents.ts`
- Create: `frontend/src/pages/ChatPage.tsx`
- Modify: `frontend/src/App.tsx`

- [ ] **Step 1: 创建 `frontend/src/api/chat.ts`**

```typescript
// SSE streaming chat API
export interface ChatEvent {
  type: 'token' | 'tool_call' | 'tool_result' | 'done' | 'error';
  data?: string;
  toolName?: string;
  toolInput?: string;
  toolResult?: string;
  error?: string;
}

export async function* streamChat(
  agentId: string,
  message: string,
): AsyncGenerator<ChatEvent> {
  const res = await fetch(`/api/agents/${encodeURIComponent(agentId)}/chat/stream`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message }),
  });
  if (!res.ok || !res.body) throw new Error(`Chat stream failed: ${res.status}`);

  const reader = res.body.getReader();
  const dec = new TextDecoder();
  let buf = '';

  while (true) {
    const { value, done } = await reader.read();
    if (done) break;
    buf += dec.decode(value, { stream: true });
    let idx;
    while ((idx = buf.indexOf('\n\n')) >= 0) {
      const evt = buf.slice(0, idx);
      buf = buf.slice(idx + 2);
      const lines = evt.split('\n');
      let data = '';
      for (const ln of lines) if (ln.startsWith('data:')) data += ln.slice(5).trim();
      if (!data) continue;
      try {
        yield JSON.parse(data) as ChatEvent;
      } catch {
        yield { type: 'token', data } as ChatEvent;
      }
    }
  }
}
```

- [ ] **Step 2: 创建 `frontend/src/api/agents.ts`**

```typescript
export interface AgentDefinition {
  id: string;
  name: string;
  builtin: boolean;
}

export async function listAgents(): Promise<AgentDefinition[]> {
  const res = await fetch('/api/agents');
  if (!res.ok) throw new Error(`Failed to list agents: ${res.status}`);
  return res.json();
}
```

- [ ] **Step 3: 创建 `frontend/src/pages/ChatPage.tsx`**

```tsx
import { useState, useRef, useEffect, useCallback } from 'react';
import { Conversation, Bubble, Sender } from '@ant-design/x';
import { streamChat, ChatEvent } from '../api/chat';

interface Message {
  key: string;
  role: 'user' | 'assistant';
  content: string;
}

let msgId = 0;

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [busy, setBusy] = useState(false);
  const [input, setInput] = useState('');

  const handleSend = useCallback(async (text: string) => {
    if (!text.trim() || busy) return;
    setBusy(true);
    const userMsg: Message = { key: `u-${msgId++}`, role: 'user', content: text };
    const replyMsg: Message = { key: `a-${msgId++}`, role: 'assistant', content: '' };
    setMessages(prev => [...prev, userMsg, replyMsg]);
    setInput('');

    try {
      for await (const evt of streamChat('default', text)) {
        if (evt.type === 'token') {
          setMessages(prev => prev.map(m =>
            m.key === replyMsg.key
              ? { ...m, content: m.content + (evt.data ?? '') }
              : m
          ));
        }
      }
    } catch (e) {
      setMessages(prev => prev.map(m =>
        m.key === replyMsg.key
          ? { ...m, content: m.content + '\n[Error: connection failed]' }
          : m
      ));
    } finally {
      setBusy(false);
    }
  }, [busy]);

  const conversationItems = messages.map(m => ({
    key: m.key,
    role: m.role,
    content: m.content,
  }));

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Conversation
        style={{ flex: 1, overflow: 'auto', padding: 16 }}
        items={conversationItems.map(m => ({
          key: m.key,
          role: m.role === 'user' ? 'user' : 'assistant',
          content: m.content,
        }))}
      />
      <Sender
        style={{ flexShrink: 0 }}
        value={input}
        onChange={setInput}
        onSubmit={handleSend}
        loading={busy}
        placeholder="输入消息..."
      />
    </div>
  );
}
```

- [ ] **Step 4: 更新 `frontend/src/App.tsx`**

```tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import ProLayout from '@ant-design/pro-layout';
import { WechatOutlined, ToolOutlined, SettingOutlined } from '@ant-design/icons';
import ChatPage from './pages/ChatPage';

export default function App() {
  return (
    <ProLayout
      title="zPaw"
      logo="https://img.alicdn.com/imgextra/i1/O1CN01nTg6w21NqT5qFKH1u_!!6000000001621-55-tps-550-550.svg"
      route={{
        path: '/',
        routes: [
          { path: '/agents', name: '聊天', icon: <WechatOutlined /> },
          { path: '/tools', name: '工具', icon: <ToolOutlined /> },
          { path: '/settings', name: '设置', icon: <SettingOutlined /> },
        ],
      }}
      menuItemRender={(item, dom) => <a href={item.path || '/'}>{dom}</a>}
    >
      <Routes>
        <Route path="/" element={<Navigate to="/agents" replace />} />
        <Route path="/agents" element={<ChatPage />} />
        <Route path="/tools" element={<div style={{ padding: 48, textAlign: 'center', color: '#999' }}>工具管理 — 开发中</div>} />
        <Route path="/settings" element={<div style={{ padding: 48, textAlign: 'center', color: '#999' }}>设置 — 开发中</div>} />
      </Routes>
    </ProLayout>
  );
}
```

- [ ] **Step 5: 前端构建验证**

```bash
cd frontend && npm run build
```

预期: 构建成功，输出到 static 目录

---

### Task 3: 集成验证

- [ ] 完整构建: `mvn clean package -DskipTests`
- [ ] 验证 JAR 可启动
- [ ] 提交代码
