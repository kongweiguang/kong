package io.github.kongweiguang.test;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.core.lang.Maps;
import io.github.kongweiguang.db.DB;
import io.github.kongweiguang.db.DbRun;
import io.github.kongweiguang.db.page.Page;
import io.github.kongweiguang.db.page.PageRes;
import io.github.kongweiguang.db.sql.*;
import io.github.kongweiguang.db.util.Wheres;
import io.github.kongweiguang.json.Json;
import io.github.kongweiguang.test.domain.User;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class DBTest {
    private static final DbRun run = DB.ofHikari("mysql");

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
        System.out.println("p1 = " + p1);
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
        System.out.println("p2 = " + p2);

        Pair<String, List<Object>> p3 = Wheres.buildGroup(
                WhereGroup.not(
                        Where.eq("id", 10),
                        Where.eq("username", "wang_yi")
                )
        );
        System.out.println("p3 = " + p3);
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
        SqlRes sr = Sql.of()
                .deleteFrom("users")
                .where("id = ?", 23)
                .ok();

        System.out.println(sr);

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);

    }

    @Test
    public void test12() throws Exception {
        SqlRes sr = Sql.of()
                .update("users")
                .set("password =? ,email = ? ", "111111", "11111@163.com")
                .where("id = ?", 23)
                .ok();

        System.out.println(sr);

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test11() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{23, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{21, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{22, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        SqlRes sr = Sql.of()
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value("?", "?", "?", "?", "?", "?", "?", "?")
                .ok();

        System.out.println(sr);

        int[] execute = run.executeBatch(sr.sql(), list);
        System.out.println(Arrays.toString(execute));
    }

    @Test
    public void test10() throws Exception {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{15, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        list.add(new Object[]{16, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date()});
        SqlRes sr = Sql.of()
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .values(list)
                .ok();

        System.out.println(sr);

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test9() throws Exception {
        SqlRes sr = Sql.of()
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value(11, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .value(12, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .ok();

        System.out.println(sr);

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }

    @Test
    public void test8() throws Exception {
        SqlRes sr = Sql.of()
                .insert("users")
                .into("id", "username", "password", "email", "first_name", "last_name", "created_at", "updated_at")
                .value(10, "zhang_san", "123456", "zhang_san@163.com", "zhang", "san", new Date(), new Date())
                .ok();

        System.out.println(sr);

        int execute = run.execute(sr.sql(), sr.params());
        System.out.println(execute);
    }


    @Test
    public void test7() throws Exception {
        DbRun run1 = DB.ofHikari("pgsql");
        SqlRes sr = Sql.of()
                .select("*")
                .from("users")
                .ok();
        System.out.println("sr = " + sr);
        PageRes<Map<String, Object>> pages = run1.page(sr.sql(), Page.of(1, 2), sr.params());
        System.out.println("pages = " + pages);
    }

    @Test
    public void test6() throws Exception {
        SqlRes rs = Sql.of()
                .select("*")
                .from("users u")
                .innerJoin("user_roles ur").on("u.id = ur.user_id and u.id = ?", 2)
                .ok();
        System.out.println("rs = " + rs);
        List<Map<String, Object>> maps = run.selectList(rs.sql(), rs.params());
        System.out.println("maps = " + maps);
    }

    @Test
    public void test5() throws Exception {
        SqlRes rs = Sql.of()
                .select("*")
                .from("users u")
                .innerJoin("user_roles ur").on("u.id = ur.user_id")
                .where("u.id = ?", 1)
                .ok();
        System.out.println("rs = " + rs);
        List<Map<String, Object>> maps = run.selectList(rs.sql(), rs.params());
        System.out.println("maps = " + maps);
    }

    @Test
    public void test4() throws Exception {
        SqlRes sr = Sql.of()
                .select("*")
                .from("users")
                .ok();
        //SELECT * FROM users
        System.out.println("sr = " + sr);
        long select = run.count(sr.sql(), sr.params());
        //10
        System.out.println(select);
    }

    @Test
    void test3() throws Exception {
        SqlRes sr = Sql.of()
                .select("*")
                .from("users")
                .ok();
        //SELECT * FROM users
        System.out.println("sr = " + sr);
        PageRes<Map<String, Object>> pages = run.page(sr.sql(), Page.of(1, 1), sr.params());

        //pages = PageRes[total=10, data=[{password=$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa, updated_at=2025-04-05T00:16:19, last_name=Doe, created_at=2025-04-05T00:16:19, id=1, first_name=John, email=john.doe@example.com, username=john_doe}]]
        System.out.println("pages = " + pages);
    }

    @Test
    void test2() throws Exception {
        SqlRes sr = Sql.of()
                .select("*")
                .from("users")
                .ok();
        //SELECT * FROM users
        System.out.println("sr = " + sr);

        List<Map<String, Object>> maps = run.selectList(sr.sql(), sr.params());

        //maps = [{password=$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa, updated_at=2025-04-05T00:16:19, last_name=Doe, created_at=2025-04-05T00:16:19, id=1, first_name=John, email=john.doe@example.com, username=john_doe}, {password=$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa, updated_at=2025-04-05T00:16:19, last_name=Smith, created_at=2025-04-05T00:16:19, id=2, first_name=Jane, email=jane.smith@example.com, username=jane_smith}, {password=$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa, updated_at=2025-04-05T00:16:19, last_name=Wonder, created_at=2025-04-05T00:16:19, id=3, first_name=Alice, email=alice.wonder@example.com, username=alice_wonder}, {password=123456, updated_at=2025-04-05T14:41:35, last_name=san, created_at=2025-04-05T14:41:35, id=10, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T14:55:18, last_name=san, created_at=2025-04-05T14:55:18, id=11, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T14:55:18, last_name=san, created_at=2025-04-05T14:55:18, id=12, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T14:56:24, last_name=san, created_at=2025-04-05T14:56:24, id=15, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T14:56:24, last_name=san, created_at=2025-04-05T14:56:24, id=16, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T15:09:04, last_name=san, created_at=2025-04-05T15:09:04, id=21, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}, {password=123456, updated_at=2025-04-05T15:09:04, last_name=san, created_at=2025-04-05T15:09:04, id=22, first_name=zhang, email=wangyunchao@163.com, username=zhang_san}]
        System.out.println("maps = " + maps);

        List<User> collect = maps.stream()
                .map(Maps::key2CamelCase)
                .map(e -> Json.toObj(e, User.class))
                .collect(Collectors.toList());
        //collect = [User[id=1, username='john_doe', password='$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa', email='john.doe@example.com', firstName='John', lastName='Doe', createdAt=2025-04-05T00:16:19, updatedAt=2025-04-05T00:16:19, roles=null], User[id=2, username='jane_smith', password='$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa', email='jane.smith@example.com', firstName='Jane', lastName='Smith', createdAt=2025-04-05T00:16:19, updatedAt=2025-04-05T00:16:19, roles=null], User[id=3, username='alice_wonder', password='$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa', email='alice.wonder@example.com', firstName='Alice', lastName='Wonder', createdAt=2025-04-05T00:16:19, updatedAt=2025-04-05T00:16:19, roles=null], User[id=10, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T14:41:35, updatedAt=2025-04-05T14:41:35, roles=null], User[id=11, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T14:55:18, updatedAt=2025-04-05T14:55:18, roles=null], User[id=12, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T14:55:18, updatedAt=2025-04-05T14:55:18, roles=null], User[id=15, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T14:56:24, updatedAt=2025-04-05T14:56:24, roles=null], User[id=16, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T14:56:24, updatedAt=2025-04-05T14:56:24, roles=null], User[id=21, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T15:09:04, updatedAt=2025-04-05T15:09:04, roles=null], User[id=22, username='zhang_san', password='123456', email='wangyunchao@163.com', firstName='zhang', lastName='san', createdAt=2025-04-05T15:09:04, updatedAt=2025-04-05T15:09:04, roles=null]]
        System.out.println("collect = " + collect);
    }

    @Test
    void test1() throws Exception {
        SqlRes sr = Sql.of()
                .select("*")
                .from("users")
                .where("id = ?", 1)
                .orderBy(Order.desc("created_at"))
                .ok();
        //SELECT * FROM users WHERE id = ? ORDER BY created_at DESC
        System.out.println("sr = " + sr);

        Map<String, Object> select = run.select(sr.sql(), sr.params());
        // {password=$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa, updated_at=2025-04-05T00:16:19, last_name=Doe, created_at=2025-04-05T00:16:19, id=1, first_name=John, email=john.doe@example.com, username=john_doe}
        System.out.println(select);

        User user = Json.toObj(Maps.key2CamelCase(select), new TypeReference<User>() {
        });

        //User[id=1, username='john_doe', password='$2a$10$EIXIX.USQKzdbTXDqyXneiHeRXkVkDxBdRpYxL1pJq2YFfjZq2mIpa', email='john.doe@example.com', firstName='John', lastName='Doe', createdAt=2025-04-05T00:16:19, updatedAt=2025-04-05T00:16:19, roles=null]
        System.out.println(user);
    }
}
