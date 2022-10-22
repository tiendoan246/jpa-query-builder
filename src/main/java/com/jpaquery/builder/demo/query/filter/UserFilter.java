package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {
    private StringExpression resourceId;
    private StringExpression firstName;
    private StringExpression lastName;
    private StringExpression email;
    private StringExpression phoneRegistered;
    private UserFilter and;
    private UserFilter or;
    private UserFilter not;
}


