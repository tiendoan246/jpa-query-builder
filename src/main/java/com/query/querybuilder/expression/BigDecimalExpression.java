package com.query.querybuilder.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BigDecimalExpression {
    private BigDecimal eq;
    private BigDecimal gt;
    private BigDecimal gte;
    private BigDecimal lt;
    private BigDecimal lte;
    private List<BigDecimal> in;
}
