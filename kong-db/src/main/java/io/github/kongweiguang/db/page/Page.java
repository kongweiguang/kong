package io.github.kongweiguang.db.page;


public class Page {
    private int pageNumber;
    private int pageSize;

    public Page(int pn, int ps) {
        this.pageNumber = pn;
        this.pageSize = ps;
    }

    public static Page of(int pn, int ps) {
        return new Page(pn, ps);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
