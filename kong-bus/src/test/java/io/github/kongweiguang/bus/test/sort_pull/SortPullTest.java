package io.github.kongweiguang.bus.test.sort_pull;

import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.bus.Bus.hub;


public class SortPullTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, 0, e -> System.out.println("1"));
        hub().pull(branch, -1, e -> System.out.println("2"));


        //推送消息
        hub().push(branch, "content", e -> System.out.println("callback 1 -> " + e));
        //打印结果 2,1
    }
}
