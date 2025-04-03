package io.github.kongweiguang.db.sql;

public class Order {

    private final String field;
    private final Sort sort;

    public Order(String field, Sort sort) {
        this.field = field;
        this.sort = sort;
    }

    public static Order of(String field, Sort sort) {
        return new Order(field, sort);
    }

    public static Order asc(String field) {
        return new Order(field, Sort.ASC);
    }

    public static Order desc(String field) {
        return new Order(field, Sort.DESC);
    }

    public String field() {
        return field;
    }

    public Sort sort() {
        return sort;
    }


}
