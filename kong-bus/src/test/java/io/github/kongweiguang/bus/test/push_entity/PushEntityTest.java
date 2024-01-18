package io.github.kongweiguang.bus.test.push_entity;

import org.junit.jupiter.api.Test;

import io.github.kongweiguang.bus.test.metedata.User;

import static io.github.kongweiguang.bus.Bus.hub;


public class PushEntityTest {

    @Test
    void test1() throws Exception {
        final User user = new User(99, "kpp", new String[]{"1", "2"});

        hub().pull(User.class, h -> {
            System.out.println(h);
            h.res("123");
        });

        hub().push(user);

    }
}
