package io.github.kongweiguang.core.pattern.bus.metedata;

import java.util.Arrays;
import java.util.StringJoiner;

public class User {
    private int id;
    private String name;
    private String[] hobby;

    public User(int id, String name, String[] hobby) {
        this.id = id;
        this.name = name;
        this.hobby = hobby;
    }

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] hobby() {
        return hobby;
    }

    public void setHobby(String[] hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("hobby=" + Arrays.toString(hobby))
                .toString();
    }
}
