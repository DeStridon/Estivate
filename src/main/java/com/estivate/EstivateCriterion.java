package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.EstivateQuery.Entity;
import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class EstivateCriterion implements EstivateNode{
	
	public Entity entity;
	public String attribute;
	
	public abstract String compile();
	
	public abstract EstivateCriterion clone();
	
//	private static String compileValue(Class entity, String attribute, Object value) {
//		try {
//			Field field = entity.getDeclaredField(attribute);
//
//			Type fieldType = field.getType();
//
//			if(fieldType instanceof Number ||
//				fieldType == String.class ||
//				fieldType == boolean.class ||
//				fieldType == Boolean.class ||
//				fieldType == Long.class) {
//				return compileGenericType(value);
//			}
//
//			else if(field.getDeclaredAnnotation(Convert.class) != null) {
//				Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
//				Object converter = convertAnnotation.converter().getConstructor().newInstance();
//				if(converter instanceof AttributeConverter) {
//					AttributeConverter attributeConverter = (AttributeConverter) converter;
//					Object convertedValue = attributeConverter.convertToDatabaseColumn(value);
//					return compileGenericType(convertedValue);
//				}
//			}
//			else if(fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
//
//				if(field.getDeclaredAnnotation(Enumerated.class) != null && field.getDeclaredAnnotation(Enumerated.class).value() != null && field.getDeclaredAnnotation(Enumerated.class).value() == EnumType.STRING) {
//					return compileGenericType(value.toString());
//				}
//				else {
//					return compileGenericType(((Enum) value).ordinal());
//				}
//			}
//		}
//		catch(Exception e) {
//			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute);
//			e.printStackTrace();
//		}
//
//		log.warn("Could not determine type of field "+entity.getSimpleName()+"."+attribute);
//
//		return compileGenericType(value);
//
//	}

	public String compileName() {
		return entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(attribute);
	}


	
		
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Operator extends EstivateCriterion{
		
		public static enum CriterionType{
			Eq("="),
			NotEq("!="),
			Lt("<"),
			Lte("<="),
			Gt(">"),
			Gte(">="),
			Like("like");
			
			public String symbol;
			
			CriterionType(String symbol) {
				this.symbol = symbol;
			}
		}
		
		public CriterionType type;

		public Object value;
		
		public Operator(Entity entity, String attribute, CriterionType type, Object value) {
			this.entity = entity;
			this.attribute = attribute;
			this.type = type;
			this.value = EstivateUtil.compileObject(entity.entity, attribute, value);
		}

		@Override
		public String compile() {
			
			StringPipe sb = new StringPipe().separator(" ")
					.append(entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(attribute))
					.append(type.symbol)
					.append(EstivateUtil.compileGenericType(value));

			return sb.toString();
			
		}
		
		public Operator clone() {
			Operator op = new Operator();
			op.entity = entity;
			op.attribute = attribute;
			op.type = type;
			op.value = value;
			return op;
		}

		@Override
		public EstivateStatement preparedStatement() {

			EstivateStatement jqps = new EstivateStatement();
			jqps.query = new StringPipe().separator(" ")
					.append(entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(attribute))
					.append(type.symbol)
					.append("?").toString();
			jqps.parameters.add(EstivateUtil.compileGenericType(value));

			return jqps;

		}

		
		
	}
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class In extends EstivateCriterion {
		
		List<Object> values;

		public In(Entity entity, String attribute, Collection<Object> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = values.stream().map(x -> EstivateUtil.compileObject(entity.entity, attribute, x)).toList();
		}
		
		@Override
		public String compile() {
			if(values.size() == 0) {
				return null;
			}
			return entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(attribute)+" in "+values.stream().map(x -> EstivateUtil.compileGenericType(x)).collect(Collectors.joining(",\n ", "(", ")"));
		}
		
		public In clone() {
			In in = new In();
			in.entity = entity;
			in.attribute = attribute;
			in.values = values.stream().toList();
			return in;
		}

		@Override
		public EstivateStatement preparedStatement() {
			if(values.size() == 0) {
				return null;
			}
			EstivateStatement jqps = new EstivateStatement();
			jqps.query = entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(attribute)+" in "+values.stream().map(x -> "?").collect(Collectors.joining(",\n ", "(", ")"));
			jqps.parameters = values;
			return jqps;
		}

	}
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Between extends EstivateCriterion{

		public Long min;
		public Long max;
		
		public Between(Entity entity, String attribute, long min, long max) {
			this.entity = entity;
			this.attribute = attribute;
			this.min = min;
			this.max = max;
		}
		
		@Override
		public String compile() {
			return entity.getName()+"."+ EstivateQuery.nameMapper.mapAttribute(attribute)+" between "+min+" and "+max;
		}
		
		public Between clone() {
			Between b = new Between();
			b.entity = entity;
			b.attribute = attribute;
			b.min = min;
			b.max = max;
			
			return b;
		}

		@Override
		public EstivateStatement preparedStatement() {
			EstivateStatement jqps = new EstivateStatement();
			jqps.query = entity.getName()+"."+ EstivateQuery.nameMapper.mapAttribute(attribute)+" between ? and ?";
			jqps.parameters.add(min);
			jqps.parameters.add(max);
			return jqps;
		}

		

	}
	
	
	public static class NullCheck extends EstivateCriterion{
		public boolean isNull;
		
		public NullCheck(Entity entity, String attribute, boolean isNull) {
			this.entity = entity;
			this.attribute = attribute;
			this.isNull = isNull;
		}
		
		@Override
		public String compile() {
			return entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(attribute)+(isNull ? " is null":" is not null");
		}
		
		public NullCheck clone() {
			return new NullCheck(entity, attribute, isNull);
		}

		@Override
		public EstivateStatement preparedStatement() {
			EstivateStatement jqps = new EstivateStatement();
			jqps.query = entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(attribute)+(isNull ? " is null":" is not null");
			return jqps;
		}

	}

}
