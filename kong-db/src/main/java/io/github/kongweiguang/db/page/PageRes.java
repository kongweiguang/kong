package io.github.kongweiguang.db.page;

import java.util.List;
import java.util.StringJoiner;

/**
 * 分页结果
 *
 * @author kongweiguang
 */
public class PageRes<T> {
    // 总数
    private Long total;
    // 数据
    private List<T> data;

    private PageRes() {
    }

    private PageRes(Long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public static <T> PageRes<T> of() {
        return new PageRes<>();
    }

    /**
     * 创建分页结果
     *
     * @param total 总数
     * @param data  数据
     * @param <T>   数据类型
     * @return PageRes
     */
    public static <T> PageRes<T> of(Long total, List<T> data) {
        return new PageRes<>(total, data);
    }

    public Long total() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> data() {
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
