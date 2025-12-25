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
	public <T> T mapTo(Entity<T> entity) throws SecurityException, IllegalArgumentException {
		
		String key = resultTable.context.nameMapper.toEntityName(entity);
		
		T t = (T) cache.get(key);
		
		if(t == null) {
			EntityMapper<T> mapper = new EntityMapper<>(resultTable.context, resultTable.query, entity);
			t = mapper.map(columnValues);
			cache.put(key, t);
		}
		
		return t;
		
	}
	
	public <T> T mapTo(Class<T> clazz) throws SecurityException, IllegalArgumentException {
		return mapTo(new Entity<T>(clazz));
	}

	
	@SneakyThrows
	public String 			columnAsString(String column) { Integer index = indexOf(column); return index == null ? null : columnValues[index]; }
	// Numbers
	public Short 			columnAsShort(String column) { String value = columnAsString(column); return value == null ? null : Short.valueOf(value); } 
	public Integer 			columnAsInteger(String column) { String value = columnAsString(column); return value == null ? null : Integer.valueOf(value); }
	public Long 			columnAsLong(String column) { String value = columnAsString(column); return value == null ? null : Long.valueOf(value); }
	public Float 			columnAsFloat(String column) { String value = columnAsString(column); return value == null ? null : Float.valueOf(value); }
	public Double 			columnAsDouble(String column) { String value = columnAsString(column); return value == null ? null : Double.valueOf(value); }
	public BigDecimal		columnAsBigDecimal(String column) { String value = columnAsString(column); return value == null ? null : new BigDecimal(value); }
	
	// Dates
	public LocalDateTime	columnAsLocalDateTime(String column) { String value = columnAsString(column); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDate		columnAsLocalDate(String column) { LocalDateTime ldt = columnAsLocalDateTime(column); return ldt == null ? null : ldt.toLocalDate(); }
	public Date 			columnAsDate(String column) { LocalDateTime ldt = columnAsLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant()); } 

	public Boolean 			columnAsBoolean(String column) { String value = columnAsString(column); return value == null ? null : Boolean.valueOf(value); }
	public Byte 			columnAsByte(String column) { String value = columnAsString(column); return value == null ? null : Byte.valueOf(value); }
	public Character 		columnAsChar(String column) { String value = columnAsString(column); return value == null ? null : value.charAt(0); }
	
	public <T extends Enum<T>> T columnAsStringEnum(String column, Class<T> enumClass) { String value = columnAsString(column); return value == null ? null : (T) Enum.valueOf((Class<T>)enumClass, columnAsString(column)); }
	public <T extends Enum<T>> T columnAsOrdinalEnum(String column, Class<T> enumClass) { String value = columnAsString(column); return value == null ? null : (T) ((Class<T>)enumClass).getEnumConstants()[columnAsInteger(column)]; }
	

	private Integer indexOf(String column){
		int index = resultTable.columnNames.indexOf(column);
		if(index == -1){
			log.error("Column not found: "+column + ", available columns: " + resultTable.columnNames);
			return null;
		}
		return index;
	}
	
	@SneakyThrows
	public <T> T attribute(Class<?> c, String attribute) { 
		try {
			Field field = c.getDeclaredField(attribute);
			Type type = field.getGenericType();

			if(type == String.class) {
				return (T) attributeAsString(c, attribute);
			}
			else if(type == boolean.class || type == Boolean.class) {
				return (T) attributeAsBoolean(c, attribute);
			}
			else if(type == byte.class || type == Byte.class) {
				return (T) attributeAsByte(c, attribute);
			}
			else if(type == short.class || type == Short.class) {
				return (T) attributeAsShort(c, attribute);
			}
			else if(type == int.class || type == Integer.class) {
				return (T) attributeAsInteger(c, attribute);
			}
			else if(type == long.class || type == Long.class) {
				return (T) attributeAsLong(c, attribute);
			}
			else if(type == float.class || type == Float.class) {
				return (T) attributeAsFloat(c, attribute);
			}
			else if(type == double.class || type == Double.class) {
				return (T) attributeAsDouble(c, attribute);
			}
			else if(type == char.class || type == Character.class) {
				return (T) attributeAsChar(c, attribute);
			}
			else if(type == Date.class) {
				return (T) attributeAsDate(c, attribute);
			}
			else if(type == LocalDateTime.class) {
				return (T) attributeAsLocalDateTime(c, attribute);
			}
			else if(type == LocalDate.class) {
				return (T) attributeAsLocalDate(c, attribute);
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
				return (T) attributeConverter.convertToEntityAttribute(attributeAsString(c, attribute));
			}

			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof jakarta.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				return (T) attributeConverter.convertToEntityAttribute(attributeAsString(c, attribute));
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return (T) columnAsStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) columnAsOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return (T) columnAsStringEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
				}
				else {
					return (T) columnAsOrdinalEnum(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute), (Class) type);
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

	public String 			attributeAsString		(Class<?> c, String attribute) 	{ return columnAsString(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public String 			attributeAsString		(Entity<?> e, String attribute)	{ return columnAsString(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Numbers
	public Short 			attributeAsShort		(Class<?> c, String attribute) 	{ return columnAsShort(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Short 			attributeAsShort		(Entity<?> e, String attribute)	{ return columnAsShort(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Integer 			attributeAsInteger		(Class<?> c, String attribute) 	{ return columnAsInteger(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Integer 			attributeAsInteger		(Entity<?> e, String attribute)	{ return columnAsInteger(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Long 			attributeAsLong			(Class<?> c, String attribute) 	{ return columnAsLong(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Long 			attributeAsLong			(Entity<?> e, String attribute)	{ return columnAsLong(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Float 			attributeAsFloat		(Class<?> c, String attribute) 	{ return columnAsFloat(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Float 			attributeAsFloat		(Entity<?> e, String attribute)	{ return columnAsFloat(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Double 			attributeAsDouble		(Class<?> c, String attribute) 	{ return columnAsDouble(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Double 			attributeAsDouble		(Entity<?> e, String attribute)	{ return columnAsDouble(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public BigDecimal		attributeAsBigDecimal 	(Class<?> c, String attribute) 	{ return columnAsBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public BigDecimal		attributeAsBigDecimal	(Entity<?> e, String attribute)	{ return columnAsBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Date
	public Date 			attributeAsDate			(Class<?> c, String attribute)	{ return columnAsDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Date 			attributeAsDate			(Entity<?> e, String attribute)	{ return columnAsDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDateTime	attributeAsLocalDateTime(Class<?> c, String attribute)	{ return columnAsLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDateTime 	attributeAsLocalDateTime(Entity<?> e, String attribute) { return columnAsLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDate		attributeAsLocalDate	(Class<?> c, String attribute)	{ return columnAsLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDate	 	attributeAsLocalDate	(Entity<?> e, String attribute) { return columnAsLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	// Other
	public Boolean 			attributeAsBoolean		(Class<?> c, String attribute) 	{ return columnAsBoolean(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Boolean 			attributeAsBoolean		(Entity<?> e, String attribute)	{ return columnAsBoolean(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Byte				attributeAsByte			(Class<?> c, String attribute) 	{ return columnAsByte(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Byte				attributeAsByte			(Entity<?> e, String attribute) { return columnAsByte(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Character		attributeAsChar			(Class<?> c, String attribute)  { return columnAsChar(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Character		attributeAsChar			(Entity<?> e, String attribute)	{ return columnAsChar(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	
	
	

	// @Enumerated
	public Enum attributeAsEnum(Class<?> entity, String attribute) {
		
		try {
			Field field = entity.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, attributeAsString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[attributeAsInteger(entity, attribute)];
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, attributeAsString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[attributeAsInteger(entity, attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
		
	}

	public Long getCount() { return columnAsLong("count(*)"); }
	public Long getCount(Class<? extends Object> c, String attribute) { return columnAsLong("count("+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) { return columnAsLong("count(distinct "+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }

	
}
