# zpaw Phase 3：工具 + 知识库 — 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**目标：** 实现文档生成工具（Apache POI）和本地知识库（全文检索）

**架构：** Agent Tool（`@Tool` 注解）提供核心能力，REST API 供前端调用，前端用 Ant Design Pro 管理页面

**技术栈：** Java 21, Apache POI 5.3.0, Lucene (本地全文检索), Ant Design Pro Table

## 全局约束

- 所有注释使用中文
- @author Zane
- SSE 事件使用 `StreamEvent` 枚举 + record
- 文件操作在 workspace 目录下进行

---

### Task 1: 文档生成工具（Apache POI）

**文件：**

- 新建：`src/main/java/cn/zane/agent/tools/DocumentTool.java`
- 修改：`src/main/java/cn/zane/agent/config/ZPawAgentConfig.java`

- [ ] **Step 1: 创建 DocumentTool.java**

```java
package cn.zane.agent.tools;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import java.io.FileOutputStream;
import java.nio.file.Path;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 文档生成工具集。
 *
 * <p>提供 Word/Excel/PPT 文档的生成能力，基于 Apache POI。
 * 所有文件输出到 Agent 的 workspace 目录。
 *
 * @author Zane
 */
@Component
public class DocumentTool {

    private static final Logger log = LoggerFactory.getLogger(DocumentTool.class);

    /**
     * 根据 Markdown 文本生成 Word 文档（.docx）。
     *
     * @param title    文档标题
     * @param content  Markdown 格式的文档内容
     * @param filename 输出文件名（不含路径）
     * @return 生成的文件路径
     */
    @Tool(name = "generate_docx",
           description = "根据 Markdown 文本生成 Word 文档")
    public String generateDocx(
            @ToolParam(name = "title", description = "文档标题") String title,
            @ToolParam(name = "content", description = "Markdown 格式的文档内容") String content,
            @ToolParam(name = "filename", description = "输出文件名，如 周报.docx") String filename) {
        try (XWPFDocument doc = new XWPFDocument()) {
            // 标题
            XWPFParagraph titlePara = doc.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            // 内容按行分割
            for (String line : content.split("\n")) {
                XWPFParagraph para = doc.createParagraph();
                XWPFRun run = para.createRun();
                run.setText(line);
                run.setFontSize(11);
            }

            Path output = Path.of(System.getProperty("user.home"), ".zpaw", filename);
            java.nio.file.Files.createDirectories(output.getParent());
            try (FileOutputStream out = new FileOutputStream(output.toFile())) {
                doc.write(out);
            }
            log.info("Word 文档已生成: {}", output);
            return "文档已生成: " + output;
        } catch (Exception e) {
            log.error("文档生成失败", e);
            return "生成失败: " + e.getMessage();
        }
    }
}
```

- [ ] **Step 2: 在 ZPawAgentConfig 中注册工具**

```java
// Phase 3: 注册自定义工具
```

- [ ] **Step 3: 编译验证**

```bash
mvn spotless:apply
mvn compile -q
```

---

### Task 2: 工具管理页面（前端）

**文件：**

- 新建：`frontend/src/pages/ToolsPage.tsx`
- 修改：`frontend/src/App.tsx`
- 新建：`frontend/src/api/tools.ts`

- [ ] **Step 1: 创建 tools.ts API**

```typescript
export interface ToolDefinition {
  name: string;
  description: string;
  enabled: boolean;
}

export async function listTools(): Promise<ToolDefinition[]> {
  const res = await fetch('/api/tools');
  if (!res.ok) throw new Error('Failed to list tools');
  return res.json();
}
```

- [ ] **Step 2: 前端构建验证**

```bash
cd frontend && npm run build
```

---

### Task 3: 知识库核心（全文检索）

**文件：**

- 新建：`src/main/java/cn/zane/knowledge/service/IndexService.java`
- 修改：`src/main/java/cn/zane/knowledge/controller/KnowledgeController.java`
- 修改：`src/main/java/cn/zane/knowledge/service/KnowledgeService.java`
- 新建：`src/main/java/cn/zane/knowledge/model/DocumentEntry.java`

- [ ] **Step 1: 实现文件索引服务**

使用 Lucene 或简单内存索引，支持：

- 上传文档（.txt, .md）
- 关键字全文搜索
- 索引状态查看

- [ ] **Step 2: 编译验证**

---

### Task 4: 知识库管理页面（前端）

**文件：**

- 新建：`frontend/src/pages/KnowledgePage.tsx`
- 修改：`frontend/src/App.tsx`
- 新建：`frontend/src/api/knowledge.ts`

- [ ] **Step 1: 创建知识库管理页面**
  - 文档上传（拖拽/选择文件）
  - 搜索框 + 结果列表
  - 索引状态展示

- [ ] **Step 2: 前端构建验证**

---

### Task 5: 集成验证

- [ ] `mvn clean package -DskipTests` 完整构建
- [ ] 验证文档生成工具可用
- [ ] 提交代码
