package com.jpaquery.builder.demo.query.builder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ObjectEqualBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.EQ;
    }

    @Override
    public List<String> getDataType() {
        return Arrays.asList(
                String.class.getTypeName()
                , Integer.class.getTypeName()
                , Long.class.getTypeName()
                , Float.class.getTypeName()
                , BigDecimal.class.getTypeName()
                , Double.class.getTypeName());
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value) {
        return builder.equal(root.get(key), value.toString());
    }
}
