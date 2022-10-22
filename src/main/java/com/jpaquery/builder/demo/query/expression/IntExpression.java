package com.jpaquery.builder.demo.query.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntExpression {
    private Integer eq;
    private Integer gt;
    private Integer gte;
    private Integer lt;
    private Integer lte;
    private List<Integer> in;
}
