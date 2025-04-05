package io.github.kongweiguang.db.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class WhereGroup {
    private final GroupType groupType;
    private final List<Where> wheres = new ArrayList<>();

    public WhereGroup(GroupType groupType, Where... wheres) {
        this.groupType = groupType;
        this.wheres.addAll(Arrays.asList(wheres));
    }

    public static WhereGroup of(GroupType groupType, Where... wheres) {
        return new WhereGroup(groupType, wheres);
    }

    public static WhereGroup of(Where... wheres) {
        return new WhereGroup(GroupType.NULL, wheres);
    }

    public static WhereGroup and(Where... wheres) {
        return of(GroupType.AND, wheres);
    }

    public static WhereGroup or(Where... wheres) {
        return of(GroupType.OR, wheres);
    }

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
