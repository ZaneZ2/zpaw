package cn.zane.web.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SSE 流式事件数据模型。
 *
 * <p>由 {@link ObjectMapper} 自动序列化为 JSON 后通过 SSE 推送至前端。
 * 只序列化非 null 字段（{@link JsonInclude#NON_NULL}），避免冗余传输。
 *
 * <p>推荐使用静态工厂方法（如 {@link #token(String)}）创建实例，
 * 而非直接调用构造器，以保证 type 与字段的正确匹配。
 *
 * @author Zane
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StreamEvent(

        /** 事件类型，枚举值参见 {@link StreamEventType} */
        StreamEventType type,

        /** 文本增量 / 错误描述 / 工具进度 */
        String data,

        /** Agent 名称（仅 {@link StreamEventType#AGENT_START} 时携带） */
        String agent,

        /** 工具名称（仅工具相关事件时携带） */
        String toolName,

        /** 工具执行结果状态：SUCCESS / ERROR（仅 {@link StreamEventType#TOOL_RESULT_END} 时携带） */
        String state) {

    private static final ObjectMapper JSON = new ObjectMapper();

    /**
     * 将当前事件序列化为 SSE 格式的字符串（{@code data: {...}\n\n}）。
     *
     * @return SSE 事件字符串
     */
    public String toSse() {
        try {
            return "data: " + JSON.writeValueAsString(this) + "\n\n";
        } catch (JsonProcessingException e) {
            return "data: {\"type\":\"error\",\"data\":\"json error\"}\n\n";
        }
    }

    // ========== 静态工厂方法 ==========

    /** 创建 LLM 文本增量事件 */
    public static StreamEvent token(String text) {
        return new StreamEvent(StreamEventType.TOKEN, text, null, null, null);
    }

    /** 创建 Agent 启动事件 */
    public static StreamEvent agentStart(String name) {
        return new StreamEvent(StreamEventType.AGENT_START, null, name, null, null);
    }

    /** 创建工具调用事件 */
    public static StreamEvent toolCall(String toolName) {
        return new StreamEvent(StreamEventType.TOOL_CALL, null, null, toolName, null);
    }

    /** 创建工具开始执行事件 */
    public static StreamEvent toolResultStart(String toolName) {
        return new StreamEvent(StreamEventType.TOOL_RESULT_START, null, null, toolName, null);
    }

    /** 创建工具执行进度事件 */
    public static StreamEvent toolResultDelta(String delta) {
        return new StreamEvent(StreamEventType.TOOL_RESULT_DELTA, delta, null, null, null);
    }

    /** 创建工具执行结束事件 */
    public static StreamEvent toolResultEnd(String state) {
        return new StreamEvent(StreamEventType.TOOL_RESULT_END, null, null, null, state);
    }

    /** 创建 SSE 流结束事件 */
    public static StreamEvent done() {
        return new StreamEvent(StreamEventType.DONE, null, null, null, null);
    }

    /** 创建错误事件 */
    public static StreamEvent error(String message) {
        return new StreamEvent(StreamEventType.ERROR, message, null, null, null);
    }
}
