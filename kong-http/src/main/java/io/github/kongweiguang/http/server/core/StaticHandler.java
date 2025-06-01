package io.github.kongweiguang.http.server.core;

import io.github.kongweiguang.http.common.core.Header;
import io.github.kongweiguang.http.common.utils.HttpServerUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.kongweiguang.http.common.utils.HttpServerUtil._404;
import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.nonNull;

/**
 * 静态资源处理器
 *
 * @author kongweiguang
 */
public class StaticHandler implements HttpHandler {

    private final String path;
    private final String base_path;
    private String index_file = "index.html";
    private boolean enableCache = true;
    private boolean enableETag = true;
    private final Map<String, byte[]> fileCache = new ConcurrentHashMap<>();
    private final Map<String, String> etagCache = new ConcurrentHashMap<>();
    // 默认10MB缓存大小
    private long maxCacheSize = 10 * 1024 * 1024;
    // 默认缓存1小时
    private int cacheMaxAge = 3600;


    /**
     * 构造带缓存选项的处理器
     *
     * @param path        路径
     * @param indexName   默认文件名称
     * @param enableCache 是否启用缓存
     * @param cacheMaxAge 缓存时间(秒)
     */
    public StaticHandler(String path, String filePath, String indexName, boolean enableCache, int cacheMaxAge) {
        this.path = path;
        this.base_path = filePath;
        if (nonNull(indexName)) {
            this.index_file = indexName;
        }
        this.enableCache = enableCache;
        if (cacheMaxAge > 0) {
            this.cacheMaxAge = cacheMaxAge;
        }
    }

    /**
     * 设置最大缓存大小
     *
     * @param maxCacheSize 最大缓存大小(字节)
     * @return 当前对象
     */
    public StaticHandler maxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    /**
     * 设置是否启用ETag
     *
     * @param enableETag 是否启用ETag
     * @return 当前对象
     */
    public StaticHandler enableETag(boolean enableETag) {
        this.enableETag = enableETag;
        return this;
    }

    /**
     * 增加自定义MIME类型
     *
     * @param extension 文件扩展名
     * @param mimeType  MIME类型
     * @return 当前对象
     */
    public StaticHandler addMimeType(String extension, String mimeType) {
        HttpServerUtil.addMimeType(extension, mimeType);
        return this;
    }

    /**
     * 生成ETag
     *
     * @param fileContent  文件内容
     * @param lastModified 最后修改时间
     * @return ETag值
     */
    private String generateETag(byte[] fileContent, long lastModified) {
        return "W/\"" + fileContent.length + "-" + lastModified + "\"";
    }

    /**
     * http处理器
     *
     * @param req http请求
     * @param res http响应
     * @throws IOException 异常
     */
    @Override
    public void doHandler(HttpReq req, HttpRes res) throws IOException {
        String requestPath = req.path();
        // 移除路径中的PATH前缀
        if (requestPath.startsWith(path)) {
            // 处理根路径和路径长度问题，避免下标越界
            if (requestPath.length() <= path.length()) {
                // 如果请求路径就是注册的路径本身（或甚至更短），使用空字符串处理根目录
                requestPath = "";
            } else {
                // 确保我们有足够的字符可以截取
                requestPath = requestPath.substring(path.length());
                // 如果第一个字符是斜杠，移除它
                if (requestPath.startsWith("/")) {
                    requestPath = requestPath.substring(1);
                }
            }
        }

        File file = new File(base_path, requestPath);

        if (file.exists()) {
            if (file.isDirectory()) {
                file = new File(file, index_file);
                if (!file.exists()) {
                    _404(req.httpExchange(), null);
                    return;
                }
            }

            // 检查是否为条件GET请求
            String ifNoneMatch = req.headers().getFirst("If-None-Match");
            long lastModified = file.lastModified();
            String filePath = file.getAbsolutePath();

            byte[] content;
            String etag = null;

            // 尝试从缓存获取文件内容
            if (enableCache && fileCache.containsKey(filePath)) {
                content = fileCache.get(filePath);
                if (enableETag) {
                    etag = etagCache.get(filePath);

                    // 如果ETag匹配，返回304状态码
                    if (etag != null && etag.equals(ifNoneMatch)) {
                        res.getHeaders().add(Header.ETAG.v(), etag);
                        res.getHeaders().add(Header.CACHE_CONTROL.v(), "max-age=" + cacheMaxAge);
                        res.write(304, new byte[0]);
                        return;
                    }
                }
            } else {
                // 读取文件内容
                content = readAllBytes(file.toPath());

                // 如果文件大小小于最大缓存大小，缓存文件内容
                if (enableCache && content.length < maxCacheSize) {
                    fileCache.put(filePath, content);

                    if (enableETag) {
                        etag = generateETag(content, lastModified);
                        etagCache.put(filePath, etag);
                    }
                }
            }

            // 设置Content-Type
            res.getHeaders().add(Header.CONTENT_TYPE.v(), HttpServerUtil.getMimeType(file));

            // 设置缓存控制头
            if (enableCache) {
                res.getHeaders().add(Header.CACHE_CONTROL.v(), "max-age=" + cacheMaxAge);
            } else {
                res.getHeaders().add(Header.CACHE_CONTROL.v(), "no-cache, no-store, must-revalidate");
                res.getHeaders().add(Header.PRAGMA.v(), "no-cache");
                res.getHeaders().add(Header.EXPIRES.v(), "0");
            }

            // 设置ETag
            if (enableETag && etag != null) {
                res.getHeaders().add(Header.ETAG.v(), etag);
            }

            // 发送响应
            res.send(content);
        } else {
            _404(req.httpExchange(), null);
        }
    }
}
