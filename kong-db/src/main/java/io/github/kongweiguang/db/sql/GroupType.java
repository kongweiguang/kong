package io.github.kongweiguang.db.sql;

/**
 * 分组的类型
 * author: kongweiguang
 */
public enum GroupType {
    AND,
    OR,
    NOT,
    NULL;

    public String getName() {
        if (this == NULL) {
            return null;
        }
        return name();
    }
}
