package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryFilter {
    private StringExpression resourceId;
    private StringExpression countryCode;
    private StringExpression countryName;
    private StringExpression currencyCode;
    private StringExpression currencyName;
    private StringExpression currencySymbol;
    private CountryFilter and;
    private CountryFilter or;
    private CountryFilter not;
}


