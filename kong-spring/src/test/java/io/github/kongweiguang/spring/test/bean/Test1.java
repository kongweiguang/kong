package io.github.kongweiguang.spring.test.bean;

import io.github.kongweiguang.spring.BeanUtil;
import io.github.kongweiguang.spring.test.User;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SpringBootTest
//@ComponentScan("io.github.kongweiguang.spring.test")
public class Test1 {
    User u = new User();

    {
        u.setAge(1);
        u.setName("kong");
        u.setHobby(new String[]{"j", "n"});
    }

    @Test
    public void test1() throws Exception {
        final BeanMap map = BeanUtil.toMap(u);
        System.out.println("map = " + map);
    }

    @Test
    public void test2() throws Exception {
        final List<String> list = Arrays.asList("ll", "fljd");
        final Map<String, Object> map = new HashMap<>();
        map.put("name", "kk");
        map.put("age", 1);
        map.put("hobby",list.toArray());
        final User user = BeanUtil.toObj(map, new User());
        System.out.println("user = " + user);
    }

    @Test
    public void test3() throws Exception {
        User user = BeanUtil.copy(u, User.class);
        System.out.println("user = " + user);
    }
}
