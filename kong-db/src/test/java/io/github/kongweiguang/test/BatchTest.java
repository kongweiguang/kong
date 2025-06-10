package io.github.kongweiguang.test;

import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.sql.Sql;
import io.github.kongweiguang.db.sql.SqlRes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchTest {
    private static final DB MYSQL = DB.of("mysql");

    @Test
    public void test() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{31, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{32, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{33, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});

        SqlRes sr = Sql.insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value("?", "?", "?", "?", "?", "?", "?", "?")
                .ok();

        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?)", sr.sql());

        int[] execute = MYSQL.executeBatch(sr.sql(), list);
        System.out.println(Arrays.toString(execute));
    }

    @Test
    public void test2() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{41, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{42, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{43, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});

        SqlRes sr = Sql.insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value("?", "?", "?", "?", "?", "?", "?", "?")
                .ok();

        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?)", sr.sql());

        int[] execute = MYSQL.sql(sr).batch(list);
        System.out.println(Arrays.toString(execute));
    }
}
