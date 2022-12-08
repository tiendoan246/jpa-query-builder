package com.jpaquery.builder.demo.query.builder;

import com.jpaquery.builder.demo.query.constant.SearchOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ObjectInBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.IN;
    }

    @Override
    public List<String> getDataType() {
        return Arrays.asList(
                ArrayList.class.getTypeName()
                , List.class.getTypeName());
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return builder.and(getObject(root, entity, key).in((List) value));
    }
}
