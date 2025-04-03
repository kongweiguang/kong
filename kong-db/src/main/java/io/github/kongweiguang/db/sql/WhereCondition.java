package io.github.kongweiguang.db.sql;//package cn.kongweiguang.toml.sql;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class WhereCondition {
//    private final StringBuilder conditionSql = new StringBuilder();
//    private final List<Object> parameters = new ArrayList<>();
//    private final List<WhereCondition> childConditions = new ArrayList<>();
//    private String logicalOperator = "AND"; // 默认逻辑运算符
//    private boolean isGrouped = false;
//
//    // 基础条件构造
//    public WhereCondition(String column, String operator, Object value) {
//        appendCondition(column, operator, value);
//    }
//
//    // 括号分组构造
//    public WhereCondition(String logicalOperator, WhereCondition... conditions) {
//        this.logicalOperator = logicalOperator;
//        addChildConditions(conditions);
//        this.isGrouped = true;
//    }
//
//    // 添加单个条件
//    public WhereCondition appendCondition(String column, String operator, Object value) {
//        if (!conditionSql.isEmpty()) {
//            conditionSql.append(" ").append(logicalOperator).append(" ");
//        }
//        conditionSql.append(escapeIdentifier(column))
//                    .append(" ").append(operator).append(" ?");
//        parameters.add(value);
//        return this;
//    }
//
//    // 添加子条件组（支持嵌套）
//    public WhereCondition addSubCondition(WhereCondition subCondition) {
//        childConditions.add(subCondition);
//        parameters.addAll(subCondition.parameters);
//        return this;
//    }
//
//    // 合并多个条件（使用当前逻辑运算符连接）
//    public WhereCondition combine(WhereCondition... conditions) {
//        for (WhereCondition cond : conditions) {
//            conditionSql.append(" ").append(logicalOperator).append(" (");
//            cond.appendTo(conditionSql);
//            conditionSql.append(")");
//        }
//        return this;
//    }
//
//    // 渲染最终条件
//    public String build() {
//        if (!childConditions.isEmpty()) {
//            conditionSql.insert(0, "(").append(")");
//            for (WhereCondition child : childConditions) {
//                conditionSql.insert(0, "( ").append(child.build()).append(" ) ").append(logicalOperator).append(" ");
//            }
//            conditionSql.setLength(conditionSql.length() - logicalOperator.length() - 2); // 移除末尾多余运算符
//        }
//        return conditionSql.toString();
//    }
//
//    // 参数收集
//    public Object[] getParameters() {
//        return parameters.toArray();
//    }
//
//    // 字段安全转义
//    private String escapeIdentifier(String identifier) {
//        return identifier.replaceAll("['\";]", "");
//    }
//
//    // 静态工厂方法
//    public static WhereCondition where(String column, String operator, Object value) {
//        return new WhereCondition(column, operator, value);
//    }
//
//    public static WhereCondition group(String logicalOperator, WhereCondition... conditions) {
//        return new WhereCondition(logicalOperator, conditions);
//    }
//}