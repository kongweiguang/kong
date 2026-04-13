package io.github.kongweiguang.http.client.consts;

/**
 * HTTP 中常用的请求头与响应头常量。
 *
 * @author kongweiguang
 */
public enum Header {

    // 通用头
    AUTHORIZATION("Authorization"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    DATE("Date"),
    CONNECTION("Connection"),
    CACHE_CONTROL("Cache-Control"),
    PRAGMA("Pragma"),
    TRAILER("Trailer"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    UPGRADE("Upgrade"),
    VIA("Via"),
    MIME_VERSION("MIME-Version"),

    // 实体头
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_DISPOSITION("Content-Disposition"),
    CONTENT_RANGE("Content-Range"),
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),

    // 请求头
    HOST("Host"),
    REFERER("Referer"),
    REFERRER_POLICY("Referrer-Policy"),
    ORIGIN("Origin"),
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_RANGES("Accept-Ranges"),
    COOKIE("Cookie"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),

    // 响应头
    WWW_AUTHENTICATE("WWW-Authenticate"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    EXPIRES("Expires"),
    ETAG("ETag"),
    LAST_MODIFIED("Last-Modified"),
    ALLOW("Allow"),
    AGE("Age"),
    SERVER("Server"),

    // CORS 头
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),

    // 安全相关头
    X_FRAME_OPTIONS("X-Frame-Options"),
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),
    X_XSS_PROTECTION("X-XSS-Protection"),
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security");

    private final String value;

    Header(final String value) {
        this.value = value;
    }

    /**
     * 返回标准头名称。
     *
     * @return 头名称
     */
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
