package com.query.querybuilder.builder;

import com.query.querybuilder.constant.SearchOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BooleanNullBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.IS_NULL;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(Boolean.class.getTypeName());
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return Boolean.TRUE.equals(value)
                ? builder.isNull(getObject(root, entity, key))
                : builder.isNotNull(getObject(root, entity, key));

    }
}
