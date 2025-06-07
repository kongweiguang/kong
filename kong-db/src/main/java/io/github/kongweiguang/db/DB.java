package io.github.kongweiguang.db;


import io.github.kongweiguang.db.ds.DS;
import io.github.kongweiguang.db.run.DbRun;

import javax.sql.DataSource;

/**
 * 数据库工具类
 *
 * @author kongweiguang
 */
public class DB extends DbRun {
    public static final String config = "kong-db.toml";

    public DB(DataSource ds) {
        super(ds);
    }

    /**
     * 获取数据库执行,采用默认数据源
     *
     * @param source 数据源名
     * @return DbRun
     */
    public static DB of(String source) {
        return new DB(DS.of(source));
    }

    /**
     * 获取数据库执行，采用hikari数据源
     *
     * @param source 数据源名
     * @return DbRun
     */
    public static DB ofHikari(String source) {
        return of(DS.ofHikari(source));
    }

    /**
     * 获取数据库执行,采用指定数据源
     *
     * @param ds 数据源
     * @return DbRun
     */
    public static DB of(DataSource ds) {
        return new DB(ds);
    }

}
