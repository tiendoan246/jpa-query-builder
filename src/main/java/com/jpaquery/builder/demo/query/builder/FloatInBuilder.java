package com.jpaquery.builder.demo.query.builder;

import com.jpaquery.builder.demo.query.constant.SearchOperator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FloatInBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.IN;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(Float.class.getTypeName());
    }

    @SneakyThrows
    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return builder.and(getObject(root, entity, key).in(Float.parseFloat(value.toString())));
    }
}
