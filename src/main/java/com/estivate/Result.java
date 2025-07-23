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

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
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

	
	public String 	columnAsString(String column) { return columns.get(column); }
	public Short 	columnAsShort(String column) 	 { return Short.valueOf(columns.get(column)); }
	public Integer 	columnAsInteger(String column) { return Integer.valueOf(columns.get(column)); }
	public Long 	columnAsLong(String column) { return Long.valueOf(columns.get(column)); }
	public Float 	columnAsFloat(String column) { return Float.valueOf(columns.get(column)); }
	public Double 	columnAsDouble(String column) { return Double.valueOf(columns.get(column)); }
	public Boolean 	columnAsBoolean(String column) { return Boolean.valueOf(columns.get(column)); }
	
	public Date columnAsDate(String column) {
		String value = columns.get(column);
		LocalDateTime ldt = LocalDateTime.parse(value, dateTimeFormater);
		return Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant());
	}

	public <U> U columnAsStringEnum(String column, Class<U> enumClass) { return (U) Enum.valueOf((Class)enumClass, columnAsString(column)); }
	public <U> U columnAsOrdinalEnum(String column, Class<U> enumClass) { return (U) enumClass.getEnumConstants()[columnAsInteger(column)]; }
	
	public String 	attributeAsString	(Class<?> c, String attribute) 	{ return columnAsString(statement.context.nameMapper.mapEntity(c, attribute)); }
	public String 	attributeAsString	(Entity<?> e, String attribute)	{ return columnAsString(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Short 	attributeAsShort	(Class<?> c, String attribute) 	{ return columnAsShort(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Short 	attributeAsShort	(Entity<?> e, String attribute)	{ return columnAsShort(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Integer 	attributeAsInteger(Class<?> c, String attribute) 	{ return columnAsInteger(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Integer 	attributeAsInteger(Entity<?> e, String attribute)	{ return columnAsInteger(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Boolean 	attributeAsBoolean(Class<?> c, String attribute) 	{ return columnAsBoolean(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Boolean 	attributeAsBoolean(Entity<?> e, String attribute)	{ return columnAsBoolean(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Long 	attributeAsLong	(Class<?> c, String attribute) 	{ return columnAsLong(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Long 	attributeAsLong	(Entity<?> e, String attribute) { return columnAsLong(statement.context.nameMapper.mapEntity(e, attribute)); }
	public Date 	attributeAsDate	(Class<?> c, String attribute)	{ return columnAsDate(statement.context.nameMapper.mapEntity(c, attribute)); }
	public Date 	attributeAsDate	(Entity<?> e, String attribute)	{ return columnAsDate(statement.context.nameMapper.mapEntity(e, attribute)); }


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
