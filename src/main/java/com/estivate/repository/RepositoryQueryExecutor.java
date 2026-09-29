package com.estivate.repository;

import java.lang.reflect.Method;
import java.util.List;

import com.estivate.context.Context;
import com.estivate.query.SelectQuery;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Executes a {@link RepositoryQuery} built from an abstract repository method call.
 */
public class RepositoryQueryExecutor<T> {

    final Class<? extends Repository<T>> repositoryClass;
    final Context context;

    public RepositoryQueryExecutor(Class<? extends Repository<T>> repositoryClass, Context context) {
        this.repositoryClass = repositoryClass;
        this.context = context;
    }

    @RuntimeType
    public Object intercept(
        @This Object self,
        @Origin Method method,
        @AllArguments Object[] args,
        @SuperMethod(nullIfImpossible = true) Method superMethod,
        @Empty Object defaultValue
    ) throws Throwable {
        RepositoryQuery queryMethod = new RepositoryQuery(method.getDeclaringClass(), method.getName());
        SelectQuery<?> query = queryMethod.getQuery(args);

        ReturnType returnType = resolveReturnType(queryMethod.returnType, method, queryMethod.returnEntity);

        if (returnType == ReturnType.Count) {
            Long count = context.extractCountAll(query);
            if (queryMethod.returnEntity == boolean.class || queryMethod.returnEntity == Boolean.class) {
                return count > 0;
            }
            return count;
        }

        if (returnType == ReturnType.DistinctList) {
            return context.extractListDistinct(query, queryMethod.entityClass, queryMethod.selectField);
        }

        if (returnType == ReturnType.List) {
            if (queryMethod.selectField != null) {
                return context.extractList(query, queryMethod.entityClass, queryMethod.selectField);
            }
            return context.extractList(query, queryMethod.returnEntity);
        }

        if (queryMethod.selectField != null) {
            return context.extractSingle(query, queryMethod.entityClass, queryMethod.selectField);
        }
        return context.extractSingle(query, queryMethod.returnEntity);
    }

    private static ReturnType resolveReturnType(ReturnType returnType, Method method, Class<?> returnEntity) throws Exception {
        if (returnType != null) {
            return returnType;
        }

        if (List.class.equals(method.getReturnType())) {
            return ReturnType.List;
        }
        if (method.getReturnType().equals(returnEntity)) {
            return ReturnType.Entity;
        }

        throw new Exception("Unsupported return type: " + method.getReturnType());
    }
}
