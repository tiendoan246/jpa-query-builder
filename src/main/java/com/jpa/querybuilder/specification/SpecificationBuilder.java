package com.jpa.querybuilder.specification;

import com.jpa.querybuilder.builder.QueryBuilder;
import com.jpa.querybuilder.constant.DateConstant;
import com.jpa.querybuilder.constant.LogicOperator;
import com.jpa.querybuilder.constant.MessageConstant;
import com.jpa.querybuilder.constant.SearchOperator;
import com.jpa.querybuilder.criteria.SearchCriteria;
import com.jpa.querybuilder.model.JsonFilter;
import com.jpa.querybuilder.utils.CommonUtils;
import com.jpa.querybuilder.utils.DateTimeUtils;
import com.jpa.querybuilder.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
                if (!(entry.getValue() instanceof Map)) {
                    continue;
                }
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
        Pair<String, Specification<T>> groupPair = null;
        for (Map.Entry entry: entries) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }

            if (isOperator(entry.getKey().toString())) {
                Map params = (Map) entry.getValue();
                if (group == null) {
                    groupPair = groupCondition(params, LogicOperator.from(entry.getKey().toString()));
                    appendQuery(builder, " and (%s)", groupPair.getLeft());
                    group = groupPair.getRight();
                } else {
                    LogicOperator opt = LogicOperator.AND.getCode().equalsIgnoreCase(entry.getKey().toString())
                            ? LogicOperator.AND
                            : LogicOperator.OR;
                    if (LogicOperator.AND.equals(operator)) {
                        groupPair = groupCondition(params, opt);
                        group = group.and(groupPair.getRight());
                        appendQuery(builder, " and (%s)", groupPair.getLeft());
                    } else {
                        groupPair = groupCondition(params, opt);
                        group = group.or(groupPair.getRight());
                        appendQuery(builder, " or (%s)", groupPair.getLeft());
                    }
                }
                continue;
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
            case JSON_IN, JSON_NOT_IN -> {
                return getJsonExpression(alias, StringUtils.camelToSnake(left), right, operator);
            }
            case REG -> {
                return getRegExpression(alias, StringUtils.camelToSnake(left), right, operator);
            }
            case IS_NULL -> {
                return getIsNullExpression(alias, StringUtils.camelToSnake(left), right);
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
            case BETWEEN, RANGE-> {
                return getBetweenExpression(StringUtils.camelToSnake(left), right);
            }
            case CONTAINS -> {
                return getContainsExpression(StringUtils.camelToSnake(left), right, operator);
            }
            case JSON_IN, JSON_NOT_IN -> {
                return getJsonExpression(StringUtils.camelToSnake(left), right, operator);
            }
            case REG -> {
                return getRegExpression(StringUtils.camelToSnake(left), right, operator);
            }
            case IS_NULL -> {
                return getIsNullExpression(StringUtils.camelToSnake(left), right);
            }
            default -> {
                return String.format(" a.%s %s %s ",
                        StringUtils.camelToSnake(left),
                        CommonUtils.sqlOperators.get(operator),
                        getSqlValue(right.getValue()));
            }
        }
    }


    private String getRegExpression(String alias, String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof ArrayList) {
            List list = (List) right.getValue();
            StringBuilder builder = new StringBuilder("(");
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    builder.append(alias + "." + left + " " + sqlOperator + " '%" + escapeSql(list.get(i)) + "%' ");
                } else {
                    builder.append(" or " + alias + "." + left + " " + sqlOperator + " '%" + escapeSql(list.get(i)) + "%' ");
                }
            }
            builder.append(")");
            return builder.toString();
        }
        return null;
    }

    private String getRegExpression(String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof ArrayList) {
            List list = (List) right.getValue();
            StringBuilder builder = new StringBuilder("(");
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    builder.append("a." + left + " " + sqlOperator + " '%" + escapeSql(list.get(i)) + "%' ");
                } else {
                    builder.append(" or a." + left + " " + sqlOperator + " '%" + escapeSql(list.get(i)) + "%' ");
                }
            }
            builder.append(")");
            return builder.toString();
        }
        return null;
    }

    private String getContainsExpression(String alias, String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof String) {
            return alias + "." + left + " " + sqlOperator + " '%" + escapeSql(right.getValue()) + "%' ";
        }
        return null;
    }

    private String getJsonExpression(String alias, String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        StringBuilder builder = new StringBuilder();
        if (right.getValue() instanceof ArrayList) {
            ArrayList<JsonFilter> jsonFilters = (ArrayList<JsonFilter>) right.getValue();
            if (jsonFilters == null || jsonFilters.isEmpty()) {
                return null;
            }
            for (int i = 0; i < jsonFilters.size(); i++) {
                String condition = "jsonb_extract_path_text(" +alias+ "." +left+ ", '" +jsonFilters.get(i).getKey()+ "') " + sqlOperator+ " '" + escapeSql(jsonFilters.get(i).getValue()) + "' ";
                if (i == 0) {
                    builder.append("(" + condition);
                    continue;
                }
                builder.append(" OR " + condition);
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(builder.toString())) {
            builder.append(")");
        }
        return builder.toString();
    }

    private String getJsonExpression(String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        StringBuilder builder = new StringBuilder();
        if (right.getValue() instanceof ArrayList) {
            ArrayList<JsonFilter> jsonFilters = (ArrayList<JsonFilter>) right.getValue();
            if (jsonFilters == null || jsonFilters.isEmpty()) {
                return null;
            }
            for (int i = 0; i < jsonFilters.size(); i++) {
                String condition = "jsonb_extract_path_text(a." +left+ ", '" +jsonFilters.get(i).getKey()+ "') " + sqlOperator+ " '" + escapeSql(jsonFilters.get(i).getValue()) + "' ";
                if (i == 0) {
                    builder.append("(" + condition);
                    continue;
                }
                builder.append(" OR " + condition);
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(builder.toString())) {
            builder.append(")");
        }
        return builder.toString();
    }

    private String getContainsExpression(String left, Map.Entry right, SearchOperator operator) {
        String sqlOperator = CommonUtils.sqlOperators.get(operator);
        if (right.getValue() instanceof String) {
            return "a." + left + " " + sqlOperator + " '%" + escapeSql(right.getValue()) + "%' ";
        }
        return null;
    }

    private String getIsNullExpression(String left, Map.Entry right) {
        if (right.getValue() instanceof String) {
            if (((String) right.getValue()).equalsIgnoreCase("true")) {
                return "a." + left + " IS NULL";
            } else {
                return "a." + left + " IS NOT NULL";
            }
        }
        return null;
    }

    private String getIsNullExpression(String alias, String left, Map.Entry right) {
        if (right.getValue() instanceof String) {
            if (((String) right.getValue()).equalsIgnoreCase("true")) {
                return alias + "." + left + " IS NULL";
            } else {
                return alias + "." + left + " IS NOT NULL";
            }
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
            String start = "";
            String end = "";
            if (array.get(0) instanceof Long) {
                start = DateTimeUtils.toUTCDateTimeString(Long.parseLong(array.get(0).toString()));
            } else {
                start = array.get(0).toString();
            }
            if (array.get(1) instanceof Long) {
                end = DateTimeUtils.toUTCDateTimeString(Long.parseLong(array.get(1).toString()));
            } else {
                end = array.get(1).toString();
            }
            return String.format(dateBetween, start, end);
        }
        return null;
    }

    @SneakyThrows
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
            String start = "";
            String end = "";
            if (array.get(0) instanceof Long) {
                start = DateTimeUtils.toUTCDateTimeString(Long.parseLong(array.get(0).toString()));
            } else {
                start = DateTimeUtils.startOfDay(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(array.get(0).toString())).toString();
            }
            if (array.get(1) instanceof Long) {
                end = DateTimeUtils.toUTCDateTimeString(Long.parseLong(array.get(1).toString()));
            } else {
                end = DateTimeUtils.endOfDay(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(array.get(1).toString())).toString();
            }
            return String.format(dateBetween, start, end);
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
