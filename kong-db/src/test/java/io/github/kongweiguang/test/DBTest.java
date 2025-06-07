package io.github.kongweiguang.test;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.map.Maps;
import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.run.DbRun;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.sql.*;
import io.github.kongweiguang.db.util.Wheres;
import io.github.kongweiguang.test.domain.User;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBTest {
    private static final DB run = DB.ofHikari("mysql");
//    private static final DbRun run = null;

    @Test
    public void test16() throws Exception {

        SqlRes res = Sql.select("u.id")
                .from("users u")
                .leftJoin("user_roles ur ").on("u.id = ur.user_id")
                .where(WhereGroup.of(Where.likeEnd("username", "kong")),
                        WhereGroup.and(Where.eq("email", "123@qq"), Where.ne("first_name", "zhang"))
                )
                .orderBy(Order.desc("created_at"))
                .ok();
        assertEquals("SELECT u.id FROM users u LEFT JOIN user_roles ur  ON u.id = ur.user_id WHERE (username LIKE '?%' ) AND (email =?  AND first_name !=? ) ORDER BY created_at DESC ", res.sql());
    }

    @Test
    public void test15() throws Exception {
        Pair<String, List<Object>> p1 = Wheres.buildGroup(
                WhereGroup.and(
                        Where.eq("id", 23),
                        Where.eq("username", "zhang_san")
                ),
                WhereGroup.or(
                        Where.eq("id", 20),
                        Where.eq("username", "li_si")
                )
        );
        assertEquals("AND (id =?  AND username =? ) OR (id =?  AND username =? )", p1.k());

        Pair<String, List<Object>> p2 = Wheres.buildGroup(
                WhereGroup.of(
                        Where.eq("id", 23),
                        Where.eq("username", "zhang_san")

                ),
                WhereGroup.or(
                        Where.eq("id", 20),
                        Where.eq("username", "li_si")
                )
        );
        assertEquals("(id =?  AND username =? ) OR (id =?  AND username =? )", p2.k());

        Pair<String, List<Object>> p3 = Wheres.buildGroup(
                WhereGroup.not(
                        Where.eq("id", 10),
                        Where.eq("username", "wang_yi")
                )
        );
        assertEquals("NOT (id =?  AND username =? )", p3.k());

    }

    @Test
    public void test14() throws Exception {
        Where eq = Where.eq("id", 23);
        Where ne = Where.ne("id", 23);
        Where gt = Where.gt("id", 23);
        Where lt = Where.lt("id", 23);
        Where ge = Where.ge("id", 23);
        Where le = Where.le("id", 23);
        Where like = Where.like("id", "23");
        Where likeStart = Where.likeStart("id", "23");
        Where likeEnd = Where.likeEnd("id", "23");
        Where in = Where.in("id", Arrays.asList(23, 24));
        Where notIn = Where.notIn("id", Arrays.asList(23, 24));
        Where isNull = Where.isNull("id");
        Where isNotNull = Where.isNotNull("id");
        Where between = Where.between("id", 23, 24);
        Where notBetween = Where.notBetween("id", 23, 24);

        System.out.println("eq = " + eq);
        System.out.println("ne = " + ne);
        System.out.println("gt = " + gt);
        System.out.println("lt = " + lt);
        System.out.println("ge = " + ge);
        System.out.println("le = " + le);
        System.out.println("like = " + like);
        System.out.println("likeStart = " + likeStart);
        System.out.println("likeEnd = " + likeEnd);
        System.out.println("in = " + in);
        System.out.println("notIn = " + notIn);
        System.out.println("isNull = " + isNull);
        System.out.println("isNotNull = " + isNotNull);
        System.out.println("between = " + between);
        System.out.println("notBetween = " + notBetween);

        System.out.println("----------------------------------------");
        System.out.println("eq.ok() = " + eq.ok());
        System.out.println("ne.ok() = " + ne.ok());
        System.out.println("gt.ok() = " + gt.ok());
        System.out.println("lt.ok() = " + lt.ok());
        System.out.println("ge.ok() = " + ge.ok());
        System.out.println("le.ok() = " + le.ok());
        System.out.println("like.ok() = " + like.ok());
        System.out.println("likeStart.ok() = " + likeStart.ok());
        System.out.println("likeEnd.ok() = " + likeEnd.ok());
        System.out.println("in.ok() = " + in.ok());
        System.out.println("notIn.ok() = " + notIn.ok());
        System.out.println("isNull.ok() = " + isNull.ok());
        System.out.println("isNotNull.ok() = " + isNotNull.ok());
        System.out.println("between.ok() = " + between.ok());
        System.out.println("notBetween.ok() = " + notBetween.ok());
    }

    @Test
    public void test13() throws Exception {
        SqlRes sr = Sql
                .deleteFrom("users")
                .where("id = ?", 23)
                .ok();
        assertEquals("DELETE FROM users WHERE id = ? ", sr.sql());

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);

    }

    @Test
    public void test12() throws Exception {
        SqlRes sr = Sql
                .update("users")
                .set("password =? ,email = ? ", "111111", "11111@163.com")
                .where("id = ?", 23)
                .ok();
        assertEquals("UPDATE users SET password =? ,email = ?  WHERE id = ? ", sr.sql());

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test11() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{23, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{21, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{22, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        SqlRes sr = Sql
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value("?", "?", "?", "?", "?", "?", "?", "?")
                .ok();

        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?)", sr.sql());

        int[] execute = run.executeBatch(sr.sql(), list);
        System.out.println(Arrays.toString(execute));
    }

    @Test
    public void test10() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{15, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{16, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        SqlRes sr = Sql
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .values(list)
                .ok();

        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?),(?,?,?,?,?,?,?,?)", sr.sql());

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test9() throws Exception {
        SqlRes sr = Sql
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value(11, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .value(12, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .ok();
        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?),(?,?,?,?,?,?,?,?)", sr.sql());

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test8() throws Exception {
        SqlRes sr = Sql
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value(10, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .ok();
        assertEquals("INSERT  INTO users  (id, username, password, email, first_name, last_name, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?)", sr.sql());

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test7() throws Exception {
        DbRun run1 = DB.ofHikari("pgsql");
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .ok();
        System.out.println("sr = " + sr);
        PageRes<Map<String, Object>> pages = run1.page(sr.sql(), Page.of(1, 2), sr.params());
        System.out.println("pages = " + pages);
    }

    @Test
    public void test6() throws Exception {
        SqlRes rs = Sql
                .select("*")
                .from("users u")
                .innerJoin("user_roles ur").on("u.id = ur.user_id and u.id = ?", 2)
                .ok();
        assertEquals("SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id and u.id = ? ", rs.sql());

        List<Map<String, Object>> maps = run.selectList(rs.sql(), rs.params());
        System.out.println("maps = " + maps);
    }

    @Test
    public void test5() throws Exception {
        SqlRes rs = Sql
                .select("*")
                .from("users u")
                .innerJoin("user_roles ur").on("u.id = ur.user_id")
                .where("u.id = ?", 1)
                .ok();
        assertEquals("SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id WHERE u.id = ? ", rs.sql());

        List<Map<String, Object>> maps = run.selectList(rs.sql(), rs.params());
        System.out.println("maps = " + maps);
    }

    @Test
    public void test4() throws Exception {
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .ok();
        assertEquals("SELECT * FROM users ", sr.sql());

        long select = run.count(sr.sql(), sr.params());

        System.out.println(select);
    }

    @Test
    void test3() throws Exception {
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .ok();
        assertEquals("SELECT * FROM users ", sr.sql());

        PageRes<Map<String, Object>> pages = run.page(sr.sql(), Page.of(1, 5), sr.params());
        List<User> list = Maps.toBeanList(pages.data(), User.class);
        System.out.println("list = " + list);
        System.out.println("pages = " + pages);
    }

    @Test
    void test2() throws Exception {
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .ok();
        assertEquals("SELECT * FROM users ", sr.sql());

        List<Map<String, Object>> maps = run.selectList(sr.sql(), sr.params());

        List<User> list = Maps.toBeanList(maps, User.class);

        System.out.println("collect = " + list);
    }

    @Test
    void test1() throws Exception {
        SqlRes sr = Sql
                .select("*")
                .from("users")
                .where("id = ?", 1)
                .orderBy(Order.desc("created_at"))
                .ok();
        assertEquals("SELECT * FROM users WHERE id = ? ORDER BY created_at DESC ", sr.sql());

        Map<String, Object> select = run.select(sr.sql(), sr.params());
        System.out.println(select);
        User user = Maps.toBean(select, User.class);
        System.out.println(user);
    }
}
