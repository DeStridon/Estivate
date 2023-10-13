package com.estivate.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Transient;

public class FieldUtils {
	
	public static Set<Field> getEntityFields(Class<? extends Object> objectClass){
		
		if(objectClass == null) {
			return new HashSet<>();
		}
		Set<Field> fields = getEntityFields(objectClass.getSuperclass());
		for(Field field : objectClass.getDeclaredFields()) {
			
			if(field.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if(field.getType() == org.slf4j.Logger.class) {
				continue;
			}
			
			fields.add(field);
			
		}
		
		return fields;
		
	}
	
	
	public static Field findField(Class<? extends Object> objectClass, String attribute) {
		
		Field result = getEntityFields(objectClass).stream().filter(x -> x.getName().equals(attribute)).findFirst().orElse(null);
		return result;
				
	}
	
	public static Set<Method> getEntityMethods(Class<? extends Object> objectClass){
		
		if(objectClass == null) {
			return new HashSet<>();
		}
		
		Set<Method> methods = new HashSet<>();
		for(Method method : objectClass.getDeclaredMethods()) {
			methods.add(method);
		}
		
		methods.addAll(getEntityMethods(objectClass.getSuperclass()));

		return methods;
	}
	
	public static Set<Method> findMethodWithAnnotation(Class<? extends Object> objectClass, Class<? extends Annotation> annotation) {
		return getEntityMethods(objectClass).stream().filter(x -> x.isAnnotationPresent(annotation)).collect(Collectors.toSet());
	}

}
