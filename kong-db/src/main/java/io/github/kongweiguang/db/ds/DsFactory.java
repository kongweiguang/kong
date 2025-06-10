package io.github.kongweiguang.db.ds;


import io.github.kongweiguang.core.exception.KongException;
import io.github.kongweiguang.core.file.Tomls;
import io.github.kongweiguang.core.resource.ClassPathResource;
import io.github.kongweiguang.db.DB;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工厂
 *
 * @author kongweiguang
 */
public class DsFactory {

    public static final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    /**
     * 注册数据源
     *
     * @param name 数据源名
     * @param ds   数据源
     */
    public static void register(String name, DataSource ds) {
        cache.put(name, ds);
    }

    /**
     * 自定义获取数据源
     *
     * @param source 数据源名
     * @param ds     默认数据源
     * @return 数据源
     */
    public static DataSource of(String source, DataSource ds) {
        return cache.computeIfAbsent(source, s -> ds);
    }

    /**
     * 从配置文件获取数据源
     *
     * @param source 数据源名
     * @return 数据源
     */
    public static DataSource of(String source, DsType dsType) {
        return cache.computeIfAbsent(source, s -> dsType.dsFun().create(source, readConf()));
    }

    private static Map<String, Object> readConf() {
        try {
            return Tomls.toMap(new ClassPathResource(DB.config).str());
        } catch (IOException e) {
            throw new KongException(e);
        }
    }
}
