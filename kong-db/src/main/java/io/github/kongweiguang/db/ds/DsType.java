package io.github.kongweiguang.db.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;
import java.util.Properties;

/**
 * 数据源枚举
 *
 * @author kongweiguang
 */
public enum DsType {

    JDK("jdk", (source, conf) -> {
        Properties props = new Properties();
        props.putAll((Map<?, ?>) conf.get(source));
        props.put("user", props.getProperty("username"));
        return new JdkDs(props);
    }),

    HIKARI("hikari", (source, conf) -> {
        Properties props = new Properties();
        props.putAll((Map<?, ?>) conf.get(source));

        Properties p = new Properties();
        p.put("jdbcUrl", props.get("url"));
        p.put("username", props.get("username"));
        p.put("password", props.get("password"));

        HikariConfig hikariConfig = new HikariConfig(p);
        hikariConfig.setDataSourceProperties(props);
        return new HikariDataSource(hikariConfig);
    });

    private final String desc;
    private final DsFun dsFun;

    DsType(String desc, DsFun dsFun) {
        this.desc = desc;
        this.dsFun = dsFun;
    }

    public String desc() {
        return desc;
    }

    public DsFun dsFun() {
        return dsFun;
    }


}
