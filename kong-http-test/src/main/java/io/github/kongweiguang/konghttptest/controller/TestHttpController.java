package io.github.kongweiguang.konghttptest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TestHttpController {
    private static final AtomicInteger RETRY_ATTEMPTS = new AtomicInteger();
    private static final AtomicInteger RETRY_BODY_ATTEMPTS = new AtomicInteger();
    private static final MediaType JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    private static final MediaType TEXT_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    @GetMapping({"/ok", "/get", "/get/one/two"})
    public ResponseEntity<String> ok() {
        return text(HttpStatus.OK, "ok");
    }

    @GetMapping("/get_string")
    public ResponseEntity<String> getString() {
        return text(HttpStatus.OK, "hello");
    }

    @GetMapping("/echo")
    public ResponseEntity<String> echo(HttpServletRequest request) {
        String query = request.getQueryString();
        return text(HttpStatus.OK, query == null ? "" : query);
    }

    @PostMapping("/post_body")
    public ResponseEntity<String> postBody(@RequestBody(required = false) String body) {
        return json(HttpStatus.OK, body == null ? "" : body);
    }

    @PostMapping({"/post_form", "/post_mul_form"})
    public ResponseEntity<String> postForm(@RequestBody(required = false) String body) {
        return text(HttpStatus.OK, body == null ? "" : body);
    }

    @PostMapping("/post_json")
    public ResponseEntity<String> postJson(HttpServletRequest request, @RequestBody(required = false) String body) {
        String query = request.getQueryString();
        return json(HttpStatus.OK, (query == null ? "" : query) + "|" + (body == null ? "" : body));
    }

    @GetMapping("/header")
    public ResponseEntity<String> header(HttpServletRequest request) {
        String headers = "Authorization=" + request.getHeader("Authorization")
                         + "\nUser-Agent=" + request.getHeader("User-Agent")
                         + "\nCookie=" + request.getHeader("Cookie")
                         + "\nname=" + request.getHeader("name")
                         + "\nname1=" + request.getHeader("name1")
                         + "\nname2=" + request.getHeader("name2");
        return text(HttpStatus.OK, headers);
    }

    @GetMapping("/download")
    public ResponseEntity<String> download() {
        return text(HttpStatus.OK, "download-content");
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return text(HttpStatus.INTERNAL_SERVER_ERROR, "error");
    }

    @GetMapping("/retry")
    public ResponseEntity<String> retry() {
        int attempt = RETRY_ATTEMPTS.incrementAndGet();
        if (attempt < 3) {
            return text(HttpStatus.INTERNAL_SERVER_ERROR, "error");
        }
        return text(HttpStatus.OK, "success-after-retry");
    }

    @GetMapping("/retry-body")
    public ResponseEntity<String> retryBody() {
        int attempt = RETRY_BODY_ATTEMPTS.incrementAndGet();
        if (attempt < 3) {
            return text(HttpStatus.OK, "short");
        }
        return text(HttpStatus.OK, "long-response-body");
    }

    @GetMapping("/timeout")
    public ResponseEntity<String> timeout() throws InterruptedException {
        Thread.sleep(1500L);
        return text(HttpStatus.OK, "timeout-ok");
    }

    @GetMapping("/int")
    public ResponseEntity<String> intValue() {
        return json(HttpStatus.OK, "123");
    }

    @GetMapping("/bool")
    public ResponseEntity<String> boolValue() {
        return json(HttpStatus.OK, "true");
    }

    @GetMapping("/user")
    public ResponseEntity<String> user() {
        return json(HttpStatus.OK, "{\"name\":\"tom\",\"age\":18,\"hobby\":[\"a\",\"b\"]}");
    }

    @GetMapping("/users")
    public ResponseEntity<String> users() {
        return json(HttpStatus.OK, "[{\"name\":\"tom\",\"age\":18,\"hobby\":[\"a\"]}]");
    }

    @GetMapping("/map")
    public ResponseEntity<String> map() {
        return json(HttpStatus.OK, "{\"k\":\"v\"}");
    }

    private static ResponseEntity<String> text(HttpStatus status, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(TEXT_UTF8);
        return new ResponseEntity<>(body, headers, status);
    }

    private static ResponseEntity<String> json(HttpStatus status, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(JSON_UTF8);
        return new ResponseEntity<>(body, headers, status);
    }
}
