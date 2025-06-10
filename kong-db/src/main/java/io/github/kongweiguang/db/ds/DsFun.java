package io.github.kongweiguang.db.ds;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据源创建函数
 *
 * @author kongweiguang
 */
@FunctionalInterface
public interface DsFun {
    DataSource create(String source, Map<String, Object> config);
}
