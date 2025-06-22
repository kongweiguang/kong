package io.github.kongweiguang.db;


import io.github.kongweiguang.db.ds.DsConf;
import io.github.kongweiguang.db.ds.DsFactory;
import io.github.kongweiguang.db.ds.DsType;
import io.github.kongweiguang.db.run.ChainDbRun;
import io.github.kongweiguang.db.sql.SqlRes;

import javax.sql.DataSource;
import java.util.function.Consumer;

/**
 * 数据库工具类
 *
 * @author kongweiguang
 */
public class DB extends ChainDbRun {
    public static final String config = "kong-db.toml";

    public DB(DataSource ds, SqlRes sqlRes) {
        super(ds, sqlRes);
    }

    /**
     * 获取数据库执行,采用默认数据源
     *
     * @param source 数据源名
     * @return DbRun
     */
    public static DB of(String source) {
        return of(DsFactory.of(source, DsType.JDK));
    }

    /**
     * 获取数据库执行,采用指定数据源
     *
     * @param confCsm 数据源配置
     * @return DbRun
     */
    public static DB of(Consumer<DsConf> confCsm) {
        DsConf conf = new DsConf();
        confCsm.accept(conf);
        return of(DsFactory.of(conf.source(), conf.type()));
    }

    /**
     * 获取数据库执行,采用指定数据源
     *
     * @param ds 数据源
     * @return DbRun
     */
    public static DB of(DataSource ds) {
        return new DB(ds, null);
    }

    /**
     * 设置sql
     *
     * @param sql sql
     * @return DbRun
     */
    public DB sql(SqlRes sql) {
        sqlRes = sql;
        return this;
    }

}
