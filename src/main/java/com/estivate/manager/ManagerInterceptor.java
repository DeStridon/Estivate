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

        
        ManagerQueryWrapper wrapper = new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        SelectQuery<?> query = wrapper.getQuery(args);

        
        if(method.getName().equals("findAll")){
            return context.fetchList(query);
        }
        if(method.getName().startsWith("findAllBy")){
            return context.fetchList(query);
        }
        else if(method.getName().startsWith("findOneBy")){
            return context.fetchSingle(query);
        }
        else if(method.getName().startsWith("countBy")){
            return context.fetchCountAll(query);
        }
        else if(method.getName().startsWith("existsBy")){
            return context.fetchCountAll(query) > 0;
        }
        else if(method.getName().startsWith("findBy")){
            if(method.getReturnType().equals(List.class)){
                return context.fetchList(query);
            }
            else if(method.getReturnType().equals(wrapper.entityClass)){
                return context.fetchSingle(query);
            }
            else{
                throw new Exception("Unsupported return type: " + method.getReturnType());
            }
        }
        else{
            throw new Exception("Unsupported method name: " + method.getName());
        }
        

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
