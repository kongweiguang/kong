package io.github.kongweiguang.http.client.core;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.kongweiguang.http.client.Req;
import io.github.kongweiguang.http.client.Res;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ResTest {

    @Test
    void testRes() {
        final Res res = Req.get("http://localhost:80/get_string")
                .query("a", "1")
                .query("b", "2")
                .query("c", "3")
                .ok();

        //返回值
        final String str = res.str();
        final byte[] bytes = res.bytes();
        final User obj = res.obj(User.class);
        final List<User> obj1 = res.obj(new TypeReference<List<User>>() {
        });
        final List<String> list = res.list();
        final Map<String, String> map = res.map();
        final InputStream stream = res.stream();
        final Integer i = res.i32();
        final Boolean b = res.bool();

        //响应头
        final String ok = res.header("ok");
        final Map<String, List<String>> headers = res.headers();

        //状态
        final int status = res.code();

        //原始响应
        final Response response = res.raw();


    }

}
