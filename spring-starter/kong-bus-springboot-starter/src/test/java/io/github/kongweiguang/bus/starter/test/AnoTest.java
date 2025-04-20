package io.github.kongweiguang.bus.starter.test;


import io.github.kongweiguang.bus.Bus;
import io.github.kongweiguang.bus.core.Oper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.github.kongweiguang.bus.Bus.hub;


@SpringBootTest(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
public class AnoTest {

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
