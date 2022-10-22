package com.jpaquery.builder.demo.query.builder;

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
public class DateBetweenBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.BETWEEN;
    }

    @Override
    public List<String> getDataType() {
        return Collections.singletonList(LocalDate.class.getTypeName());
    }

    @SneakyThrows
    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value) {
        List dates = (List) value;
        if (dates.size() != 2) {
            throw new BusinessException(MessageConstant.DATE_BETWEEN_INVALID_ERROR.getCode()
                    , MessageConstant.DATE_BETWEEN_INVALID_ERROR.getMessage());
        }
        return builder.between(root.get(key)
                , DateTimeUtils.startOfDay(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(dates.get(0).toString()))
                , DateTimeUtils.endOfDay(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(dates.get(1).toString())));
    }
}
