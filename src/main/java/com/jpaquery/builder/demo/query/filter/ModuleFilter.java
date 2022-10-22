package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleFilter {
    private StringExpression resourceId;
    private BooleanExpression privileged;
    private ModuleFilter and;
    private ModuleFilter or;
    private ModuleFilter not;
}


