package io.github.kongweiguang.core.convert;

import org.junit.jupiter.api.Test;

public class CvtTest {
    @Test
    public void test() throws Exception {
        Integer i = Cvts.toInt("123");
        System.out.println("i = " + i);
    }
}
