package io.github.kongweiguang.db.func;

import io.github.kongweiguang.db.run.DbRun;

/**
 * 数据库事务操作接口
 *
 * @author kongweiguang
 **/
@FunctionalInterface
public interface SqlRun {
    void run(DbRun con);
}
