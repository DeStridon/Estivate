package com.estivate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.util.Chronometer;
import com.estivate.util.FieldUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Result {

	@Getter Query query;
	@Getter Map<String, String> columns;
	
	public Result(Query query, Map<String, String> columns) {
		this.query = query;
		this.columns = columns;
	}
	
	private Map<String, Object> cache = new HashMap<>();
	
	public <U> U mapAs(Class<U> clazz) throws SecurityException, IllegalArgumentException {
		
		if(!cache.containsKey(clazz.getSimpleName())) {
			cache.put(clazz.getSimpleName(), generateObject(clazz, columns));
		}
		
		return (U) cache.get(clazz.getSimpleName());
		
	}
	
	protected <U> U mapAs(Class<U> clazz, Chronometer chronometer) throws SecurityException, IllegalArgumentException {
		
		if(!cache.containsKey(clazz.getSimpleName())) {
			cache.put(clazz.getSimpleName(), generateObject(clazz, columns, chronometer));
		}
		
		return (U) cache.get(clazz.getSimpleName());
		
	}
	
	public String mapAsString(Class c, String attribute) {
		return columns.get(Query.nameMapper.mapEntity(c, attribute));
	}
	
	public Integer mapAsInteger(Class c, String attribute) {
		return Integer.valueOf(columns.get(Query.nameMapper.mapEntity(c, attribute)));		
	}
	
	public Long mapAsLong(Class c, String attribute) {
		return Long.valueOf(columns.get(Query.nameMapper.mapEntity(c, attribute)));		
	}
	
	
	public Long mapCount() {
		if(columns.containsKey("COUNT(*)")) {
			return Long.valueOf(columns.get("COUNT(*)"));			
		}
		return null;
	}
	
	public Long mapCount(Class<? extends Object> c, String attribute) {
		if(columns.containsKey("COUNT(distinct "+Query.nameMapper.mapEntity(c, attribute)+")")) {
			return Long.valueOf(columns.get("COUNT(distinct "+Query.nameMapper.mapEntity(c, attribute)+")"));
		}
		return null;
	}
	
	
	@SneakyThrows
	public static <U> U generateObject(Class<U> clazz, Map<String, String> arguments) {

		Constructor<U> constructor = clazz.getConstructor();
		U obj = constructor.newInstance();
		
		Class<?> currentClazz = clazz;
		Entity entity = new Entity(clazz);
		
		
		while(currentClazz != Object.class) {

			Set<Field> fields = FieldUtils.getEntityFields(currentClazz);
			for(Field field : fields) {
				setGeneratedField(entity, arguments, field, obj);
			}
			currentClazz = currentClazz.getSuperclass();
		}
		
		Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
		for(Method method : methods) {
			method.invoke(obj);
		}
		
		return obj;
	
	}
	
	@SneakyThrows
	protected static <U> U generateObject(Class<U> clazz, Map<String, String> arguments, Chronometer chronometer) {

		Constructor<U> constructor = clazz.getConstructor();
		U obj = constructor.newInstance();
		
		Class<?> currentClazz = clazz;
		Entity entity = new Entity(clazz);
		
		
		while(currentClazz != Object.class) {

			Set<Field> fields = FieldUtils.getEntityFields(currentClazz);
			for(Field field : fields) {
				setGeneratedField(entity, arguments, field, obj);
			}
			currentClazz = currentClazz.getSuperclass();
		}
		
		Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
		for(Method method : methods) {
			method.invoke(obj);
		}
		
		return obj;
	
	}
	

	public static <U> void setGeneratedField(Entity entity, Map<String, String> arguments, Field field, U obj) throws IllegalAccessException, AttributeInUseException, NoSuchMethodException, ParseException, InvocationTargetException, InstantiationException {
		Type type = field.getGenericType();
		
		String value = arguments.get(FieldUtils.getFieldName(entity, field));
		
		if(value == null) {
			return;
		}

		if(type == String.class) {
			field.set(obj, value);
		}
		else if(type == long.class) {
			field.setLong(obj, Long.parseLong(value));
		}
		else if(type == Long.class) {
			field.set(obj, Long.parseLong(value));
		}
		else if(type == boolean.class) {
			field.setBoolean(obj, Boolean.parseBoolean(value));
		}
		else if(type == Boolean.class) {
			field.set(obj, Boolean.parseBoolean(value));
		}
		else if(type == Byte.class) {
			field.set(obj, Byte.parseByte(value));
		}
		else if(type == double.class) {
			field.setDouble(obj, Double.parseDouble(value));
		}
		else if(type == Double.class) {
			field.set(obj, Double.parseDouble(value));
		}
		else if(type == Character.class && value.length() > 0) {
			field.set(obj, value.charAt(0));
		}
		else if(type == Float.class) {
			field.set(obj, Float.parseFloat(value));
		}
		else if(type == int.class) {
			field.setInt(obj, Integer.parseInt(value));
		}
		else if(type == Integer.class) {
			field.set(obj, Integer.parseInt(value));
		}
		else if(type == short.class) {
			field.setShort(obj, Short.parseShort(value));
		}
		else if(type == Short.class) {
			field.set(obj, Short.parseShort(value));
		}
		else if(type == Date.class) {
			// TODO : check date format is the right one
			field.set(obj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value));
		}
		// @Convert (might be enum, this condition should be tested before classic enum)
		else if(field.getDeclaredAnnotation(Convert.class) != null) {
			Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
			Object converter = convertAnnotation.converter().getConstructor().newInstance();
			if(!(converter instanceof AttributeConverter)) {
				log.error("Cannot convert with converter "+converter.getClass());
				return;
			}
			AttributeConverter attributeConverter = (AttributeConverter) converter;
			Object attributeValue = attributeConverter.convertToEntityAttribute(value);
			field.set(obj, attributeValue);
		}
		// @Enumerated
		else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {

			Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
			if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
				field.set(obj, Enum.valueOf((Class)type, value));
			}
			else {
				int ordinal = Integer.parseInt(value);
				field.set(obj, field.getType().getEnumConstants()[ordinal]);
			}
		}
		else {
			log.error("This type is not mapped yet : "+type);
			throw new AttributeInUseException("This type is not mapped yet : "+type);
		}
	}
	
}
