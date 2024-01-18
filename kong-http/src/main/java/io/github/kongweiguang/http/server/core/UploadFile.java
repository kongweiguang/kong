package io.github.kongweiguang.http.server.core;

import java.io.InputStream;

/**
 * 上传文件
 *
 * @author kongweiguang
 */
public class UploadFile {

    private String fileName;
    private InputStream content;

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    public String fileName() {
        return fileName;
    }

    /**
     * 设置文件名
     *
     * @param fileName 文件名
     * @return {@link UploadFile}
     */
    public UploadFile fileName(final String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 获取文件内容
     *
     * @return 内容流
     */
    public InputStream content() {
        return content;
    }

    /**
     * 设置文件内容
     *
     * @param content 内容流
     * @return {@link UploadFile}
     */
    public UploadFile content(final InputStream content) {
        this.content = content;
        return this;
    }
}
