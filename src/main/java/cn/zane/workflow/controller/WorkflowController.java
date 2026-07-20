package cn.zane.workflow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 工作流管理 REST 控制器。
 *
 * <p>提供工作流的列表查询接口（当前为桩实现）。</p>
 *
 * @author Zane
 */
@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    /**
     * 获取所有工作流。
     *
     * @return a JSON array string of workflows (currently returns empty array)
     */
    @GetMapping
    public Mono<String> list() {
        return Mono.just("[]");
    }
}
