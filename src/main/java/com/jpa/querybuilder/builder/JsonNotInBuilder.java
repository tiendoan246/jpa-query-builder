package com.jpa.querybuilder.builder;

import com.jpa.querybuilder.constant.SearchOperator;
import com.jpa.querybuilder.model.JsonFilter;
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
public class JsonNotInBuilder<T> implements QueryBuilder<T> {

    @Override
    public SearchOperator getOperator() {
        return SearchOperator.JSON_NOT_IN;
    }

    @Override
    public List<String> getDataType() {
        return Arrays.asList(
                ArrayList.class.getTypeName()
                , List.class.getTypeName());
    }

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, Root<T> root, String key, Object value, String entity) {
        List list = (List) value;
        List<Predicate> criteriaBuilders = new ArrayList<>();
        for (Object obj: list) {
            JsonFilter jsonFilter = (JsonFilter)obj;
            criteriaBuilders.add(builder.notEqual(builder.function("jsonb_extract_path_text",
                    String.class, getObject(root, entity, key), builder.literal(jsonFilter.getKey())), jsonFilter.getValue()));
        }
        return builder.and(criteriaBuilders.toArray(new Predicate[0]));
    }
}
