package com.jpa.querybuilder.expression;

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
    private String isNull;
    private List<String> reg;
    private List<String> in;
}
