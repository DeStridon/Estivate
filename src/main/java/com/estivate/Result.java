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
	

	final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

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
	public Enum getAsEnum(Class c, String attribute) {
		
		try {
			Field[] fields = c.getDeclaredFields();
			Field field = c.getDeclaredField(attribute);
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
	
				Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
					return Enum.valueOf((Class)type, mapToString(c, attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[mapToInteger(c, attribute)];
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

	
	
	
//	@SneakyThrows
//	public static <U> U generateObject(Class<U> clazz, Map<String, String> arguments) {
//		
//		Mapper<U> mapper = new Mapper<>(clazz); 
//		
//
//		//Constructor<U> constructor = clazz.getConstructor();
////		U obj = constructor.newInstance();
////		
////		Class<?> currentClazz = clazz;
////		Entity entity = new Entity(clazz);
//		
//		
//		while(currentClazz != Object.class) {
//
//			Set<Field> fields = FieldUtils.getEntityFields(currentClazz);
//			for(Field field : fields) {
//				setGeneratedField(entity, arguments, field, obj);
//			}
//			currentClazz = currentClazz.getSuperclass();
//		}
//		
//		Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
//		for(Method method : methods) {
//			method.invoke(obj);
//		}
//		
//		return obj;
//	
//	}
	

	

//	public static <U> void setGeneratedField(Entity entity, Map<String, String> arguments, Field field, U obj) throws IllegalAccessException, AttributeInUseException, NoSuchMethodException, ParseException, InvocationTargetException, InstantiationException {
//		Type type = field.getGenericType();
//		
//		String value = arguments.get(FieldUtils.getFieldName(entity, field));
//		
//		if(value == null) {
//			return;
//		}
//
//		if(type == String.class) {
//			field.set(obj, value);
//		}
//		else if(type == long.class) {
//			field.setLong(obj, Long.parseLong(value));
//		}
//		else if(type == Long.class) {
//			field.set(obj, Long.parseLong(value));
//		}
//		else if(type == boolean.class) {
//			field.setBoolean(obj, Boolean.parseBoolean(value));
//		}
//		else if(type == Boolean.class) {
//			field.set(obj, Boolean.parseBoolean(value));
//		}
//		else if(type == Byte.class) {
//			field.set(obj, Byte.parseByte(value));
//		}
//		else if(type == double.class) {
//			field.setDouble(obj, Double.parseDouble(value));
//		}
//		else if(type == Double.class) {
//			field.set(obj, Double.parseDouble(value));
//		}
//		else if(type == Character.class && value.length() > 0) {
//			field.set(obj, value.charAt(0));
//		}
//		else if(type == Float.class) {
//			field.set(obj, Float.parseFloat(value));
//		}
//		else if(type == int.class) {
//			field.setInt(obj, Integer.parseInt(value));
//		}
//		else if(type == Integer.class) {
//			field.set(obj, Integer.parseInt(value));
//		}
//		else if(type == short.class) {
//			field.setShort(obj, Short.parseShort(value));
//		}
//		else if(type == Short.class) {
//			field.set(obj, Short.parseShort(value));
//		}
//		else if(type == Date.class) {
//			// TODO : check date format is the right one
//			field.set(obj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value));
//		}
//		// @Convert (might be enum, this condition should be tested before classic enum)
//		else if(field.getDeclaredAnnotation(Convert.class) != null) {
//			Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
//			Object converter = convertAnnotation.converter().getConstructor().newInstance();
//			if(!(converter instanceof AttributeConverter)) {
//				log.error("Cannot convert with converter "+converter.getClass());
//				return;
//			}
//			AttributeConverter attributeConverter = (AttributeConverter) converter;
//			Object attributeValue = attributeConverter.convertToEntityAttribute(value);
//			field.set(obj, attributeValue);
//		}
//		// @Enumerated
//		else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
//
//			Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
//			if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
//				field.set(obj, Enum.valueOf((Class)type, value));
//			}
//			else {
//				int ordinal = Integer.parseInt(value);
//				field.set(obj, field.getType().getEnumConstants()[ordinal]);
//			}
//		}
//		else {
//			log.error("This type is not mapped yet : "+type);
//			throw new AttributeInUseException("This type is not mapped yet : "+type);
//		}
//	}
	
}
