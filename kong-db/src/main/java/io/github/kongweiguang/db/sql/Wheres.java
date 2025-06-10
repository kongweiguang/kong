package io.github.kongweiguang.db.sql;

import io.github.kongweiguang.core.lang.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Where条件工具类
 *
 * @author kongweiguang
 */
public class Wheres {
    /**
     * 构建Where条件
     *
     * @param wheres 条件列表
     * @return Pair
     */
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

    /**
     * 构建Where条件组
     *
     * @param groups 条件组列表
     * @return Pair
     */
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
            Pair<String, List<Object>> p = buildWhere(wheres.toArray(new Where[0]));
            sb.append(p.k());
            paramsList.addAll(p.v());
            sb.append(") ");
        }

        return Pair.of(sb.toString(), paramsList);
    }
}
