package io.github.kongweiguang.spring.test;

import io.github.kongweiguang.spring.SpringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
//@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan("io.github.kongweiguang.spring.test")
public class Test1 {
//    @Resource
//    C ccc;

    @Test
    public void test1() throws Exception {
        ApplicationContext context = SpringUtil.context();
        System.out.println(context.getBeanDefinitionCount());
    }

}
