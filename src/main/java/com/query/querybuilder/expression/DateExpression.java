package com.query.querybuilder.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateExpression {
    private LocalDate eq;
    private LocalDate gt;
    private LocalDate gte;
    private LocalDate lt;
    private LocalDate lte;
    private List<LocalDate> between;
}
