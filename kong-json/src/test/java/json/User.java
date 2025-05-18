package json;

import java.util.Arrays;

public class User {

    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public User setHobby(String[] hobby) {
        this.hobby = hobby;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
               "name='" + name + '\'' +
               ", age=" + age +
               ", hobby=" + Arrays.toString(hobby) +
               '}';
    }
}
