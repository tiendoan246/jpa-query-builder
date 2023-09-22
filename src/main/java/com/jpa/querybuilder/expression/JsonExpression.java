package com.jpa.querybuilder.expression;

import com.jpa.querybuilder.model.JsonFilter;
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
