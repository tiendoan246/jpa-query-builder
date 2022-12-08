package com.jpaquery.builder.demo.query.builder;

import com.jpaquery.builder.demo.query.constant.SearchOperator;
import io.micrometer.core.instrument.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

public interface QueryBuilder<T> {

    default SearchOperator getOperator() {
        return SearchOperator.EQ;
    }

    default List<String> getDataType() {
        return Collections.singletonList(String.class.getTypeName());
    }

    default <Y>Path<Y> getObject(Root<T> root, String entity, String key) {
        return StringUtils.isBlank(entity) ? root.get(key) : root.join(entity, JoinType.LEFT).get(key);
    }

    Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity);
}
