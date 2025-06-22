package io.github.kongweiguang.core.pattern.bus.del_pull;

import io.github.kongweiguang.core.pattern.bus.core.Merge;
import io.github.kongweiguang.core.pattern.bus.core.Oper;
import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.core.pattern.bus.Bus.hub;


public class DelPullTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, new Merge<Oper<Object, Object>>() {
            @Override
            public String name() {
                return "k_pull";
            }

            @Override
            public void mr(Oper<Object, Object> a) throws Exception {
                System.out.println(a);
            }
        });


        //推送消息
        hub().push(branch, "content");

        //删除拉取
        hub().remove(branch, "k_pull");

        //推送消息
        hub().push(branch, "content");

    }
}
