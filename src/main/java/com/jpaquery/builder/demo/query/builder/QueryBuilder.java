package com.jpaquery.builder.demo.query.builder;

import javax.persistence.criteria.CriteriaBuilder;
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

    Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value);
}
