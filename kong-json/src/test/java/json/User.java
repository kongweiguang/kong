package json;

import java.util.Arrays;

public final class User {

    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public User setName(final String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public User setAge(final Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public User setHobby(final String[] hobby) {
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
