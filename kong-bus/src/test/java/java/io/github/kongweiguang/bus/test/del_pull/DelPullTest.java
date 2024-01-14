package java.io.github.kongweiguang.bus.test.del_pull;

import io.github.kongweiguang.bus.core.Merge;
import io.github.kongweiguang.bus.core.Operation;
import org.junit.jupiter.api.Test;

import static io.github.kongweiguang.bus.Bus.hub;


public class DelPullTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        hub().pull(branch, new Merge<Operation<Object, Object>>() {
            @Override
            public String name() {
                return "k_pull";
            }

            @Override
            public void mr(final Operation<Object, Object> a) throws Exception {
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
