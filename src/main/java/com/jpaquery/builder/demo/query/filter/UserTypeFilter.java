package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTypeFilter {
    private StringExpression resourceId;
    private StringExpression code;
    private UserTypeFilter and;
    private UserTypeFilter or;
    private UserTypeFilter not;
}


