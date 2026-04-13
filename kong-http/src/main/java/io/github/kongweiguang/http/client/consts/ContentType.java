package io.github.kongweiguang.http.client.consts;

/**
 * HTTP 中常用的 Content-Type 常量。
 *
 * @author kongweiguang
 */
public enum ContentType {

    /**
     * 表单提交编码。
     */
    FORM_URLENCODED("application/x-www-form-urlencoded"),

    /**
     * Multipart 表单编码，通常用于文件上传。
     */
    MULTIPART("multipart/form-data"),

    /**
     * JSON 数据。
     */
    JSON("application/json"),

    /**
     * XML 数据。
     */
    XML("application/xml"),

    /**
     * 纯文本。
     */
    TEXT_PLAIN("text/plain"),

    /**
     * XML 文本。
     */
    TEXT_XML("text/xml"),

    /**
     * HTML 文本。
     */
    TEXT_HTML("text/html"),

    /**
     * CSS 样式表。
     */
    TEXT_CSS("text/css"),

    /**
     * JavaScript 资源。
     */
    APPLICATION_JAVASCRIPT("application/javascript"),

    /**
     * Server-Sent Events 事件流。
     */
    EVENT_STREAM("text/event-stream"),

    /**
     * 通用二进制流。
     */
    OCTET_STREAM("application/octet-stream"),

    /**
     * PDF 文档。
     */
    PDF("application/pdf"),

    /**
     * ZIP 压缩包。
     */
    ZIP("application/zip"),

    /**
     * RAR 压缩包。
     */
    RAR("application/x-rar-compressed"),

    /**
     * 7z 压缩包。
     */
    SEVEN_ZIP("application/x-7z-compressed"),

    /**
     * PNG 图片。
     */
    IMAGE_PNG("image/png"),

    /**
     * JPEG 图片。
     */
    IMAGE_JPEG("image/jpeg"),

    /**
     * GIF 图片。
     */
    IMAGE_GIF("image/gif"),

    /**
     * SVG 图片。
     */
    IMAGE_SVG("image/svg+xml"),

    /**
     * ICO 图标。
     */
    IMAGE_ICON("image/x-icon"),

    /**
     * WebP 图片。
     */
    IMAGE_WEBP("image/webp"),

    /**
     * MP4 视频。
     */
    VIDEO_MP4("video/mp4"),

    /**
     * MPEG 音频。
     */
    AUDIO_MP3("audio/mpeg"),

    /**
     * WOFF 字体。
     */
    FONT_WOFF("font/woff"),

    /**
     * WOFF2 字体。
     */
    FONT_WOFF2("font/woff2"),

    /**
     * TrueType 字体。
     */
    FONT_TTF("font/ttf");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    /**
     * 返回常量对应的 MIME 值。
     *
     * @return MIME 值
     */
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
