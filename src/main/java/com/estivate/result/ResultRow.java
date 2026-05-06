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
import com.estivate.util.FieldUtils;

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
	public <T> T as(Entity<T> entity) throws SecurityException, IllegalArgumentException {
		
		String key = resultTable.context.nameMapper.toEntityName(entity);
		
		T t = (T) cache.get(key);
		
		if(t == null) {
			EntityMapper<T> mapper = new EntityMapper<>(resultTable.context, resultTable.query, entity);
			t = mapper.map(columnValues);
			cache.put(key, t);
		}
		
		return t;
		
	}
	
	public <T> T as(Class<T> clazz) throws SecurityException, IllegalArgumentException {
		return as(new Entity<T>(clazz));
	}

	
	@SneakyThrows
	public String 			asString(String column) { Integer index = indexOf(column); return index == null ? null : columnValues[index]; }
	// Numbers
	public Short 			asShort(String column) { String value = asString(column); return value == null ? null : Short.valueOf(value); } 
	public Integer 			asInteger(String column) { String value = asString(column); return value == null ? null : Integer.valueOf(value); }
	public Long 			asLong(String column) { String value = asString(column); return value == null ? null : Long.valueOf(value); }
	public Float 			asFloat(String column) { String value = asString(column); return value == null ? null : Float.valueOf(value); }
	public Double 			asDouble(String column) { String value = asString(column); return value == null ? null : Double.valueOf(value); }
	public BigDecimal		asBigDecimal(String column) { String value = asString(column); return value == null ? null : new BigDecimal(value); }
	
	// Dates
	public LocalDateTime	asLocalDateTime(String column) { String value = asString(column); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDate		asLocalDate(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : ldt.toLocalDate(); }
	public Date 			asDate(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant()); } 

	public Boolean 			asBoolean(String column) { String value = asString(column); return value == null ? null : Boolean.valueOf(value); }
	public Byte 			asByte(String column) { String value = asString(column); return value == null ? null : Byte.valueOf(value); }
	public Character 		asChar(String column) { String value = asString(column); return value == null ? null : value.charAt(0); }
	
	public <T extends Enum<T>> T asStringEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : (T) Enum.valueOf((Class<T>)enumClass, asString(column)); }
	public <T extends Enum<T>> T asOrdinalEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : (T) ((Class<T>)enumClass).getEnumConstants()[asInteger(column)]; }
	

	private Integer indexOf(String column){
		int index = resultTable.columnNames.indexOf(column);
		if(index == -1){
			log.error("Column not found: "+column + ", available columns: " + resultTable.columnNames);
			return null;
		}
		return index;
	}
	
	@SneakyThrows
	public <T> T as(Class<?> c, String attribute) { 
//		try {
			Field field = FieldUtils.findField(c, attribute);
			Type type = field.getGenericType();

			if(type == String.class) {
				return (T) asString(c, attribute);
			}
			else if(type == boolean.class || type == Boolean.class) {
				return (T) asBoolean(c, attribute);
			}
			else if(type == byte.class || type == Byte.class) {
				return (T) asByte(c, attribute);
			}
			else if(type == short.class || type == Short.class) {
				return (T) asShort(c, attribute);
			}
			else if(type == int.class || type == Integer.class) {
				return (T) asInteger(c, attribute);
			}
			else if(type == long.class || type == Long.class) {
				return (T) asLong(c, attribute);
			}
			else if(type == float.class || type == Float.class) {
				return (T) asFloat(c, attribute);
			}
			else if(type == double.class || type == Double.class) {
				return (T) asDouble(c, attribute);
			}
			else if(type == char.class || type == Character.class) {
				return (T) asChar(c, attribute);
			}
			else if(type == Date.class) {
				return (T) asDate(c, attribute);
			}
			else if(type == LocalDateTime.class) {
				return (T) asLocalDateTime(c, attribute);
			}
			else if(type == LocalDate.class) {
				return (T) asLocalDate(c, attribute);
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
				return (T) attributeConverter.convertToEntityAttribute(asString(c, attribute));
			}

			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof jakarta.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				return (T) attributeConverter.convertToEntityAttribute(asString(c, attribute));
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return (T) asStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) asOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return (T) asStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) asOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
			}
			else {
				log.error("This type is not mapped yet : "+type);
			}
//		}
//		catch(NoSuchFieldException e) {
//			log.error("Didn't manage to get field", e);
//		}
		return null;
	}

	public String 			asString		(Class<?> c, String attribute) 	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public String 			asString		(Entity<?> e, String attribute)	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Numbers
	public Short 			asShort		(Class<?> c, String attribute) 	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Short 			asShort		(Entity<?> e, String attribute)	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Integer 			asInteger	(Class<?> c, String attribute) 	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Integer 			asInteger	(Entity<?> e, String attribute)	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Long 			asLong		(Class<?> c, String attribute) 	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Long 			asLong		(Entity<?> e, String attribute)	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Float 			asFloat		(Class<?> c, String attribute) 	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Float 			asFloat		(Entity<?> e, String attribute)	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Double 			asDouble		(Class<?> c, String attribute) 	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Double 			asDouble		(Entity<?> e, String attribute)	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public BigDecimal		asBigDecimal	(Class<?> c, String attribute) 	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public BigDecimal		asBigDecimal	(Entity<?> e, String attribute)	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Date
	public Date 			asDate			(Class<?> c, String attribute)	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Date 			asDate			(Entity<?> e, String attribute)	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDateTime	asLocalDateTime	(Class<?> c, String attribute)	{ return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDateTime 	asLocalDateTime	(Entity<?> e, String attribute) { return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDate		asLocalDate		(Class<?> c, String attribute)	{ return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDate	 	asLocalDate		(Entity<?> e, String attribute) { return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Other
	public Boolean 			asBoolean		(Class<?> c, String attribute) 	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Boolean 			asBoolean		(Entity<?> e, String attribute)	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Byte				asByte			(Class<?> c, String attribute) 	{ return asByte(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Byte				asByte			(Entity<?> e, String attribute) { return asByte(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Character		asChar			(Class<?> c, String attribute)  { return asChar(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Character		asChar			(Entity<?> e, String attribute)	{ return asChar(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	
	
	

	// @Enumerated
	public Enum asEnum(Class<?> entity, String attribute) {
		
		try {
			Field field = entity.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, asString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[asInteger(entity, attribute)];
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, asString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[asInteger(entity, attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
		
	}

	public Long getCount() { return asLong("count(*)"); }
	public Long getCount(Class<? extends Object> c, String attribute) { return asLong("count("+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) { return asLong("count(distinct "+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }

	
}
