package io.github.kongweiguang.test;

import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.DbRun;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.sql.Order;
import io.github.kongweiguang.db.sql.Sql;
import io.github.kongweiguang.db.sql.SqlRes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class DBTest {
    private static final DbRun run = DB.ofHikari("mysql");

    @Test
    void test3() {
        SqlRes sr = Sql.of()
                .select("*")
                .from("user_info")
                .ok();
        System.out.println("sr = " + sr);
        PageRes<Map<String, Object>> pages = run.page(sr.sql(), Page.of(0, 1), sr.params());
        System.out.println("pages = " + pages);
    }

    @Test
    void test2() {
        SqlRes sr = Sql.of()
                .select("*")
                .from("user_info")
                .ok();
        System.out.println("sr = " + sr);
        List<Map<String, Object>> maps = run.selectList(sr.sql(), sr.params());
        System.out.println("maps = " + maps);
    }

    @Test
    void test1() {
        SqlRes sr = Sql.of()
                .select("*")
                .from("user_info")
                .where("user_id = ?", 123)
                .orderBy(Order.desc("user_id"))
                .ok();
        System.out.println("sr = " + sr);
        Map<String, Object> select = run.select(sr.sql(), sr.params());
        System.out.println(select);
    }
}
