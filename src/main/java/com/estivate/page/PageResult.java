package com.estivate.page;


import com.estivate.result.ResultTable;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageResult extends ResultTable{
    
   
    
    // Pagination metadata
    private final PagingStrategy pagingStrategy;
    private final int pageSize;
    private final boolean hasNext;
    private final boolean hasPrevious;
    private final long totalElements;    // optional, requires count query (-1 if not available)
    private final int totalPages;        // optional, calculated from totalElements (-1 if not available)
    
    
    
    
}