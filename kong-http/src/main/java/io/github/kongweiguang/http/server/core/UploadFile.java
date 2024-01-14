package io.github.kongweiguang.http.server.core;

import java.io.InputStream;

public class UploadFile {

    private String fileName;
    private InputStream content;


    public String fileName() {
        return fileName;
    }

    public UploadFile setFileName(final String fileName) {
        this.fileName = fileName;
        return this;
    }

    public InputStream content() {
        return content;
    }

    public UploadFile setContent(final InputStream content) {
        this.content = content;
        return this;
    }
}
