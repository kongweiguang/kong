package io.github.kongweiguang.test;

import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.sql.Order;
import io.github.kongweiguang.db.sql.Sql;
import io.github.kongweiguang.db.sql.SqlRes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbChainTest {
    private static final DB MYSQL = DB.of("mysql");

    @Test
    public void test1() throws Exception {
        SqlRes rql = Sql.select("*")
                .from("users")
                .where("id = ?", 1)
                .orderBy(Order.desc("created_at"))
                .ok();

        Map<String, Object> one = MYSQL.sql(rql).one();
        System.out.println(one);
    }

    @Test
    public void test2() throws Exception {
        SqlRes sr = Sql
                .select("count(*)")
                .from("users")
                .ok();

        assertEquals("SELECT count(*) FROM users ", sr.sql());

        long select = MYSQL.sql(sr).count();

        System.out.println(select);
    }

    @Test
    public void test3() throws Exception {
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .where("id =?", 666)
                .ok();

        assertEquals("SELECT * FROM users WHERE id =? ", sr.sql());

        boolean select = MYSQL.sql(sr).exist();

        System.out.println(select);
    }
}
