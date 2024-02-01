package io.github.kongweiguang.core.pattern;

import io.github.kongweiguang.core.pattern.chain.ChainHandler;
import io.github.kongweiguang.core.pattern.chain.Chain;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ChainTest {
    @Test
    public void test1() throws Exception {
        final HashMap<String, Object> map = new HashMap<>();

        Chain.<Map<String, Object>>of(map)
                .add(new ChainHandler<Map<String, Object>>() {
                    @Override
                    public boolean handler(Chain<Map<String, Object>> chain) {
                        System.out.println(1);
                        return true;
                    }
                })
                .add(new ChainHandler<Map<String, Object>>() {
                    @Override
                    public boolean handler(Chain<Map<String, Object>> chain) {
//                        chain.end();
                        System.out.println(2);
                        return true;
                    }
                })
                .add(new ChainHandler<Map<String, Object>>() {
                    @Override
                    public boolean handler(Chain<Map<String, Object>> chain) {
//                        chain.skip(1);
                        System.out.println(3);
                        return true;
                    }
                })
                .add(new ChainHandler<Map<String, Object>>() {
                    @Override
                    public boolean handler(Chain<Map<String, Object>> chain) {
                        chain.get().put("666", "999");
                        System.out.println(4);
                        return true;
                    }
                }).add(new ChainHandler<Map<String, Object>>() {
                    @Override
                    public boolean handler(Chain<Map<String, Object>> chain) {
                        System.out.println(5);
                        return true;
                    }
                })
                .process();

        System.out.println("map = " + map);
    }
}
