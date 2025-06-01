package io.github.kongweiguang.core.pattern;

import io.github.kongweiguang.core.pattern.pipe.Pipe;
import io.github.kongweiguang.core.pattern.pipe.PipeHandler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PipeTest {
    @Test
    public void test1() throws Exception {
        BigDecimal exec = Pipe.of((PipeHandler<BigDecimal, Integer>) input -> null)
                .next((PipeHandler<Integer, Double>) input -> null)
                .next((PipeHandler<Double, String>) input -> null)
                .next((PipeHandler<String, Integer>) input -> null)
                .next(input -> new BigDecimal(666))
                .exec(new BigDecimal(1));

        System.out.println("exec = " + exec);

    }
}
