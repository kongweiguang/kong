package io.github.kongweiguang.http.server.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static io.github.kongweiguang.http.server.core.InnerUtil._404;
import static java.util.Objects.nonNull;

/**
 * 静态资源处理器
 *
 * @author kongweiguang
 */
public final class WebHandler implements HttpHandler {

    public static final String PATH = "_static_";
    private final String base_path;
    private String index_file = "index.html";

    /**
     * 构造处理器
     *
     * @param path      路径
     * @param indexName 默认文件名称
     */
    public WebHandler(final String path, final String indexName) {
        this.base_path = path;
        if (nonNull(indexName)) {
            this.index_file = indexName;
        }
    }

    /**
     * http处理器
     *
     * @param req http请求
     * @param res http响应
     * @throws IOException 异常
     */
    @Override
    public void doHandler(final HttpReq req, final HttpRes res) throws IOException {

        File file = new File(base_path, req.path());

        if (file.exists()) {

            if (file.isDirectory()) {
                file = new File(file, index_file);
                if (!file.exists()) {
                    _404(req.httpExchange(), null);
                    return;
                }
            }

            res.send(Files.readAllBytes(file.toPath()));

        } else {
            _404(req.httpExchange(), null);
        }

    }

}
