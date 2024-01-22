package io.github.kongweiguang.spring.test;

import io.github.kongweiguang.spring.SpringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("ccc")
public class C {

    A a;

    @PostConstruct
    public void init() {
        a = SpringUtil.getBean("aaa");
    }

    public void m1() {
        a.m1();
    }
}
