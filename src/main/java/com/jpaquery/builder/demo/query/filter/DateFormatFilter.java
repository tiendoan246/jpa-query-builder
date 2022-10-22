package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateFormatFilter {
    private StringExpression resourceId;
    private StringExpression format;
    private DateFormatFilter and;
    private DateFormatFilter or;
    private DateFormatFilter not;
}


