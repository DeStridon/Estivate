package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	//final ResultSetMetaData resultSetMetaData;
	final String[] columnValues;
	final String[] columnNames;
	final NameMapper nameMapper;

	
//	@SneakyThrows
//	public Result(String[] values, String[] columns, Statement statement) {
//		
//		//this.resultSetMetaData = resultSet.getMetaData();
//		this.columnValues = new String[resultSet.getMetaData().getColumnCount()];
//		for(int i = 0; i < columnValues.length; i++) {
//			this.columnValues[i] = resultSet.getString(i+1);
//		}
//		this.columnNames = columnNames;
//		
//
//	}
	
	
	public Result(String[] values, String[] columns, NameMapper nameMapper) {
		this.columnValues = values;
		this.columnNames = columns;
		this.nameMapper = nameMapper;
	}
	

	//final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]").withZone(ZoneId.systemDefault());

	private Map<String, Object> cache = new HashMap<>();
	
	
	
	@SneakyThrows
	public <U> U mapTo(Entity<U> clazz) throws SecurityException, IllegalArgumentException {
		
		String key = nameMapper.mapEntityClass(clazz);
		
		U u = (U) cache.get(key);
		
		if(u == null) {
			EntityMapper<U> mapper = new EntityMapper<>(clazz.entity);
			u = mapper.map(columnValues);
			cache.put(key, u);
		}
		
		return u;
		
	}
	
	public <U> U mapTo(Class<U> clazz) throws SecurityException, IllegalArgumentException {
		return mapTo(new Entity<U>(clazz));
	}

	
	@SneakyThrows
	public String 	columnAsString(String column) { Integer index = ArrayUtils.indexOf(columnNames, column); return index == null ? null : columnValues[index]; }
	public Short 	columnAsShort(String column) { String value = columnAsString(column); return value == null ? null : Short.valueOf(value); } 
	public Integer 	columnAsInteger(String column) { String value = columnAsString(column); return value == null ? null : Integer.valueOf(value); }
	public Long 	columnAsLong(String column) { String value = columnAsString(column); return value == null ? null : Long.valueOf(value); }
	public Float 	columnAsFloat(String column) { String value = columnAsString(column); return value == null ? null : Float.valueOf(value); }
	public Double 	columnAsDouble(String column) { String value = columnAsString(column); return value == null ? null : Double.valueOf(value); }
	public Boolean 	columnAsBoolean(String column) { String value = columnAsString(column); return value == null ? null : Boolean.valueOf(value); }
	
	public Date columnAsDate(String column) {
		String value = columnAsString(column);
		if(value == null) return null;
		LocalDateTime ldt = LocalDateTime.parse(value, DateMapper.formatter);
		return Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant());
	}

	public <U> U columnAsStringEnum(String column, Class<U> enumClass) { String value = columnAsString(column); return value == null ? null : (U) Enum.valueOf((Class)enumClass, columnAsString(column)); }
	public <U> U columnAsOrdinalEnum(String column, Class<U> enumClass) { String value = columnAsString(column); return value == null ? null : (U) enumClass.getEnumConstants()[columnAsInteger(column)]; }
	
	public String 	attributeAsString	(Class<?> c, String attribute) 	{ return columnAsString(nameMapper.mapEntity(c, attribute)); }
	public String 	attributeAsString	(Entity<?> e, String attribute)	{ return columnAsString(nameMapper.mapEntity(e, attribute)); }
	public Short 	attributeAsShort	(Class<?> c, String attribute) 	{ return columnAsShort(nameMapper.mapEntity(c, attribute)); }
	public Short 	attributeAsShort	(Entity<?> e, String attribute)	{ return columnAsShort(nameMapper.mapEntity(e, attribute)); }
	public Integer 	attributeAsInteger	(Class<?> c, String attribute) 	{ return columnAsInteger(nameMapper.mapEntity(c, attribute)); }
	public Integer 	attributeAsInteger	(Entity<?> e, String attribute)	{ return columnAsInteger(nameMapper.mapEntity(e, attribute)); }
	public Long 	attributeAsLong		(Class<?> c, String attribute) 	{ return columnAsLong(nameMapper.mapEntity(c, attribute)); }
	public Long 	attributeAsLong		(Entity<?> e, String attribute) { return columnAsLong(nameMapper.mapEntity(e, attribute)); }
	public Float 	attributeAsFloat	(Class<?> c, String attribute) 	{ return columnAsFloat(nameMapper.mapEntity(c, attribute)); }
	public Float 	attributeAsFloat	(Entity<?> e, String attribute)	{ return columnAsFloat(nameMapper.mapEntity(e, attribute)); }
	public Double 	attributeAsDouble	(Class<?> c, String attribute) 	{ return columnAsDouble(nameMapper.mapEntity(c, attribute)); }
	public Double 	attributeAsDouble	(Entity<?> e, String attribute)	{ return columnAsDouble(nameMapper.mapEntity(e, attribute)); }
	public Boolean 	attributeAsBoolean	(Class<?> c, String attribute) 	{ return columnAsBoolean(nameMapper.mapEntity(c, attribute)); }
	public Boolean 	attributeAsBoolean	(Entity<?> e, String attribute)	{ return columnAsBoolean(nameMapper.mapEntity(e, attribute)); }
	public Date 	attributeAsDate		(Class<?> c, String attribute)	{ return columnAsDate(nameMapper.mapEntity(c, attribute)); }
	public Date 	attributeAsDate		(Entity<?> e, String attribute)	{ return columnAsDate(nameMapper.mapEntity(e, attribute)); }


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
