<h1 align="center" style="text-align:center;">
  kong-http
</h1>
<p align="center">
	<strong>基于jdbc封装的轻量级操作数据库客户端</strong>
</p>

<p align="center">
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" alt="jdk-8+" />
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
    <version>0.4</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-db:0.4'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-db:0.4")
```

# 简单介绍

## select

```java

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
```

## selectList

```java

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

```

## page

```java

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
```

## count

```java

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
```