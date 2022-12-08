package com.jpaquery.builder.demo.query.utils;

import com.jpaquery.builder.demo.query.builder.QueryBuilder;
import com.jpaquery.builder.demo.query.constant.SearchOperator;
import com.jpaquery.builder.demo.query.criteria.SearchCriteria;
import com.jpaquery.builder.demo.query.filter.PageFilter;
import com.jpaquery.builder.demo.query.filter.SortFilter;
import com.jpaquery.builder.demo.query.specification.BaseSpecification;
import com.jpaquery.builder.demo.query.specification.SpecificationBuilder;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchQueryBuilder<T> {

    private List<QueryBuilder<T>> queryBuilders;
    private Map<String, Object> map;
    private PageFilter pageFilter;
    private SortFilter sortFilter;
    private Specification<T> root;
    private Specification<T> withDelete;

    private SearchQueryBuilder(List<QueryBuilder<T>> queryBuilders) {
        this.queryBuilders = queryBuilders;
    }

    public static <T> SearchQueryBuilder<T> builder(List<QueryBuilder<T>> queryBuilders) {
        return new SearchQueryBuilder<>(queryBuilders);
    }

    public SearchQueryBuilder<T> withData(Object map) {
        if (map == null) {
            return this;
        }

        this.map = convertToMap(map);
        return this;
    }

    public SearchQueryBuilder<T> withPage(PageFilter pageFilter) {
        this.pageFilter = pageFilter;
        return this;
    }

    public SearchQueryBuilder<T> withSort(SortFilter sortFilter) {
        this.sortFilter = sortFilter;
        return this;
    }

    public SearchQueryBuilder<T> withDelete() {
        withDelete = new BaseSpecification<T>(
                queryBuilders,
                new SearchCriteria("deleted", SearchOperator.EQ, false), null);
        return this;
    }

    public Pair<Specification<T>, Pageable> build() {
        SpecificationBuilder<T> builder = SpecificationBuilder.createInstance(queryBuilders);
        root = builder.buildSpecification(map);

        if (root != null && withDelete != null) {
            root = root.and(withDelete);
        }

        if (root == null && withDelete != null) {
            root = withDelete;
        }

        Pageable pageable = SortBuilder.builder()
                .withPageFilter(pageFilter)
                .withSort(sortFilter)
                .build();

        return Pair.of(root, pageable);
    }

    @SneakyThrows
    private Map<String, Object> convertToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);

            if (Objects.isNull(value)) {
                continue;
            }

            if (value instanceof LocalDate
                    || value instanceof Date
                    || value instanceof LocalDateTime
                    || value instanceof Boolean
                    || value instanceof Integer
                    || value instanceof String
                    || value instanceof Float
                    || value instanceof BigDecimal
                    || value instanceof ArrayList
                    || value instanceof List) {
                map.put(field.getName(), value);
            } else {
                map.put(field.getName(), convertToMap(value));
            }
        }
        return map;
    }

}
