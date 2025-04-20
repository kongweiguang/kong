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
    <artifactId>kong-socket</artifactId>
    <version>0.5</version>
</dependency>
```

Gradle

```
implementation 'io.github.kongweiguang:kong-socket:0.5'
```

Gradle-Kotlin

```
implementation("io.github.kongweiguang:kong-socket:0.5")
```

# 简单介绍

这是一个基于Java NIO的高性能网络服务器工具包，提供了简单易用的API来创建和配置NIO服务器和客户端。

## 特性

- 基于Java NIO的非阻塞I/O
- Boss/Worker线程模型
- 可自定义请求处理器
- 简单易用的API
- 支持多端口绑定
- 可配置的线程数和缓冲区大小

# 快速开始

## 创建简单的HTTP服务器

```java
// 创建并启动一个HTTP服务器
new NioServer()
    .setRequestHandler(new DefaultRequestHandler("Hello World!"))
    .bind(8080);

System.out.println("HTTP服务器已启动，监听端口: 8080");
