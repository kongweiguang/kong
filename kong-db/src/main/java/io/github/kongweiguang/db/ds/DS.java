package io.github.kongweiguang.db.ds;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.util.Tomls;
import org.tomlj.TomlParseResult;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工厂
 *
 * @author kongweiguang
 */
public class DS {

    public static final Map<String, DataSource> cache = new ConcurrentHashMap<>();

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
    public static DataSource of(String source) {
        return cache.computeIfAbsent(source, s -> {
            TomlParseResult result = Tomls.resource(DB.config);
            Properties props = new Properties();
            props.putAll(result.getTable(source).toMap());
            props.put("user", props.getProperty("username"));
            return new JdkDataSource(props);
        });
    }

    /**
     * 从配置文件获取Hikari数据源
     *
     * @param source 数据源名
     * @return 数据源
     */
    public static DataSource ofHikari(String source) {
        return cache.computeIfAbsent(source, s -> {
            TomlParseResult result = Tomls.resource(DB.config);
            Properties props = new Properties();
            props.putAll(result.getTable(source).toMap());

            Properties p = new Properties();
            p.put("jdbcUrl", props.get("url"));
            p.put("username", props.get("username"));
            p.put("password", props.get("password"));

            HikariConfig hikariConfig = new HikariConfig(p);
            hikariConfig.setDataSourceProperties(props);
            return new HikariDataSource(hikariConfig);
        });
    }
}
