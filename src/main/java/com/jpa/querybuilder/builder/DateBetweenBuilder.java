package com.jpa.querybuilder.builder;

import com.jpa.querybuilder.constant.DateConstant;
import com.jpa.querybuilder.constant.MessageConstant;
import com.jpa.querybuilder.constant.SearchOperator;
import com.jpa.querybuilder.utils.DateTimeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return Collections.singletonList(java.util.ArrayList.class.getTypeName());
    }

    @SneakyThrows
    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        List dates = (List) value;
        if (dates.size() != 2) {
            throw new RuntimeException(MessageConstant.DATE_BETWEEN_INVALID_ERROR.getMessage());
        }
        return builder.between(getObject(root, entity, key)
                , DateTimeUtils.startOfDaySql(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(dates.get(0).toString()))
                , DateTimeUtils.endOfDaySql(new SimpleDateFormat(DateConstant.DATE_FORMAT).parse(dates.get(1).toString())));
    }
}
