package com.query.querybuilder.utils;

import com.query.querybuilder.criteria.SearchCriteria;
import com.query.querybuilder.builder.QueryBuilder;
import com.query.querybuilder.constant.SearchOperator;
import com.query.querybuilder.filter.PageFilter;
import com.query.querybuilder.filter.SortFilter;
import com.query.querybuilder.specification.BaseSpecification;
import com.query.querybuilder.specification.SpecificationBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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

@Slf4j
public class SearchQueryBuilder<T> {

    private List<QueryBuilder<T>> queryBuilders;
    private Map<String, Object> map;
    private PageFilter pageFilter;
    private SortFilter sortFilter;
    private Specification<T> root;
    private Specification<T> withDelete;
    private String nativeSqlCondition;
    private String timezone;

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

    public SearchQueryBuilder<T> withTimezone(String timezone) {
        this.timezone = timezone;
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
        root = builder.buildSpecification(map, timezone);
        nativeSqlCondition = builder.getNativeSqlCondition();

        log.info("CONDITION: {}", nativeSqlCondition);

        if (root != null && withDelete != null) {
            root = root.and(withDelete);
        }

        if (root == null && withDelete != null) {
            root = withDelete;
        }

        if (withDelete != null) {
            nativeSqlCondition = nativeSqlCondition + " and a.deleted = false";
        }

        Pageable pageable = SortBuilder.builder()
                .withPageFilter(pageFilter)
                .withSort(sortFilter)
                .build();

        return Pair.of(root, pageable);
    }

    public String getNativeSqlCondition() {
        return nativeSqlCondition;
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
