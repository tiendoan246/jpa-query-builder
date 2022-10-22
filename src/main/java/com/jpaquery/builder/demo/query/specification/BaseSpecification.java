package com.jpaquery.builder.demo.query.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BaseSpecification<T> implements Specification<T> {

    private List<QueryBuilder<T>> queryBuilders;
    private SearchCriteria criteria;

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
                .map(b -> b.buildPredicate(builder, root, criteria.getKey(), criteria.getValue()))
                .orElse(null);
    }

    public static <T, U> Specification<T> hasNested(String fieldObject, String nestedField, String value) {
        return (root, query, criteriaBuilder) -> {
            Join<T, U> nestedObject = root.join(fieldObject);
            return criteriaBuilder.equal(nestedObject.get(nestedField), value);
        };
    }
}
