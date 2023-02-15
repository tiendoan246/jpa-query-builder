package com.jpaquery.builder.demo.query.specification;

import com.jpaquery.builder.demo.query.builder.QueryBuilder;
import com.jpaquery.builder.demo.query.constant.LogicOperator;
import com.jpaquery.builder.demo.query.constant.MessageConstant;
import com.jpaquery.builder.demo.query.constant.SearchOperator;
import com.jpaquery.builder.demo.query.criteria.SearchCriteria;
import com.jpaquery.builder.demo.query.utils.CommonUtils;
import com.jpaquery.builder.demo.query.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SpecificationBuilder<T> {

    private List<QueryBuilder<T>> queryBuilders;
    private StringBuilder stringBuilder;
    private String timeZone;

    private SpecificationBuilder(List<QueryBuilder<T>> queryBuilders) {
        this.queryBuilders = queryBuilders;
    }

    public static <T> SpecificationBuilder<T> createInstance(List<QueryBuilder<T>> queryBuilders) {
        return new SpecificationBuilder<>(queryBuilders);
    }

    public String getNativeSqlCondition() {
        if (Objects.isNull(stringBuilder)) {
            return null;
        }

        return stringBuilder.toString();
    }

    public Specification<T> buildSpecification(Map<String, Object> map, String timeZone) {
        this.timeZone = timeZone;
        return this.buildSpecification(map);
    }

    public Specification<T> buildSpecification(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Specification<T> root = null;
        stringBuilder = new StringBuilder();

        Set<Map.Entry<String, Object>> entries =  map.entrySet();
        for (Map.Entry entry: entries) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }

            if (isOperator(entry.getKey().toString())) {
                Map params = (Map) entry.getValue();
                LogicOperator operator = LogicOperator.AND.getCode().equalsIgnoreCase(entry.getKey().toString())
                        ? LogicOperator.AND
                        : LogicOperator.OR;

                if (Objects.isNull(root)) {
                    appendQuery(stringBuilder, " (%s) ", groupCondition(params, operator).getLeft());
                    root = groupCondition(params, operator).getRight();
                } else {
                    appendQuery(stringBuilder, " and (%s) ", groupCondition(params, operator).getLeft());
                    root = root.and(groupCondition(params, operator).getRight());
                }
            } else {
                Set<Map.Entry<String, Object>> filters =  ((Map) entry.getValue()).entrySet();
                for (Map.Entry f: filters) {
                    if (Objects.isNull(f.getValue())) {
                        continue;
                    }
                    if (Objects.isNull(root)) {
                        appendQuery(stringBuilder, " %s ", this.createSpecification(entry, f).getLeft());
                        root = this.createSpecification(entry, f).getRight();
                    } else {
                        appendQuery(stringBuilder, " and %s ", this.createSpecification(entry, f).getLeft());
                        root = root.and(createSpecification(entry, f).getRight());
                    }
                }
            }
        }

        return root;
    }

    private Pair<String, Specification<T>> groupCondition(Map map, LogicOperator operator) {
        StringBuilder builder = new StringBuilder();
        Set<Map.Entry<String, Object>> entries =  map.entrySet();
        Specification<T> group = null;
        for (Map.Entry entry: entries) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }

            if (isOperator(entry.getKey().toString())) {
                Map params = (Map) entry.getValue();
                if (group == null) {
                    return groupCondition(params, LogicOperator.from(entry.getKey().toString()));
                } else {
                    if (LogicOperator.AND.getCode().equalsIgnoreCase(entry.getKey().toString())) {
                        appendQuery(builder, " and (%s)", groupCondition(params, LogicOperator.AND).getLeft());
                        return Pair.of(builder.toString(), group.and(groupCondition(params, LogicOperator.AND).getRight()));
                    } else {
                        appendQuery(builder, " and (%s)", groupCondition(params, LogicOperator.OR).getLeft());
                        return Pair.of(builder.toString(), group.or(groupCondition(params, LogicOperator.OR).getRight()));
                    }
                }
            }

            Map params = (Map) entry.getValue();
            String fieldName = entry.getKey().toString();
            Set<Map.Entry<String, Object>> operators =  params.entrySet();
            for (Map.Entry key: operators) {
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                if (LogicOperator.AND.equals(operator)) {
                    if (Objects.isNull(group)) {
                        appendQuery(builder, " %s ", createSpecification(fieldName, key).getLeft());
                        group = createSpecification(fieldName, key).getRight();
                    } else {
                        appendQuery(builder, " and %s ", createSpecification(fieldName, key).getLeft());
                        group = group.and(createSpecification(fieldName, key).getRight());
                    }
                } else {
                    if (Objects.isNull(group)) {
                        appendQuery(builder, " %s ", createSpecification(fieldName, key).getLeft());
                        group = createSpecification(fieldName, key).getRight();
                    } else {
                        appendQuery(builder, " or %s ", createSpecification(fieldName, key).getLeft());
                        group = group.or(createSpecification(fieldName, key).getRight());
                    }
                }
            }
        }
        return Pair.of(builder.toString(), group);
    }

    private Pair<String, BaseSpecification<T>> createSpecification(String fieldName, Map.Entry right) {
        SearchOperator operator = SearchOperator.from(right.getKey().toString());
        if (Objects.isNull(operator)) {
            return createNestedSpecification(fieldName, right);
        }

        String condition = getExpression(fieldName, right, operator);

        return Pair.of(condition, new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        fieldName,
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), null));
    }

    private Pair<String, BaseSpecification<T>> createSpecification(Map.Entry left, Map.Entry right) {
        SearchOperator operator = SearchOperator.from(right.getKey().toString());
        if (Objects.isNull(operator)) {
            return createNestedSpecification(left.getKey().toString(), right);
        }

        String condition = getExpression(left.getKey().toString(), right, operator);

        return Pair.of(condition, new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        left.getKey().toString(),
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), null));
    }

    private Pair<String, BaseSpecification<T>> createNestedSpecification(String fieldName, Map.Entry right) {
        Map params = (Map) right.getValue();
        Set<Map.Entry<String, Object>> operators =  params.entrySet();
        String condition = "";
        for (Map.Entry ops: operators) {
            condition = getExpression(fieldName, right.getKey().toString(), ops, SearchOperator.from(ops.getKey().toString()));
            return Pair.of(condition, new BaseSpecification<T>(queryBuilders,
                    new SearchCriteria(
                            right.getKey().toString(),
                            SearchOperator.from(ops.getKey().toString()),
                             ops.getValue()),
                    fieldName));
        }

        return Pair.of("", new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        fieldName,
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), fieldName));
    }

    private void appendQuery(StringBuilder builder, String expression, String condition) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(condition)) {
            builder.append(String.format(expression, condition));
        }
    }

    private String getExpression(String alias, String left, Map.Entry right, SearchOperator operator) {
        switch (operator) {
            case BETWEEN -> {
                return getBetweenExpression(alias, StringUtils.camelToSnake(left), right);
            }
            case CONTAINS -> {
                return getContainsExpression(alias, StringUtils.camelToSnake(left), right, operator);
            }
            default -> {
                return String.format(" %s.%s %s %s ",
                        alias,
                        StringUtils.camelToSnake(left),
                        CommonUtils.sqlOperators.get(operator),
                        getSqlValue(right.getValue()));
            }
        }
    }

    private String getExpression(String left, Map.Entry right, SearchOperator operator) {
        switch (operator) {
            case BETWEEN -> {
                return getBetweenExpression(StringUtils.camelToSnake(left), right);
            }
            case CONTAINS -> {
                return getContainsExpression(StringUtils.camelToSnake(left), right, operator);
            }
            default -> {
                return String.format(" a.%s %s %s ",
                        StringUtils.camelToSnake(left),
                        CommonUtils.sqlOperators.get(operator),
                        getSqlValue(right.getValue()));
            }
        }
    }

    private String getContainsExpression(String alias, String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof String) {
            return alias + "." + left + " " + sqlOperator + " '%" + escapeSql(right.getValue()) + "%' ";
        }
        return null;
    }

    private String getContainsExpression(String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof String) {
            return "a." + left + " " + sqlOperator + " '%" + escapeSql(right.getValue()) + "%' ";
        }
        return null;
    }

    private String getBetweenExpression(String alias, String left, Map.Entry right) {
        if (right.getValue() instanceof ArrayList) {
            ArrayList array = (ArrayList) right.getValue();
            if (array.size() != 2) {
                throw new RuntimeException(MessageConstant.DATE_BETWEEN_INVALID_ERROR.getMessage());
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(timeZone)) {
                timeZone = "UTC";
            }
            String dateBetween = CommonUtils.DATE_BETWEEN_ALIAS_EXPRESSION
                    .replace("{ALIAS}", alias)
                    .replace("{KEY}", left)
                    .replace("{TIME_ZONE}", timeZone);
            return String.format(dateBetween, array.get(0), array.get(1));
        }
        return null;
    }

    private String getBetweenExpression(String left, Map.Entry right) {
        if (right.getValue() instanceof ArrayList) {
            ArrayList array = (ArrayList) right.getValue();
            if (array.size() != 2) {
                throw new RuntimeException(MessageConstant.DATE_BETWEEN_INVALID_ERROR.getMessage());
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(timeZone)) {
                timeZone = "UTC";
            }
            String dateBetween = CommonUtils.DATE_BETWEEN_EXPRESSION
                    .replace("{KEY}", left)
                    .replace("{TIME_ZONE}", timeZone);
            return String.format(dateBetween, array.get(0), array.get(1));
        }
        return null;
    }

    private String escapeSql(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return obj.toString().replaceAll("\'+", "''");
    }

    private Object getSqlValue(Object obj) {
        if (obj instanceof String) {
            return String.format("'%s'", escapeSql(obj));
        }

        if (obj instanceof Date
                || obj instanceof LocalDate
                || obj instanceof Timestamp) {
            return String.format("'%s'", obj);
        }

        if (obj instanceof ArrayList) {
            ArrayList array = (ArrayList) obj;
            Object list = array
                    .stream()
                    .map(a -> getObjectValue(a))
                    .collect(Collectors.toList());
            String result = String.join(",", (List<String>)list);
            return String.format("(%s)", result);
        }
        return obj;
    }

    private Object getObjectValue(Object obj) {
        if (obj instanceof String) {
            return String.format("'%s'", escapeSql(obj));
        }
        return obj;
    }

    private boolean isOperator(String key) {
        return LogicOperator.from(key) != null;
    }
}
