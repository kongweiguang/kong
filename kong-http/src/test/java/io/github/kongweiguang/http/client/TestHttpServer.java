package io.github.kongweiguang.http.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class TestHttpServer {
    private static final AtomicInteger RETRY_ATTEMPTS = new AtomicInteger();
    private static final AtomicInteger RETRY_BODY_ATTEMPTS = new AtomicInteger();
    private static volatile HttpServer server;
    private static volatile int port;

    private TestHttpServer() {
    }

    public static synchronized void start() {
        if (server != null) {
            return;
        }
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(0), 0);
            httpServer.createContext("/", TestHttpServer::handle);
            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();
            server = httpServer;
            port = httpServer.getAddress().getPort();
        } catch (IOException e) {
            throw new RuntimeException("start test server failed", e);
        }
    }

    public static String url(String path) {
        start();
        String fixed = path.startsWith("/") ? path : "/" + path;
        return "http://127.0.0.1:" + port + fixed;
    }

    public static void resetRetry() {
        RETRY_ATTEMPTS.set(0);
    }

    public static int retryAttempts() {
        return RETRY_ATTEMPTS.get();
    }

    public static void resetRetryBody() {
        RETRY_BODY_ATTEMPTS.set(0);
    }

    public static int retryBodyAttempts() {
        return RETRY_BODY_ATTEMPTS.get();
    }

    private static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getRawQuery();
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        switch (path) {
            case "/ok":
            case "/get":
            case "/get/one/two":
                send(exchange, 200, "ok", "text/plain");
                return;
            case "/get_string":
                send(exchange, 200, "hello", "text/plain");
                return;
            case "/echo":
                send(exchange, 200, query == null ? "" : query, "text/plain");
                return;
            case "/post_body":
                send(exchange, 200, requestBody, "application/json");
                return;
            case "/post_form":
            case "/post_mul_form":
                send(exchange, 200, requestBody, "text/plain");
                return;
            case "/post_json":
                send(exchange, 200, (query == null ? "" : query) + "|" + requestBody, "application/json");
                return;
            case "/header":
                String headers = "Authorization=" + exchange.getRequestHeaders().getFirst("Authorization")
                        + "\nUser-Agent=" + exchange.getRequestHeaders().getFirst("User-Agent")
                        + "\nCookie=" + exchange.getRequestHeaders().getFirst("Cookie")
                        + "\nname=" + exchange.getRequestHeaders().getFirst("name")
                        + "\nname1=" + exchange.getRequestHeaders().getFirst("name1")
                        + "\nname2=" + exchange.getRequestHeaders().getFirst("name2");
                send(exchange, 200, headers, "text/plain");
                return;
            case "/download":
                send(exchange, 200, "download-content", "text/plain");
                return;
            case "/error":
                send(exchange, 500, "error", "text/plain");
                return;
            case "/retry":
                int attempt = RETRY_ATTEMPTS.incrementAndGet();
                if (attempt < 3) {
                    send(exchange, 500, "error", "text/plain");
                } else {
                    send(exchange, 200, "success-after-retry", "text/plain");
                }
                return;
            case "/retry-body":
                int bodyAttempt = RETRY_BODY_ATTEMPTS.incrementAndGet();
                if (bodyAttempt < 3) {
                    send(exchange, 200, "short", "text/plain");
                } else {
                    send(exchange, 200, "long-response-body", "text/plain");
                }
                return;
            case "/timeout":
                try {
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                send(exchange, 200, "timeout-ok", "text/plain");
                return;
            case "/int":
                send(exchange, 200, "123", "application/json");
                return;
            case "/bool":
                send(exchange, 200, "true", "application/json");
                return;
            case "/user":
                send(exchange, 200, "{\"name\":\"tom\",\"age\":18,\"hobby\":[\"a\",\"b\"]}", "application/json");
                return;
            case "/users":
                send(exchange, 200, "[{\"name\":\"tom\",\"age\":18,\"hobby\":[\"a\"]}]", "application/json");
                return;
            case "/map":
                send(exchange, 200, "{\"k\":\"v\"}", "application/json");
                return;
            default:
                send(exchange, 404, "not-found", "text/plain");
        }
    }

    private static void send(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
