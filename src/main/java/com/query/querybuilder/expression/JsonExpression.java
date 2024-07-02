package com.query.querybuilder.expression;

import com.query.querybuilder.model.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonExpression {
    private List<JsonFilter> jsonIn;
    private List<JsonFilter> jsonNotIn;
}
