package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.client.core.ContentType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * 响应参数
 */
public final class HttpRes {

    private final HttpExchange he;
    private Charset charset = StandardCharsets.UTF_8;
    private String contentType = ContentType.text_plain.v();

    public HttpRes(final HttpExchange httpExchange) {
        this.he = httpExchange;
    }

    public HttpExchange httpExchange() {
        return he;
    }

    public Headers getHeaders() {
        return httpExchange().getResponseHeaders();
    }

    public OutputStream out() {
        return httpExchange().getResponseBody();
    }

    public HttpRes header(final String k, final String v) {
        getHeaders().set(k, v);
        return this;
    }

    public HttpRes headers(final Map<String, List<String>> headers) {
        getHeaders().putAll(headers);
        return this;
    }


    public HttpRes charset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    public Charset charset() {
        return this.charset;
    }

    public HttpRes contentType(final String contentType) {
        this.contentType = contentType;
        header(Header.content_type.v(), String.join(";charset=", contentType, charset().name()));
        return this;
    }

    public HttpRes sendOk() {
        try {
            httpExchange().sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public HttpRes send(final String str) {
        if (isNull(str)) {
            return this;
        }

        return send(str.getBytes(charset()));
    }

    public HttpRes send(final byte[] bytes) {
        return write(200, bytes);
    }

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

    public HttpRes write(int code, final byte[] bytes) {
        try (final OutputStream out = out()) {
            httpExchange().sendResponseHeaders(code, bytes.length);
            header(Header.content_type.v(), contentType);

            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public PrintWriter writer() {
        return new PrintWriter(new OutputStreamWriter(out(), charset()));
    }

    public void close() {
        try {
            out().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
