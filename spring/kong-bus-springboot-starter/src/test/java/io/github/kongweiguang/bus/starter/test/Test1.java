package io.github.kongweiguang.bus.starter.test;


import io.github.kongweiguang.bus.Bus;
import io.github.kongweiguang.bus.core.Oper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static io.github.kongweiguang.bus.Bus.hub;


@ContextConfiguration
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan("io.github.kongweiguang.bus.starter.test")
public class Test1 {

    @Test
    public void test() throws Exception {

        //推送branch为bala的消息
        hub().push(Oper.of("bala", new User(1, "k", new String[]{"h"})), object -> System.out.println("object = " + object));

        //推送branch为bala1的消息
        hub().push("bala1", new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        //推送user类的branch
        hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        Bus.<User, String>hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

    }
}
