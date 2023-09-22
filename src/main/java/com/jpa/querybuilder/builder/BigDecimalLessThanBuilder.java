package com.jpa.querybuilder.builder;

import com.jpa.querybuilder.constant.SearchOperator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BigDecimalLessThanBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.LT;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(BigDecimal.class.getTypeName());
    }

    @SneakyThrows
    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return builder.lessThan(getObject(root, entity, key), new BigDecimal(value.toString()));
    }
}
