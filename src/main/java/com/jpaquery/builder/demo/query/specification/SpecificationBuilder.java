package com.jpaquery.builder.demo.query.specification;

import com.jpaquery.builder.demo.query.builder.QueryBuilder;
import com.jpaquery.builder.demo.query.constant.LogicOperator;
import com.jpaquery.builder.demo.query.constant.SearchOperator;
import com.jpaquery.builder.demo.query.criteria.SearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class SpecificationBuilder<T> {

    private List<QueryBuilder<T>> queryBuilders;

    private SpecificationBuilder(List<QueryBuilder<T>> queryBuilders) {
        this.queryBuilders = queryBuilders;
    }

    public static <T> SpecificationBuilder<T> createInstance(List<QueryBuilder<T>> queryBuilders) {
        return new SpecificationBuilder<>(queryBuilders);
    }

    public Specification<T> buildSpecification(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Specification<T> root = null;

        Set<Map.Entry<String, Object>> entries =  map.entrySet();
        for (Map.Entry entry: entries) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }

            if (isOperator(entry.getKey().toString())) {
                Map params = (Map) entry.getValue();
                if (LogicOperator.AND.getCode().equalsIgnoreCase(entry.getKey().toString())) {
                    root = root == null
                            ? groupCondition(params, LogicOperator.AND)
                            : root.and(groupCondition(params, LogicOperator.AND));
                } else {
                    root = root == null
                            ? groupCondition(params, LogicOperator.OR)
                            : root.and(groupCondition(params, LogicOperator.OR));
                }
            } else {
                Set<Map.Entry<String, Object>> filters =  ((Map) entry.getValue()).entrySet();
                for (Map.Entry f: filters) {
                    if (Objects.isNull(f.getValue())) {
                        continue;
                    }
                    root = root == null
                            ? this.createSpecification(entry, f)
                            : root.and(createSpecification(entry, f));
                }
            }
        }

        return root;
    }

    private Specification<T> groupCondition(Map map, LogicOperator operator) {
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
                        return group.and(groupCondition(params, LogicOperator.AND));
                    } else {
                        return group.or(groupCondition(params, LogicOperator.OR));
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
                    group = group == null
                            ? createSpecification(fieldName, key)
                            : group.and(createSpecification(fieldName, key));
                } else {
                    group = group == null
                            ? createSpecification(fieldName, key)
                            : group.or(createSpecification(fieldName, key));
                }
            }
        }
        return group;
    }

    private BaseSpecification<T> createSpecification(String fieldName, Map.Entry right) {
        SearchOperator operator = SearchOperator.from(right.getKey().toString());
        if (Objects.isNull(operator)) {
            return createNestedSpecification(fieldName, right);
        }

        return new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        fieldName,
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), null);
    }

    private BaseSpecification<T> createSpecification(Map.Entry left, Map.Entry right) {
        SearchOperator operator = SearchOperator.from(right.getKey().toString());
        if (Objects.isNull(operator)) {
            return createNestedSpecification(left.getKey().toString(), right);
        }

        return new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        left.getKey().toString(),
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), null);
    }

    private BaseSpecification<T> createNestedSpecification(String fieldName, Map.Entry right) {
        Map params = (Map) right.getValue();
        Set<Map.Entry<String, Object>> operators =  params.entrySet();
        for (Map.Entry ops: operators) {
            return new BaseSpecification<T>(queryBuilders,
                    new SearchCriteria(
                            right.getKey().toString(),
                            SearchOperator.from(ops.getKey().toString()),
                            ops.getValue()),
                    fieldName);
        }

        return new BaseSpecification<T>(queryBuilders,
                new SearchCriteria(
                        fieldName,
                        SearchOperator.from(right.getKey().toString()),
                        right.getValue()), fieldName);
    }

    private boolean isOperator(String key) {
        return LogicOperator.from(key) != null;
    }
}
