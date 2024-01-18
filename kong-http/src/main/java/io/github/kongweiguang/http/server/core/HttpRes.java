package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.client.core.ContentType;
import io.github.kongweiguang.http.client.core.Header;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * 响应参数
 *
 * @author kongweiguang
 */
public final class HttpRes {

    private final HttpExchange he;
    private Charset charset = StandardCharsets.UTF_8;
    private String contentType = ContentType.text_plain.v();

    public HttpRes(final HttpExchange httpExchange) {
        this.he = httpExchange;
    }

    /**
     * @return {@link HttpExchange}
     */
    public HttpExchange httpExchange() {
        return he;
    }

    /**
     * 获取响应的头
     *
     * @return {@link Headers}
     */
    public Headers getHeaders() {
        return httpExchange().getResponseHeaders();
    }

    /**
     * 响应流
     *
     * @return {@link OutputStream}
     */
    public OutputStream out() {
        return httpExchange().getResponseBody();
    }

    /**
     * 添加头信息
     *
     * @param name  名称
     * @param value 值
     * @return {@link HttpRes}
     */
    public HttpRes header(final String name, final String value) {
        getHeaders().set(name, value);
        return this;
    }

    /**
     * 添加头信息
     *
     * @param headers 响应头
     * @return {@link HttpRes}
     */
    public HttpRes headers(final Map<String, List<String>> headers) {
        getHeaders().putAll(headers);
        return this;
    }

    /**
     * 设置编码集
     *
     * @param charset 编码集
     * @return {@link HttpRes}
     */
    public HttpRes charset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 获取编码集
     *
     * @return 编码集 {@link Charset}
     */
    public Charset charset() {
        return this.charset;
    }

    /**
     * 设置响应的contentType
     *
     * @param contentType contentType
     * @return {@link HttpRes}
     */
    public HttpRes contentType(final String contentType) {
        this.contentType = contentType;
        header(Header.content_type.v(), String.join(";charset=", contentType, charset().name()));
        return this;
    }

    /**
     * 直接相应成功
     *
     * @return {@link HttpRes}
     */
    public HttpRes sendOk() {
        try {
            httpExchange().sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * 响应字符串
     *
     * @param str 内容
     * @return {@link HttpRes}
     */
    public HttpRes send(final String str) {
        if (isNull(str)) {
            return this;
        }

        return send(str.getBytes(charset()));
    }

    /**
     * 响应byte数组
     *
     * @param bytes 内容
     * @return {@link HttpRes}
     */
    public HttpRes send(final byte[] bytes) {
        return write(200, bytes);
    }

    /**
     * 响应文件
     *
     * @param fileName 文件名
     * @param bytes    内容
     * @return {@link HttpRes}
     */
    public HttpRes file(final String fileName, final byte[] bytes) {
        try {
            header(Header.content_disposition.v(),
                    "attachment;filename=" + URLEncoder.encode(fileName, charset().name()));
            contentType(InnerUtil.getMimeType(fileName));

            send(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * 响应结果
     *
     * @param code  响应code
     * @param bytes 内容
     * @return {@link HttpRes}
     */
    public HttpRes write(int code, final byte[] bytes) {
        try {
            httpExchange().sendResponseHeaders(code, bytes.length);
            header(Header.content_type.v(), contentType);

            final OutputStream out = out();
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * 获取打印流
     *
     * @return {@link PrintWriter}
     */
    public PrintWriter writer() {
        return new PrintWriter(new OutputStreamWriter(out(), charset()));
    }

    /**
     * 关闭响应流
     */
    public void close() {
        try {
            out().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
