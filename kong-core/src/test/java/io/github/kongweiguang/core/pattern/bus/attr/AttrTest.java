package io.github.kongweiguang.core.pattern.bus.attr;

import io.github.kongweiguang.core.pattern.bus.Bus;
import io.github.kongweiguang.core.pattern.bus.core.Oper;
import org.junit.jupiter.api.Test;

public class AttrTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<String, String>hub().pull(branch, System.out::println);

        //推送消息
        Bus.<String, Void>hub().push(Oper.<String, Void>of(branch, "content").tag("k", "v"));
    }
}
