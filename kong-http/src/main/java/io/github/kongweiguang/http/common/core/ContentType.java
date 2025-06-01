package io.github.kongweiguang.http.common.core;

/**
 * HTTP Content-Type 常量枚举
 *
 * @author kongweiguang
 */
public enum ContentType {

    // 表单和数据格式
    /**
     * 标准表单编码，当action为get时候，浏览器用x-www-form-urlencoded的编码方式把form数据转换成一个字串（name1=value1&amp;name2=value2…）
     */
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    /**
     * 文件上传编码，浏览器会把整个表单以控件为单位分割，并为每个部分加上Content-Disposition，并加上分割符(boundary)
     */
    MULTIPART("multipart/form-data"),
    /**
     * JSON数据格式
     */
    JSON("application/json"),
    /**
     * XML数据格式
     */
    XML("application/xml"),

    // 文本格式
    /**
     * 纯文本格式
     */
    TEXT_PLAIN("text/plain"),
    /**
     * XML文本格式
     */
    TEXT_XML("text/xml"),
    /**
     * HTML文本格式
     */
    TEXT_HTML("text/html"),
    /**
     * CSS样式表
     */
    TEXT_CSS("text/css"),
    /**
     * JavaScript代码
     */
    TEXT_JAVASCRIPT("application/javascript"),
    /**
     * 服务器发送事件流
     */
    EVENT_STREAM("text/event-stream"),

    // 二进制格式
    /**
     * 二进制流数据
     */
    OCTET_STREAM("application/octet-stream"),
    /**
     * PDF文档
     */
    PDF("application/pdf"),
    /**
     * ZIP压缩文件
     */
    ZIP("application/zip"),
    /**
     * RAR压缩文件
     */
    RAR("application/x-rar-compressed"),
    /**
     * 7Z压缩文件
     */
    SEVEN_ZIP("application/x-7z-compressed"),

    // 图片格式
    /**
     * PNG图片
     */
    IMAGE_PNG("image/png"),
    /**
     * JPEG图片
     */
    IMAGE_JPEG("image/jpeg"),
    /**
     * GIF图片
     */
    IMAGE_GIF("image/gif"),
    /**
     * SVG图片
     */
    IMAGE_SVG("image/svg+xml"),
    /**
     * ICO图标
     */
    IMAGE_ICON("image/x-icon"),
    /**
     * WebP图片
     */
    IMAGE_WEBP("image/webp"),

    // 音视频格式
    /**
     * MP4视频
     */
    VIDEO_MP4("video/mp4"),
    /**
     * MP3音频
     */
    AUDIO_MP3("audio/mpeg"),

    // 字体格式
    /**
     * WOFF字体
     */
    FONT_WOFF("font/woff"),
    /**
     * WOFF2字体
     */
    FONT_WOFF2("font/woff2"),
    /**
     * TTF字体
     */
    FONT_TTF("font/ttf");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    /**
     * 获取枚举值的字符串
     *
     * @return 枚举值
     */
    public String v() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
