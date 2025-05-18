package io.github.kongweiguang.db;


import io.github.kongweiguang.db.ds.DS;

import javax.sql.DataSource;

/**
 * 数据库工具类
 *
 * @author kongweiguang
 */
public class DB {
    public static final String config = "kong-db.toml";

    /**
     * 获取数据库执行,采用默认数据源
     *
     * @param source 数据源名
     * @return DbRun
     */
    public static DbRun of(String source) {
        return DbRun.of(DS.of(source));
    }

    /**
     * 获取数据库执行，采用hikari数据源
     *
     * @param source 数据源名
     * @return DbRun
     */
    public static DbRun ofHikari(String source) {
        return DbRun.of(DS.ofHikari(source));
    }

    /**
     * 获取数据库执行,采用指定数据源
     *
     * @param ds 数据源
     * @return DbRun
     */
    public static DbRun of(DataSource ds) {
        return DbRun.of(ds);
    }

}
