package io.github.kongweiguang.core.domain;

import java.util.StringJoiner;
import java.util.Date;

public class User {
    private String userName;
    private Integer userAge;
    private Boolean isActive;
    private Date date;

    public String userName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer userAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Date date() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("userName='" + userName + "'")
                .add("userAge=" + userAge)
                .add("isActive=" + isActive)
                .add("date=" + date)
                .toString();
    }
}
