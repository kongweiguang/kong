package io.github.kongweiguang.core.pattern.bus.push_entity;

import org.junit.jupiter.api.Test;

import io.github.kongweiguang.core.pattern.bus.metedata.User;

import static io.github.kongweiguang.core.pattern.bus.Bus.hub;


public class PushEntityTest {

    @Test
    void test1() throws Exception {
        User user = new User(99, "kpp", new String[]{"1", "2"});

        hub().pull(User.class, h -> {
            System.out.println(h);
            h.res("123");
        });

        hub().push(user);

    }
}
