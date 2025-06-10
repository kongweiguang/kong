package io.github.kongweiguang.test;

import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.sql.Sql;
import org.junit.jupiter.api.Test;

public class TxTest {
    private static final DB MYSQL = DB.of("mysql");

    @Test
    public void test() throws Exception {
        MYSQL.tx(db -> {
            db.sql(Sql.of().first("""
                            update users
                            set username = 'yyy'
                            where id = 22;
                            """).ok())
                    .exec();
            int a = 1 / 0;
            db.sql(Sql.of().last("""
                            update users
                            set username = 'xxx'
                            where id = 21;
                            """).ok())
                    .exec();
        });
    }
}
