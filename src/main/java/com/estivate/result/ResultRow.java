package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.estivate.Entity;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper.DateMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class ResultRow {

	final ResultTable resultTable;
	final String[] columnValues;
	

	private Map<String, Object> cache = new HashMap<>();

	
	public ResultRow(ResultTable resultTable, String[] columnValues) {
		this.resultTable = resultTable;
		this.columnValues = columnValues;
	}

	
	@SneakyThrows
	public <T> T get(Entity<T> entity) throws SecurityException, IllegalArgumentException {
		
		String key = resultTable.context.nameMapper.toEntityName(entity);
		
		T t = (T) cache.get(key);
		
		if(t == null) {
			EntityMapper<T> mapper = new EntityMapper<>(resultTable.context, resultTable.query, entity);
			t = mapper.map(columnValues);
			cache.put(key, t);
		}
		
		return t;
		
	}
	
	public <T> T get(Class<T> clazz) throws SecurityException, IllegalArgumentException {
		return get(new Entity<T>(clazz));
	}

	
	@SneakyThrows
	public String 			getAsString(String column) { Integer index = indexOf(column); return index == null ? null : columnValues[index]; }
	// Numbers
	public Short 			getAsShort(String column) { String value = getAsString(column); return value == null ? null : Short.valueOf(value); } 
	public Integer 			getAsInteger(String column) { String value = getAsString(column); return value == null ? null : Integer.valueOf(value); }
	public Long 			getAsLong(String column) { String value = getAsString(column); return value == null ? null : Long.valueOf(value); }
	public Float 			getAsFloat(String column) { String value = getAsString(column); return value == null ? null : Float.valueOf(value); }
	public Double 			getAsDouble(String column) { String value = getAsString(column); return value == null ? null : Double.valueOf(value); }
	public BigDecimal		getAsBigDecimal(String column) { String value = getAsString(column); return value == null ? null : new BigDecimal(value); }
	
	// Dates
	public LocalDateTime	getAsLocalDateTime(String column) { String value = getAsString(column); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDate		getAsLocalDate(String column) { LocalDateTime ldt = getAsLocalDateTime(column); return ldt == null ? null : ldt.toLocalDate(); }
	public Date 			getAsDate(String column) { LocalDateTime ldt = getAsLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant()); } 

	public Boolean 			getAsBoolean(String column) { String value = getAsString(column); return value == null ? null : Boolean.valueOf(value); }
	public Byte 			getAsByte(String column) { String value = getAsString(column); return value == null ? null : Byte.valueOf(value); }
	public Character 		getAsChar(String column) { String value = getAsString(column); return value == null ? null : value.charAt(0); }
	
	public <T extends Enum<T>> T getAsStringEnum(String column, Class<T> enumClass) { String value = getAsString(column); return value == null ? null : (T) Enum.valueOf((Class<T>)enumClass, getAsString(column)); }
	public <T extends Enum<T>> T getAsOrdinalEnum(String column, Class<T> enumClass) { String value = getAsString(column); return value == null ? null : (T) ((Class<T>)enumClass).getEnumConstants()[getAsInteger(column)]; }
	

	private Integer indexOf(String column){
		int index = resultTable.columnNames.indexOf(column);
		if(index == -1){
			log.error("Column not found: "+column + ", available columns: " + resultTable.columnNames);
			return null;
		}
		return index;
	}
	
	@SneakyThrows
	public <T> T get(Class<?> c, String attribute) { 
		try {
			Field field = c.getDeclaredField(attribute);
			Type type = field.getGenericType();

			if(type == String.class) {
				return (T) getAsString(c, attribute);
			}
			else if(type == boolean.class || type == Boolean.class) {
				return (T) getAsBoolean(c, attribute);
			}
			else if(type == byte.class || type == Byte.class) {
				return (T) getAsByte(c, attribute);
			}
			else if(type == short.class || type == Short.class) {
				return (T) getAsShort(c, attribute);
			}
			else if(type == int.class || type == Integer.class) {
				return (T) getAsInteger(c, attribute);
			}
			else if(type == long.class || type == Long.class) {
				return (T) getAsLong(c, attribute);
			}
			else if(type == float.class || type == Float.class) {
				return (T) getAsFloat(c, attribute);
			}
			else if(type == double.class || type == Double.class) {
				return (T) getAsDouble(c, attribute);
			}
			else if(type == char.class || type == Character.class) {
				return (T) getAsChar(c, attribute);
			}
			else if(type == Date.class) {
				return (T) getAsDate(c, attribute);
			}
			else if(type == LocalDateTime.class) {
				return (T) getAsLocalDateTime(c, attribute);
			}
			else if(type == LocalDate.class) {
				return (T) getAsLocalDate(c, attribute);
			}
			// @Convert (might be enum, this condition should be tested before classic enum)
			else if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
				javax.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(javax.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof javax.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				javax.persistence.AttributeConverter attributeConverter = (javax.persistence.AttributeConverter) converter;
				return (T) attributeConverter.convertToEntityAttribute(getAsString(c, attribute));
			}

			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof jakarta.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				return (T) attributeConverter.convertToEntityAttribute(getAsString(c, attribute));
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return (T) getAsStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) getAsOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return (T) getAsStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) getAsOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
			}
			else {
				log.error("This type is not mapped yet : "+type);
			}
		}
		catch(NoSuchFieldException e) {
			log.error("Didn't manage to get field", e);
		}
		return null;
	}

	public String 			getAsString		(Class<?> c, String attribute) 	{ return getAsString(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public String 			getAsString		(Entity<?> e, String attribute)	{ return getAsString(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Numbers
	public Short 			getAsShort		(Class<?> c, String attribute) 	{ return getAsShort(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Short 			getAsShort		(Entity<?> e, String attribute)	{ return getAsShort(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Integer 			getAsInteger	(Class<?> c, String attribute) 	{ return getAsInteger(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Integer 			getAsInteger	(Entity<?> e, String attribute)	{ return getAsInteger(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Long 			getAsLong		(Class<?> c, String attribute) 	{ return getAsLong(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Long 			getAsLong		(Entity<?> e, String attribute)	{ return getAsLong(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Float 			getAsFloat		(Class<?> c, String attribute) 	{ return getAsFloat(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Float 			getAsFloat		(Entity<?> e, String attribute)	{ return getAsFloat(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Double 			getAsDouble		(Class<?> c, String attribute) 	{ return getAsDouble(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Double 			getAsDouble		(Entity<?> e, String attribute)	{ return getAsDouble(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public BigDecimal		getAsBigDecimal	(Class<?> c, String attribute) 	{ return getAsBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public BigDecimal		getAsBigDecimal	(Entity<?> e, String attribute)	{ return getAsBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Date
	public Date 			getAsDate			(Class<?> c, String attribute)	{ return getAsDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Date 			getAsDate			(Entity<?> e, String attribute)	{ return getAsDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDateTime	getAsLocalDateTime	(Class<?> c, String attribute)	{ return getAsLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDateTime 	getAsLocalDateTime	(Entity<?> e, String attribute) { return getAsLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDate		getAsLocalDate		(Class<?> c, String attribute)	{ return getAsLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDate	 	getAsLocalDate		(Entity<?> e, String attribute) { return getAsLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Other
	public Boolean 			getAsBoolean	(Class<?> c, String attribute) 	{ return getAsBoolean(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Boolean 			getAsBoolean	(Entity<?> e, String attribute)	{ return getAsBoolean(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Byte				getAsByte		(Class<?> c, String attribute) 	{ return getAsByte(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Byte				getAsByte		(Entity<?> e, String attribute) { return getAsByte(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Character		getAsChar		(Class<?> c, String attribute)  { return getAsChar(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Character		getAsChar		(Entity<?> e, String attribute)	{ return getAsChar(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	
	
	

	// @Enumerated
	public Enum getAsEnum(Class<?> entity, String attribute) {
		
		try {
			Field field = entity.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, getAsString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[getAsInteger(entity, attribute)];
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, getAsString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[getAsInteger(entity, attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
		
	}

	public Long getCount() { return getAsLong("count(*)"); }
	public Long getCount(Class<? extends Object> c, String attribute) { return getAsLong("count("+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) { return getAsLong("count(distinct "+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }

	
}
