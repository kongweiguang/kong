<h1 align="center" style="text-align:center;">
  kong-db
</h1>
<p align="center">
	<strong>基于jdbc封装的轻量级操作数据库客户端</strong>
</p>

<p align="center">
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
				<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
	</a>
    <br />
</p>

<br/>

<hr />

# 使用方式

Maven

```xml

<dependency>
    <groupId>io.github.kongweiguang</groupId>
    <artifactId>kong-db</artifactId>
    <version>0.5</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-db:0.5'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-db:0.5")
```

# 简单介绍

## select

```java

@Test
void test1() throws Exception {
    SqlRes sr = Sql
            .select("*")
            .from("users")
            .where("id = ?", 1)
            .orderBy(Order.desc("created_at"))
            .ok();
    assertEquals("SELECT * FROM users WHERE id = ? ORDER BY created_at DESC", sr.sql());

    Map<String, Object> select = run.select(sr.sql(), sr.params());

    User user = Json.toObj(Maps.key2CamelCase(select), new TypeReference<>() {
    });

    System.out.println(user);
}
```

## selectList

```java

@Test
void test2() throws Exception {
    SqlRes sr = Sql
            .select("*")
            .from("users")
            .ok();
    assertEquals("SELECT * FROM users", sr.sql());

    List<Map<String, Object>> maps = run.selectList(sr.sql(), sr.params());

    List<User> collect = maps.stream()
            .map(Maps::key2CamelCase)
            .map(e -> Json.toObj(e, User.class))
            .toList();

    System.out.println("collect = " + collect);
}

```

## page

```java

@Test
void test3() throws Exception {
    SqlRes sr = Sql
            .select("*")
            .from("users")
            .ok();
    assertEquals("SELECT * FROM users", sr.sql());

    PageRes<Map<String, Object>> pages = run.page(sr.sql(), Page.of(1, 1), sr.params());

    System.out.println("pages = " + pages);
}

```

## count

```java

@Test
public void test4() throws Exception {
    SqlRes sr = Sql
            .select("*")
            .from("users")
            .ok();
    assertEquals("SELECT * FROM users", sr.sql());

    long select = run.count(sr.sql(), sr.params());

    System.out.println(select);
}

```

## insert
```java

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

```

## update
```java
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
```
## delete
```java

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
```

## 连表查询
```java

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
```