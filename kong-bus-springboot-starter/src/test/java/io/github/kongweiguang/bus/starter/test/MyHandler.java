package io.github.kongweiguang.bus.starter.test;

import io.github.kongweiguang.bus.core.Oper;
import io.github.kongweiguang.bus.core.Pull;
import org.springframework.stereotype.Component;

@Component
public class MyHandler {
    @Pull
    public String fn(User user) {
        System.out.println(user);
        return "hello";
    }

    @Pull("bala")
    public String fn1() {
        System.out.println("fn1");
        return "hello1";
    }

    @Pull("bala")
    public void fn2() {
        System.out.println("fn2");
    }

    @Pull
    public String fn3(Oper<User, String> oper) {
        System.out.println(oper);
        return "hello2";
    }

}
