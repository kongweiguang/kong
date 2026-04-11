package io.github.kongweiguang.http.client.core;

import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.builder.DefHTTPReqBuilder;
import io.github.kongweiguang.http.client.builder.SSEReqBuilder;
import io.github.kongweiguang.http.client.builder.WSReqBuilder;
import io.github.kongweiguang.http.common.core.ContentType;
import io.github.kongweiguang.http.common.core.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjTest {

    @Test
    void test1() {
        DefHTTPReqBuilder custom = Req.of().method(Method.GET).url("http://localhost:8080/get");
        Assertions.assertEquals(Method.GET, custom.method());

        Assertions.assertEquals(Method.GET, Req.get("http://localhost:8080/get").method());
        Assertions.assertEquals(Method.POST, Req.post("http://localhost:8080/post").method());
        Assertions.assertEquals(Method.DELETE, Req.delete("http://localhost:8080/delete").method());
        Assertions.assertEquals(Method.PUT, Req.put("http://localhost:8080/put").method());
        Assertions.assertEquals(Method.PATCH, Req.patch("http://localhost:8080/patch").method());
        Assertions.assertEquals(Method.HEAD, Req.head("http://localhost:8080/head").method());
        Assertions.assertEquals(Method.OPTIONS, Req.options("http://localhost:8080/options").method());
        Assertions.assertEquals(Method.TRACE, Req.trace("http://localhost:8080/trace").method());
        Assertions.assertEquals(Method.CONNECT, Req.connect("http://localhost:8080/connect").method());

        DefHTTPReqBuilder form = Req.formUrlencoded("http://localhost:8080/formUrlencoded");
        Assertions.assertTrue(form.contentType().contains(ContentType.FORM_URLENCODED.v()));

        DefHTTPReqBuilder multipart = Req.multipart("http://localhost:8080/multipart");
        Assertions.assertTrue(multipart.contentType().contains(ContentType.MULTIPART.v()));

        WSReqBuilder ws = Req.ws("http://localhost:8080/ws");
        Assertions.assertEquals(ReqType.ws, ws.reqType());

        SSEReqBuilder sse = Req.sse("http://localhost:8080/sse");
        Assertions.assertEquals(ReqType.sse, sse.reqType());
    }

}
