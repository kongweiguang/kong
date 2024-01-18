package io.github.kongweiguang.bus.test.multi_pull;

import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.bus.Bus.hub;


public class MultiPullTest {
    String branch = "branch.test1";

    @Test
    void test1() throws Exception {
        //拉取消息
        hub().pull(branch, System.out::println);
        hub().pull(branch, System.out::println);

        //推送消息
        hub().push(branch, "content");
    }
}
