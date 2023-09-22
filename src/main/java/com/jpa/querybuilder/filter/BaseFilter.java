package com.jpa.querybuilder.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseFilter <T> {
    protected T and;
    protected T or;
    protected T not;
}
