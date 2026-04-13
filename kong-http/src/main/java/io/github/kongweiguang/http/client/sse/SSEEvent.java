package io.github.kongweiguang.http.client.sse;

/**
 * SSE 事件构建器。
 *
 * @author kongweiguang
 */
public class SseEvent {

    /**
     * 保存 sb
     */
    private final StringBuilder sb = new StringBuilder();

    /**
     * 保存 id
     */
    private String id;

    /**
     * 保存 type
     */
    private String type;

    /**
     * 保存 retry config
     */
    private String retry;

    /**
     * 保存 data
     */
    private String data;

    /**
     * 根据给定对象创建 wrapper instance
     */
    public static SseEvent of() {
        return new SseEvent();
    }

    /**
     * 设置 id
     */
    public SseEvent id(String id) {
        this.id = id;
        append("id:").append(this.id).append("\n");
        return this;
    }

    /**
     * 设置 type
     */
    public SseEvent type(String type) {
        this.type = type;
        append("event:").append(this.type).append("\n");
        return this;
    }

    /**
     * 设置 reconnect time
     */
    public SseEvent reconnectTime(long reconnectTimeMillis) {
        this.retry = String.valueOf(reconnectTimeMillis);
        append("retry:").append(this.retry).append("\n");
        return this;
    }

    /**
     * 设置 data
     */
    public SseEvent data(String data) {
        this.data = data;
        append("data:").append(this.data).append("\n");
        return this;
    }

    /**
     * 设置 append
     */
    private SseEvent append(String text) {
        this.sb.append(text);
        return this;
    }

    /**
     * 构建 SseEvent instance
     */
    public String build() {
        return append("\n").sb.toString();
    }

    /**
     * 返回 id
     */
    public String id() {
        return id;
    }

    /**
     * 返回 type
     */
    public String type() {
        return type;
    }

    /**
     * 配置 retry behavior
     */
    public String retry() {
        return retry;
    }

    /**
     * 返回 data
     */
    public String data() {
        return data;
    }

    @Override
    public String toString() {
        return build();
    }
}
