package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressFilter {
    private StringExpression resourceId;
    private StringExpression owningEntity;
    private StringExpression ownerResourceId;
    private StringExpression addressType;
    private StringExpression city;
    private StringExpression postalCode;
    private StringExpression country;
    private AddressFilter and;
    private AddressFilter or;
    private AddressFilter not;
}


