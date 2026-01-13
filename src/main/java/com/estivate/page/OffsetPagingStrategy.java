package com.estivate.page;

import java.util.ArrayList;
import java.util.List;

import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultRow;
import com.estivate.result.ResultTable;

import lombok.Getter;


public class OffsetPagingStrategy implements PagingStrategy {
    
    @Getter
    private final int pageNumber; // 0-based page number
    @Getter
    private final int pageSize;
    private final boolean fetchTotalCount;
    
    /**
     * Creates an offset paging strategy
     * @param pageNumber 0-based page number
     * @param pageSize number of items per page
     * @param fetchTotalCount whether to fetch total count (requires additional query)
     */
    public OffsetPagingStrategy(int pageNumber, int pageSize, boolean fetchTotalCount) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.fetchTotalCount = fetchTotalCount;
    }
    
    /**
     * Creates an offset paging strategy without total count
     */
    public OffsetPagingStrategy(int pageNumber, int pageSize) {
        this(pageNumber, pageSize, false);
    }

    @Override
    public PageResult fetch(Context context, SelectQuery<?> query) {
        // Clone the query to avoid modifying the original
        SelectQuery<?> pagedQuery = query.clone();
        
        // Calculate offset
        int offset = pageNumber * pageSize;
        
        // Apply limit and offset
        pagedQuery.limit(pageSize + 1); // Fetch one extra to determine if there's a next page
        pagedQuery.offset(offset);
        
        // Execute query
        ResultTable resultTable = context.fetch(pagedQuery);

        // Determine if there's a next page
        boolean hasNext = resultTable.size() > pageSize;
        
        // Fetch total count if requested
        Long totalElements = null;
        if (fetchTotalCount) {
            totalElements = context.fetchCountAll(query);
        }
        
        return PageResult.builder()
            .rows(limitToPageSize(resultTable, pageSize))
            .columnNames(resultTable.getColumnNames())
            .query(resultTable.getQuery())
            .pageSize(pageSize)
            .pagingStrategy(new OffsetPagingStrategy(pageNumber + 1, pageSize))
            .hasNext(hasNext)
            .build();
    }

    private List<ResultRow> limitToPageSize(ResultTable resultTable, int pageSize) {
        if (resultTable.size() > pageSize) {
            return new ArrayList<>(resultTable.getRows().subList(0, pageSize));
        }
        return resultTable.getRows();
    }
}
