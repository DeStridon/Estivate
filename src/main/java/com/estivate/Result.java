package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.query.Query;
import com.estivate.query.Query.Entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Result {

	Statement statement;
	@Getter Map<String, String> columns;
	
	public Result(Statement statement, Map<String, String> columns) {
		this.statement = statement;
		this.columns = columns;
	}
	

	final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]").withZone(ZoneId.systemDefault());

	private Map<String, Object> cache = new HashMap<>();
	
	
	
	public <U> U mapTo(Entity<U> clazz) throws SecurityException, IllegalArgumentException {
		
		String key = statement.context.nameMapper.mapEntityClass(clazz);
		
		U u = (U) cache.get(key);
		
		if(u == null) {
			Mapper<U> mapper = new Mapper<>(clazz.entity, statement.context);
			u = mapper.map(columns);
			cache.put(key, u);
		}
		
		return u;
		
	}
	
	public <U> U mapTo(Class<U> clazz) throws SecurityException, IllegalArgumentException {
		return mapTo(new Entity<U>(clazz));
	}

	
	public String 	getAsString(String column) { return columns.get(column); }
	public Short 	getAsShort(String column) 	 { return Short.valueOf(columns.get(column)); }
	public Integer 	getAsInteger(String column) { return Integer.valueOf(columns.get(column)); }
	public Long 	getAsLong(String column) { return Long.valueOf(columns.get(column)); }
	public Float 	getAsFloat(String column) { return Float.valueOf(columns.get(column)); }
	public Double 	getAsDouble(String column) { return Double.valueOf(columns.get(column)); }
	public Boolean 	getAsBoolean(String column) { return Boolean.valueOf(columns.get(column)); }
	
	public Date getAsDate(String column) {
		String value = columns.get(column);
		LocalDateTime ldt = LocalDateTime.parse(value, dateTimeFormater);
		return Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant());
	}
	
	
	public String 	mapToString	(Class<?> c, String attribute) 	{ return getAsString(statement.context.nameMapper.mapEntity(c, attribute)); }
	public String 	mapToString	(Entity<?> e, String attribute)	{ return getAsString(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Short 	mapToShort	(Class<?> c, String attribute) 	{ return getAsShort(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Short 	mapToShort	(Entity<?> e, String attribute)	{ return getAsShort(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Integer 	mapToInteger(Class<?> c, String attribute) 	{ return getAsInteger(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Integer 	mapToInteger(Entity<?> e, String attribute)	{ return getAsInteger(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Boolean 	mapToBoolean(Class<?> c, String attribute) 	{ return getAsBoolean(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Boolean 	mapToBoolean(Entity<?> e, String attribute)	{ return getAsBoolean(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Long 	mapToLong	(Class<?> c, String attribute) 	{ return getAsLong(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Long 	mapToLong	(Entity<?> e, String attribute) { return getAsLong(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Date 	mapToDate	(Class<?> c, String attribute)	{ return getAsDate(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Date 	mapToDate	(Entity<?> e, String attribute)	{ return getAsDate(statement.context.nameMapper.mapEntity(e, attribute)); }

	
	

	// @Enumerated
	public Enum getAsEnum(Class<?> entity, String attribute) {
		
		try {
			Field[] fields = entity.getDeclaredFields();
			Field field = entity.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
	
				Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
					return Enum.valueOf((Class)type, mapToString(entity, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[mapToInteger(entity, attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
		
	}
	
	
	public Long getCount() {
		if(columns.containsKey("COUNT(*)")) {
			return Long.valueOf(columns.get("COUNT(*)"));			
		}
		return null;
	}
	
	public Long getCount(Class<? extends Object> c, String attribute) {
		if(columns.containsKey("COUNT("+statement.context.nameMapper.mapDatabase(c, attribute)+")")) {
			return Long.valueOf(columns.get("COUNT("+statement.context.nameMapper.mapDatabase(c, attribute)+")"));
		}
		return null;
	}
	

	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) {
		if(columns.containsKey("COUNT(DISTINCT "+statement.context.nameMapper.mapDatabase(c, attribute)+")")) {
			return Long.valueOf(columns.get("COUNT(DISTINCT "+statement.context.nameMapper.mapDatabase(c, attribute)+")"));
		}
		return null;
	}

	
}
