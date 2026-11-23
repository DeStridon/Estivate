package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.query.Attribute;
import com.estivate.query.Attribute.Function;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.util.EntityMetaCache;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
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
	@SuppressWarnings("unchecked")
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
	public String 			asString(Attribute attribute) { Integer index = indexOf(attribute); return index == null ? null : columnValues[index]; }
	// Numbers
	public Short 			asShort(String column) { String value = asString(column); return value == null ? null : Short.valueOf(value); } 
	public Short 			asShort(Attribute attribute) { String value = asString(attribute); return value == null ? null : Short.valueOf(value); }
	public Integer 			asInteger(String column) { String value = asString(column); return value == null ? null : Integer.valueOf(value); }
	public Integer 			asInteger(Attribute attribute) { String value = asString(attribute); return value == null ? null : Integer.valueOf(value); }
	public Long 			asLong(String column) { String value = asString(column); return value == null ? null : Long.valueOf(value); }	
	public Long 			asLong(Attribute attribute) { String value = asString(attribute); return value == null ? null : Long.valueOf(value); }
	public Float 			asFloat(String column) { String value = asString(column); return value == null ? null : Float.valueOf(value); }
	public Float 			asFloat(Attribute attribute) { String value = asString(attribute); return value == null ? null : Float.valueOf(value); }
	public Double 			asDouble(String column) { String value = asString(column); return value == null ? null : Double.valueOf(value); }
	public Double 			asDouble(Attribute attribute) { String value = asString(attribute); return value == null ? null : Double.valueOf(value); }
	public BigDecimal		asBigDecimal(String column) { String value = asString(column); return value == null ? null : new BigDecimal(value); }
	public BigDecimal		asBigDecimal(Attribute attribute) { String value = asString(attribute); return value == null ? null : new BigDecimal(value); }
	
	// Dates
	public LocalDateTime	asLocalDateTime(String column) { String value = asString(column); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDateTime	asLocalDateTime(Attribute attribute) { String value = asString(attribute); return value == null ? null : LocalDateTime.parse(value, DateMapper.formatter); }
	public LocalDate		asLocalDate(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : ldt.toLocalDate(); }
	public LocalDate		asLocalDate(Attribute attribute) { LocalDateTime ldt = asLocalDateTime(attribute); return ldt == null ? null : ldt.toLocalDate(); }
	public Instant			asInstant(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : ldt.atZone(resultTable.context.serverZoneId).toInstant(); }
	public Instant			asInstant(Attribute attribute) { LocalDateTime ldt = asLocalDateTime(attribute); return ldt == null ? null : ldt.atZone(resultTable.context.serverZoneId).toInstant(); }
	public Date 			asDate(String column) { LocalDateTime ldt = asLocalDateTime(column); return ldt == null ? null : Date.from(ldt.atZone(resultTable.context.serverZoneId).toInstant()); } 
	public Date 			asDate(Attribute attribute) { LocalDateTime ldt = asLocalDateTime(attribute); return ldt == null ? null : Date.from(ldt.atZone(resultTable.context.serverZoneId).toInstant()); } 

	public Boolean 			asBoolean(String column) {  return FieldUtils.parseBoolean(asString(column)); }
	public Boolean 			asBoolean(Attribute attribute) {  return FieldUtils.parseBoolean(asString(attribute)); }
	public Byte 			asByte(String column) { String value = asString(column); return value == null ? null : Byte.valueOf(value); }
	public Byte 			asByte(Attribute attribute) { String value = asString(attribute); return value == null ? null : Byte.valueOf(value); }
	public Character 		asChar(String column) { String value = asString(column); return value == null ? null : value.charAt(0); }
	public Character 		asChar(Attribute attribute) { String value = asString(attribute); return value == null ? null : value.charAt(0); }
	
	public <T extends Enum<T>> T asStringEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : Enum.valueOf(enumClass, value); }
	public <T extends Enum<T>> T asOrdinalEnum(String column, Class<T> enumClass) { String value = asString(column); return value == null ? null : enumClass.getEnumConstants()[asInteger(column)]; }
	

	private Integer indexOf(String column){
		return resultTable.indexOf(column);
	}

	private Integer indexOf(Attribute attribute){
		return resultTable.indexOf(attribute);
	}


	public Object as(Attribute attribute) {
		return as(attribute.getEntity(), attribute.getAttribute(), attribute.getFunction());
	}

	public <T> T as(Class<?> c, String attribute) {
		return as(c, attribute, null);
	}

	public <T> T as(Class<?> c, String attribute, Function function) {
		return as(new Entity<>(c), attribute, function);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public <T> T as(Entity<?> entity, String attribute, Function function) {

		Class<?> c = entity == null ? null : entity.entity;
		Class<?> returnType = function == null ? null : function.getReturnType();

		if(returnType == null){
			Field field = EntityMetaCache.findField(c, attribute);
			Type type = field.getGenericType();

			if(type == String.class) { return (T) asString(entity, attribute, function); }
			else if(type == boolean.class || type == Boolean.class) { return (T) asBoolean(entity, attribute, function);}
			else if(type == byte.class || type == Byte.class) { return (T) asByte(entity, attribute, function);}
			else if(type == short.class || type == Short.class) { return (T) asShort(entity, attribute, function);}
			else if(type == int.class || type == Integer.class) { return (T) asInteger(entity, attribute, function);}
			else if(type == long.class || type == Long.class) { return (T) asLong(entity, attribute, function);}
			else if(type == float.class || type == Float.class) { return (T) asFloat(entity, attribute, function);}
			else if(type == double.class || type == Double.class) { return (T) asDouble(entity, attribute, function);}
			else if(type == char.class || type == Character.class) { return (T) asChar(entity, attribute, function);}
			else if(type == Date.class) { return (T) asDate(entity, attribute, function);}
			else if(type == LocalDateTime.class) { return (T) asLocalDateTime(entity, attribute, function);}
			else if(type == Instant.class) { return (T) asInstant(entity, attribute, function);}
			else if(type == LocalDate.class) { return (T) asLocalDate(entity, attribute, function);}
			// @Convert (might be enum, this condition should be tested before classic enum)
			else if(FieldUtils.hasConvertAnnotation(field)) {
				return (T) EntityMetaCache.fieldBinding(field).toEntityAttribute(asString(entity, attribute));
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {

				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				String column = resultTable.context.nameMapper.toEntityNameAttribute(entity, attribute);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return asEnumFromString(column, (Class<?>) type);
				}
				else {
					return asEnumFromOrdinal(column, (Class<?>) type);
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				String column = resultTable.context.nameMapper.toEntityNameAttribute(entity, attribute);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return asEnumFromString(column, (Class<?>) type);
				}
				else {
					return asEnumFromOrdinal(column, (Class<?>) type);
				}
			}
			else {
				log.error("This type is not mapped yet : "+type);
			}
		}
		else if(returnType == Integer.class){ return (T) asInteger(entity, attribute, function); }
		else if(returnType == Long.class){ 	return (T) asLong(entity, attribute, function); }
		else if(returnType == Float.class){ 	return (T) asFloat(entity, attribute, function); }
		else if(returnType == Double.class){	return (T) asDouble(entity, attribute, function); }
		else if(returnType == String.class){ return (T) asString(entity, attribute, function); }
		else if(returnType == Character.class){ return (T) asChar(entity, attribute, function); }
		else if(returnType == Boolean.class){ return (T) asBoolean(entity, attribute, function); }
		else if(returnType == Byte.class){ return (T) asByte(entity, attribute, function); }
		else if(returnType == Short.class){ return (T) asShort(entity, attribute, function); }
		else if(returnType == Date.class){ return (T) asDate(entity, attribute, function); }
		else if(returnType == LocalDateTime.class){ return (T) asLocalDateTime(entity, attribute, function); }
		else if(returnType == Instant.class){ return (T) asInstant(entity, attribute, function); }
		else if(returnType == LocalDate.class){ return (T) asLocalDate(entity, attribute, function); }
		else {
			log.error("This type is not mapped yet : "+returnType);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T, E extends Enum<E>> T asEnumFromString(String column, Class<?> enumType) {
		return (T) asStringEnum(column, (Class<E>) enumType);
	}

	@SuppressWarnings("unchecked")
	private <T, E extends Enum<E>> T asEnumFromOrdinal(String column, Class<?> enumType) {
		return (T) asOrdinalEnum(column, (Class<E>) enumType);
	}

	public <T, R> R  as(@NonNull AttributeGetter<T, R> attributeGetter) {
		Attribute attribute = Estivate.attribute(attributeGetter);
		return as(attribute.getEntity().entity, attribute.getAttribute());
	}
	

	//public 		 String			asString		(Attribute attribute) { return asString(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public 		 String 		asString		(Class<?> c, String attribute) 	{ return asString(Estivate.attribute(c, attribute)); }
	public 		 String 		asString		(Class<?> c, String attribute, Function function) 	{ return asString(Estivate.attribute(c, attribute, function)); }
	public 		 String 		asString		(Entity<?> e, String attribute)	{ return asString(Estivate.attribute(e, attribute)); }
	public 		 String 		asString		(Entity<?> e, String attribute, Function function) 	{ return asString(Estivate.attribute(e, attribute, function)); }
	public <T,R> String 		asString		(@NonNull AttributeGetter<T, R> attributeGetter) { return asString(Estivate.attribute(attributeGetter)); }
	// Numbers
//	public Short 				asShort			(Attribute attribute) { return asShort(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Short 				asShort			(Class<?> c, String attribute) 	{ return asShort(Estivate.attribute(c, attribute)); }
	public Short 				asShort			(Class<?> c, String attribute, Function function) 	{ return asShort(Estivate.attribute(c, attribute, function)); }
	public Short 				asShort			(Entity<?> e, String attribute)	{ return asShort(Estivate.attribute(e, attribute)); }
	public Short 				asShort			(Entity<?> e, String attribute, Function function) 	{ return asShort(Estivate.attribute(e, attribute, function)); }
	public <T,R> Short 			asShort			(@NonNull AttributeGetter<T, R> attributeGetter) { return asShort(Estivate.attribute(attributeGetter)); }
//	public Integer 				asInteger		(Attribute attribute) { return asInteger(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Integer 				asInteger		(Class<?> c, String attribute) 	{ return asInteger(Estivate.attribute(c, attribute)); }
	public Integer 				asInteger		(Class<?> c, String attribute, Function function) 	{ return asInteger(Estivate.attribute(c, attribute, function)); }
	public Integer 				asInteger		(Entity<?> e, String attribute)	{ return asInteger(Estivate.attribute(e, attribute)); }
	public Integer 				asInteger		(Entity<?> e, String attribute, Function function) 	{ return asInteger(Estivate.attribute(e, attribute, function)); }
	public <T,R> Integer 		asInteger		(@NonNull AttributeGetter<T, R> attributeGetter) { return asInteger(Estivate.attribute(attributeGetter)); }
//	public Long 				asLong			(Attribute attribute) { return asLong(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Long 				asLong			(Class<?> c, String attribute) 	{ return asLong(Estivate.attribute(c, attribute)); }
	public Long 				asLong			(Class<?> c, String attribute, Function function) 	{ return asLong(Estivate.attribute(c, attribute, function)); }
	public Long 				asLong			(Entity<?> e, String attribute)	{ return asLong(Estivate.attribute(e, attribute)); }
	public Long 				asLong			(Entity<?> e, String attribute, Function function) 	{ return asLong(Estivate.attribute(e, attribute, function)); }
	public <T,R> Long 			asLong			(@NonNull AttributeGetter<T, R> attributeGetter) { return asLong(Estivate.attribute(attributeGetter)); }
//	public Float 				asFloat			(Attribute attribute) { return asFloat(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Float 				asFloat			(Class<?> c, String attribute) 	{ return asFloat(Estivate.attribute(c, attribute)); }
	public Float 				asFloat			(Class<?> c, String attribute, Function function) 	{ return asFloat(Estivate.attribute(c, attribute, function)); }
	public Float 				asFloat			(Entity<?> e, String attribute)	{ return asFloat(Estivate.attribute(e, attribute)); }
	public Float 				asFloat			(Entity<?> e, String attribute, Function function) 	{ return asFloat(Estivate.attribute(e, attribute, function)); }
	public <T,R> Float 			asFloat			(@NonNull AttributeGetter<T, R> attributeGetter) { return asFloat(Estivate.attribute(attributeGetter)); }
//	public Double 				asDouble		(Attribute attribute) { return asDouble(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Double 				asDouble		(Class<?> c, String attribute) 	{ return asDouble(Estivate.attribute(c, attribute)); }
	public Double 				asDouble		(Class<?> c, String attribute, Function function) 	{ return asDouble(Estivate.attribute(c, attribute, function)); }
	public Double 				asDouble		(Entity<?> e, String attribute)	{ return asDouble(Estivate.attribute(e, attribute)); }
	public Double 				asDouble		(Entity<?> e, String attribute, Function function) 	{ return asDouble(Estivate.attribute(e, attribute, function)); }
	public <T,R> Double 		asDouble		(@NonNull AttributeGetter<T, R> attributeGetter) { return asDouble(Estivate.attribute(attributeGetter)); }
//	public BigDecimal			asBigDecimal	(Attribute attribute) { return asBigDecimal(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public BigDecimal			asBigDecimal	(Class<?> c, String attribute) 	{ return asBigDecimal(Estivate.attribute(c, attribute)); }
	public BigDecimal			asBigDecimal	(Class<?> c, String attribute, Function function) 	{ return asBigDecimal(Estivate.attribute(c, attribute, function)); }
	public BigDecimal			asBigDecimal	(Entity<?> e, String attribute)	{ return asBigDecimal(Estivate.attribute(e, attribute)); }
	public BigDecimal			asBigDecimal	(Entity<?> e, String attribute, Function function) 	{ return asBigDecimal(Estivate.attribute(e, attribute, function)); }
	public <T,R> BigDecimal		asBigDecimal	(@NonNull AttributeGetter<T, R> attributeGetter) { return asBigDecimal(Estivate.attribute(attributeGetter)); }
	// Date
//	public Date 				asDate			(Attribute attribute) { return asDate(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Date 				asDate			(Class<?> c, String attribute)	{ return asDate(Estivate.attribute(c, attribute)); }
	public Date 				asDate			(Class<?> c, String attribute, Function function) 	{ return asDate(Estivate.attribute(c, attribute, function)); }
	public Date 				asDate			(Entity<?> e, String attribute)	{ return asDate(Estivate.attribute(e, attribute)); }
	public Date 				asDate			(Entity<?> e, String attribute, Function function) 	{ return asDate(Estivate.attribute(e, attribute, function)); }
	public <T,R> Date 			asDate			(@NonNull AttributeGetter<T, R> attributeGetter) { return asDate(Estivate.attribute(attributeGetter)); }
//	public LocalDateTime		asLocalDateTime	(Attribute attribute) { return asLocalDateTime(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public LocalDateTime		asLocalDateTime	(Class<?> c, String attribute)	{ return asLocalDateTime(Estivate.attribute(c, attribute)); }
	public LocalDateTime 		asLocalDateTime	(Class<?> c, String attribute, Function function) 	{ return asLocalDateTime(Estivate.attribute(c, attribute, function)); }
	public LocalDateTime 		asLocalDateTime	(Entity<?> e, String attribute) { return asLocalDateTime(Estivate.attribute(e, attribute)); }
	public LocalDateTime 		asLocalDateTime	(Entity<?> e, String attribute, Function function) 	{ return asLocalDateTime(Estivate.attribute(e, attribute, function)); }
	public <T,R> LocalDateTime 	asLocalDateTime	(@NonNull AttributeGetter<T, R> attributeGetter) { return asLocalDateTime(Estivate.attribute(attributeGetter)); }
//	public Instant				asInstant		(Attribute attribute) { return asInstant(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Instant				asInstant		(Class<?> c, String attribute)	{ return asInstant(Estivate.attribute(c, attribute)); }
	public Instant				asInstant		(Class<?> c, String attribute, Function function) 	{ return asInstant(Estivate.attribute(c, attribute, function)); }
	public Instant		 		asInstant		(Entity<?> e, String attribute) { return asInstant(Estivate.attribute(e, attribute)); }
	public Instant				asInstant		(Entity<?> e, String attribute, Function function) 	{ return asInstant(Estivate.attribute(e, attribute, function)); }
	public <T,R> Instant 		asInstant		(@NonNull AttributeGetter<T, R> attributeGetter) { return asInstant(Estivate.attribute(attributeGetter)); }
//	public LocalDate			asLocalDate		(Attribute attribute) { return asLocalDate(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public LocalDate			asLocalDate		(Class<?> c, String attribute)	{ return asLocalDate(Estivate.attribute(c, attribute)); }
	public LocalDate			asLocalDate		(Class<?> c, String attribute, Function function) 	{ return asLocalDate(Estivate.attribute(c, attribute, function)); }
	public LocalDate	 		asLocalDate		(Entity<?> e, String attribute) { return asLocalDate(Estivate.attribute(e, attribute)); }
	public LocalDate	 		asLocalDate		(Entity<?> e, String attribute, Function function) 	{ return asLocalDate(Estivate.attribute(e, attribute, function)); }
	public <T,R> LocalDate 		asLocalDate		(@NonNull AttributeGetter<T, R> attributeGetter) { return asLocalDate(Estivate.attribute(attributeGetter)); }
	// Other
//	public Boolean 				asBoolean		(Attribute attribute) { return asBoolean(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Boolean 				asBoolean		(Class<?> c, String attribute) 	{ return asBoolean(Estivate.attribute(c, attribute)); }
	public Boolean 				asBoolean		(Class<?> c, String attribute, Function function) 	{ return asBoolean(Estivate.attribute(c, attribute, function)); }
	public Boolean 				asBoolean		(Entity<?> e, String attribute)	{ return asBoolean(Estivate.attribute(e, attribute)); }
	public Boolean 				asBoolean		(Entity<?> e, String attribute, Function function) 	{ return asBoolean(Estivate.attribute(e, attribute, function)); }
	public <T,R> Boolean 		asBoolean		(@NonNull AttributeGetter<T, R> attributeGetter) { return asBoolean(Estivate.attribute(attributeGetter)); }
//	public Byte					asByte			(Attribute attribute) { return asByte(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Byte					asByte			(Class<?> c, String attribute) 	{ return asByte(Estivate.attribute(c, attribute)); }
	public Byte					asByte			(Class<?> c, String attribute, Function function) 	{ return asByte(Estivate.attribute(c, attribute, function)); }
	public Byte					asByte			(Entity<?> e, String attribute) { return asByte(Estivate.attribute(e, attribute)); }
	public Byte					asByte			(Entity<?> e, String attribute, Function function) 	{ return asByte(Estivate.attribute(e, attribute, function)); }
	public <T,R> Byte 			asByte			(@NonNull AttributeGetter<T, R> attributeGetter) { return asByte(Estivate.attribute(attributeGetter)); }
//	public Character			asChar			(Attribute attribute) { return asChar(resultTable.context.nameMapper.toEntityNameAttribute(attribute.getEntity(), attribute.getAttribute())); }
	public Character			asChar			(Class<?> c, String attribute)  					{ return asChar(Estivate.attribute(c, attribute)); }
	public Character			asChar			(Class<?> c, String attribute, Function function) 	{ return asChar(Estivate.attribute(c, attribute, function)); }
	public Character			asChar			(Entity<?> e, String attribute)						{ return asChar(Estivate.attribute(e, attribute)); }
	public Character			asChar			(Entity<?> e, String attribute, Function function) 	{ return asChar(Estivate.attribute(e, attribute, function)); }
	public <T,R> Character 		asChar			(@NonNull AttributeGetter<T, R> attributeGetter) { return asChar(Estivate.attribute(attributeGetter)); }
	
	

	// @Enumerated
	public Enum<?> asEnum(Attribute attribute) {
		try {
			Field field = attribute.getEntity().entity.getDeclaredField(attribute.getAttribute());
			Type type = field.getGenericType();

			if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {

				javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
					return enumValueOf((Class<?>) type, asString(attribute));
				}
				else {
					return (Enum<?>) field.getType().getEnumConstants()[asInteger(attribute)];
				}
			}
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return enumValueOf((Class<?>) type, asString(attribute));
				}
				else {
					return (Enum<?>) field.getType().getEnumConstants()[asInteger(attribute)];
				}
			}
		}
		catch(Exception e) {
			log.error("Didn't manage to mapAsEnum", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <E extends Enum<E>> Enum<?> enumValueOf(Class<?> enumType, String value) {
		return Enum.valueOf((Class<E>) enumType, value);
	}

	public Enum<?> asEnum(Class<?> entity, String attribute) { return asEnum(Estivate.attribute(entity, attribute)); }
	public Enum<?> asEnum(Entity<?> entity, String attribute) { return asEnum(Estivate.attribute(entity, attribute)); }
	public <T,R> Enum<?> asEnum(@NonNull AttributeGetter<T, R> attributeGetter) { return asEnum(Estivate.attribute(attributeGetter)); }
    

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
