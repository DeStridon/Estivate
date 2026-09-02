package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.query.Attribute;
import com.estivate.query.Attribute.Function;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;

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
	public Instant			asInstant(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : ldt.atZone(resultTable.context.serverZoneId).toInstant(); }
	public Date 			asDate(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(resultTable.context.serverZoneId).toInstant()); } 

	public Boolean 			asBoolean(String column) {  return FieldUtils.parseBoolean(asString(column)); }
	public Byte 			asByte(String column) { String value = asString(column); return value == null ? null : Byte.valueOf(value); }
	public Character 		asChar(String column) { String value = asString(column); return value == null ? null : value.charAt(0); }
	
	public <T extends Enum<T>> T asStringEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : (T) Enum.valueOf((Class<T>)enumClass, asString(column)); }
	public <T extends Enum<T>> T asOrdinalEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : (T) ((Class<T>)enumClass).getEnumConstants()[asInteger(column)]; }
	

	private Integer indexOf(String column){

		for(int i = 0; i < resultTable.columnNames.size(); i++){
			if(resultTable.columnNames.get(i).equalsIgnoreCase(column)){
				return i;
			}
		}
		log.error("Column not found: "+column + ", available columns: " + resultTable.columnNames);
		return null;
	}

	public Object as(Attribute attribute) {
		return as(attribute.getEntity().entity, attribute.getAttribute(), attribute.getFunction());
	}

	public <T> T as(Class<?> c, String attribute) {
		return as(c, attribute, null);
	}

	@SneakyThrows
	public <T> T as(Class<?> c, String attribute, Function function) {
		
		if(function == null || function.getReturnType() == null){
//		try {
			Field field = FieldUtils.findField(c, attribute);
			Type type = field.getGenericType();

			if(type == String.class) { return (T) asString(c, attribute, function); }
			else if(type == boolean.class || type == Boolean.class) { return (T) asBoolean(c, attribute, function);}
			else if(type == byte.class || type == Byte.class) { return (T) asByte(c, attribute, function);}
			else if(type == short.class || type == Short.class) { return (T) asShort(c, attribute, function);}
			else if(type == int.class || type == Integer.class) { return (T) asInteger(c, attribute, function);}
			else if(type == long.class || type == Long.class) { return (T) asLong(c, attribute, function);}
			else if(type == float.class || type == Float.class) { return (T) asFloat(c, attribute, function);}
			else if(type == double.class || type == Double.class) { return (T) asDouble(c, attribute, function);}
			else if(type == char.class || type == Character.class) { return (T) asChar(c, attribute, function);}
			else if(type == Date.class) { return (T) asDate(c, attribute, function);}
			else if(type == LocalDateTime.class) { return (T) asLocalDateTime(c, attribute, function);}
			else if(type == Instant.class) { return (T) asInstant(c, attribute, function);}
			else if(type == LocalDate.class) { return (T) asLocalDate(c, attribute, function);}
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
		}
		else if(function.getReturnType() == Integer.class){ return (T) asInteger(c, attribute, function); }
		else if(function.getReturnType() == Long.class){ 	return (T) asLong(c, attribute, function); }
		else if(function.getReturnType() == Float.class){ 	return (T) asFloat(c, attribute, function); }
		else if(function.getReturnType() == Double.class){	return (T) asDouble(c, attribute, function); }
		else if(function.getReturnType() == String.class){ return (T) asString(c, attribute, function); }
		else if(function.getReturnType() == Character.class){ return (T) asChar(c, attribute, function); }
		else if(function.getReturnType() == Boolean.class){ return (T) asBoolean(c, attribute, function); }
		else if(function.getReturnType() == Byte.class){ return (T) asByte(c, attribute, function); }
		else if(function.getReturnType() == Short.class){ return (T) asShort(c, attribute, function); }
		else if(function.getReturnType() == Date.class){ return (T) asDate(c, attribute, function); }
		else if(function.getReturnType() == LocalDateTime.class){ return (T) asLocalDateTime(c, attribute, function); }
		else if(function.getReturnType() == Instant.class){ return (T) asInstant(c, attribute, function); }
		else if(function.getReturnType() == LocalDate.class){ return (T) asLocalDate(c, attribute, function); }
		log.error("This type is not mapped yet : "+function.getReturnType()); return null;
	}

	public <T, R> R  as(AttributeGetter<T, R> attributeGetter) {
		Attribute attribute = Estivate.attribute(attributeGetter);
		return as(attribute.getEntity().entity, attribute.getAttribute());
	}
	

	public 		 String			asString		(Attribute attribute) { return asString(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public 		 String 		asString		(Class<?> c, String attribute) 	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public 		 String 		asString		(Class<?> c, String attribute, Function function) 	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public 		 String 		asString		(Entity<?> e, String attribute)	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public 		 String 		asString		(Entity<?> e, String attribute, Function function) 	{ return asString(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> String 		asString		(AttributeGetter<T, R> attributeGetter) { return asString(Estivate.attribute(attributeGetter)); }
	// Numbers
	public Short 				asShort			(Attribute attribute) { return asShort(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Short 				asShort			(Class<?> c, String attribute) 	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Short 				asShort			(Class<?> c, String attribute, Function function) 	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Short 				asShort			(Entity<?> e, String attribute)	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Short 				asShort			(Entity<?> e, String attribute, Function function) 	{ return asShort(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Short 			asShort			(AttributeGetter<T, R> attributeGetter) { return asShort(Estivate.attribute(attributeGetter)); }
	public Integer 				asInteger		(Attribute attribute) { return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Integer 				asInteger		(Class<?> c, String attribute) 	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Integer 				asInteger		(Class<?> c, String attribute, Function function) 	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Integer 				asInteger		(Entity<?> e, String attribute)	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Integer 				asInteger		(Entity<?> e, String attribute, Function function) 	{ return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Integer 		asInteger		(AttributeGetter<T, R> attributeGetter) { return asInteger(Estivate.attribute(attributeGetter)); }
	public Long 				asLong			(Attribute attribute) { return asLong(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Long 				asLong			(Class<?> c, String attribute) 	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Long 				asLong			(Class<?> c, String attribute, Function function) 	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Long 				asLong			(Entity<?> e, String attribute)	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Long 				asLong			(Entity<?> e, String attribute, Function function) 	{ return asLong(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Long 			asLong			(AttributeGetter<T, R> attributeGetter) { return asLong(Estivate.attribute(attributeGetter)); }
	public Float 				asFloat			(Attribute attribute) { return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Float 				asFloat			(Class<?> c, String attribute) 	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Float 				asFloat			(Class<?> c, String attribute, Function function) 	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Float 				asFloat			(Entity<?> e, String attribute)	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Float 				asFloat			(Entity<?> e, String attribute, Function function) 	{ return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Float 			asFloat			(AttributeGetter<T, R> attributeGetter) { return asFloat(Estivate.attribute(attributeGetter)); }
	public Double 				asDouble		(Attribute attribute) { return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Double 				asDouble		(Class<?> c, String attribute) 	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Double 				asDouble		(Class<?> c, String attribute, Function function) 	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Double 				asDouble		(Entity<?> e, String attribute)	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Double 				asDouble		(Entity<?> e, String attribute, Function function) 	{ return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Double 		asDouble		(AttributeGetter<T, R> attributeGetter) { return asDouble(Estivate.attribute(attributeGetter)); }
	public BigDecimal			asBigDecimal	(Attribute attribute) { return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public BigDecimal			asBigDecimal	(Class<?> c, String attribute) 	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public BigDecimal			asBigDecimal	(Class<?> c, String attribute, Function function) 	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public BigDecimal			asBigDecimal	(Entity<?> e, String attribute)	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public BigDecimal			asBigDecimal	(Entity<?> e, String attribute, Function function) 	{ return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> BigDecimal		asBigDecimal	(AttributeGetter<T, R> attributeGetter) { return asBigDecimal(Estivate.attribute(attributeGetter)); }
	// Date
	public Date 				asDate			(Attribute attribute) { return asDate(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Date 				asDate			(Class<?> c, String attribute)	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Date 				asDate			(Class<?> c, String attribute, Function function) 	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Date 				asDate			(Entity<?> e, String attribute)	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Date 				asDate			(Entity<?> e, String attribute, Function function) 	{ return asDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Date 			asDate			(AttributeGetter<T, R> attributeGetter) { return asDate(Estivate.attribute(attributeGetter)); }
	public LocalDateTime		asLocalDateTime	(Attribute attribute) { return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public LocalDateTime		asLocalDateTime	(Class<?> c, String attribute)	{ return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDateTime 		asLocalDateTime	(Class<?> c, String attribute, Function function) 	{ return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public LocalDateTime 		asLocalDateTime	(Entity<?> e, String attribute) { return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDateTime 		asLocalDateTime	(Entity<?> e, String attribute, Function function) 	{ return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> LocalDateTime 	asLocalDateTime	(AttributeGetter<T, R> attributeGetter) { return asLocalDateTime(Estivate.attribute(attributeGetter)); }
	public Instant				asInstant		(Attribute attribute) { return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Instant				asInstant		(Class<?> c, String attribute)	{ return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Instant				asInstant		(Class<?> c, String attribute, Function function) 	{ return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Instant		 		asInstant		(Entity<?> e, String attribute) { return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Instant				asInstant		(Entity<?> e, String attribute, Function function) 	{ return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Instant 		asInstant		(AttributeGetter<T, R> attributeGetter) { return asInstant(Estivate.attribute(attributeGetter)); }
	public LocalDate			asLocalDate		(Attribute attribute) { return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public LocalDate			asLocalDate		(Class<?> c, String attribute)	{ return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public LocalDate			asLocalDate		(Class<?> c, String attribute, Function function) 	{ return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public LocalDate	 		asLocalDate		(Entity<?> e, String attribute) { return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public LocalDate	 		asLocalDate		(Entity<?> e, String attribute, Function function) 	{ return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> LocalDate 		asLocalDate		(AttributeGetter<T, R> attributeGetter) { return asLocalDate(Estivate.attribute(attributeGetter)); }
	// Other
	public Boolean 				asBoolean		(Attribute attribute) { return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Boolean 				asBoolean		(Class<?> c, String attribute) 	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Boolean 				asBoolean		(Class<?> c, String attribute, Function function) 	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Boolean 				asBoolean		(Entity<?> e, String attribute)	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Boolean 				asBoolean		(Entity<?> e, String attribute, Function function) 	{ return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Boolean 		asBoolean		(AttributeGetter<T, R> attributeGetter) { return asBoolean(Estivate.attribute(attributeGetter)); }
	public Byte					asByte			(Attribute attribute) { return asByte(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Byte					asByte			(Class<?> c, String attribute) 	{ return asByte(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Byte					asByte			(Class<?> c, String attribute, Function function) 	{ return asByte(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Byte					asByte			(Entity<?> e, String attribute) { return asByte(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Byte					asByte			(Entity<?> e, String attribute, Function function) 	{ return asByte(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Byte 			asByte			(AttributeGetter<T, R> attributeGetter) { return asByte(Estivate.attribute(attributeGetter)); }
	public Character			asChar			(Attribute attribute) { return asChar(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Character			asChar			(Class<?> c, String attribute)  { return asChar(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute)); }
	public Character			asChar			(Class<?> c, String attribute, Function function) 	{ return asChar(resultTable.context.nameMapper.toEntityNameAttribute(c, attribute, function)); }
	public Character			asChar			(Entity<?> e, String attribute)	{ return asChar(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute)); }
	public Character			asChar			(Entity<?> e, String attribute, Function function) 	{ return asChar(resultTable.context.nameMapper.toEntityNameAttribute(e, attribute, function)); }
	public <T,R> Character 		asChar			(AttributeGetter<T, R> attributeGetter) { return asChar(Estivate.attribute(attributeGetter)); }
	
	

	// @Enumerated
	public Enum asEnum(Attribute attribute) {
		try {
			Class entity = attribute.getEntity().entity;
			Field field = attribute.getEntity().entity.getDeclaredField(attribute.getAttribute());
			Type type = field.getGenericType();
			
			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
	
				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, asString(attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[asInteger(attribute)];
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, asString(attribute));
				}
				else {
					return (Enum) field.getType().getEnumConstants()[asInteger(attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
	}
	public Enum asEnum(Class<?> entity, String attribute) { return asEnum(Estivate.attribute(entity, attribute)); }
	public Enum asEnum(Entity<?> entity, String attribute) { return asEnum(Estivate.attribute(entity, attribute)); }
	public <T,R> Enum asEnum(AttributeGetter<T, R> attributeGetter) { return asEnum(Estivate.attribute(attributeGetter)); }
    

	public Long getCount() { return asLong("count(*)"); }
	public Long getCount(Class<? extends Object> c, String attribute) { return asLong("count("+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }	
	public Long getCountDistinct(Class<? extends Object> c, String attribute) { return asLong("count(distinct "+resultTable.context.nameMapper.toTableNameAttribute(c, attribute)+")"); }

	public static Boolean parseBoolean(String content){
		if(content == null){
			return null;
		}
		else if(content.equals("1") || content.equalsIgnoreCase("true")){
			return true;
		}
		else if(content.equals("0") || content.equalsIgnoreCase("false")){
			return false;
		}
		return null;
	}

}
