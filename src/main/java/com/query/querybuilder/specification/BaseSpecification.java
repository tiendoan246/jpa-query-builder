package com.query.querybuilder.specification;

import com.query.querybuilder.builder.QueryBuilder;
import com.query.querybuilder.constant.SearchOperator;
import com.query.querybuilder.criteria.SearchCriteria;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BaseSpecification<T> implements Specification<T> {

    private List<QueryBuilder<T>> queryBuilders;
    private SearchCriteria criteria;
    private String entity;

    protected QueryBuilder<T> getQueryBuilder(String dataType, SearchOperator operator) {
        return queryBuilders
                .stream()
                .filter(q -> q.getOperator().equals(operator) && q.getDataType().contains(dataType))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        QueryBuilder<T> queryBuilder = getQueryBuilder(criteria.getValue().getClass().getName(), criteria.getOperation());
        return Optional.ofNullable(queryBuilder)
                .map(b -> b.buildPredicate(builder, root, criteria.getKey(), criteria.getValue(), entity))
                .orElse(null);
    }
}
