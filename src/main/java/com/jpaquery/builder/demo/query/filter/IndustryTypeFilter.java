package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndustryTypeFilter {
    private StringExpression resourceId;
    private StringExpression name;
    private IndustryTypeFilter and;
    private IndustryTypeFilter or;
    private IndustryTypeFilter not;
}


