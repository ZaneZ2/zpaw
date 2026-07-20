# zpaw Phase 1: 基座搭建 — 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 zpaw 项目骨架，后端能启动 HarnessAgent，前端能展示页面结构

**架构：** 模块化单体，后端基于 agentscope-harness 2.0.0 (Maven Central)，前端基于 Vite + React 19 + Ant Design X 2.8.0

**Tech Stack:** Java 21, Spring Boot 4.1+, WebFlux, AgentScope 2.0.0, React 19, Ant Design X 2.8.0, Vite 6

## Global Constraints

- Java 21 编译目标 (pom.xml 已配置)
- Spring Boot 4.1+ + WebFlux (采用 reactive stack)
- AgentScope 2.0.0 从 Maven Central 拉取，不编译本地项目
- 前端 Vite 构建输出到 `src/main/resources/static`
- 前端开发时通过 Vite proxy 将 `/api` 代理到 `http://localhost:8080`
- 包基础路径：`cn.zane.*`（zpaw 特定代码），可保留 `io.agentscope.claw2.*`（paw 复用代码）

---

### Task 1: 清理并更新项目 pom.xml

**Files:**

- Modify: `pom.xml`
- Create: `.mvn/jvm.config`（可选，统一 JVM 参数）

**Interfaces:**

- Consumes: 现有 pom.xml（已有 agentscope 2.0.0 + Spring Boot 4.1.0 配置）
- Produces: 编译通过的可构建项目

- [ ] **Step 1: 确认 agentscope 版本可用**

验证 Maven Central 上 `io.agentscope:agentscope-harness:2.0.0` 可用：

```bash
mvn dependency:resolve -pl . -am 2>&1 | grep "agentscope"
```

预期：能正常解析所有 agentscope 依赖

- [ ] **Step 2: 更新 pom.xml 添加 Apache POI 依赖（为 Phase 3 文档工具准备）**

在 `<dependencies>` 中添加：

```xml
<!-- 文档生成 (Phase 3) -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>
```

- [ ] **Step 3: 验证编译**

```bash
mvn clean compile -DskipTests
```

预期：BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add pom.xml
git commit -m "chore: update pom with Apache POI dependency"
```

---

### Task 2: 搭建后端模块结构（骨架）

**Files:**

- Create: `src/main/java/cn/zane/bootstrap/ZPawBootstrap.java`
- Create: `src/main/java/cn/zane/bootstrap/config/ApplicationConfig.java`
- Create: `src/main/java/cn/zane/bootstrap/config/ModelConfig.java`
- Create: `src/main/java/cn/zane/agent/config/ZPawAgentConfig.java`
- Create: `src/main/java/cn/zane/agent/factory/ZPawAgentFactory.java`
- Create: `src/main/java/cn/zane/web/config/SecurityConfig.java`
- Create: `src/main/java/cn/zane/web/config/WebConfig.java`
- Create: `src/main/java/cn/zane/knowledge/controller/KnowledgeController.java`
- Create: `src/main/java/cn/zane/knowledge/service/KnowledgeService.java`
- Create: `src/main/java/cn/zane/workflow/controller/WorkflowController.java`
- Create: `src/main/java/cn/zane/workflow/service/WorkflowService.java`

**Interfaces:**

- Consumes: agentscope-harness 的 HarnessAgent, ModelRegistry
- Produces: 模块包结构就绪，各 Controller 为 stub

- [ ] **Step 1: 创建 `ZPawBootstrap.java` — 基于 paw 的 ClawBootstrap 简化版**

```java
package cn.zane.bootstrap;

import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * zpaw 应用启动器。在 Spring Boot 就绪后初始化 HarnessAgent。
 *
 * <p>Phase 1: 创建默认 Agent，不做通道绑定。后续阶段逐步启用 IM 通道和自定义能力。
 */
@Component
public class ZPawBootstrap {

    private static final Logger log = LoggerFactory.getLogger(ZPawBootstrap.class);

    private HarnessAgent defaultAgent;

    @PostConstruct
    public void init() {
        log.info("ZPawBootstrap initialized — agent scaffolding ready");
        // Phase 2: 在此创建 HarnessAgent 实例
    }

    public HarnessAgent getDefaultAgent() {
        return defaultAgent;
    }
}
```

- [ ] **Step 2: 创建 `ApplicationConfig.java`**

```java
package cn.zane.bootstrap.config;

import org.springframework.context.annotation.Configuration;

/** 全局应用配置。Spring Bean 定义集中在这里。 */
@Configuration
public class ApplicationConfig {
    // Phase 2+ 逐步添加 Bean
}
```

- [ ] **Step 3: 创建 `ModelConfig.java`**

```java
package cn.zane.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 多模型配置。支持从 application.yaml 读取多个模型定义。 */
@Configuration
@ConfigurationProperties(prefix = "zpaw.model")
public class ModelConfig {

    /** 默认模型 ID */
    private String defaultModel = "dashscope:qwen-plus";

    /** 可用模型列表 */
    private java.util.List<ModelEntry> providers = new java.util.ArrayList<>();

    // getter / setter
    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String v) { this.defaultModel = v; }
    public java.util.List<ModelEntry> getProviders() { return providers; }
    public void setProviders(java.util.List<ModelEntry> v) { this.providers = v; }

    public static class ModelEntry {
        private String id;
        private String apiKey;
        private String modelName;
        private boolean stream = true;
        // getter / setter
        public String getId() { return id; }
        public void setId(String v) { this.id = v; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String v) { this.apiKey = v; }
        public String getModelName() { return modelName; }
        public void setModelName(String v) { this.modelName = v; }
        public boolean isStream() { return stream; }
        public void setStream(boolean v) { this.stream = v; }
    }
}
```

- [ ] **Step 4: 创建 stub Controller**

```java
// KnowledgeController.java
package cn.zane.knowledge.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @PostMapping("/upload")
    public Mono<String> upload() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }

    @PostMapping("/search")
    public Mono<String> search() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }
}
```

```java
// WorkflowController.java
package cn.zane.workflow.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    @GetMapping
    public Mono<String> list() {
        return Mono.just("[]");
    }
}
```

- [ ] **Step 5: 创建 SecurityConfig 和 WebConfig**

```java
// SecurityConfig.java
package cn.zane.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(e -> e.anyExchange().permitAll())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }
}
```

```java
// WebConfig.java
package cn.zane.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
```

- [ ] **Step 6: 编译验证**

```bash
mvn clean compile -DskipTests
```

预期: BUILD SUCCESS，所有 stub 类编译通过

- [ ] **Step 7: Commit**

```bash
git add src/main/java/cn/zane/
git commit -m "feat: add backend module scaffolding with stub controllers"
```

---

### Task 3: 更新 application.yaml

**Files:**

- Modify: `src/main/resources/application.yaml`

**Interfaces:**

- Consumes: 现有 application.yaml
- Produces: 按 zpaw 命名规范更新的配置

- [ ] **Step 1: 重写 application.yaml**

```yaml
server:
  port: ${ZPAW_PORT:9426}

spring:
  application:
    name: zpaw

zpaw:
  home: ${ZPAW_HOME:#{systemProperties['user.home']}/.zpaw}
  agent:
    name: ${ZPAW_AGENT_NAME:zpaw}
    sys-prompt: >
      You are a helpful local assistant named zpaw. Answer accurately and concisely.
      You have access to tools for file operations, document generation, and knowledge search.
      When generating documents, create them in your workspace directory.
  model:
    default-model: ${ZPAW_DEFAULT_MODEL:dashscope:qwen-plus}
    providers:
      - id: dashscope
        api-key: ${DASHSCOPE_API_KEY:}
        model-name: ${ZPAW_MODEL_NAME:qwen-plus}
        stream: true

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    cn.zane: DEBUG
    io.agentscope: INFO
```

- [ ] **Step 2: 编译验证**

```bash
mvn clean compile -DskipTests
```

预期: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application.yaml
git commit -m "feat: update application.yaml for zpaw"
```

---

### Task 4: 搭建前端脚手架（Vite + React + Ant Design X）

**Files:**

- Create: `frontend/package.json`
- Create: `frontend/tsconfig.json`
- Create: `frontend/tsconfig.node.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/index.html`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/App.tsx`
- Create: `frontend/src/vite-env.d.ts`

**Interfaces:**

- Consumes: 无
- Produces: 可启动的 React 前端项目，集成 Ant Design X 2.8.0

- [ ] **Step 1: 创建 `package.json`**

```json
{
  "name": "zpaw-frontend",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext ts,tsx"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^7.0.0",
    "@ant-design/x": "^2.8.0",
    "@ant-design/pro-layout": "^7.0.0",
    "@ant-design/pro-components": "^2.0.0",
    "antd": "^5.22.0",
    "@ant-design/icons": "^5.5.0"
  },
  "devDependencies": {
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@vitejs/plugin-react": "^4.3.0",
    "typescript": "^5.7.0",
    "vite": "^6.0.0"
  }
}
```

- [ ] **Step 2: 创建 `vite.config.ts`**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9426',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 3: 创建 `tsconfig.json`**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "isolatedModules": true,
    "moduleDetection": "force",
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedSideEffectImports": true
  },
  "include": ["src"]
}
```

- [ ] **Step 4: 创建 `index.html`**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>zPaw - Personal Agent</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

- [ ] **Step 5: 创建 `src/vite-env.d.ts`**

```typescript
/// <reference types="vite/client" />
```

- [ ] **Step 6: 创建 `src/main.tsx`（最简入口）**

```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ConfigProvider>
  </React.StrictMode>,
);
```

- [ ] **Step 7: 创建 `src/App.tsx`（最简单的路由 + ProLayout）**

```tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import ProLayout from '@ant-design/pro-layout';
import { ToolOutlined, WechatOutlined, SettingOutlined } from '@ant-design/icons';

function Placeholder({ title }: { title: string }) {
  return (
    <div style={{ padding: 48, textAlign: 'center', color: '#999', fontSize: 18 }}>
      {title} — 开发中
    </div>
  );
}

function Dashboard() {
  return <Placeholder title="zPaw 仪表盘" />;
}

export default function App() {
  return (
    <ProLayout
      title="zPaw"
      logo="https://img.alicdn.com/imgextra/i1/O1CN01nTg6w21NqT5qFKH1u_!!6000000001621-55-tps-550-550.svg"
      route={{
        path: '/',
        routes: [
          { path: '/agents', name: 'Agents', icon: <WechatOutlined /> },
          { path: '/tools', name: '工具', icon: <ToolOutlined /> },
          { path: '/settings', name: '设置', icon: <SettingOutlined /> },
        ],
      }}
      menuItemRender={(item, dom) => (
        <a href={item.path || '/'}>{dom}</a>
      )}
    >
      <Routes>
        <Route path="/" element={<Navigate to="/agents" replace />} />
        <Route path="/agents" element={<Dashboard />} />
        <Route path="/tools" element={<Placeholder title="工具管理" />} />
        <Route path="/settings" element={<Placeholder title="设置" />} />
      </Routes>
    </ProLayout>
  );
}
```

- [ ] **Step 8: 安装依赖并验证构建**

```bash
cd frontend
npm install
npm run build
```

预期: 构建成功，`src/main/resources/static/` 下生成产物

- [ ] **Step 9: Commit**

```bash
git add frontend/
git commit -m "feat: scaffold frontend with Vite + React + Ant Design X"
```

---

### Task 5: 集成验证 — 启动并访问

**Files:**

- Modify: `src/main/java/cn/zane/ZPaw.java`
- No new files

**Interfaces:**

- Consumes: 所有 Task 1-4 的产出
- Produces: 可启动的应用

- [ ] **Step 1: 更新 ZPaw.java 添加日志确认**

```java
package cn.zane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZPaw {
    private static final Logger log = LoggerFactory.getLogger(ZPaw.class);

    public static void main(String[] args) {
        log.info("Starting zPaw...");
        SpringApplication.run(ZPaw.class, args);
    }
}
```

- [ ] **Step 2: 完整构建**

```bash
mvn clean package -DskipTests
```

预期: BUILD SUCCESS（前后端都构建成功）

- [ ] **Step 3: 启动应用并验证**

```bash
java -jar target/zpaw-1.0.0-SNAPSHOT.jar
```

验证:

1. 访问 `http://localhost:9426/` → 能看到 ProLayout 布局页面
2. 访问 `http://localhost:9426/actuator/health` → 返回 `{"status":"UP"}`
3. 导航栏能点击切换页面

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: initial working zpaw application with frontend shell"
```

---

## Phase 1 完成检查清单

- [ ] `mvn clean package -DskipTests` 编译通过
- [ ] 后端启动无异常日志
- [ ] 前端页面通过 Spring Boot 托管正常访问
- [ ] ProLayout 布局正确，路由能切换
- [ ] Actuator health endpoint 正常
- [ ] 代码已 commit

## 下一步（Phase 2 预览）

完成 Phase 1 后，Phase 2 将：

1. 从 agentscope-paw 复制 ClawBootstrap + Controller 层代码
2. 实现 HarnessAgent 启动和 SSE 流式聊天
3. 用 Ant Design X 的 Conversation + Bubble + Sender 替换目前的前端占位符
4. 实现真实的聊天页面
