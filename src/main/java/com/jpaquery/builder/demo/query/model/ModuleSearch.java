package com.jpaquery.builder.demo.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSearch {
    private ModuleFilter filter;
    private PageFilter page;
    private SortFilter sort;
}
