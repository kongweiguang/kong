package io.github.kongweiguang.db.ds;

/**
 * 数据源配置
 *
 * @author kongweiguang
 */
public class DsConf {

    private String source;
    private DsType type;


    public String source() {
        return source;
    }

    public DsConf setSource(String source) {
        this.source = source;
        return this;
    }

    public DsType type() {
        return type;
    }

    public DsConf setType(DsType type) {
        this.type = type;
        return this;
    }
}
