package cn.zane.web.api;

import cn.zane.agent.tools.DocumentTool;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工具管理 API。
 *
 * <p>返回当前已注册的 Agent 工具列表，供前端工具管理页面展示。
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/tools")
public class ToolsController {

    private final DocumentTool documentTool;

    public ToolsController(DocumentTool documentTool) {
        this.documentTool = documentTool;
    }

    /**
     * 获取所有可用工具的列表。
     *
     * @return 工具列表，每个工具包含名称、描述、启用状态
     */
    @GetMapping
    public List<Map<String, Object>> list() {
        return List.of(
                Map.of(
                        "name", "generate_docx",
                        "description", "根据 Markdown 文本生成 Word 文档",
                        "enabled", true));
    }
}
