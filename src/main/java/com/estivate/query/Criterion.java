package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Criterion extends Attribute implements EstivateNode{

	
	public abstract Criterion clone();
		
	
	@ToString(callSuper = true)
	public static class Operator extends Criterion{
		
		public enum OperatorType{
			Eq("="),
			NotEq("!="),
			Lt("<"),
			Lte("<="),
			Gt(">"),
			Gte(">="),
			Like("like"),
			NotLike("not like");
			
			public String symbol;
			
			OperatorType(String symbol) {
				this.symbol = symbol;
			}
		}
		
		public OperatorType type;

		public Object value;
		
		public Operator(Entity<?> entity, String attribute, Function function, OperatorType type, Object value) {
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.type = type;
			this.value = value;
		}

		public Operator(Attribute attribute, OperatorType type, Object value) {
			this(attribute.entity, attribute.attribute, attribute.function, type, value);
		}

		public Operator clone() { return new Operator(entity, attribute, function, type, value); }

	}
	
	
	@ToString(callSuper = true)
	public static class In extends Criterion {
		
		@Getter
		List<?> values;

		public In(Entity<?> entity, String attribute, Function function, Collection<?> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.values = new ArrayList<>(values) ; 
		}

		public In(Attribute attribute, Collection<?> values) {
			this(attribute.entity, attribute.attribute, attribute.function, values);
		}
		
		public In clone() { return new In(entity, attribute, function, values.stream().collect(Collectors.toList())); }

	}
	
	
	@ToString(callSuper = true)
	public static class NotIn extends Criterion {
		
		@Getter
		List<?> values;

		public NotIn(Entity<?> entity, String attribute, Function function, Collection<?> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = new ArrayList<>(values);
		}

		public NotIn(Attribute attribute, Collection<?> values) {
			this(attribute.entity, attribute.attribute, attribute.function, values);
		}

		public NotIn clone() { return new NotIn(entity, attribute, function, values.stream().collect(Collectors.toList())); }

	}
	
	
	@ToString(callSuper = true)
	public static class Between extends Criterion{

		public Object min;
		public Object max;
		
		public Between(Entity<?> entity, String attribute, Function function, Object min, Object max) {
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.min = min;
			this.max = max;
		}

		public Between(Attribute attribute, Object min, Object max) {
			this.entity = attribute.entity;
			this.attribute = attribute.attribute;
			this.min = min;
			this.max = max;
		}
		
		public Between clone() { return new Between(entity, attribute, function, min, max); }
	}
	
	
	@ToString(callSuper = true)
	public static class NullCheck extends Criterion{
		
		public boolean isNull;
		
		public NullCheck(Entity<?> entity, String attribute, Function function, boolean isNull) {
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.isNull = isNull;
		}

		public NullCheck(Attribute attribute, boolean isNull) {
			this(attribute.entity, attribute.attribute, attribute.function, isNull);
		}

		public NullCheck clone() { return new NullCheck(entity, attribute, function, isNull); }

	}
	
	
	// MatchAgainst Criterion
	@ToString(callSuper = true)
	public static class MatchAgainst extends Criterion{
		
		public Collection<String> attributes;
		public Object value;
		public boolean inclusive;
		
		public MatchAgainst(Entity<?> entity, Collection<String> attributes, Function function, Object value, boolean inclusive) {
			this.entity = entity;
			this.attributes = attributes;
			this.function = function;
			this.value = value;
			this.inclusive = inclusive;
		}

		public MatchAgainst(Attribute attribute, Object value, boolean inclusive) {
			this(attribute.entity, Arrays.asList(attribute.attribute), attribute.function, value, inclusive);
		}
		
		public MatchAgainst clone() { return new MatchAgainst(entity, attributes, function, value, inclusive); } 
		
	}
	
	
	@ToString(callSuper = true)
	public static class NativeCriterion extends Criterion{
		
		public String criterion;

		public NativeCriterion(Entity<?> entity, String attribute, Function function, String criterion) {
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.criterion = criterion;
		}

		public NativeCriterion(Attribute attribute, String criterion) {
			this(attribute.entity, attribute.attribute, attribute.function, criterion);
		}
		
		public NativeCriterion clone() { return new NativeCriterion(entity, attribute, function, criterion); } 
		
	}

	
	@ToString(callSuper = true)
	public static class InSubQuery extends Criterion{

		public SelectQuery<?> subQuery;
		public boolean include;

		public InSubQuery(Entity<?> entity, String attribute, Function function, SelectQuery<?> subQuery, boolean include){
			this.entity = entity;
			this.attribute = attribute;
			this.function = function;
			this.subQuery = subQuery;
			this.include = include;
		}

		public InSubQuery(Attribute attribute, SelectQuery subQuery, boolean include){
			this(attribute.entity, attribute.attribute, attribute.function, subQuery, include);
		}

		public InSubQuery clone() {return new InSubQuery(entity, attribute, function, subQuery, include);}

	}

	
	@ToString(callSuper = true)
	public static class ExistsSubQuery extends Criterion{

		public SelectQuery subQuery;
		public boolean include;

		public ExistsSubQuery(SelectQuery subQuery, boolean include){
			this.subQuery = subQuery;
			this.include = include;
		}

		public ExistsSubQuery clone(){return new ExistsSubQuery(subQuery, include);}

	}

	

}
