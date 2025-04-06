package io.github.kongweiguang.db.sql;

import java.util.StringJoiner;

/**
 * 排序对象
 *
 * @author kongweiguang
 */
public class Order {
    // 字段
    private final String field;
    // 排序方式
    private final Sort sort;

    private Order(String field, Sort sort) {
        this.field = field;
        this.sort = sort;
    }

    /**
     * 创建排序对象
     *
     * @param field 字段
     * @param sort  排序方式
     * @return Order
     */
    public static Order of(String field, Sort sort) {
        return new Order(field, sort);
    }

    /**
     * 创建升序排序对象
     *
     * @param field 字段
     * @return Order
     */
    public static Order asc(String field) {
        return new Order(field, Sort.ASC);
    }

    /**
     * 创建降序排序对象
     *
     * @param field 字段
     * @return Order
     */
    public static Order desc(String field) {
        return new Order(field, Sort.DESC);
    }

    public String field() {
        return field;
    }

    public Sort sort() {
        return sort;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
                .add("field='" + field + "'")
                .add("sort=" + sort)
                .toString();
    }
}
