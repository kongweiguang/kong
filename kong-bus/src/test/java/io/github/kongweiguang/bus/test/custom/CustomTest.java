package io.github.kongweiguang.bus.test.custom;

import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.bus.Bus.hub;


public class CustomTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub("hub1").pull(branch, System.out::println);
        //拉取默认的消息
        hub().pull(branch, System.out::println);


        //推送消息
        hub("hub1").push(branch, "content", e -> System.out.println("callback 1 -> " + e));
    }
}
