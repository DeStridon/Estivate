package com.estivate.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.estivate.entity.CachedEntity;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;

public class FieldUtils {
	
	static Map<Class<?>, Set<Method>> classPostLoadMethods = new HashMap<>();
	
	static Map<Class<?>, Set<Field>> classFields = new HashMap<>();
	
	static Map<Integer, String> fieldNames = new HashMap<>();
	
	public static Set<Field> getEntityFields(Class<?> objectClass){
		
		//System.out.println("getEntityFields("+(objectClass != null ? objectClass.getCanonicalName() : "null")+") starting");
		
		if(objectClass == null || objectClass == Object.class || objectClass == CachedEntity.class){
			//System.out.println("getEntityFields("+objectClass.getSimpleName()+") returning new HashSet");
			return new HashSet<>();
		}
		
		Set<Field> fields = classFields.get(objectClass);
		if(fields == null) {
			//System.out.println("getEntityFields("+objectClass.getSimpleName()+") generating new fieldSet"); 
			fields = new HashSet<>();
			fields.addAll(getEntityFields(objectClass.getSuperclass()));
			
			//System.out.println("getEntityFields("+objectClass.getSimpleName()+") got fields from upper class : "+fields.toString());
			
			for(Field field : objectClass.getDeclaredFields()) {
				
				if(field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				if(field.getType() == org.slf4j.Logger.class) {
					continue;
				}
				
				field.setAccessible(true);
				
				fields.add(field);
				
			}
			
			//System.out.println("getEntityFields("+objectClass.getSimpleName()+") creating new map entry class "+objectClass.getSimpleName()+" : "+fields.toString());
			
			classFields.put(objectClass, fields);
		}
		
		//System.out.println("getEntityFields("+objectClass.getSimpleName()+") returning field set for class "+objectClass.getSimpleName()+" : "+fields.toString());
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
	
	public static Set<Method> getPostLoadMethods(Class<? extends Object> objectClass) {
		
		Set<Method> methods = classPostLoadMethods.get(objectClass);
		if(methods == null) {
			methods = getEntityMethods(objectClass).stream().filter(x -> x.isAnnotationPresent(PostLoad.class)).collect(Collectors.toSet());
			classPostLoadMethods.put(objectClass, methods);
		}
		
		return methods;
	
	}
	
	public static String getFieldName(Entity entity, Field field) {
		int hash = Objects.hash(entity, field);
		String fieldName = fieldNames.get(hash);
		if(fieldName == null) {
			fieldName = entity.getName() + "." + Query.nameMapper.mapAttribute(field.getName());
			fieldNames.put(hash, fieldName);
		}
		return fieldName;
		
	}

}
