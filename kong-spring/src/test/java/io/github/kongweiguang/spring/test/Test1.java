package io.github.kongweiguang.spring.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@ContextConfiguration
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan("io.github.kongweiguang.spring.test")
public class Test1 {
    @Resource
    C ccc;

    @Test
    public void test1() throws Exception {
//        final C c = SpringUtil.getBean(C.class);
//        c.m1();
        ccc.m1();
    }

}
