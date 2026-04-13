package io.github.kongweiguang.http.client.consts;

/**
 * HTTP 请求方法常量。
 *
 * @author kongweiguang
 */
public enum Method {
    GET(false),
    POST(true),
    DELETE(false),
    PUT(true),
    PATCH(true),
    HEAD(false),
    OPTIONS(false),
    TRACE(false),
    CONNECT(false);

    private final boolean permitsRequestBody;

    Method(final boolean permitsRequestBody) {
        this.permitsRequestBody = permitsRequestBody;
    }

    /**
     * 返回方法名。
     *
     * @return HTTP 方法名
     */
    public String value() {
        return name();
    }

    /**
     * 判断该方法在当前客户端中是否允许携带请求体。
     *
     * @return 允许时返回 {@code true}
     */
    public boolean permitsRequestBody() {
        return permitsRequestBody;
    }

    @Override
    public String toString() {
        return value();
    }
}
