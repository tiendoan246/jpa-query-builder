package com.jpa.querybuilder.utils;

import com.jpa.querybuilder.constant.OrderBy;
import com.jpa.querybuilder.constant.SearchConstant;
import com.jpa.querybuilder.filter.PageFilter;
import com.jpa.querybuilder.filter.SortFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class SortBuilder {

    private PageFilter pageFilter;
    private SortFilter sortFilter;
    private static final String DEFAULT_SORT_COLUMN_NAME = "createdAt";
    private static final String ID_SORT_COLUMN_NAME = "resourceId";

    public static SortBuilder builder() {
        return new SortBuilder();
    }

    public SortBuilder withPageFilter(PageFilter pageFilter) {
        this.pageFilter = pageFilter;
        return this;
    }

    public SortBuilder withSort(SortFilter sortFilter) {
        this.sortFilter = sortFilter;
        return this;
    }

    public Pageable build() {
        PageFilter page = getPageFilter(this.pageFilter);
        Sort sort = buildSortBy(this.sortFilter);

        if (sort != null) {
            return PageRequest.of(page.getOffset(), page.getLimit(), sort);
        } else {
            return PageRequest.of(page.getOffset(), page.getLimit());
        }
    }

    private Sort buildSortBy(SortFilter sortFilter) {
        if (sortFilter == null) {
            return getDefaultSort();
        }

        Sort sortBy = null;
        if (!sortFilter.getSortBy().isEmpty()) {
            if (OrderBy.ASC.getCode().equalsIgnoreCase(sortFilter.getOrder())) {
                sortBy = Sort.by(sortFilter.getSortBy().get(0)).ascending();
            } else {
                sortBy = Sort.by(sortFilter.getSortBy().get(0)).descending();
            }

            if (sortFilter.getSortBy().size() > 1) {
                for (int i = 1; i < sortFilter.getSortBy().size(); i++) {
                    if (OrderBy.ASC.getCode().equalsIgnoreCase(sortFilter.getOrder())) {
                        sortBy = sortBy.and(Sort.by(sortFilter.getSortBy().get(i)).ascending());
                    } else {
                        sortBy = sortBy.and(Sort.by(sortFilter.getSortBy().get(i)).descending());
                    }
                }
            }
        }

        sortBy = sortBy.and(getSortId());

        return sortBy;
    }

    private Sort getDefaultSort() {
        return Sort.by(DEFAULT_SORT_COLUMN_NAME).ascending();
    }

    private Sort getSortId() {
        return Sort.by(ID_SORT_COLUMN_NAME).ascending();
    }

    private PageFilter getPageFilter(PageFilter pageFilter) {
        return Optional.ofNullable(pageFilter)
                .orElseGet(() -> PageFilter.builder()
                        .offset(0)
                        .limit(SearchConstant.DEFAULT_PAGE_SIZE)
                        .build());
    }
}
