package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.ArrayUtils;

import com.estivate.IMapper.DateMapper;
import com.estivate.IMapper.EntityMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class Result {

	final String[] columnValues;
	final String[] columnNames;
	final NameMapper nameMapper;

	private Map<String, Object> cache = new HashMap<>();

	
	public Result(String[] values, String[] columns, NameMapper nameMapper) {
		this.columnValues = values;
		this.columnNames = columns;
		this.nameMapper = nameMapper;  
	}

	
	@SneakyThrows
	public <U> U mapTo(Entity<U> clazz) throws SecurityException, IllegalArgumentException {
		
		String key = nameMapper.mapEntityClass(clazz);
		
		U u = (U) cache.get(key);
		
		if(u == null) {
			EntityMapper<U> mapper = new EntityMapper<>(clazz.entity);
			mapper.setNameMapper(nameMapper);
			mapper.setColumnNames(columnNames);
			u = mapper.map(columnValues);
			cache.put(key, u);
		}
		
		return u;
		
	}
	
	public <U> U mapTo(Class<U> clazz) throws SecurityException, IllegalArgumentException {
		return mapTo(new Entity<U>(clazz));
	}

	
	@SneakyThrows
	public String 			columnAsString(String column) { Integer index = ArrayUtils.indexOf(columnNames, column); return index == null ? null : columnValues[index]; }
	public Short 			columnAsShort(String column) { String value = columnAsString(column); return value == null ? null : Short.valueOf(value); } 
	public Integer 			columnAsInteger(String column) { String value = columnAsString(column); return value == null ? null : Integer.valueOf(value); }
	public Long 			columnAsLong(String column) { String value = columnAsString(column); return value == null ? null : Long.valueOf(value); }
	public Float 			columnAsFloat(String column) { String value = columnAsString(column); return value == null ? null : Float.valueOf(value); }
	public Double 			columnAsDouble(String column) { String value = columnAsString(column); return value == null ? null : Double.valueOf(value); }
	public Boolean 			columnAsBoolean(String column) { String value = columnAsString(column); return value == null ? null : Boolean.valueOf(value); }
	public Byte 			columnAsByte(String column) { String value = columnAsString(column); return value == null ? null : Byte.valueOf(value); }
	public Character 		columnAsChar(String column) { String value = columnAsString(column); return value == null ? null : value.charAt(0); }
	public LocalDateTime	columnAsLocalDateTime(String column) { String value = columnAsString(column); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDate		columnAsLocalDate(String column) { LocalDateTime ldt = columnAsLocalDateTime(column); return ldt == null ? null : ldt.toLocalDate(); }
	public Date 			columnAsDate(String column) { LocalDateTime ldt = columnAsLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant()); } 

	public <U> U columnAsStringEnum(String column, Class<U> enumClass) { String value = columnAsString(column); return value == null ? null : (U) Enum.valueOf((Class)enumClass, columnAsString(column)); }
	public <U> U columnAsOrdinalEnum(String column, Class<U> enumClass) { String value = columnAsString(column); return value == null ? null : (U) enumClass.getEnumConstants()[columnAsInteger(column)]; }
	
	public Object 	attribute(Class<?> c, String attribute) { 
		try {
			Field field = c.getDeclaredField(attribute);
			Type type = field.getGenericType();

			if(type == String.class) {
				return attributeAsString(c, attribute);
			}
			else if(type == boolean.class || type == Boolean.class) {
				return attributeAsBoolean(c, attribute);
			}
			else if(type == byte.class || type == Byte.class) {
				return attributeAsByte(c, attribute);
			}
			else if(type == short.class || type == Short.class) {
				return attributeAsShort(c, attribute);
			}
			else if(type == int.class || type == Integer.class) {
				return attributeAsInteger(c, attribute);
			}
			else if(type == long.class || type == Long.class) {
				return attributeAsLong(c, attribute);
			}
			else if(type == float.class || type == Float.class) {
				return attributeAsFloat(c, attribute);
			}
			else if(type == double.class || type == Double.class) {
				return attributeAsDouble(c, attribute);
			}
			else if(type == char.class || type == Character.class) {
				return attributeAsChar(c, attribute);
			}
			else if(type == Date.class) {
				return attributeAsDate(c, attribute);
			}
			else if(type == LocalDateTime.class) {
				return attributeAsLocalDateTime(c, attribute);
			}
			else if(type == LocalDate.class) {
				return attributeAsLocalDate(c, attribute);
			}
			// @Convert (might be enum, this condition should be tested before classic enum)
			else if(field.getDeclaredAnnotation(Convert.class) != null) {
				try {
					Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
					Object converter = convertAnnotation.converter().getConstructor().newInstance();
					if(!(converter instanceof AttributeConverter)) {
						log.error("Cannot convert with converter "+converter.getClass());
						return null;
					}
					AttributeConverter attributeConverter = (AttributeConverter) converter;
					return attributeConverter.convertToEntityAttribute(attributeAsString(c, attribute));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
	
				Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
					return columnAsStringEnum(nameMapper.mapEntity(c, attribute), (Class) type);
				}
				else {
					return columnAsOrdinalEnum(nameMapper.mapEntity(c, attribute), (Class) type);
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

	public String 			attributeAsString		(Class<?> c, String attribute) 	{ return columnAsString(nameMapper.mapEntity(c, attribute)); }
	public String 			attributeAsString		(Entity<?> e, String attribute)	{ return columnAsString(nameMapper.mapEntity(e, attribute)); }
	public Short 			attributeAsShort		(Class<?> c, String attribute) 	{ return columnAsShort(nameMapper.mapEntity(c, attribute)); }
	public Short 			attributeAsShort		(Entity<?> e, String attribute)	{ return columnAsShort(nameMapper.mapEntity(e, attribute)); }
	public Integer 			attributeAsInteger		(Class<?> c, String attribute) 	{ return columnAsInteger(nameMapper.mapEntity(c, attribute)); }
	public Integer 			attributeAsInteger		(Entity<?> e, String attribute)	{ return columnAsInteger(nameMapper.mapEntity(e, attribute)); }
	public Long 			attributeAsLong			(Class<?> c, String attribute) 	{ return columnAsLong(nameMapper.mapEntity(c, attribute)); }
	public Long 			attributeAsLong			(Entity<?> e, String attribute)	{ return columnAsLong(nameMapper.mapEntity(e, attribute)); }
	public Float 			attributeAsFloat		(Class<?> c, String attribute) 	{ return columnAsFloat(nameMapper.mapEntity(c, attribute)); }
	public Float 			attributeAsFloat		(Entity<?> e, String attribute)	{ return columnAsFloat(nameMapper.mapEntity(e, attribute)); }
	public Double 			attributeAsDouble		(Class<?> c, String attribute) 	{ return columnAsDouble(nameMapper.mapEntity(c, attribute)); }
	public Double 			attributeAsDouble		(Entity<?> e, String attribute)	{ return columnAsDouble(nameMapper.mapEntity(e, attribute)); }
	public Boolean 			attributeAsBoolean		(Class<?> c, String attribute) 	{ return columnAsBoolean(nameMapper.mapEntity(c, attribute)); }
	public Boolean 			attributeAsBoolean		(Entity<?> e, String attribute)	{ return columnAsBoolean(nameMapper.mapEntity(e, attribute)); }
	public Byte				attributeAsByte			(Class<?> c, String attribute) 	{ return columnAsByte(nameMapper.mapEntity(c, attribute)); }
	public Byte				attributeAsByte			(Entity<?> e, String attribute) { return columnAsByte(nameMapper.mapEntity(e, attribute)); }
	public Character		attributeAsChar			(Class<?> c, String attribute)  { return columnAsChar(nameMapper.mapEntity(c, attribute)); }
	public Character		attributeAsChar			(Entity<?> e, String attribute)	{ return columnAsChar(nameMapper.mapEntity(e, attribute)); }
	public Date 			attributeAsDate			(Class<?> c, String attribute)	{ return columnAsDate(nameMapper.mapEntity(c, attribute)); }
	public Date 			attributeAsDate			(Entity<?> e, String attribute)	{ return columnAsDate(nameMapper.mapEntity(e, attribute)); }
	public LocalDateTime	attributeAsLocalDateTime(Class<?> c, String attribute)	{ return columnAsLocalDateTime(nameMapper.mapEntity(c, attribute)); }
	public LocalDateTime 	attributeAsLocalDateTime(Entity<?> e, String attribute) { return columnAsLocalDateTime(nameMapper.mapEntity(e, attribute)); }
	public LocalDate		attributeAsLocalDate	(Class<?> c, String attribute)	{ return columnAsLocalDate(nameMapper.mapEntity(c, attribute)); }
	public LocalDate	 	attributeAsLocalDate	(Entity<?> e, String attribute) { return columnAsLocalDate(nameMapper.mapEntity(e, attribute)); }
	
	

	// @Enumerated
	public Enum attributeAsEnum(Class<?> entity, String attribute) {
		
		try {
			Field field = entity.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
	
				Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
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
	public Long getCount(Class<? extends Object> c, String attribute) { return columnAsLong("count("+nameMapper.mapDatabase(c, attribute)+")"); }	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) { return columnAsLong("count(distinct "+nameMapper.mapDatabase(c, attribute)+")"); }

	
}
