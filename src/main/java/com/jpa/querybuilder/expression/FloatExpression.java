package com.jpa.querybuilder.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloatExpression {
    private Float eq;
    private Float gt;
    private Float gte;
    private Float lt;
    private Float lte;
    private List<Float> in;
}
