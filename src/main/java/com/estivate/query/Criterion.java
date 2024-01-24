package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Query.Entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Criterion implements EstivateNode{
	
	public Entity entity;
	public String attribute;
	
	public abstract Criterion clone();

		
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Operator extends Criterion{
		
		public enum CriterionType{
			Eq("="),
			NotEq("!="),
			Lt("<"),
			Lte("<="),
			Gt(">"),
			Gte(">="),
			Like("like"),
			NotLike("not like");
			
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
			this.value = value;
		}

//		@Override
//		public String compile() {
//			
//			StringPipe sb = new StringPipe().separator(" ")
//					.append(entity.getName() + "." + Query.nameMapper.mapAttribute(attribute))
//					.append(type.symbol)
//					.append(EstivateUtil.compileAttribute(entity.entity, attribute, value));
//
//			return sb.toString();
//			
//		}
		
		public Operator clone() {
			Operator op = new Operator();
			op.entity = entity;
			op.attribute = attribute;
			op.type = type;
			op.value = value;
			return op;
		}

	}
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class In extends Criterion {
		
		@Getter
		List<Object> values;

		public In(Entity entity, String attribute, Collection<Object> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = new ArrayList<>(values) ; 
		}
		
		public In clone() {
			In in = new In();
			in.entity = entity;
			in.attribute = attribute;
			in.values = values.stream().collect(Collectors.toList());
			return in;
		}

	}
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NotIn extends Criterion {
		
		@Getter
		List<Object> values;

		public NotIn(Entity entity, String attribute, Collection<Object> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = new ArrayList<>(values) ; 
		}
		
		public NotIn clone() {
			NotIn in = new NotIn();
			in.entity = entity;
			in.attribute = attribute;
			in.values = values.stream().collect(Collectors.toList());
			return in;
		}

	}
	
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Between extends Criterion{

		public Long min;
		public Long max;
		
		public Between(Entity entity, String attribute, long min, long max) {
			this.entity = entity;
			this.attribute = attribute;
			this.min = min;
			this.max = max;
		}
		
//		@Override
//		public String compile() {
//			return entity.getName()+"."+ Query.nameMapper.mapAttribute(attribute)+" between "+min+" and "+max;
//		}
		
		public Between clone() {
			Between b = new Between();
			b.entity = entity;
			b.attribute = attribute;
			b.min = min;
			b.max = max;
			
			return b;
		}


	}
	
	
	public static class NullCheck extends Criterion{
		public boolean isNull;
		
		public NullCheck(Entity entity, String attribute, boolean isNull) {
			this.entity = entity;
			this.attribute = attribute;
			this.isNull = isNull;
		}
		
//		@Override
//		public String compile() {
//			return entity.getName() + "." + Query.nameMapper.mapAttribute(attribute)+(isNull ? " is null":" is not null");
//		}
		
		public NullCheck clone() {
			return new NullCheck(entity, attribute, isNull);
		}



	}

}
