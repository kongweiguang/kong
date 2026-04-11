<h1 align="center" style="text-align:center;">
  kong-http
</h1>
<p align="center">
	<strong>基于okhttp封装的轻量级http客户端</strong>
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
    <artifactId>kong-http</artifactId>
    <version>0.6</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-http:0.6'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-http:0.6")
```

# 简单介绍

## 请求对象

```java
public class ObjTest {

    @Test
    void test1() throws Exception {

        //自定义请求创建
        Req.of().method(Method.GET).url("http://localhost:8080/get");

        //基本的http请求
        Req.get("http://localhost:8080/get");
        Req.post("http://localhost:8080/post");
        Req.delete("http://localhost:8080/delete");
        Req.put("http://localhost:8080/put");
        Req.patch("http://localhost:8080/patch");
        Req.head("http://localhost:8080/head");
        Req.options("http://localhost:8080/options");
        Req.trace("http://localhost:8080/trace");
        Req.connect("http://localhost:8080/connect");

        //特殊http请求
        //application/x-www-form-urlencoded
        Req.formUrlencoded("http://localhost:8080/formUrlencoded");
        //multipart/form-data
        Req.multipart("http://localhost:8080/multipart");

        //ws协议请求创建
        Req.ws("http://localhost:8080/ws");

        //sse协议请求创建
        Req.sse("http://localhost:8080/sse");

    }

}
```

## url请求地址

url添加有两种方式，可以混合使用，如果url和构建函数里面都有值，按构建函数里面为主

- 直接使用url方法

```java

public class UrlTest {

    @Test
    void test1() throws Exception {
        Res res = Req.get("http://localhost:8080/get/one/two").ok();

        Assertions.assertEquals("ok", res.str());
    }

    @Test
    void test2() {
        // http://localhost:8080/get/one/two
        Res res = Req.of()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("get")
                .path("one")
                .path("two")
                .ok();

        Assertions.assertEquals("ok", res.str());
    }

    @Test
    void test3() throws Exception {
        // http://localhost:8080/get/one/two
        Res res = Req.get("/get")
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("one")
                .path("two")
                .ok();

        Assertions.assertEquals("ok", res.str());
    }
}
```

## url参数

```java

public class UrlQueryTest {

    @Test
    void test1() throws Exception {
        //http://localhost:8080/get/one/two?q=1&k1=v1&k2=1&k2=2&k3=v3&k4=v4
        Res res = Req.get("http://localhost:8080/get/one/two?q=1")
                .query("k1", "v1")
                .query("k2", Arrays.asList("1", "2"))
                .query(new HashMap<String, Object>() {{
                    put("k3", "v3");
                    put("k4", "v4");
                }})
                .ok();

        //服务端接受{q=[1], k1=[v1], k2=[1, 2], k3=[v3], k4=[v4]}
    }

}
```

## 请求头

设置请求头内容，cookie等

```java
public class HeaderTest {

    @Test
    void test1() throws Exception {
        final Res res = Req.get("http://localhost:8080/header")
                //contentype
                .contentType(ContentType.JSON.v())
                //charset
                .charset(StandardCharsets.UTF_8)
                //user-agent
                .userAgent(UserAgent.Mac.chrome.v())
                //authorization
                .auth("auth qwe")
                //authorization bearer
                .bearer("qqq")
                //header
                .header("name", "value")
                //headers
                .headers(new HashMap<>() {{
                    put("name1", "value1");
                    put("name2", "value2");
                }})
                //cookie
                .cookie("k", "v")
                //cookies
                .cookies(new HashMap<>() {{
                    put("k1", "v1");
                    put("k2", "v2");
                }})
                .ok();

        Assertions.assertEquals("ok", res.str());
        //headers = {Cookie=[k1=v1; k2=v2; k=v;], Accept-encoding=[gzip], Authorization=[Bearer qqq], Content-type=[application/json;charset=UTF-8], Connection=[Keep-Alive], Host=[localhost:8080], User-agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36], Name1=[value1], Name=[value], Name2=[value2]}
    }
}
```

## 请求体

get和head请求就算添加了请求体也不会携带在请求
post请求体必须有个请求体，默认会传入空字符串

```java

public class BodyTest {

    User user = new User().setAge(12).setHobby(new String[]{"a", "b", "c"}).setName("kkk");
    String json = """
            {
                "age": 12,
                "name": "kkk",
                "hobby": ["a", "b", "c"]
            }
            """;

    @Test
    public void test1() throws Exception {
        Res res = Req.post("http://localhost:8080/post_body")
                //自动会将对象转成json字符串，使用jackson
                .json(user)
                .ok();

        Assertions.assertEquals(json, res.str());
    }

    @Test
    public void test2() throws Exception {
        Res res = Req.post("http://localhost:8080/post_body")
                //自动会将对象转成json字符串，使用jackson
                .body("text", ContentType.TEXT_PLAIN.v())
                .ok();
        System.out.println("res.str() = " + res.str());
    }

}
```

### form表单请求

可发送application/x-www-form-urlencoded表单请求，如果需要上传文件则使用multipart/form-data

```java
public class FormTest {

    @Test
    public void testForm() throws IOException {
        //application/x-www-form-urlencoded
        Res ok = Req.formUrlencoded("http://localhost:8080/post_form")
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();
        Assertions.assertEquals("ok", ok.str());
        //{a=[1], b=[2]}
    }

    @Test
    public void test2() throws Exception {
        //multipart/form-data
        Res ok = Req.multipart("http://localhost:8080/post_mul_form")
                .file("test", "test.txt", Files.readAllBytes(Paths.get("C:", "test", "test.txt")))
                .form("a", "1")
                .form(new HashMap<>() {{
                    put("b", "2");
                }})
                .ok();
        Assertions.assertEquals("ok", ok.str());
        //params = {a=[1], b=[2]}
        //files = {test=[io.github.kongweiguang.http.server.core.UploadFile@6231d793]}
    }
}
```

## 异步请求

异步请求返回的是future，也可以使用join()或者get()方法等待请求执行完，具体使用请看CompletableFuture（异步编排）
请求错误的时候不会抛出异常，而是会调用fail回调方法，res内容是空的

```java
public class AsyncTest {

    @Test
    public void test1() throws Exception {
        CompletableFuture<Res> future = Req.get("http://localhost:8080/get")
                .query("a", "1")
                .success(r -> System.out.println(r.str()))
                .fail(t -> System.out.println("error"))
                .okAsync();

        future.get(3, TimeUnit.MINUTES);
    }

    @Test
    public void test2() throws Exception {
        CompletableFuture<Res> future = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .success(r -> System.out.println(r.str()))
                .fail(t -> System.out.println("error"))
                .okAsync();

        Res res = future.get(3, TimeUnit.MINUTES);
        System.out.println(res);
    }
}
```

## 请求超时时间设置

```java
public class TimeoutTest {

    @Test
    void test1() throws Exception {
        Res res = Req.get("http://localhost:8080/timeout")
                .timeout(Duration.ofSeconds(1))
//        .timeout(10, 10, 10)
                .ok();
        System.out.println(res.str());
    }

}
```

## 响应对象

```java

public class ResTest {

    @Test
    void testRes() {
        Res res = Req.get("http://localhost:80/get_string")
                .query("a", "1")
                .query("b", "2")
                .query("c", "3")
                .ok();

        //返回值
        String str = res.str();
        byte[] bytes = res.bytes();
        User obj = res.obj(User.class);
        List<User> obj1 = res.obj(new TypeReference<List<User>>() {
        });
        List<String> list = res.list(String.class);
        Map<String, String> map = res.map(String.class, String.class);
        JsonNode node = res.node();
        InputStream stream = res.stream();
        Integer i = res.i32();
        Long l = res.i64();
        Boolean b = res.bool();

        //响应头
        String ok = res.header("ok");
        Map<String, List<String>> headers = res.headers();

        //状态
        int status = res.code();

        //原始响应
        Response response = res.raw();


    }

}
```

## 重试

重试可以实现同步重试和异步重试，重试的条件可自定义实现

```java

public class RetryTest {

    @Test
    public void testRetry() {
        Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofSeconds(2)))
                .ok();
        System.out.println("res = " + res.str());
    }

    @Test
    public void testRetry2() {
        Res res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(retry -> retry.maxAttempts(3)
                        .delay(Duration.ofSeconds(2))
                        .predicate((r, t) -> {
                            String str = r.str();
                            if (str.length() > 10) {
                                return Pair.of(false, r);
                            }
                            return Pair.of(true, r);
                        }))
                .ok();
        System.out.println("res.str() = " + res.str());
    }

    @Test
    public void testRetry3() {
        //异步重试
        CompletableFuture<Res> res = Req.get("http://localhost:8080/error")
                .query("a", "1")
                .retry(r -> r.maxAttempts(3))
                .okAsync();
        System.out.println("res.join().str() = " + res.join().str());
    }
}
```

## 请求代理

代理默认是http，可以设置socket代理

```java

public class ProxyTest {

    @Test
    void test1() throws Exception {

        OK.conf()
                .proxy("127.0.0.1", 80)
                .proxy(Type.SOCKS, "127.0.0.1", 80)
                .proxyAuthenticator("k", "pass");

        Res res = Req.get("http://localhost:8080/get/one/two")
                .query("a", "1")
                .ok();
    }

    @Test
    void test2() throws Exception {
        Res res = Req.get("http://localhost:8080/get/one/two")
                .config(e -> e.proxy("127.0.0.1", 80)
                        .proxy(Type.SOCKS, "127.0.0.1", 80)
                        .proxyAuthenticator("k", "pass"))
                .query("a", "1")
                .ok();
    }

}
```

## 下载

```java

public class DowTest {

    @Test
    void testDow() {
        Res ok = Req.get("http://localhost:8080/xz").ok();

        try {
            ok.file("C:\\test\\k.txt");
        } catch (IOException e) {
            throw new KongHttpRuntimeException(e);
        }
    }
}
```

## 添加日志

```java

public class LogTest {
    @Test
    public void test() throws Exception {
        Req.get("http://localhost:8080/get/one/two")

                .log(ReqLog.console, HttpLoggingInterceptor.Level.BODY)
                .timeout(Duration.ofMillis(1000))
                .ok()
                .then(r -> {
                    System.out.println(r.code());
                    System.out.println("ok -> " + r.isOk());
                })
                .then(r -> {
                    System.out.println("redirect -> " + r.isRedirect());
                });
    }
}

```

## ws请求

ws请求返回的res对象为null

```java

public class WsTest {

    @Test
    public void test() {
        WSListener listener = new WSListener() {
            @Override
            public void open(WSReqBuilder req, Res res) {
                this.ws.send("123");
            }

            @Override
            public void msg(WSReqBuilder req, String text) {
                System.out.println(text);
            }

            @Override
            public void closed(WSReqBuilder req, int code, String reason) {
                System.out.println(reason);
            }
        };

        WebSocket ws = Req.ws("ws://localhost:8889/ws")
                .query("k", "v")
                .header("h", "v")
                .wsListener(listener)
                .ok();
        Threads.sleep(1000);

        for (int i = 0; i < 100; i++) {
            Threads.sleep(1000);
            ws.send("123");
        }

        Threads.sync(this);
    }

}
```

## sse请求

sse请求返回的res对象为null

```java

public class SseTest {


    @Test
    void test() throws InterruptedException {
        SSEListener listener = new SSEListener() {
            @Override
            public void event(SSEReqBuilder req, SseEvent msg) {
                System.out.println("sse -> " + msg.id());
                System.out.println("sse -> " + msg.type());
                System.out.println("sse -> " + msg.data());
                if (Objects.equals(msg.data(), "done")) {
                    closeCon();
                }
            }

            @Override
            public void open(SSEReqBuilder req, Res res) {
                System.out.println(req);
                System.out.println(res);
            }

            @Override
            public void fail(SSEReqBuilder req, Res res, Throwable t) {
                System.out.println("fail" + t);
            }

            @Override
            public void closed(SSEReqBuilder req) {
                System.out.println("close");
            }
        };

        EventSource es = Req.sse("http://localhost:8080/sse")
                .sseListener(listener)
                .ok();

        Request request = es.request();

        Threads.sync(this);
    }

}
```

## 全局配置设置

```java

public class ConfigTest {

    @Test
    void test1() throws Exception {

        //设置代理
        OK.conf()
                .proxy("127.0.0.1", 80)
                .proxy(Type.SOCKS, "127.0.0.1", 80)
                .proxyAuthenticator("k", "pass")

                //设置拦截器
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        System.out.println(1);
                        return chain.proceed(chain.request());
                    }
                })
                //设置连接池
                .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
                //设置异步调用的线程池
                .exec(Executors.newCachedThreadPool());
    }

}
```

## 单次请求配置

```java
public class SingingConfigTest {
    @Test
    public void test1() throws Exception {
        Res res = Req.get("http://localhost:80/get_string")
                .config(c -> c.followRedirects(false).ssl(false))
                .ok();
    }
}
```
