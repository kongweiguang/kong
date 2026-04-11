package io.github.kongweiguang.http.common.sse;

/**
 * sse 事件
 *
 * @author kongweiguang
 */
public class SseEvent {

    private final StringBuilder sb = new StringBuilder();

    private String id;
    private String type;
    private String retry;

    private String data;

    /**
     * 创建默认实例。
     */
    public static SseEvent of() {
        return new SseEvent();
    }

    /**
     * 添加 SSE "id" 行.
     */
    public SseEvent id(String id) {
        this.id = id;
        append("id:").append(this.id).append("\n");
        return this;
    }

    /**
     * 添加 SSE "event" 行.
     */
    public SseEvent type(String type) {
        this.type = type;
        append("event:").append(this.type).append("\n");
        return this;
    }

    /**
     * 添加 SSE "retry" 行.
     */
    public SseEvent reconnectTime(long reconnectTimeMillis) {
        this.retry = String.valueOf(reconnectTimeMillis);
        append("retry:").append(this.retry).append("\n");
        return this;
    }

    /**
     * 添加 SSE "data" 行.
     */
    public SseEvent data(String data) {
        this.data = data;
        append("data:").append(this.data).append("\n");
        return this;
    }

    private SseEvent append(String text) {
        this.sb.append(text);
        return this;
    }

    /**
     * 构建并返回最终结果。
     */
    public String build() {
        return append("\n").sb.toString();
    }

    /**
     * 获取id 对应值。
     */
    public String id() {
        return id;
    }

    /**
     * 获取type 对应值。
     */
    public String type() {
        return type;
    }

    /**
     * 获取retry 对应值。
     */
    public String retry() {
        return retry;
    }

    /**
     * 获取data 对应值。
     */
    public String data() {
        return data;
    }

    @Override
    /**
     * 返回对象的可读字符串表示。
     */
    public String toString() {
        return build();
    }
}