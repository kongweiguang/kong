package io.github.kongweiguang.db.page;


/**
 * 分页对象
 *
 * @author kongweiguang
 */
public class Page {
    // 当前页 默认是1
    private int pageNumber;
    // 每页显示记录数
    private int pageSize;

    public Page(int pn, int ps) {
        this.pageNumber = pn;
        this.pageSize = ps;
    }

    /**
     * 创建分页对象
     *
     * @param pn 当前页
     * @param ps 每页显示记录数
     * @return Page
     */
    public static Page of(int pn, int ps) {
        return new Page(pn, ps);
    }

    public int pageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int pageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
