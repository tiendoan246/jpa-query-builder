package com.jpaquery.builder.demo.query.specification;

import java.util.List;

public class UserSpecification extends BaseSpecification<UserEntity> {

    public UserSpecification(List<QueryBuilder<UserEntity>> queryBuilders, SearchCriteria criteria) {
        super(queryBuilders, criteria);
    }
}
