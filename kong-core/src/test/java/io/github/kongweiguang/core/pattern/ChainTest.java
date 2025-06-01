package io.github.kongweiguang.core.pattern;

import io.github.kongweiguang.core.pattern.chain.Chain;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ChainTest {
    @Test
    public void test1() throws Exception {
        HashMap<String, Object> map = new HashMap<>();

        Chain.<Map<String, Object>>of(map)
                .add(chain -> {
                    System.out.println(1);
                    return true;
                })
                .add(chain -> {
//                        chain.end();
                    System.out.println(2);
                    return true;
                })
                .add(chain -> {
//                        chain.skip(1);
                    System.out.println(3);
                    return true;
                })
                .add(chain -> {
                    chain.get().put("666", "999");
                    System.out.println(4);
                    return true;
                }).add(chain -> {
                    System.out.println(5);
                    return true;
                })
                .process();

        System.out.println("map = " + map);
    }
}
