# zpaw 个人智能助手 — 设计文档

> 版本：1.0
> 日期：2026-01-15
> 状态：草稿 → 待评审

## 1. 概述

zpaw（Z's Paw）是一款基于 Alibaba AgentScope Java 框架构建的个人智能助手，运行在用户自己的机器上。它能通过自然语言交互，帮助用户管理知识、生成文档、调度任务，并通过多渠道（Web UI / 钉钉 / 企微 / 飞书）触达。

### 1.1 核心定位

- **个人助手**：单用户本地运行，不做多租户隔离
- **知识管理**：本地文档管理 + 全文检索（Phase 3 初版），后续升级语义搜索（Phase 4+）
- **工作流引擎**：定时任务 + 事件驱动
- **文档生成**：Office 文档（Word / Excel / PPT）生成
- **多渠道**：Web UI + 钉钉 + 企微 + 飞书 + GitHub + GitLab

### 1.2 设计原则

- **复用优先**：最大化复用 agentscope-paw 的成熟基建
- **工具即能力**：新功能优先实现为 Agent Tool，再按需提供 REST API
- **模块化单体**：按业务领域划分模块，每个模块自包含
- **渐进增强**：从核心对话开始，逐步叠加能力

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户机器 (local)                          │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  zpaw Spring Boot Backend                                │   │
│  │  ┌──────────┐ ┌────────────────────┐ ┌───────────────┐  │   │
│  │  │ Channels │ │ HarnessAgent       │ │ 自定义能力层   │  │   │
│  │  │ ├ chatui │ │ ├ LLM (多模型)     │ │ ├ 📚 RAG 知识库│  │   │
│  │  │ ├ dingtk │ │ ├ Skills·Subagnts │ │ ├ ⏰ 工作流    │  │   │
│  │  │ ├ wecom  │ │ ├ MCP 工具        │ │ └ 🔧 Z-Tools  │  │   │
│  │  │ └ github │ │ └ 自进化循环      │ └───────┬───────┘  │   │
│  │  └────┬─────┘ └────────┬──────────┘         │           │   │
│  │       └────────────────┼────────────────────┘           │   │
│  │                   REST + SSE                             │   │
│  └────────────────────────┼──────────────────────────────────┘   │
│                           │                                       │
│  ┌────────────────────────▼──────────────────────────────────┐   │
│  │  React + Ant Design X 前端                                │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │   │
│  │  │ Agent 管理│ │ 聊天面板 │ │ 知识库   │ │ 工作流   │     │   │
│  │  │  ProTable │ │ Convers. │ │ 上传搜索 │ │ Cron 配置│     │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## 3. 技术栈

### 3.1 后端

| 组件 | 技术 | 说明 |
| ------ | ------ | ------ |
| 语言 | Java 21 | 当前项目已配置 |
| 框架 | Spring Boot 4.1+ + WebFlux | 响应式 HTTP + SSE |
| Agent 框架 | AgentScope Java 2.0 | agentscope-harness + core |
| 安全 | Spring Security (随 Boot BOM) | 仅用于 CORS + permit-all |
| 构建 | Maven | |

### 3.2 前端

| 组件 | 技术 | 说明 |
| ------ | ------ | ------ |
| 框架 | React 19 + TypeScript 5 | |
| 构建 | Vite 6 | |
| UI 库 | Ant Design X 2.8.0 + Ant Design Pro (latest) | AI 聊天组件 + 布局/表格 |
| 路由 | React Router 6 | |
| 通讯 | REST + SSE Streaming | |

### 3.3 模型提供商（多模型支持）

| 提供商 | 扩展模块 | 说明 |
| -------- | ---------- | ------ |
| 阿里 DashScope | agentscope-extensions-model-dashscope | 通义千问系列（默认） |
| OpenAI | agentscope-extensions-model-openai | GPT 系列 |
| Ollama | agentscope-extensions-model-ollama | 本地模型（可选） |
| Anthropic | agentscope-extensions-model-anthropic | Claude 系列（可选） |
| Gemini | agentscope-extensions-model-gemini | Gemini 系列（可选） |

## 4. 模块设计

### 4.1 模块划分

```
cn.zane
├── ZPaw.java                          # Spring Boot 入口
│
├── bootstrap/                          # 应用组装层
│   ├── ZPawBootstrap.java              # 基于 ClawBootstrap 的启动器
│   └── config/                         # Agent 配置
│       ├── ApplicationConfig.java      # 全局配置
│       └── ...
│
├── agent/                              # Agent 层
│   ├── config/
│   │   ├── ZPawAgentConfig.java        # 默认 Agent 配置
│   │   └── ModelConfig.java            # 多模型配置
│   ├── tools/                          # 🆕 自定义工具集
│   │   ├── DocumentTool.java           # 文档生成（docx/xlsx/pptx）
│   │   ├── KnowledgeTool.java          # 知识库搜索
│   │   ├── WorkflowTool.java           # 工作流管理
│   │   └── SystemTool.java             # 系统操作（文件/Shell）
│   └── factory/
│       └── ZPawAgentFactory.java       # Agent 工厂
│
├── knowledge/                          # 🆕 知识库模块（领域模块）
│   ├── controller/
│   │   └── KnowledgeController.java    # REST API
│   ├── service/
│   │   ├── KnowledgeService.java       # 业务逻辑
│   │   └── IndexService.java           # 索引服务
│   ├── model/
│   │   ├── Document.java               # 文档聚合
│   │   ├── IndexStatus.java            # 索引状态
│   │   └── SearchResult.java           # 搜索结果
│   └── repository/
│       └── KnowledgeRepository.java    # 数据持久化
│
├── workflow/                           # 🆕 工作流模块（领域模块）
│   ├── controller/
│   │   └── WorkflowController.java     # REST API
│   ├── service/
│   │   ├── WorkflowService.java        # 业务逻辑
│   │   └── SchedulerService.java       # Cron 调度
│   ├── model/
│   │   ├── WorkflowTask.java           # 任务定义
│   │   ├── TaskLog.java                # 执行日志
│   │   └── TriggerType.java            # 触发类型
│   └── repository/
│       └── WorkflowRepository.java
│
└── web/                                # 接入层（继承 paw）
    ├── api/                            # REST Controllers
    │   ├── AgentController.java
    │   ├── ChatController.java
    │   ├── SessionController.java
    │   ├── ChannelController.java
    │   ├── TemplateController.java
    │   └── ToolController.java
    ├── config/
    │   ├── SecurityConfig.java
    │   └── WebConfig.java
    ├── scaffold/
    │   └── WorkspaceScaffolder.java
    └── session/
        └── ...
```

### 4.2 模块职责

| 模块 | 职责 | 依赖 |
| ------ | ------ | ------ |
| `bootstrap` | 应用生命周期管理、Agent 组装 | agentscope-harness |
| `agent` | Agent 配置、自定义工具注册 | bootstrap, knowledge, workflow |
| `knowledge` | 文档索引、语义搜索、知识管理 | 无（独立模块） |
| `workflow` | Cron 任务调度、事件驱动、执行日志 | 无（独立模块） |
| `web` | REST API、SSE Streaming、频道管理 | 所有模块 |

## 5. 前端设计

### 5.1 页面路由

```
/                                    → 重定向到 /agents
/agents                              → Agent Hub（Agent 列表）
/agents/new                          → 创建 Agent（模板/AI 草稿）
/agents/:id                          → Agent 详情（Tab 布局）
  /agents/:id/chat                   → 💬 聊天面板
  /agents/:id/tools                  → 🛠️ 工具 + MCP 管理
  /agents/:id/knowledge              → 📚 知识库绑定
  /agents/:id/workflows              → ⏰ 工作流配置
  /agents/:id/workspace              → 📁 文件浏览器
  /agents/:id/skills                 → 📝 自进化技能
  /agents/:id/subagents              → 👥 子智能体
  /agents/:id/channels               → 🔗 频道绑定
  /agents/:id/sessions               → 📋 会话历史
  /agents/:id/sessions/:key          → 会话详情
  /agents/:id/settings               → ⚙️ 设置
/knowledge                           → 📚 全局知识库管理
/workflows                           → ⏰ 全局工作流管理
/channels                            → 🔌 频道管理
/channels/:id                        → 频道详情
```

### 5.2 关键组件

**AppShell** — 主布局

- 基于 Ant Design ProLayout
- 左侧导航栏
- 顶部栏（搜索、状态）

**ChatPanel** — 聊天面板（核心）

- 基于 Ant Design X 的 `Conversation` + `Bubble` + `Sender`
- SSE 流式接收消息
- `ThoughtChain` 展示工具调用过程
- 会话管理（新建/切换/历史）

**ToolManager** — 工具管理

- 工具列表（ProTable）
- MCP 服务器配置
- 工具启用/禁用

## 6. 新增 API 设计

### 6.1 知识库 API

```
POST   /api/knowledge/upload          ← 上传文档
POST   /api/knowledge/search          ← 语义搜索
GET    /api/knowledge/index           ← 获取索引状态
DELETE /api/knowledge/:id             ← 删除文档
```

### 6.2 工作流 API

```
GET    /api/workflows                 ← 任务列表
POST   /api/workflows                 ← 创建任务
PUT    /api/workflows/:id             ← 更新任务
DELETE /api/workflows/:id             ← 删除任务
GET    /api/workflows/:id/logs        ← 执行日志
```

### 6.3 Agent Tool 定义

文档生成采用 Apache POI 自研 Tool + 模板填充模式。MCP 文档服务作为后续可选项。

```java
@Tool(name = "generate_docx",
       description = "Generate a Word document from template and data")
public String generateDocx(
    @ToolParam(name = "template", description = "Template name or path")
    String template,
    @ToolParam(name = "data", description = "JSON data to fill in")
    String data,
    @ToolParam(name = "output", description = "Output filename")
    String output
) { ... }
```

## 7. 实施路线图

### Phase 1：基座搭建（预估 1-2 天）

| 步骤 | 产出 |
| ------ | ------ |
| 1.1 本地编译 agentscope-java | `mvn -DskipTests install` 成功 |
| 1.2 搭建 zpaw 后端骨架 | 项目结构建立，引入 paw 核心代码 |
| 1.3 搭建前端脚手架 | Vite + React + Ant Design X 项目初始化 |
| 1.4 实现 AppShell + 路由 | ProLayout 布局，页面路由 |
| **里程碑** | **前端能正常浏览页面结构** |

### Phase 2：核心对话（预估 2-3 天）

| 步骤 | 产出 |
| ------ | ------ |
| 2.1 对接 Agent 启动 | 后端能启动 HarnessAgent |
| 2.2 实现 SSE 流式聊天 | Ant Design X Conversation + Sender |
| 2.3 ThoughtChain 工具展示 | 工具调用链可视化 |
| 2.4 会话管理 | 新建/切换/历史对话 |
| **里程碑** | **能通过 Web UI 与 Agent 正常对话** |

### Phase 3：工具 + 知识库（预估 3-5 天）

| 步骤 | 产出 |
| ------ | ------ |
| 3.1 文档生成工具（Apache POI） | generate_docx/xlsx/pptx，模板填充模式 |
| 3.2 工具管理页面 | Tool Catalog + MCP 配置 |
| 3.3 RAG 知识库核心 | 文档上传 + 全文检索（Lucene/BM25），不依赖向量库 |
| 3.4 知识库管理页面 | 上传/关键字搜索/索引状态管理 |
| 3.5 Skills / Sub-agents 页面 | 技能和子智能体管理 |
| **里程碑** | **Agent 能调工具生成文档、搜索知识** |

### Phase 4：工作流 + 完善（预估 3-5 天）

| 步骤 | 产出 |
| ------ | ------ |
| 4.1 定时工作流引擎 | Cron 调度 + 事件驱动 |
| 4.2 工作流管理页面 | 任务配置 + 执行日志 |
| 4.3 IM 频道绑定页面 | 钉钉/企微/飞书配置 |
| 4.4 会话历史管理 | 完整会话记录查看 |
| **里程碑** | **完整功能可用的个人助手** |

## 8. 依赖管理

### Maven 依赖

```xml
<!-- AgentScope 核心 -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-harness</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- 模型扩展（多选） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-model-dashscope</artifactId>
</dependency>
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-model-openai</artifactId>
</dependency>
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-model-ollama</artifactId>
</dependency>

<!-- IM 频道（可选） -->
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-channel-dingtalk</artifactId>
</dependency>
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-channel-wecom</artifactId>
</dependency>
<dependency>
    <groupId>io.agentscope</groupId>
    <artifactId>agentscope-extensions-channel-feishu</artifactId>
</dependency>

<!-- 文档生成 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>
```

## 9. 质量要求

- 所有新增代码通过 `mvn compile` 编译
- 前端通过 `npm run build` 构建
- SSE 流式响应延迟 < 500ms（首 token）
- 知识库全文搜索响应 < 2s
- 前端遵循 Ant Design 设计规范

## 10. 附录

### A. 与 agentscope-paw 的差异对比

| 维度 | agentscope-paw | zpaw |
| ------ | --------------- | ------ |
| 前端 UI | 纯手写 React + CSS | Ant Design X 2.8.0 + Pro |
| 知识管理 | 无 | 全文检索 (Phase 3) → 向量 RAG (Phase 4+) |
| 文档生成 | 无 | Apache POI Tool (初版) → MCP 扩展 (后期) |
| 定时工作流 | 无 | Cron 任务引擎 |
| 模型支持 | 单一 DashScope | 多模型可切换 |
| 项目结构 | 内置在 agentscope-examples | 独立 Maven 项目 |

### B. 术语表

| 术语 | 说明 |
| ------ | ------ |
| HarnessAgent | AgentScope 的生产级 Agent 实现 |
| SSE | Server-Sent Events，流式推送 |
| MCP | Model Context Protocol，模型上下文协议 |
| RAG | Retrieval-Augmented Generation，检索增强生成 |
| Tool Calling | Agent 调用外部工具的能力 |
| Channel | IM 平台接入通道 |
