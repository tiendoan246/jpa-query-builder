package com.jpaquery.builder.demo.query.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageFilter {
    private Integer limit;
    private Integer offset;
}
