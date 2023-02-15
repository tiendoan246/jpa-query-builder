package com.jpaquery.builder.demo.query.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringExpression {
    private String eq;
    private String neq;
    private String contains;
    private List<String> in;
}
