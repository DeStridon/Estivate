package com.estivate.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.NameMapper;
//github.com/DeStridon/Estivate.git
import com.estivate.util.Chronometer;
import com.estivate.util.EstivateException;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Data
public abstract class IMapper<U> {

	NameMapper nameMapper;
	String[] columnNames;
	
	
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
			return new Result(row, columnNames, nameMapper);
		}
		
	}
 

}
