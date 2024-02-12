package com.estivate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.query.Query.Entity;
import com.estivate.util.Chronometer;
import com.estivate.util.FieldUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper<U> {
	
	final Class<?> targetClass;
	final Constructor<U> constructor;

	final Set<Field> fields;
	final Set<Method> postLoadMethods;
	
	final Chronometer chronometer;
	
	@SneakyThrows
	public Mapper(Class<U> targetClass) {
		chronometer = new Chronometer("Mapper "+targetClass.getSimpleName()).timeThreshold(null);
		
		this.targetClass = targetClass;

		constructor = targetClass.getConstructor();
		
		fields = FieldUtils.getEntityFields(targetClass);
		postLoadMethods = FieldUtils.getPostLoadMethods(targetClass);
		
		chronometer.step("mapper constructor");
	}
	
	@SneakyThrows
	public U map(Map<String, String> arguments)  {
		U obj = constructor.newInstance();
		Entity entity = new Entity(targetClass);
		chronometer.step("constructor & entity");
		
		for(Field field : fields) {
			String value = arguments.get(FieldUtils.getFieldName(entity, field));
			chronometer.step("get field value"+field.getName());
			
			setGeneratedField(entity, value, field, obj);
			chronometer.step("set generated field "+field.getName());
		}
		
		for(Method method : postLoadMethods) {
			method.invoke(obj);
			chronometer.step("invoke method "+method.getName());
		}
		
		return obj;
		
	}
	
	
	public void setGeneratedField(Entity entity, String value, Field field, U obj) throws IllegalAccessException, AttributeInUseException, NoSuchMethodException, ParseException, InvocationTargetException, InstantiationException {
		Type type = field.getGenericType();
		
//		
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

	public String getStats() {
		return chronometer.getLog();
	}


	public void attachMetadata(ResultSetMetaData metadata) {
		
	}

	//
	public U map(Object[] result) {
		return null;
	}

}
