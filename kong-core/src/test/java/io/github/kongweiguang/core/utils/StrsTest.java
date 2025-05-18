package io.github.kongweiguang.core.utils;

import io.github.kongweiguang.core.lang.Strs;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class StrsTest {
    @Test
    public void test1() throws Exception {
        // 方法1测试
        String template1 = "Hello {}, this is {} test\\{\\}  \\123 {}";
        System.out.println(Strs.fmt(template1, "World", "performance"));
        // 输出：Hello World, this is performance test{}  \123 {}
    }

    @Test
    public void test2() throws Exception {
        // 方法2测试
        Map<String, String> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", "30");
        String template2 = "User {name} is {age} years old \\{meta\\}  \\123 {demo} ";
        System.out.println(Strs.fmt(template2, map));
        // 输出：User Alice is 30 years old {meta}   \123 {demo}
    }

    @Test
    public void test3() throws Exception {
        for (int i = 0; i < 50; i++) {
            System.out.println(i);
        }
        String fmt1 = "this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}this is {} for {}";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100_000_000; i++) {
            Strs.fmt(fmt1, "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b");
        }

        long end = System.currentTimeMillis();
        System.out.println("use time -> " + (end - start) + "ms");
    }

    @Test
    public void test4() throws Exception {
        for (int i = 0; i < 50; i++) {
            System.out.println(i);
        }

        Map<String, String> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", "30");
        String template2 = "User {name} is {age} years old \\{meta\\}  \\123 {demo} User {name} is {age} years old \\{meta\\}  \\123 {demo} User {name} is {age} years old \\{meta\\}  \\123 {demo} User {name} is {age} years old \\{meta\\}  \\123 {demo} User {name} is {age} years old \\{meta\\}  \\123 {demo} User {name} is {age} years old \\{meta\\}  \\123 {demo} ";
        long start = System.currentTimeMillis();


        for (int i = 0; i < 100_000_000; i++) {
            Strs.fmt(template2, map);
        }

        long end = System.currentTimeMillis();
        System.out.println("use time -> " + (end - start) + "ms");
    }
}
