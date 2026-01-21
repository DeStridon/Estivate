package com.estivate.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;


import com.estivate.util.FieldUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

public class ManagerInterceptor<T> {
	
	final Class<? extends EntityManager<T>> abstractClass;
	final Context context;
    
	public ManagerInterceptor(Class<? extends EntityManager<T>> abstractClass, Context context) {
		this.abstractClass = abstractClass;
		this.context = context;
	}

	 

    public static abstract class EntityManager<T> {}
	
	 
    @RuntimeType
    public Object intercept(@This Object self, 
                                 @Origin Method method, 
                                 @AllArguments Object[] args, 
                                 @SuperMethod(nullIfImpossible = true) Method superMethod,
                                 @Empty Object defaultValue) throws Throwable {

        

        
        String methodName = method.getName();

        Class<? extends EntityManager<T>> managerClass = (Class<? extends EntityManager<T>>) method.getDeclaringClass();

        // Extract the generic type T from the EntityManager<T>
        Type genericSuperclass = managerClass.getGenericSuperclass();
        Type entityType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        Class<T> entityClass = (Class<T>) entityType;
        
        if(methodName.equals("findAll")){
            SelectQuery<?> query = Estivate.selectQuery(entityClass);
            return context.fetchList(query);
        }
        if(methodName.startsWith("findAllBy")){
            SelectQuery<?> query = getQuery(entityClass, methodName.substring(9), args);
            return context.fetchList(query);
        }
        else if(methodName.startsWith("findOneBy")){
            SelectQuery<?> query = getQuery(entityClass, methodName.substring(9), args);
            return context.fetchSingle(query);
        }
        else if(methodName.startsWith("countBy")){
            SelectQuery<?> query = getQuery(entityClass, methodName.substring(7), args);
            return context.fetchCountAll(query);
        }
        else if(methodName.startsWith("existsBy")){
            SelectQuery<?> query = getQuery(entityClass, methodName.substring(8), args);
            return context.fetchCountAll(query) > 0;
        }
        else if(methodName.startsWith("findBy")){
            SelectQuery<?> query = getQuery(entityClass, methodName.substring(6), args);
            if(method.getReturnType().equals(List.class)){
                return context.fetchList(query);
            }
            else if(method.getReturnType().equals(entityClass)){
                return context.fetchSingle(query);
            }
            else{
                throw new Exception("Unsupported return type: " + method.getReturnType());
            }
        }
        else{
            throw new Exception("Unsupported method name: " + methodName);
        }
        

    }

    private SelectQuery<?> getQuery(Class<T> entityClass, String methodName, Object[] args) throws Exception{

        int argCounter = 0;

        List<String> criterionList = getMethods().stream()
            .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1))
            .sorted((a, b) -> Integer.compare(b.length(), a.length())) // Sort by length descending
            .collect(Collectors.toList());

        List<String> fieldList = FieldUtils.getEntityFields(entityClass).stream()
    		.map(Member::getName)
    		.map(x -> x.substring(0, 1).toUpperCase() + x.substring(1))
    		.sorted((a, b) -> Integer.compare(b.length(), a.length())) 
            .collect(Collectors.toList());

        SelectQuery<?> query = Estivate.selectQuery(entityClass);
        for(int i = 0; i < methodName.length();){

            // match field
            final int fieldCursor = i;
            String field = fieldList.stream()
            		.filter(m -> methodName.startsWith(m, fieldCursor))
                    .map(x -> x.substring(0, 1).toLowerCase() + x.substring(1))
                    .findFirst()
                    .orElse(null);

            if(field == null){
                throw new Exception("No field match for " + methodName.substring(i));
            }
            
            i += field.length();

            // match criterion
            final int criterionCursor = i;
            String criterion = criterionList.stream()
                    .filter(m -> methodName.startsWith(m, criterionCursor))
                    .map(x -> x.substring(0, 1).toLowerCase() + x.substring(1))
                    .findFirst()
                    .orElse("");

            
            
            i += criterion.length();
            
            // match criterion method
            Method criterionMethod = Arrays.asList(SelectQuery.class.getMethods()).stream()
                .filter(x -> x.getName().equals(criterion.isEmpty() ? "eq" : criterion))
                .filter(x -> x.getParameterTypes()[0].equals(String.class))
                .findFirst().orElseThrow(() -> new Exception("No criterion method found, available methods: " + criterionList.stream().collect(Collectors.joining(", "))));
        

            // move forward with arguments
            // if between, move forward with 2 arguments
            if(criterion.startsWith("between")){
                criterionMethod.invoke(query, field, args[argCounter], args[argCounter + 1]);
                argCounter += 2;
            }
            // if isNull or isNotNull, move forward with 0 arguments
            else if(criterion.startsWith("isNull") || criterion.startsWith("isNotNull")){
                criterionMethod.invoke(query, field);
            }
            // if other, move forward with 1 argument
            else{
                criterionMethod.invoke(query, field, args[argCounter]);
                argCounter++;
            }
        	
            // match And
            if(i < methodName.length() && !methodName.startsWith("And", i)){
                throw new Exception("Method name must start with And");
            }
            i += 3;
                
        }
        return query;
    }


    public static List<String> getMethods(){
        List<String> result = new ArrayList<>();

        result.addAll(new StringComposer().compose("in", "notIn").compose("", "IfNotEmpty", "IfNotEmptyNullable", "OrNull", "IfNotEmptyOrNull", "OrFalseIfEmpty").results);
        result.addAll(new StringComposer().compose("eq", "notEq", "gt", "gte", "lt", "lte", "between").compose("", "IfNotNull", "OrNull", "Nullable").results);
        result.addAll(new StringComposer().compose("like", "notLike").compose("", "StartsWith", "EndsWith", "Contains").compose("", "IfNotNull", "In", "InIfNotEmpty").results);
        result.addAll(Arrays.asList("isNull", "isNotNull"));
        return result;
    }

    public static class StringComposer{
        public List<String> results = new ArrayList<>(Arrays.asList(""));
        public StringComposer compose (String... terms){
            List<String> newResult = new ArrayList<>();
            for(String oldLine : results){
                for(String term : terms){
                    newResult.add(oldLine + term);
                }
            }
            results = newResult;
            return this;
        }
    }

    

}
