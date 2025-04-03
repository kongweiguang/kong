package io.github.kongweiguang.db.page;

import java.util.List;
import java.util.StringJoiner;

public class PageRes<T> {

    private Long total;
    private List<T> data;

    public PageRes() {
    }

    public PageRes(Long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public static <T> PageRes<T> of() {
        return new PageRes();
    }

    public static <T> PageRes<T> of(Long total, List<T> data) {
        return new PageRes(total, data);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PageRes.class.getSimpleName() + "[", "]")
                .add("total=" + total)
                .add("data=" + data)
                .toString();
    }
}
