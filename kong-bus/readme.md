<h1 align="center" style="text-align:center;">
  kong-bus
</h1>
<p align="center">
	<strong>类似git操作的轻量级的eventbus</strong>
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
    <artifactId>kong-bus</artifactId>
    <version>0.3</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-bus:0.1'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-bus:0.1")
```

# 简单介绍

根据分支拉取消息

```java
public class AttrTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<String, String>hub().pull(branch, System.out::println);

        //推送消息
        Bus.<String, Void>hub().push(Oper.<String, Void>of(branch, "content").tag("k", "v"));
    }
}

```

推送对象

```java
public class PushEntityTest {

    @Test
    void test1() throws Exception {
        final User user = new User(99, "kpp", new String[]{"1", "2"});

        hub().pull(User.class, h -> {
            System.out.println(h);
            h.res("123");
        });

        hub().push(user);

    }
}
```

异步订阅

```java

public class AsyncTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<String, String>hub().pull(branch, h -> CompletableFuture.runAsync(() -> {
            h.res("123");
            System.out.println(Thread.currentThread().getName());
            System.out.println(h);
        }));

        //推送消息
        Bus.<String, String>hub().push(branch, "content", r -> System.out.println(r));
    }
}
```

区分不同的hub
```java
public class CustomTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub("hub1").pull(branch, System.out::println);
        //拉取默认的消息
        hub().pull(branch, System.out::println);


        //推送消息
        hub("hub1").push(branch, "content", e -> System.out.println("callback 1 -> " + e));
    }
}
```

删除拉取
```java
public class DelPullTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, new Merge<Oper<Object, Object>>() {
            @Override
            public String name() {
                return "k_pull";
            }

            @Override
            public void mr(final Oper<Object, Object> a) throws Exception {
                System.out.println(a);
            }
        });


        //推送消息
        hub().push(branch, "content");

        //删除拉取
        hub().remove(branch, "k_pull");

        //推送消息
        hub().push(branch, "content");

    }
}
```


多个拉取
```java
public class MultiPullTest {
    String branch = "branch.test1";

    @Test
    void test1() throws Exception {
        //拉取消息
        hub().pull(branch, System.out::println);
        hub().pull(branch, System.out::println);

        //推送消息
        hub().push(branch, "content");
    }
}
```

注解方式
```java
public class MyHandler {
    @Pull
    public String fn(User user) {
        System.out.println(user);
        return "hello";
    }


    @Pull("bala")
    public String fn1() {
        System.out.println("fn1");
        return "hello1";
    }

    @Pull("bala")
    public void fn2() {
        System.out.println("fn2");
    }

    @Pull
    public String fn3(Oper<User, String> oper) {
        System.out.println(oper);
        return "hello2";
    }


    //push
    public User push_user() {
        return new User(1, "push_1", new String[]{"h1", "h2"});
    }

    public User push_user1() {
        return new User(2, "push_2", new String[]{"h1", "h2"});
    }


}

```

```java
public class PushObjMethodTest {
    @Test
    void test1() throws Exception {
        //设置拉取消息的处理
        hub().pullClass(new MyHandler());

        //推送tipic为bala的消息
        hub().push(Oper.of("bala", new User(1, "k", new String[]{"h"})), object -> System.out.println("object = " + object));

        //推送topic为bala1的消息
        hub().push("bala1", new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        //推送user类的topic
        hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        Bus.<User, String>hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

    }
}

```

排序拉取
```java
public class SortPullTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, 0, e -> System.out.println("1"));
        hub().pull(branch, -1, e -> System.out.println("2"));


        //推送消息
        hub().push(branch, "content", e -> System.out.println("callback 1 -> " + e));
        //打印结果 2,1
    }
}
```

使用springboot
```java

@Component
public class MyHandler {
    @Pull
    public String fn(User user) {
        System.out.println(user);
        return "hello";
    }

    @Pull("bala")
    public String fn1() {
        System.out.println("fn1");
        return "hello1";
    }

    @Pull("bala")
    public void fn2() {
        System.out.println("fn2");
    }

    @Pull
    public String fn3(Oper<User, String> oper) {
        System.out.println(oper);
        return "hello2";
    }

}
```
```java
@ContextConfiguration
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan("io.github.kongweiguang.bus.starter.test")
public class Test1 {

    @Test
    public void test() throws Exception {

        //推送branch为bala的消息
        hub().push(Oper.of("bala", new User(1, "k", new String[]{"h"})), object -> System.out.println("object = " + object));

        //推送branch为bala1的消息
        hub().push("bala1", new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        //推送user类的branch
        hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        Bus.<User, String>hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

    }
}
```