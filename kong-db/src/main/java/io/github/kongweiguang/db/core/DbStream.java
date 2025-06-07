package io.github.kongweiguang.db.core;

import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.sql.SqlRes;

import java.sql.SQLException;
import java.util.Map;

public class DbStream {

    private final DB db;
    private final SqlRes sqlRes;

    public DbStream(DB db, SqlRes sqlRes) {
        this.db = db;
        this.sqlRes = sqlRes;
    }

    public Map<String, Object> select() throws SQLException {
        return db.select(sqlRes.sql(), sqlRes.params());
    }

}
