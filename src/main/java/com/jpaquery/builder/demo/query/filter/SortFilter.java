package com.jpaquery.builder.demo.query.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SortFilter {
    private List<String> sortBy;
    private String order;
}
