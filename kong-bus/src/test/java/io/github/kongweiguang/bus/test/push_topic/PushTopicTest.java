package io.github.kongweiguang.bus.test.push_topic;

import io.github.kongweiguang.bus.core.Operation;
import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.bus.Bus.hub;


public class PushTopicTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, m -> {
            System.out.println(m.id());
            System.out.println(m.content());
        });


        //推送消息
        hub().push(branch, "content");
        hub().push(branch, "content", e -> System.out.println("callback 1 -> " + e));
        hub().push(Operation.of(branch, "content"));
        hub().push(Operation.of(branch, "content"), e -> System.out.println("callback 2 -> " + e));
    }
}
