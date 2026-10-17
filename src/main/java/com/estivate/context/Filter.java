package com.estivate.context;

import java.util.function.Function;

import com.estivate.query.AlterQuery;
import com.estivate.query.DeleteQuery;
import com.estivate.query.InsertQuery;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.result.ResultTable;


@Deprecated
public final class Filter {

    @FunctionalInterface
    public interface AlterFilter {
        Boolean filter(AlterQuery<?> query, Function<AlterQuery<?>, Boolean> proceed);
    }

    
    @FunctionalInterface
    public interface DeleteFilter {

        Boolean filter(DeleteQuery<?> query, Function<DeleteQuery<?>, Boolean> proceed);

    }

    @FunctionalInterface
    public interface SelectFilter {

        ResultTable filter(SelectQuery<?> query, Function<SelectQuery<?>, ResultTable> proceed);

    }

    @FunctionalInterface
    public interface UpdateFilter {

        Boolean filter(UpdateQuery<?> query, Function<UpdateQuery<?>, Boolean> proceed);

    }

    @FunctionalInterface
    public interface InsertFilter {

        Void filter(InsertQuery<?> query, Function<InsertQuery<?>, Void> proceed);

    }

}
