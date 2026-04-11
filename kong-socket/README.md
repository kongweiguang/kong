<h1 align="center" style="text-align:center;">
  kong-socket
</h1>
<p align="center">
	<strong>Java NIO服务器工具类</strong>
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
    <artifactId>kong-socket</artifactId>
    <version>0.6</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-socket:0.6'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-socket:0.6")
```

# 简单介绍

这是一个基于Java NIO的高性能网络服务器工具包，提供了简单易用的API来创建和配置NIO服务器和客户端

## 特性

- 基于Java NIO的非阻塞I/O
- Boss/Worker线程模型
- 可自定义请求处理器
- 简单易用的API
- 支持多端口绑定
- 可配置的线程数和缓冲区大小

# 快速开始

## server

```java
/**
 * NIO服务器示例类
 * 展示如何使用NioServer工具类创建和配置NIO服务器
 */
public class ServerTest {

    public static void main(String[] args) {
        // 创建自定义配置
        NioServerConfig config = NioServerConfig.of()
                .bossThreads(1)
                .workerThreads(4)
                .bufferSize(8192);

        // 创建服务器实例
        NioServer.of(config)
//                .socketHandler(new DefaultRequestHandler())
                .socketHandler(new EchoSocketHandler())
                .bind(8888)
                .bind("localhost", 8887);

        System.out.println("服务器已启动，监听端口: 8888, 8887");
        LockSupport.park();
    }

}
```

## client

```java

/**
 * NIO客户端示例类
 * 展示如何使用NioClient工具类创建和配置NIO客户端
 */
public class ClientTest {

    public static void main(String[] args) {
        // 创建客户端实例并连接
        NioClient client = NioClient.of(c -> c.bufferSize(100))
                .socketHandler((response, channel) -> {
                    //打印响应
                    byte[] data = new byte[response.remaining()];
                    response.get(data);
                    System.out.println("收到响应: " + new String(data, StandardCharsets.UTF_8));
                    return null;
                })
                .connect("localhost", 8888);

        System.out.println("客户端已启动，连接到服务器: localhost:8888");


        // 等待连接建立
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要发送的消息:");
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && !request.trim().isEmpty()) {
                System.out.println("request = " + request);
                client.send(request);
            }
        }
    }

}
```
