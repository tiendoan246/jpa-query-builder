package com.jpaquery.builder.demo.query.builder;

import com.jpaquery.builder.demo.query.constant.DateConstant;
import com.jpaquery.builder.demo.query.constant.SearchOperator;
import com.jpaquery.builder.demo.query.utils.DateTimeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DateLessThanEqualBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.LTE;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(LocalDate.class.getTypeName());
    }

    @SneakyThrows
    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        return builder.lessThanOrEqualTo(getObject(root, entity, key)
                , DateTimeUtils.endOfDay(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(value.toString())));
    }
}
