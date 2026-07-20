package cn.zane.agent.tools;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import java.io.FileOutputStream;
import java.nio.file.Files;
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
 * 所有文件输出到用户主目录下的 .zpaw 工作区。
 *
 * @author Zane
 */
@Component
public class DocumentTool {

    private static final Logger log = LoggerFactory.getLogger(DocumentTool.class);

    /** 工作区根目录 */
    private static final Path WORKSPACE =
            Path.of(System.getProperty("user.home"), ".zpaw", "documents");

    /**
     * 根据 Markdown 文本生成 Word 文档（.docx）。
     *
     * @param title    文档标题
     * @param content  Markdown 格式的文档内容，按段落换行
     * @param filename 输出文件名，如 "周报.docx"
     * @return 生成结果，包含文件路径或错误信息
     */
    @Tool(name = "generate_docx", description = "根据 Markdown 文本生成 Word 文档")
    public String generateDocx(
            @ToolParam(name = "title", description = "文档标题") String title,
            @ToolParam(name = "content", description = "Markdown 格式的文档内容") String content,
            @ToolParam(name = "filename", description = "输出文件名，如 周报.docx") String filename) {
        try {
            Files.createDirectories(WORKSPACE);
            Path output = WORKSPACE.resolve(filename);
            log.info("开始生成 Word 文档: {}", output);

            try (XWPFDocument doc = new XWPFDocument()) {
                // 标题段落
                XWPFParagraph titlePara = doc.createParagraph();
                XWPFRun titleRun = titlePara.createRun();
                titleRun.setText(title);
                titleRun.setBold(true);
                titleRun.setFontSize(18);

                // 按行分割生成段落
                for (String line : content.split("\n")) {
                    XWPFParagraph para = doc.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setText(line);
                    run.setFontSize(11);
                }

                try (FileOutputStream out = new FileOutputStream(output.toFile())) {
                    doc.write(out);
                }
            }

            log.info("Word 文档已生成: {}", output);
            return "文档已生成: " + output.toAbsolutePath();
        } catch (Exception e) {
            log.error("文档生成失败", e);
            return "生成失败: " + e.getMessage();
        }
    }
}
