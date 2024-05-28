<h1 align="center" style="text-align:center;">
  kong-json
</h1>
<p align="center">
	<strong>基于jackson封装的json工具</strong>
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
    <artifactId>kong-json</artifactId>
    <version>0.1</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-json:0.1'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-json:0.1")
```

# 简单介绍

构建json object 和json array

```java

public class JsonBuilderTest {

    User u = new User().setAge(1).setName("kong").setHobby(new String[]{"j", "n"});

    /**
     * {
     *   "a" : "b",
     *   "c" : [ "d1", "d2" ],
     *   "e" : "f",
     *   "g" : [ "1", "2", "3", "4", "4" ],
     *   "u1" : "{\"name\":\"kong\",\"age\":1,\"hobby\":[\"j\",\"n\"]}",
     *   "u2" : {
     *     "name" : "kong",
     *     "age" : 1,
     *     "hobby" : [ "j", "n" ]
     *   },
     *   "i" : "1"
     * }
     * 4
     *
     * @throws Exception
     */
    @Test
    void testJsonObj() throws Exception {

        final String str = JsonObj.of()
                .put("a", "b")
                .putAry("c", o -> o.add("d1").add("d2"))
                .put("e", "f")
                .putAry("g", c -> c.addColl(Arrays.asList(1, 2, 3, 4, 4)))
                .put("u1", u)
                .putObj("u2", u)
                .put("i", 1)
                .toPrettyJson();

        System.out.println(str);

        System.out.println(Json.toNode(str).get("g").get(3).asInt());
    }

    /**
     * ["1","2","3",{"name":"kong","age":1,"hobby":["j","n"]},{"a":"a","b":"b"},["6","7"],"0","0"]
     * [1, 2, 3, {name=kong, age=1, hobby=[j, n]}, {a=a, b=b}, [6, 7], 0, 0]
     *
     * @throws Exception
     */
    @Test
    void testJsonAry() throws Exception {

        final String ary = JsonAry.of()
                .add(1)
                .add(2)
                .add(3)
                .addObj(u)
                .addObj(c -> c.put("a", "a").put("b", "b"))
                .addAry(c -> c.add(6).add(7))
                .addColl(Arrays.asList(0, 0))
                .toJson();

        System.out.println(ary);

        final List<Object> list = Json.toList(ary, Object.class);

        System.out.println(list);
    }

    /**
     * {1=true}
     * {"1":"true"}
     *
     * @throws Exception
     */
    @Test
    void test1() throws Exception {
        final JsonObj jsonObj = Json.obj().put("1", "true");
        System.out.println(jsonObj.toMap());
        System.out.println(jsonObj.toJson());
    }

    /**
     * ["1","2",["66","888"],{"name":"kong","age":1,"hobby":["j","n"]}]
     *
     * @throws Exception
     */
    @Test
    void test2() throws Exception {
        final String json = Json.ary().add(1).add(new BigDecimal(2)).addAry(e -> e.add(66).add(888)).addObj(u).toJson();
        System.out.println(json);
    }

    /**
     *  {
     *   "1" : null
     *  }
     *
     * @throws Exception
     */
    @Test
    void test3() throws Exception {
        System.out.println(Json.obj().put("1", null).toPrettyJson());
    }
}
```