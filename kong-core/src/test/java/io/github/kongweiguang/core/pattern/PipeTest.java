package io.github.kongweiguang.core.pattern;

import io.github.kongweiguang.core.pattern.pipe.PipeHandler;
import io.github.kongweiguang.core.pattern.pipe.Pipe;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PipeTest {
    @Test
    public void test1() throws Exception {
        final BigDecimal exec = Pipe.of(new PipeHandler<String, Integer>() {
                    @Override
                    public Integer handle(String input) {
                        return null;
                    }

                    ;
                })
                .next(new PipeHandler<Integer, Double>() {
                    @Override
                    public Double handle(Integer input) {
                        return null;
                    }
                })
                .next(new PipeHandler<Double, String>() {
                    @Override
                    public String handle(Double input) {
                        return null;
                    }
                })
                .next(new PipeHandler<String, Integer>() {
                    @Override
                    public Integer handle(String input) {
                        return null;
                    }
                })
                .next(new PipeHandler<Integer, BigDecimal>() {
                    @Override
                    public BigDecimal handle(Integer input) {
                        return new BigDecimal(666);
                    }
                })
                .exec("");


        System.out.println("exec = " + exec);
    }
}
