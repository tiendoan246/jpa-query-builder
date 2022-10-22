package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTypeFilter {
    private StringExpression resourceId;
    private StringExpression name;
    private BooleanExpression deleted;

    private OrganizationTypeFilter and;
    private OrganizationTypeFilter or;
    private OrganizationTypeFilter not;
}
