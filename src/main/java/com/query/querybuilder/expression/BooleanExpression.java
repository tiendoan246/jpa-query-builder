package com.query.querybuilder.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooleanExpression {
    private Boolean eq;
    private Boolean isNull;
}
