package io.github.kongweiguang.bus.test.push_obj_method;

import io.github.kongweiguang.bus.Bus;
import io.github.kongweiguang.bus.core.Oper;
import org.junit.jupiter.api.Test;

import io.github.kongweiguang.bus.test.metedata.User;

import static io.github.kongweiguang.bus.Bus.hub;


public class PushObjMethodTest {
    @Test
    void test1() throws Exception {
        //设置拉取消息的处理
        hub().pullClass(new MyHandler());

        //推送tipic为bala的消息
        hub().push(Oper.of("bala", new User(1, "k", new String[]{"h"})), object -> System.out.println("object = " + object));

        //推送topic为bala1的消息
        hub().push("bala1", new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        //推送user类的topic
        hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

        Bus.<User, String>hub().push(new User(1, "k", new String[]{"h"}), object -> System.out.println("object = " + object));

    }
}
