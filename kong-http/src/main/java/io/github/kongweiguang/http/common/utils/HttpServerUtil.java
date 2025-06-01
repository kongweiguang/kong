package io.github.kongweiguang.http.common.utils;

import com.sun.net.httpserver.HttpExchange;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.server.core.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

/**
 * http server 内部工具
 *
 * @author kongweiguang
 */
public class HttpServerUtil {

    // 自定义错误页面存储
    private static final Map<Integer, String> ERROR_PAGES = new ConcurrentHashMap<>();

    // MIME类型映射表
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    // 初始化常用MIME类型
    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("zip", "application/zip");
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("rar", "application/x-rar-compressed");
        MIME_TYPES.put("7z", "application/x-7z-compressed");
        MIME_TYPES.put("wgt", "application/widget");
        MIME_TYPES.put("webp", "image/webp");
    }

    /**
     * 设置自定义错误页面
     *
     * @param statusCode 状态码
     * @param content    错误页面内容
     */
    public static void setErrorPage(int statusCode, String content) {
        ERROR_PAGES.put(statusCode, content);
    }

    /**
     * 获取自定义错误页面
     *
     * @param statusCode 状态码
     * @return 错误页面内容，如果没有自定义则返回null
     */
    public static String getErrorPage(int statusCode) {
        return ERROR_PAGES.get(statusCode);
    }

    /**
     * 判断是否有处理器，没有响应404
     *
     * @param he      HttpExchange
     * @param handler 处理器
     * @return 是否有处理器
     * @throws IOException
     */
    public static boolean _404(HttpExchange he, HttpHandler handler) throws IOException {
        if (isNull(handler)) {
            String errorContent = ERROR_PAGES.getOrDefault(404, "404 not found");
            sendError(he, 404, errorContent);
            return true;
        }
        return false;
    }

    /**
     * 发送错误响应
     *
     * @param he         HttpExchange
     * @param statusCode 状态码
     * @param content    错误内容
     * @throws IOException 异常
     */
    public static void sendError(HttpExchange he, int statusCode, String content) throws IOException {
        byte[] bytes = content.getBytes();
        he.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        he.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = he.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * 增加自定义MIME类型
     *
     * @param extension 文件扩展名
     * @param mimeType  MIME类型
     */
    public static void addMimeType(String extension, String mimeType) {
        MIME_TYPES.put(extension.toLowerCase(), mimeType);
    }

    /**
     * 获取文件的类型
     *
     * @param fileName 文件名
     * @return 类型
     */
    public static String getMimeType(final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return ContentType.OCTET_STREAM.v();
        }

        // 从扩展名获取MIME类型
        int lastDotPos = fileName.lastIndexOf(".");
        if (lastDotPos > 0) {
            String ext = fileName.substring(lastDotPos + 1).toLowerCase();
            String mimeType = MIME_TYPES.get(ext);
            if (mimeType != null) {
                return mimeType;
            }

            // 特殊处理一些复合扩展名
            if (ext.equals("js")) {
                return "application/x-javascript";
            }
        }

        // 检查特殊文件类型（例如没有扩展名但有特定格式的文件）
        for (Map.Entry<String, String> entry : MIME_TYPES.entrySet()) {
            if (fileName.toLowerCase().endsWith("." + entry.getKey())) {
                return entry.getValue();
            }
        }

        return ContentType.OCTET_STREAM.v();
    }

    /**
     * 获取文件的MIME类型
     *
     * @param file 文件
     * @return MIME类型
     */
    public static String getMimeType(File file) {
        if (file == null) {
            return ContentType.OCTET_STREAM.v();
        }

        // 先尝试通过文件名获取
        String name = file.getName();
        String mimeType = getMimeType(name);

        // 如果已经找到具体类型（不是默认的二进制流类型），直接返回
        if (!ContentType.OCTET_STREAM.v().equals(mimeType)) {
            return mimeType;
        }

        // 无法从扩展名确定，尝试从系统获取MIME类型
        try {
            String contentType = Files.probeContentType(file.toPath());
            return contentType != null ? contentType : ContentType.OCTET_STREAM.v();
        } catch (IOException e) {
            // 出现异常时返回默认二进制流类型
            return ContentType.OCTET_STREAM.v();
        }
    }
}
