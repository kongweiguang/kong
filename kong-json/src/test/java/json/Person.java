package json;

import java.util.Arrays;

public final class Person {

    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public Person setName(final String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Person setAge(final Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public Person setHobby(final String[] hobby) {
        this.hobby = hobby;
        return this;
    }


    @Override
    public String toString() {
        return "Person{" +
               "name='" + name + '\'' +
               ", age=" + age +
               ", hobby=" + Arrays.toString(hobby) +
               '}';
    }
}
