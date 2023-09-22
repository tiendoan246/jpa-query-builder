package com.jpa.querybuilder.criteria;

import com.jpa.querybuilder.constant.SearchOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    private String key;
    private SearchOperator operation;
    private Object value;
}
