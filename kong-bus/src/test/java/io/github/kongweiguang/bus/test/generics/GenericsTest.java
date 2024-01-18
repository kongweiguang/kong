package io.github.kongweiguang.bus.test.generics;

import io.github.kongweiguang.bus.Bus;
import org.junit.jupiter.api.Test;

import io.github.kongweiguang.bus.test.metedata.User;
import java.util.Collections;
import java.util.List;

public class GenericsTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<User, List<String>>hub().pull(branch, h -> {
            final User user = h.content();
            System.out.println("user = " + user);
            h.res(Collections.singletonList(user.hobby()[0]));
        });


        //推送消息
        Bus.<User, List<String>>hub().push(branch, new User(1, "kpp", new String[]{"code"}), c -> {
            System.out.println(c.size());
            System.out.println(c.get(0));
        });
    }
}
