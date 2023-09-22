package com.jpa.querybuilder.builder;

import com.jpa.querybuilder.constant.SearchOperator;
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
public class ObjectNotEqualBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.NEQ;
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
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return builder.notEqual(getObject(root, entity, key), value.toString());
    }
}
