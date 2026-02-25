package com.estivate.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Projection;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.util.EstivateException;
import com.estivate.util.FieldUtils;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityMapper<U> {

	// Entity side
	final Entity<U> entity;

	final Constructor<U> entityConstructor;
	final Set<Method> entityPostLoadMethods;

	List<ColumnMapping> columnMappings = new ArrayList<>();
	
	
	// Result side

	
	final Context context;

	final SelectQuery<?> query;


	@SneakyThrows
	public EntityMapper(Context context, SelectQuery<?> query, Entity<U> entity, boolean tracePerformances) {
	
		this.entity = entity;

		// Get constructor
		entityConstructor = entity.entity.getConstructor();

		// Get Fields
		columnMappings = FieldUtils.getColumnMappings(query, entity);

		// Get PostLoadMethods
		this.entityPostLoadMethods = FieldUtils.getPostLoadMethods(entity.entity);

		this.context = context;
		this.query = query;

	}

	public EntityMapper(Context context, SelectQuery<?> query, Entity<U> entity) {
		this(context, query, entity, false);
	}

	@SneakyThrows
	public U map(String[] row) {
		U obj = entityConstructor.newInstance();
		
		for (int i = 0; i < row.length; i++) {
			if (i >= columnMappings.size() || columnMappings.get(i) == null) {
				continue;
			}
			Field field = columnMappings.get(i).getField();
			if (field != null) {
				setGeneratedField(entity, field, obj, row[i]);
			}
		}
		for (Method method : entityPostLoadMethods) {
			method.invoke(obj);
		}
		return obj;
	}

	@SneakyThrows
	public U map(Map<String, String> arguments) {

		U obj = entityConstructor.newInstance();

		Class<?> currentClass = entity.entity;
		while (currentClass != Object.class) {

			Set<Field> fields = FieldUtils.getEntityFields(currentClass);
			for (Field field : fields) {
				String columnName = fieldToColumnName(entity, field);
				String value = arguments.get(columnName);
				setGeneratedField(entity, field, obj, value);
			}

			currentClass = currentClass.getSuperclass();

		}

		Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
		for (Method method : methods) {
			method.invoke(obj);
		}

		return obj;

	}

	// can this method be used for several entities ?
	public String fieldToColumnName(Entity<?> entity, Field field) {
		
		Projection.Attribute attributeAnnotation = field.getDeclaredAnnotation(Projection.Attribute.class);
		if (attributeAnnotation != null) {
			return getColumnNameFromAnnotation(attributeAnnotation.entity(), attributeAnnotation.attribute(), attributeAnnotation.alias());
		}

		Projection.Count countAnnotation = field.getDeclaredAnnotation(Projection.Count.class);
		if (countAnnotation != null) {
			return getColumnNameFromAnnotation(countAnnotation.entity(), countAnnotation.attribute(), countAnnotation.alias());
		}

		Projection.Sum sumAnnotation = field.getDeclaredAnnotation(Projection.Sum.class);
		if (sumAnnotation != null) {
			return getColumnNameFromAnnotation(sumAnnotation.entity(), sumAnnotation.attribute(), sumAnnotation.alias());
		}

		Projection.Min minAnnotation = field.getDeclaredAnnotation(Projection.Min.class);
		if (minAnnotation != null) {
			return getColumnNameFromAnnotation(minAnnotation.entity(), minAnnotation.attribute(), minAnnotation.alias());
		}

		Projection.Max maxAnnotation = field.getDeclaredAnnotation(Projection.Max.class);
		if (maxAnnotation != null) {
			return getColumnNameFromAnnotation(maxAnnotation.entity(), maxAnnotation.attribute(), maxAnnotation.alias());
		}

		Projection.Avg avgAnnotation = field.getDeclaredAnnotation(Projection.Avg.class);
		if (avgAnnotation != null) {
			return getColumnNameFromAnnotation(avgAnnotation.entity(), avgAnnotation.attribute(), avgAnnotation.alias());
		}

		Projection.Function functionAnnotation = field.getDeclaredAnnotation(Projection.Function.class);
		if (functionAnnotation != null) {
			return getColumnNameFromAnnotation(functionAnnotation.entity(), functionAnnotation.attribute(), functionAnnotation.alias());
		}
		
		return context.nameMapper.toEntityNameAttribute(entity, field.getName());
		
	}



	/**
	 * Common logic to get column name from annotations with entity, attribute, and
	 * alias
	 */
	private String getColumnNameFromAnnotation(Class<?> entityClass, String attribute, String alias) {
		// If alias is not null and not empty, use it directly
		if (alias != null && !alias.isEmpty()) {
			return context.nameMapper.mapEntityField(alias);
		}

		// If alias is null or empty, find the corresponding field in the entity and use
		// its mapped name
		Field mappingField = FieldUtils.getEntityFields(entityClass).stream().filter(x -> x.getName().equals(attribute))
				.findFirst().orElse(null);

		if (mappingField == null) {
			log.error("Field " + attribute + " not found in entity " + entityClass);
			return null;
		}

		return fieldToColumnName(new Entity<>(entityClass), mappingField);
	}

	public void setGeneratedField(Entity<?> entity, Field field, U obj, String value) throws EstivateException {
        try {
            
            if(value == null) {
                return;
            }

			Object convertedValue = convertValue(field, value);
			if(convertedValue != null) {
				field.set(obj, convertedValue);
				return;
			}
            
        }
        catch(Throwable e) {
            log.error("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
            throw new EstivateException("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
        }
    }

	@SneakyThrows
	private static Object convertValue(Field field, String value) {
		
		if(field == null || value == null) {
			return null;
		}
		Type type = field.getGenericType();

		// @Projection.Attribute
		if(field.getDeclaredAnnotation(Projection.Attribute.class) != null) {
			Projection.Attribute annotation = field.getDeclaredAnnotation(Projection.Attribute.class);
			Field mappingField = FieldUtils.findField(annotation.entity(), annotation.attribute());
			return convertValue(mappingField, value);
		}

		// @Convert
		if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
			javax.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(javax.persistence.Convert.class);
			Object converter = convertAnnotation.converter().getConstructor().newInstance();
			if(converter instanceof javax.persistence.AttributeConverter) {
				javax.persistence.AttributeConverter attributeConverter = (javax.persistence.AttributeConverter) converter;
				return attributeConverter.convertToEntityAttribute(value);
			}
		}
		if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
			jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
			Object converter = convertAnnotation.converter().getConstructor().newInstance();
			if(converter instanceof jakarta.persistence.AttributeConverter) {
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				return attributeConverter.convertToEntityAttribute(value);
			}
		}

		// @Enumerated
		if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {

			javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
			if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
				return Enum.valueOf((Class)type, value);
			}
			else {
				int ordinal = Integer.parseInt(value);
				return field.getType().getEnumConstants()[ordinal];
			}
		}
		if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
			jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
			if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
				return Enum.valueOf((Class)type, value);
			}
			else {
				int ordinal = Integer.parseInt(value);
				return field.getType().getEnumConstants()[ordinal];
			}
		}

		// Classic types
		if(type == String.class) { return value; }
		if(type == short.class 	|| type == Short.class) 	{ return Short.parseShort(value); }
		if(type == int.class 	|| type == Integer.class) 	{ return Integer.parseInt(value); }
		if(type == long.class 	|| type == Long.class) 		{ return Long.parseLong(value); }
		if(type == float.class 	|| type == Float.class) 	{ return Float.parseFloat(value); }
		if(type == double.class || type == Double.class) 	{ return Double.parseDouble(value); }
		if(type == BigDecimal.class) { return new BigDecimal(value); }
		if(type == boolean.class || type == Boolean.class) { return StringUtils.equals("true", value.toLowerCase()) || StringUtils.equals("1", value); }
		if(type == Byte.class) { return Byte.parseByte(value); }
		if(type == Character.class && value.length() > 0) { return value.charAt(0); }
		
		// Date
		if(type == Date.class) {
			LocalDateTime dateTime = DateMapper.mapDate(value);
            return Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant());
        }
        else if(type == LocalDateTime.class) { return DateMapper.mapDate(value); }
        else if(type == LocalDate.class) { return DateMapper.mapDate(value).toLocalDate(); }

		log.error("This type is not mapped yet : "+type);
		return null;

	}
	
	@Data
	public static class ColumnMapping {

		Attribute attribute;

		Field field;


		public ColumnMapping(Attribute attribute, Field field){
			this.attribute = attribute;
			this.field = field;
		}

	}
		
}
