package io.github.kongweiguang.db.sql;

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
