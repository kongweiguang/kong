package io.github.kongweiguang.db.util;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;

/**
 * TOML工具类
 *
 * @author kongweiguang
 */
public class Tomls {
    /**
     * 解析resource下的TOML文件
     *
     * @param fileName 文件名
     * @return TomlParseResult
     */
    public static TomlParseResult resource(String fileName) {
        try {
            return Toml.parse(Tomls.class.getClassLoader().getResource(fileName).openStream());
        } catch (IOException e) {
            throw new RuntimeException("resource下未找到文件：" + fileName);
        }
    }

}
