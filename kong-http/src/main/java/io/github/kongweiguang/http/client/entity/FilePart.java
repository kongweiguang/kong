package io.github.kongweiguang.http.client.entity;

/**
 * 文件对象
 *
 * @param name     保存 field name
 * @param fileName 保存 uploaded file name
 * @param bytes    保存 binary content
 * @author kongweiguang
 */
public record FilePart(String name, String fileName, byte[] bytes) {
}