package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.NameMapper;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Data
public abstract class IMapper<U> {

	NameMapper nameMapper;
	String[] resultColumnNames;
	
	
	abstract public U map(String[] row);
	
	
	


	public static class StringMapper extends IMapper<String>{ public String map(String[] row) { return row[0]; } }
	public static class IntegerMapper extends IMapper<Integer>{ public Integer map(String[] row){ if(row[0] == null) return null; return Integer.parseInt(row[0]); } }
	public static class FloatMapper extends IMapper<Float>{ public Float map(String[] row) { if(row[0] == null) return null; return Float.parseFloat(row[0]); } }
	public static class DoubleMapper extends IMapper<Double>{ public Double map(String[] row) { if(row[0] == null) return null; return Double.parseDouble(row[0]); } }
	public static class LongMapper extends IMapper<Long>{ public Long map(String[] row) { if(row[0] == null) return null; return Long.parseLong(row[0]); } }
	public static class ShortMapper extends IMapper<Short>{ public Short map(String[] row) { if(row[0] == null) return null; return Short.parseShort(row[0]); } }
	public static class BooleanMapper extends IMapper<Boolean>{ public Boolean map(String[] row) { if(row[0] == null) return null; return Boolean.parseBoolean(row[0]); } }

	public static class LocalDateTimeMapper extends IMapper<LocalDateTime> { public LocalDateTime map(String[] row) { if(row[0]==null) return null; return LocalDateTime.parse(row[0], DateMapper.formatter); }}
	public static class DateMapper extends IMapper<Date> { 

		static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]");
	
		
		public Date map(String[] row) { 
			if(row[0] == null) { return null; } 
			LocalDateTime ldt = LocalDateTime.parse(row[0], formatter);
			return Date.from(ldt.atZone(ZoneOffset.systemDefault()).toInstant());
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
		public U map(String[] row) {
			if(row[0] == null) {
				return null;
			}
			return (U) Enum.valueOf((Class<U>) enumClass, row[0]);
		}
	}

	@AllArgsConstructor
	public static class OrdinalEnumMapper<U extends Enum<U>> extends IMapper<U>{

		final Class<U> enumClass;

		@Override
		public U map(String[] row) {
			if(row[0] == null) {
				return null;
			}
			return (U) enumClass.getEnumConstants()[Integer.parseInt(row[0])];
		}
	}

	
	public static class ResultMapper extends IMapper<Result>{

		@Override
		public Result map(String[] row) {
			return new Result(row, resultColumnNames, nameMapper);
		}
		
	}

	@Slf4j
	@AllArgsConstructor
	public static class AttributeMapper extends IMapper<Object>{

		final Field field;
		final Type type;

		public AttributeMapper(Class entity, String attributeName) {
			field = FieldUtils.findField(entity, attributeName);
			type = field.getGenericType();
		}
 
		@Override
		@SneakyThrows
		public Object map(String[] row) {
			
			// @Convert
			if(field.getDeclaredAnnotation(Convert.class) != null) {
                Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
                Object converter = convertAnnotation.converter().getConstructor().newInstance();
                if(!(converter instanceof AttributeConverter)) {
                    log.error("Cannot convert with converter "+converter.getClass());
                    return null;
                }
                AttributeConverter attributeConverter = (AttributeConverter) converter;
                Object attributeValue = attributeConverter.convertToEntityAttribute(row[0]);
                return attributeValue;
            }

			if(type == String.class) { return row[0]; }
			if(type == boolean.class || type == Boolean.class) { return Boolean.parseBoolean(row[0]); }
			if(type == byte.class || type == Byte.class) { return Byte.parseByte(row[0]);}
			if(type == short.class || type == Short.class) { return Short.parseShort(row[0]);}
			if(type == int.class || type == Integer.class) { return Integer.parseInt(row[0]);}
			if(type == long.class || type == Long.class) { return Long.parseLong(row[0]);}
			if(type == float.class || type == Float.class) { return Float.parseFloat(row[0]);}
			if(type == double.class || type == Double.class) { return Double.parseDouble(row[0]);}
			if(type == BigDecimal.class) { return new BigDecimal(row[0]);}
			if(type == Date.class) { LocalDateTime dateTime = DateMapper.mapDate(row[0]); return Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant()); }
			if(type == LocalDateTime.class) { return LocalDateTime.parse(row[0], DateMapper.formatter);}
			if(type == LocalDate.class) { return LocalDate.parse(row[0], DateMapper.formatter);}
			if(type == Character.class) { return row[0].charAt(0); }
			
			// @Enumerated
            if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {
    
                Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
                if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
                    return Enum.valueOf((Class)type, row[0]);
                }
                else {
                    int ordinal = Integer.parseInt(row[0]);
                    return field.getType().getEnumConstants()[ordinal];
                }
            }

			log.error("This type is not mapped yet : "+type);
			return null;
		}
	
	}

}
