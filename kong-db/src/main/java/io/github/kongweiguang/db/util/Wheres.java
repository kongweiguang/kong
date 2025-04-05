package io.github.kongweiguang.db.util;

import io.github.kongweiguang.core.lang.Pair;
import io.github.kongweiguang.db.sql.GroupType;
import io.github.kongweiguang.db.sql.Where;
import io.github.kongweiguang.db.sql.WhereGroup;

import java.util.ArrayList;
import java.util.List;

public class Wheres {

    public static Pair<String, List<Object>> buildWhere(Where[] wheres) {
        StringBuilder sb = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();
        for (Where where : wheres) {
            Pair<String, List<Object>> ok = where.ok();
            sb.append(ok.k()).append(" AND ");
            paramsList.addAll(ok.v());
        }
        sb.delete(sb.length() - 5, sb.length());
        return Pair.of(sb.toString(), paramsList);
    }

    public static Pair<String, List<Object>> buildGroup(WhereGroup... groups) {
        StringBuilder sb = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();
        for (WhereGroup group : groups) {
            GroupType type = group.groupType();
            List<Where> wheres = group.wheres();
            if (type != GroupType.NULL) {
                sb.append(type.getName()).append(" ");
            }
            sb.append("(");
            buildWhere(wheres.toArray(new Where[0]));
            sb.append(") ");
        }

        return Pair.of(sb.toString(), paramsList);
    }
}
