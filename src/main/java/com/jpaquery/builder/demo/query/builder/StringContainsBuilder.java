package com.jpaquery.builder.demo.query.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class StringContainsBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.CONTAINS;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(String.class.getTypeName());
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value) {
        return builder.like(root.get(key), "%" + value.toString() + "%");
    }
}
