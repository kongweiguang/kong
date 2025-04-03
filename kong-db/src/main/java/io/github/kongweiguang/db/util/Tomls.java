package io.github.kongweiguang.db.util;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;

public class Tomls {
    public static TomlParseResult resource(String fileName) {
        try {
            return Toml.parse(Tomls.class.getClassLoader().getResource(fileName).openStream());
        } catch (IOException e) {
            throw new RuntimeException("resource下未找到文件：" + fileName);
        }
    }

}
