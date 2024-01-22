package io.github.kongweiguang.spring.test;

import io.github.kongweiguang.spring.SpringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("ccc")
public class C {

    A aaa;

    @PostConstruct
    public void run() {
//        aaa = SpringUtil.getBean(A.class);
//        aaa.m1();
    }


    public void m1() {
        aaa.m1();
    }
}
