package json;

import java.util.Arrays;

public class Person {

    private String name;
    private Integer age;
    private String[] hobby;

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Person setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String[] getHobby() {
        return hobby;
    }

    public Person setHobby(String[] hobby) {
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
