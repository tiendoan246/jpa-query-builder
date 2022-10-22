package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerFilter {
    private StringExpression resourceId;
    private StringExpression status;
    private StringExpression name;
    private StringExpression nameLegal;
    private StringExpression country;
    private StringExpression registrationId;
    private StringExpression taxId;
    private StringExpression email;
    private StringExpression logo;
    private BooleanExpression deleted;

    private PartnerFilter and;
    private PartnerFilter or;
    private PartnerFilter not;
}
