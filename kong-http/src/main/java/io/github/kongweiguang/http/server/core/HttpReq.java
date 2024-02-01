package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.core.util.Objs;
import io.github.kongweiguang.core.util.IoUtil;
import io.github.kongweiguang.http.client.core.Header;
import io.github.kongweiguang.http.client.core.Method;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * 请求参数
 *
 * @author kongweiguang
 */
public final class HttpReq {

    private final HttpExchange he;
    private Map<String, List<String>> paramMap;
    private Map<String, List<UploadFile>> fileMap;

    /**
     * 构造
     *
     * @param httpExchange {@link HttpExchange}
     */
    public HttpReq(final HttpExchange httpExchange) {
        this.he = httpExchange;
    }

    /**
     * 将url中的请求参数解析到集合中
     *
     * @param url 请求地址
     * @param map 请求参数集合
     */
    private static void getParams(final String url, final Map<String, List<String>> map) {
        if (isNull(url)) {
            return;
        }

        for (String part : url.split("&")) {
            String[] kv = part.split("=");
            if (kv.length > 1) {
                map.computeIfAbsent(kv[0], k -> new ArrayList<>()).add(kv[1]);
            }
        }
    }

    /**
     * 获取请求的HttpExchange
     *
     * @return HttpExchange
     */
    public HttpExchange httpExchange() {
        return this.he;
    }

    /**
     * 获取所有的请求头
     *
     * @return 请求头 {@link  Headers}
     */
    public Headers headers() {
        return httpExchange().getRequestHeaders();
    }

    /**
     * 获取指定的请求头
     *
     * @param name 请求头的名称
     * @return 请求头的值
     */
    public String header(final String name) {
        return headers().getFirst(name);
    }

    /**
     * 获取请求的contentType
     *
     * @return contentType
     */
    public String contentType() {
        return header(Header.content_type.v());
    }

    /**
     * 获取请求的字符集charset
     *
     * @return {@link Charset}
     */
    public Charset charset() {
        try {
            final String contentType = contentType();
            if (nonNull(contentType)) {
                final String[] parts = contentType.split(";");
                if (parts.length > 1) {
                    final String part = parts[1];
                    if (part.startsWith("charset=")) {
                        return Charset.forName(part.substring("charset=".length()));
                    }

                }
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    /**
     * 获取请求头中的user_agent
     *
     * @return user_agent
     */
    public String ua() {
        return header(Header.user_agent.v());
    }

    /**
     * 请求的方法
     *
     * @return 方法
     */
    public String method() {
        return httpExchange().getRequestMethod();
    }

    /**
     * 获取请求的uri
     *
     * @return {@link URI}
     */
    public URI uri() {
        return this.he.getRequestURI();
    }

    /**
     * 请求的路径
     *
     * @return 路径
     */
    public String path() {
        return uri().getPath();
    }

    /**
     * 请求地址上的参数字符串
     *
     * @return 请求参数字符串
     */
    public String query() {
        return uri().getQuery();
    }

    /**
     * 判断当前请求是否是表单请求
     *
     * @return 是否是表单请求
     */
    public boolean isMultipart() {
        if (!Objects.equals(Method.valueOf(method()), Method.POST)) {
            return false;
        }

        final String contentType = contentType();

        if (isNull(contentType)) {
            return false;
        }

        return contentType.toLowerCase().startsWith("multipart/");
    }

    /**
     * 获取表单请求的参数
     *
     * @return 表单参数
     */
    public Map<String, List<String>> params() {
        if (isNull(paramMap)) {
            this.paramMap = new HashMap<>();

            ofNullable(query()).ifPresent(q -> getParams(q, paramMap));

            if (isMultipart()) {
                parserForm();
            } else {
                getParams(str(), paramMap);
            }

        }

        return paramMap;
    }

    /**
     * 请求体byte数组
     *
     * @return byte数组
     */
    public byte[] bytes() {
        final String length = header(Header.content_length.v());
        return IoUtil.toByteArray(stream(), Integer.parseInt(isNull(length) ? "0" : length));

    }

    /**
     * 请求体字符串
     *
     * @return 字符串
     */
    public String str() {
        if (bytes().length == 0) {
            return null;
        }

        return new String(bytes(), Objs.defaultIfNull(charset(), StandardCharsets.UTF_8));
    }

    /**
     * 获取请求体的流
     *
     * @return 输入流
     */
    public InputStream stream() {
        return httpExchange().getRequestBody();
    }

    /**
     * 解析form表单
     */
    private void parserForm() {
        this.fileMap = new HashMap<>();
        FormResolver.parser(this);
    }

    /**
     * 获取上传文件
     *
     * @return 上传文件
     */
    public Map<String, List<UploadFile>> fileMap() {
        if (isNull(fileMap)) {
            parserForm();
        }

        return fileMap;
    }
}