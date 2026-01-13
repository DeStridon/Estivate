package com.estivate.page;

import com.estivate.context.Context;
import com.estivate.query.SelectQuery;

public interface PagingStrategy {

    PageResult fetch(Context context, SelectQuery<?> query);
    

}
