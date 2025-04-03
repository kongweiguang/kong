package io.github.kongweiguang.db.func;

import io.github.kongweiguang.db.DbRun;

@FunctionalInterface
public interface SqlRun {

    void run(DbRun con);
}
