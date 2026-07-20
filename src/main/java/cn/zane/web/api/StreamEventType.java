package cn.zane.web.api;

/**
 * SSE 流式事件类型枚举。
 *
 * <p>每个枚举值对应 {@link StreamEvent} 的一条 SSE 消息，
 * 前端根据 type 字段区分事件类型并执行不同渲染逻辑。
 *
 * @author Zane
 */
public enum StreamEventType {

    /** Agent 开始处理用户请求 */
    AGENT_START,

    /** LLM 输出的文本增量，由 data 字段携带 */
    TOKEN,

    /** LLM 请求调用工具，由 toolName 字段指定工具名 */
    TOOL_CALL,

    /** 工具开始执行 */
    TOOL_RESULT_START,

    /** 工具执行过程中的进度文本（来自 ToolEmitter） */
    TOOL_RESULT_DELTA,

    /** 工具执行结束，由 state 字段标记 SUCCESS / ERROR */
    TOOL_RESULT_END,

    /** SSE 流正常结束 */
    DONE,

    /** 发生错误，由 data 字段携带错误描述 */
    ERROR
}
