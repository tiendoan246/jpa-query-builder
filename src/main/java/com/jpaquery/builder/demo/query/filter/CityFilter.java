package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityFilter {
    private StringExpression resourceId;
    private StringExpression countryName;
    private StringExpression countryCode;
    private StringExpression cityName;

    private CityFilter and;
    private CityFilter or;
    private CityFilter not;
}
