package com.jpa.querybuilder.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortFilter {
    private List<String> sortBy;
    private String order;
}
