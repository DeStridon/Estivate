package com.estivate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstivateUtil {
	
	static Object compileObject(Class entity, String attribute, Object value) {
		try {
			Field field = entity.getDeclaredField(attribute);
			
			Type fieldType = field.getType();
			
			// Convert annotation takes priority
			if(field.getDeclaredAnnotation(Convert.class) != null) {
				Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(converter instanceof AttributeConverter) {
					AttributeConverter attributeConverter = (AttributeConverter) converter;
					Object convertedValue = attributeConverter.convertToDatabaseColumn(value);
					return convertedValue;
				}
			}
			// Then check if enum and with annotation
			if(fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {

				if(field.getDeclaredAnnotation(Enumerated.class) != null && field.getDeclaredAnnotation(Enumerated.class).value() != null && field.getDeclaredAnnotation(Enumerated.class).value() == EnumType.STRING) {
					return value.toString();
				}
				else {
					return ((Enum) value).ordinal();
				}
			}

			return value;

		}
		catch(Exception e) {
			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute);
			e.printStackTrace();
		}
		
		log.warn("Could not determine type of field "+entity.getSimpleName()+"."+attribute);
		
		return compileGenericType(value);

	}
	
	private static String compileGenericType(Object value) {
		if(value instanceof String) {
			
			return "'"+((String) value)
					.replace("\\", "\\\\")
					//.replace("'", "\'\'")
					.replace("\"", "\"\"")
					.replace(":", "\\:")
					+"'";
		}
		else if(value instanceof java.util.Date) {
			return "\"" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) value) + "\""; 
		}
		else if(value instanceof Boolean) {
			return (boolean) value ? "1":"0";
		}
		else {
			return value.toString();
		}
	}
	
	public static String compileAttribute(Class entity, String attribute, Object value) {
		return compileGenericType(compileObject(entity, attribute, value));
	}
	
	public static Field getFieldWithAnnotation(Class entityClass, Class<? extends Annotation> annotationClass) {
		
		for(Field field : entityClass.getFields()) {
			if(field.isAnnotationPresent(annotationClass)) {
				return field;
			}
		}
		
		return null;
		
	}

}
