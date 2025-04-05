package io.github.kongweiguang.core.utils;

import io.github.kongweiguang.core.util.Strs;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class StrsTest {
    @Test
    public void test1() throws Exception {
        // 方法1测试
        String template1 = "Hello {}, this is {} test\\{\\}  \\123";
        System.out.println(Strs.fmt(template1, "World", "performance"));
        // 输出：Hello World, this is performance test{}  \123
    }

    @Test
    public void test2() throws Exception {
        // 方法2测试
        Map<String, String> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", "30");
        String template2 = "User {name} is {age} years old \\{meta\\}   \\123";
        System.out.println(Strs.fmt(template2, map));
        // 输出：User Alice is 30 years old {meta}   \123
    }

    @Test
    public void test3() throws Exception {
        System.out.println(" \\= ");
    }

}
