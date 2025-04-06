package io.github.kongweiguang.db.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Where条件组
 *
 * @author kongweiguang
 */
public class WhereGroup {
    // 组类型
    private final GroupType groupType;
    // 条件列表
    private final List<Where> wheres = new ArrayList<>();

    private WhereGroup(GroupType groupType, Where... wheres) {
        this.groupType = groupType;
        this.wheres.addAll(Arrays.asList(wheres));
    }

    private static WhereGroup of(GroupType groupType, Where... wheres) {
        return new WhereGroup(groupType, wheres);
    }

    /**
     * 创建Where条件组
     *
     * @param wheres 条件列表
     * @return WhereGroup
     */
    public static WhereGroup of(Where... wheres) {
        return new WhereGroup(GroupType.NULL, wheres);
    }

    /**
     * 创建and条件组
     *
     * @param wheres 条件列表
     * @return WhereGroup
     */
    public static WhereGroup and(Where... wheres) {
        return of(GroupType.AND, wheres);
    }

    /**
     * 创建or条件组
     *
     * @param wheres 条件列表
     * @return WhereGroup
     */
    public static WhereGroup or(Where... wheres) {
        return of(GroupType.OR, wheres);
    }

    /**
     * 创建not条件组
     *
     * @param wheres 条件列表
     * @return WhereGroup
     */
    public static WhereGroup not(Where... wheres) {
        return of(GroupType.NOT, wheres);
    }

    public GroupType groupType() {
        return groupType;
    }

    public List<Where> wheres() {
        return wheres;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WhereGroup.class.getSimpleName() + "[", "]")
                .add("groupType=" + groupType)
                .add("wheres=" + wheres)
                .toString();
    }

}
