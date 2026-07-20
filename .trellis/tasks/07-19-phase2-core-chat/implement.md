# Phase 2：核心对话对接 — 实施计划

**Goal:** 实现 HarnessAgent 启动 + SSE 流式聊天 + Ant Design X 对话界面

## Task 1: 复制 paw 后端核心代码

**Files to copy from `agentscope-java/agentscope-examples/agents/agentscope-paw/`:**

- `src/main/java/io/agentscope/claw2/runtime/ClawBootstrap.java` → 适配为 ZPawBootstrap
- `src/main/java/io/agentscope/claw2/runtime/config/*.java` → config 包
- `src/main/java/io/agentscope/claw2/runtime/gateway/HarnessGateway.java`
- `src/main/java/io/agentscope/claw2/runtime/session/*.java` → session 管理
- `src/main/java/io/agentscope/claw2/web/api/*Controller.java` → REST API

**Adaptation:** 将 `io.agentscope.claw2` 包路径映射到 `cn.zane`，去掉多租户相关代码

## Task 2: 启动 HarnessAgent

整合 ModelConfig 配置，在 ZPawBootstrap 中创建 HarnessAgent 实例。

## Task 3: SSE 流式聊天 API

实现 `POST /api/agents/{id}/chat/stream` 端点，返回 SSE 事件流。

## Task 4: 前端聊天面板

用 Ant Design X 的 `Conversation` + `Bubble` + `Sender` 替换 `Placeholder`。
