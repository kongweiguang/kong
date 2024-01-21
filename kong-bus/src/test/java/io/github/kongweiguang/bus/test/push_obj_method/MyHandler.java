package io.github.kongweiguang.bus.test.push_obj_method;


import io.github.kongweiguang.bus.core.Oper;
import io.github.kongweiguang.bus.core.Pull;

import io.github.kongweiguang.bus.test.metedata.User;

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


    //push
    public User push_user() {
        return new User(1, "push_1", new String[]{"h1", "h2"});
    }

    public User push_user1() {
        return new User(2, "push_2", new String[]{"h1", "h2"});
    }


}
