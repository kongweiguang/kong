package io.github.kongweiguang.test.domain;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Date createdAt;
    private LocalDateTime updatedAt;
    private List<Role> roles;

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .add("roles=" + roles)
                .toString();
    }
}