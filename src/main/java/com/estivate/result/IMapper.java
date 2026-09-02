package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import com.estivate.context.Context;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Data
public abstract class IMapper<U> {
	
	abstract public U map(String row);

	public static class StringMapper extends IMapper<String>{ public String map(String row) { return row; } }
	public static class IntegerMapper extends IMapper<Integer>{ public Integer map(String row){ if(row == null) return null; return Integer.parseInt(row); } }
	public static class FloatMapper extends IMapper<Float>{ public Float map(String row) { if(row == null) return null; return Float.parseFloat(row); } }
	public static class DoubleMapper extends IMapper<Double>{ public Double map(String row) { if(row == null) return null; return Double.parseDouble(row); } }
	public static class LongMapper extends IMapper<Long>{ public Long map(String row) { if(row == null) return null; return Long.parseLong(row); } }
	public static class ShortMapper extends IMapper<Short>{ public Short map(String row) { if(row == null) return null; return Short.parseShort(row); } }
	public static class BooleanMapper extends IMapper<Boolean>{ public Boolean map(String row) { if(row == null) return null; return Boolean.parseBoolean(row); } }

	public static class LocalDateTimeMapper extends IMapper<LocalDateTime> { public LocalDateTime map(String row) { if(row==null) return null; return LocalDateTime.parse(row, DateMapper.formatter); }}
	public static class InstantMapper extends IMapper<Instant> {
		final Context context;
		public InstantMapper(Context context) {
			this.context = context;
		}

		public Instant map(String row) {
			if (row == null) return null;
			LocalDateTime ldt = DateMapper.mapDate(row);
			return ldt.atZone(context.serverZoneId).toInstant();
		}
	}
	public static class DateMapper extends IMapper<Date> { 

		final Context context;
		public DateMapper(Context context) {
			this.context = context;
		}

		static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]");
	
		
		public Date map(String row) { 
			if(row == null) { return null; } 
			LocalDateTime ldt = LocalDateTime.parse(row, formatter);
			return Date.from(ldt.atZone(context.serverZoneId).toInstant());
		}	

		static LocalDateTime mapDate(String value) {
			TemporalAccessor temporalAccessor = formatter.parseBest(value, LocalDateTime::from, LocalDate::from);
			if (temporalAccessor instanceof LocalDateTime) {
				return (LocalDateTime)temporalAccessor;
			}
			else {
				return ((LocalDate)temporalAccessor).atStartOfDay();
			}
		}

	}

	@AllArgsConstructor
	public static class StringEnumMapper<U extends Enum<U>> extends IMapper<U>{
		
		final Class<U> enumClass;

		@Override
		public U map(String row) {
			if(row == null) {
				return null;
			}
			return (U) Enum.valueOf((Class<U>) enumClass, row);
		}
	}

	@AllArgsConstructor
	public static class OrdinalEnumMapper<U extends Enum<U>> extends IMapper<U>{

		final Class<U> enumClass;

		@Override
		public U map(String row) {
			if(row == null) {
				return null;
			}
			return (U) enumClass.getEnumConstants()[Integer.parseInt(row)];
		}
	}

	
	// public static class ResultMapper extends IMapper<ResultRow>{

	// 	String[] columnNames = null;

	// 	final Context context;
	// 	final SelectQuery<?> query;

	// 	public ResultMapper(Context context, SelectQuery<?> query) {
	// 		columnNames = new String[query.getSelects().size()];

	// 		this.context = context;
	// 		this.query = query;

	// 		List<Attribute> attributes = new ArrayList<>(query.getSelects());
	// 		for(int i = 0; i < attributes.size(); i++) {
	// 			Attribute attribute = attributes.get(i);
	// 			if(attribute.alias != null) {
	// 				columnNames[i] = attribute.alias;
	// 			}
	// 			else if(attribute.function != null) {

	// 				columnNames[i] = attribute.function.render( context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute));
	// 			}
	// 			else {
	// 				columnNames[i] = context.nameMapper.toTableNameAttribute(attributes.get(i).entity, attributes.get(i).attribute);
	// 			}
	// 		}
	// 	}

	// 	@Override
	// 	public ResultRow map(String[] row) {
	// 		return new ResultRow(row, columnNames, context, query);
	// 	}
		
	// }

	@Slf4j
	@AllArgsConstructor
	public static class AttributeMapper extends IMapper<Object>{

		final Context context;
		final Field field;
		final Type type;
		
		// final Attribute attribute;
		// final SelectQuery<?> query;

		public AttributeMapper(Context context, Class<?> entity, String attributeName) {
			this.context = context;
			this.field = FieldUtils.findField(entity, attributeName);
			this.type = field.getGenericType();
		}

		// public AttributeMapper(SelectQuery<?> query, Attribute attribute) {
		// 	field = FieldUtils.findField(attribute.entity.entity, attribute.attribute);
		// 	type = field.getGenericType();
		// 	attribute = attribute;
		// }
 
		@Override
		@SneakyThrows
		public Object map(String row) {
			
			// @Convert
			if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
				javax.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(javax.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof javax.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				javax.persistence.AttributeConverter attributeConverter = (javax.persistence.AttributeConverter) converter;
				Object attributeValue = attributeConverter.convertToEntityAttribute(row);
				return attributeValue;
			}
			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof jakarta.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return null;
				}
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				Object attributeValue = attributeConverter.convertToEntityAttribute(row);
				return attributeValue;
			}

			if(type == String.class) { return row; }
			if(type == boolean.class || type == Boolean.class) { return Boolean.parseBoolean(row); }
			if(type == byte.class || type == Byte.class) { return Byte.parseByte(row); }
			if(type == short.class || type == Short.class) { return Short.parseShort(row); }
			if(type == int.class || type == Integer.class) { return Integer.parseInt(row); }
			if(type == long.class || type == Long.class) { return Long.parseLong(row); }
			if(type == float.class || type == Float.class) { return Float.parseFloat(row); }
			if(type == double.class || type == Double.class) { return Double.parseDouble(row); }
			if(type == BigDecimal.class) { return FieldUtils.scaleBigDecimal(field, new BigDecimal(row)); }
			if(type == Date.class) { LocalDateTime dateTime = DateMapper.mapDate(row); return Date.from(dateTime.atZone(context.serverZoneId).toInstant()); }
			if(type == LocalDateTime.class) { return LocalDateTime.parse(row, DateMapper.formatter); }
			if(type == Instant.class) { LocalDateTime dateTime = DateMapper.mapDate(row); return dateTime.atZone(context.serverZoneId).toInstant(); }
			if(type == LocalDate.class) { return LocalDate.parse(row, DateMapper.formatter);}
			if(type == Character.class) { return row.charAt(0); }
			
			// @Enumerated
            if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
    
                javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
                if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
                    return Enum.valueOf((Class)type, row);
                }
                else {
                    int ordinal = Integer.parseInt(row);
                    return field.getType().getEnumConstants()[ordinal];
                }
            }
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					return Enum.valueOf((Class)type, row);
				}
				else {
					int ordinal = Integer.parseInt(row);
					return field.getType().getEnumConstants()[ordinal];
				}
			}
			
			log.error("This type is not mapped yet : "+type);
			return null;
		}
	
	}



}
