package io.github.kongweiguang.db;


import io.github.kongweiguang.db.ds.DS;

import javax.sql.DataSource;

public class DB {
    public static final String config = "config.toml";

    public static DbRun of(String source) {
        return DbRun.of(DS.of(source));
    }

    public static DbRun ofHikari(String source) {
        return DbRun.of(DS.ofHikari(source));
    }

    public static DbRun of(DataSource ds) {
        return DbRun.of(ds);
    }

}
