<h1 align="center" style="text-align:center;">
  kong-json
</h1>
<p align="center">
    <strong>基于 jackson 封装的链式 JSON 工具</strong>
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
    <artifactId>kong-json</artifactId>
    <version>0.6</version>
</dependency>
```

Gradle

```groovy
implementation 'io.github.kongweiguang:kong-json:0.6'
```

Gradle Kotlin

```kotlin
implementation("io.github.kongweiguang:kong-json:0.6")
```

# 特性

- `Json.obj()` / `Json.ary()` 支持链式构建
- 默认按真实 JSON 类型写入：数字是数字，布尔是布尔，`null` 是 `null`
- `putString` / `addString` 用于显式写入字符串
- 保留 `toObj`、`toList`、`toMap`、`toNode` 等 jackson 封装能力

# 推荐写法

## 构建 JSON Object

```java
User user = new User()
        .setAge(1)
        .setName("kong")
        .setHobby(new String[]{"j", "n"});

String json = Json.obj()
        .put("name", "kong")
        .put("age", 1)
        .put("active", true)
        .put("nullable", null)
        .putObj("profile", user)
        .putAry("tags", ary -> ary.add("java").add(8))
        .putString("literalNumber", 1)
        .toPrettyJson();
```

输出：

```json
{
  "name" : "kong",
  "age" : 1,
  "active" : true,
  "nullable" : null,
  "profile" : {
    "name" : "kong",
    "age" : 1,
    "hobby" : [ "j", "n" ]
  },
  "tags" : [ "java", 8 ],
  "literalNumber" : "1"
}
```

## 构建 JSON Array

```java
String json = Json.ary()
        .add(1)
        .add(true)
        .add(null)
        .addObj(user)
        .addAry(ary -> ary.add("nested").add(false))
        .addString(66)
        .toJson();
```

输出：

```json
[1,true,null,{"name":"kong","age":1,"hobby":["j","n"]},["nested",false],"66"]
```

## 对 jackson 的常用封装

```java
String userJson = "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}";

User user = Json.toObj(userJson, User.class);
JsonNode node = Json.toNode(user);
List<User> users = Json.toList("[{\"name\":\"kong\",\"age\":1}]", User.class);
Map<String, Object> map = Json.toMap(user, String.class, Object.class);
```

## 自定义 Mapper

```java
JsonMapper customMapper = JsonMapper.builder().build();
Json.setMapper(customMapper);
```

兼容旧用法：

```java
Json.mapper(customMapper);
```
