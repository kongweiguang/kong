package java.io.github.kongweiguang.bus.test.attr;

import io.github.kongweiguang.bus.Bus;
import io.github.kongweiguang.bus.core.Operation;
import org.junit.jupiter.api.Test;

public class AttrTest {
    String branch = "branch.test1";

    @Test
    void test() throws Exception {
        //拉取消息
        Bus.<String, String>hub().pull(branch, System.out::println);

        //推送消息
        Bus.<String, Void>hub().push(Operation.<String, Void>of(branch, "content").tag("k", "v"));
    }
}
