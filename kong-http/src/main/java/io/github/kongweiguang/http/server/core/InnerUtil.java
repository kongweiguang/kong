package io.github.kongweiguang.http.server.core;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.client.core.ContentType;

import java.io.IOException;

import static java.util.Objects.isNull;

/**
 * http server 内部工具
 *
 * @author kongweiguang
 */
public final class InnerUtil {
    /**
     * 判断是否有处理器，没有响应404
     *
     * @param he      HttpExchange
     * @param handler 处理器
     * @return 是否有处理器
     * @throws IOException
     */
    public static boolean _404(final HttpExchange he, final HttpHandler handler) throws IOException {
        if (isNull(handler)) {
            String _404 = "404 not found";
            he.sendResponseHeaders(404, _404.length());
            he.getResponseBody().write(_404.getBytes());
            return true;
        }
        return false;
    }

    /**
     * 参考hutool
     * 获取文件的类型
     *
     * @param fileName 文件名
     * @return 类型
     */
    public static String getMimeType(final String fileName) {

        // 补充一些常用的mimeType
        if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/x-javascript";
        } else if (fileName.endsWith(".rar")) {
            return "application/x-rar-compressed";
        } else if (fileName.endsWith(".7z")) {
            return "application/x-7z-compressed";
        } else if (fileName.endsWith(".wgt")) {
            return "application/widget";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        }

        return ContentType.octet_stream.v();
    }
}
