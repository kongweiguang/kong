package io.github.kongweiguang.http.common.core;

/**
 * HTTP请求和响应头部常量枚举
 * 包含了常用的HTTP头部字段，按照通用头域、请求头域和响应头域分类
 *
 * @author kongweiguang
 */
public enum Header {

    //------------------------------------------------------------- 通用头域
    /**
     * 提供验证头，例如：
     * <pre>
     * Authorization: Basic YWxhZGRpbjpvcGVuc2VzYW1l
     * </pre>
     */
    AUTHORIZATION("Authorization"),
    /**
     * 提供给代理服务器的用于身份验证的凭证，例如：
     * <pre>
     * Proxy-Authorization: Basic YWxhZGRpbjpvcGVuc2VzYW1l
     * </pre>
     */
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    /**
     * 提供日期和时间标志,说明报文是什么时间创建的
     */
    DATE("Date"),
    /**
     * 允许客户端和服务器指定与请求/响应连接有关的选项
     */
    CONNECTION("Connection"),
    /**
     * 给出发送端使用的MIME版本
     */
    MIME_VERSION("MIME-Version"),
    /**
     * 如果报文采用了分块传输编码(chunked transfer encoding) 方式,就可以用这个首部列出位于报文拖挂(trailer)部分的首部集合
     */
    TRAILER("Trailer"),
    /**
     * 告知接收端为了保证报文的可靠传输,对报文采用了什么编码方式
     */
    TRANSFER_ENCODING("Transfer-Encoding"),
    /**
     * 给出了发送端可能想要"升级"使用的新版本和协议
     */
    UPGRADE("Upgrade"),
    /**
     * 显示了报文经过的中间节点
     */
    VIA("Via"),
    /**
     * 指定请求和响应遵循的缓存机制，常见值包括：
     * <pre>
     * - no-cache：不使用缓存
     * - no-store：不存储缓存
     * - max-age=秒：缓存的内容将在指定的秒数后失效
     * - public：可以被任何缓存区缓存
     * - private：只能被私有缓存区缓存
     * </pre>
     */
    CACHE_CONTROL("Cache-Control"),
    /**
     * 用来包含实现特定的指令，最常用的是Pragma:no-cache
     * 在HTTP/1.1协议中，它的含义和Cache-Control:no-cache相同
     * 主要用于向后兼容HTTP/1.0客户端
     */
    PRAGMA("Pragma"),
    /**
     * 请求表示提交内容类型或返回内容的MIME类型
     * 例如：
     * <pre>
     * Content-Type: text/html; charset=UTF-8
     * Content-Type: application/json
     * Content-Type: multipart/form-data; boundary=something
     * </pre>
     */
    CONTENT_TYPE("Content-Type"),

    //------------------------------------------------------------- 请求头域
    /**
     * 指定请求资源的Intenet主机和端口号，必须表示请求url的原始服务器或网关的位置。HTTP/1.1请求必须包含主机头域，否则系统会以400状态码返回
     */
    HOST("Host"),
    /**
     * 允许客户端指定请求uri的源资源地址，这可以允许服务器生成回退链表，可用来登陆、优化cache等。他也允许废除的或错误的连接由于维护的目的被
     * 追踪。如果请求的uri没有自己的uri地址，Referer不能被发送。如果指定的是部分uri地址，则此地址应该是一个相对地址
     */
    REFERER("Referer"),
    /**
     * 指定请求的域
     */
    ORIGIN("Origin"),
    /**
     * HTTP客户端运行的浏览器类型的详细信息。通过该头部信息，web服务器可以判断到当前HTTP请求的客户端浏览器类别
     */
    USER_AGENT("User-Agent"),
    /**
     * 指定客户端能够接收的内容类型，内容类型中的先后次序表示客户端接收的先后次序
     */
    ACCEPT("Accept"),
    /**
     * 指定HTTP客户端浏览器用来展示返回信息所优先选择的语言
     */
    ACCEPT_LANGUAGE("Accept-Language"),
    /**
     * 指定客户端浏览器可以支持的web服务器返回内容压缩编码类型
     */
    ACCEPT_ENCODING("Accept-Encoding"),
    /**
     * 浏览器可以接受的字符编码集
     */
    ACCEPT_CHARSET("Accept-Charset"),
    /**
     * HTTP请求发送时，会把保存在该请求域名下的所有cookie值一起发送给web服务器
     */
    COOKIE("Cookie"),
    /**
     * 请求的内容长度
     */
    CONTENT_LENGTH("Content-Length"),

    //------------------------------------------------------------- 响应头域
    /**
     * 提供WWW验证响应头
     */
    WWW_AUTHENTICATE("WWW-Authenticate"),
    /**
     * Cookie
     */
    SET_COOKIE("Set-Cookie"),
    /**
     * Content-Encoding
     */
    CONTENT_ENCODING("Content-Encoding"),
    /**
     * Content-Disposition
     */
    CONTENT_DISPOSITION("Content-Disposition"),

    /**
     * 重定向指示到的URL
     */
    LOCATION("Location"),

    /**
     * 指定从什么时间开始响应过期
     * 例如：
     * <pre>
     * Expires: Thu, 01 Dec 2022 16:00:00 GMT
     * </pre>
     */
    EXPIRES("Expires"),

    /**
     * 实体标签，用于标识资源的特定版本
     * 例如：
     * <pre>
     * ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
     * </pre>
     */
    ETAG("ETag"),

    /**
     * 指定资源的最后修改时间
     * 例如：
     * <pre>
     * Last-Modified: Tue, 15 Nov 2022 12:45:26 GMT
     * </pre>
     */
    LAST_MODIFIED("Last-Modified"),

    /**
     * 用于指定客户端可以接受的内容范围
     * 例如：
     * <pre>
     * Accept-Ranges: bytes
     * </pre>
     */
    ACCEPT_RANGES("Accept-Ranges"),

    /**
     * 用于指定响应的内容范围
     * 例如：
     * <pre>
     * Content-Range: bytes 200-1000/67589
     * </pre>
     */
    CONTENT_RANGE("Content-Range"),

    /**
     * 用于控制网页与框架的关系，防止点击劫持
     * 例如：
     * <pre>
     * X-Frame-Options: DENY
     * X-Frame-Options: SAMEORIGIN
     * </pre>
     */
    X_FRAME_OPTIONS("X-Frame-Options"),

    /**
     * 用于跨域资源共享
     * 例如：
     * <pre>
     * Access-Control-Allow-Origin: *
     * Access-Control-Allow-Origin: https://example.com
     * </pre>
     */
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),

    /**
     * 用于跨域请求中，指定允许的HTTP方法
     * 例如：
     * <pre>
     * Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
     * </pre>
     */
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),

    /**
     * 用于跨域请求中，指定允许的头部字段
     * 例如：
     * <pre>
     * Access-Control-Allow-Headers: Content-Type, Authorization
     * </pre>
     */
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),

    /**
     * 用于跨域请求中，指定预检请求的有效期
     * 例如：
     * <pre>
     * Access-Control-Max-Age: 86400
     * </pre>
     */
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),

    /**
     * 用于跨域请求中，指定是否允许发送Cookie
     * 例如：
     * <pre>
     * Access-Control-Allow-Credentials: true
     * </pre>
     */
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),

    /**
     * 用于指定内容安全策略，防止XSS攻击
     * 例如：
     * <pre>
     * Content-Security-Policy: default-src 'self'
     * </pre>
     */
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),

    /**
     * 用于指定是否允许浏览器进行MIME类型嗅探
     * 例如：
     * <pre>
     * X-Content-Type-Options: nosniff
     * </pre>
     */
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),

    /**
     * 用于指定浏览器如何处理跨站点请求，防止CSRF攻击
     * 例如：
     * <pre>
     * X-XSS-Protection: 1; mode=block
     * </pre>
     */
    X_XSS_PROTECTION("X-XSS-Protection"),

    /**
     * 用于指定是否将HTTP请求重定向到HTTPS
     * 例如：
     * <pre>
     * Strict-Transport-Security: max-age=31536000; includeSubDomains
     * </pre>
     */
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),

    // X_FRAME_OPTIONS已经定义，此处不再重复定义

    /**
     * 用于指定是否允许浏览器发送Referer头
     * 例如：
     * <pre>
     * Referrer-Policy: no-referrer
     * Referrer-Policy: origin
     * </pre>
     */
    REFERRER_POLICY("Referrer-Policy"),

    /**
     * 用于指定允许的HTTP方法
     * 例如：
     * <pre>
     * Allow: GET, POST, HEAD
     * </pre>
     */
    ALLOW("Allow"),

    /**
     * 用于指定响应的年龄（秒）
     * 例如：
     * <pre>
     * Age: 12
     * </pre>
     */
    AGE("Age"),

    /**
     * 用于指定服务器名称
     * 例如：
     * <pre>
     * Server: Apache/2.4.1 (Unix)
     * </pre>
     */
    SERVER("Server");


    private final String v;

    Header(String v) {
        this.v = v;
    }

    /**
     * 获取头部字段的字符串值
     *
     * @return 头部字段的字符串值
     */
    public String v() {
        return v;
    }

    @Override
    /**
     * 返回对象的可读字符串表示。
     */
    public String toString() {
        return v();
    }
}
