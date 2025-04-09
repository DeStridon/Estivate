package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Query.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Criterion implements EstivateNode{
	
	public Entity<?> entity;
	public String attribute;
	public List<Attribute.Function> functions = new ArrayList<>();
	
	public abstract Criterion clone();
	

	
	
	
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
		
		public Operator(Entity<?> entity, String attribute, OperatorType type, Object value) {
			this.entity = entity;
			this.attribute = attribute;
			this.type = type;
			this.value = value;
		}

		public Operator(Attribute attribute, OperatorType type, Object value) {
			this(attribute.entity, attribute.attribute, type, value);
		}

		public Operator clone() { return new Operator(entity, attribute, type, value); }

	}
	
	
	public static class In extends Criterion {
		
		@Getter
		List<?> values;

		public In(Entity<?> entity, String attribute, Collection<?> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = new ArrayList<>(values) ; 
		}

		public In(Attribute attribute, Collection<?> values) {
			this(attribute.entity, attribute.attribute, values);
		}
		
		public In clone() { return new In(entity, attribute, values.stream().collect(Collectors.toList())); }

	}
	
	
	public static class NotIn extends Criterion {
		
		@Getter
		List<?> values;

		public NotIn(Entity<?> entity, String attribute, Collection<?> values) {
			this.entity = entity;
			this.attribute = attribute;
			this.values = new ArrayList<>(values) ; 
		}

		public NotIn(Attribute attribute, Collection<?> values) {
			this(attribute.entity, attribute.attribute, values);
		}

		public NotIn clone() { return new NotIn(entity, attribute, values.stream().collect(Collectors.toList())); }

	}
	
	
	public static class Between extends Criterion{

		public Object min;
		public Object max;
		
		public Between(Entity<?> entity, String attribute, Object min, Object max) {
			this.entity = entity;
			this.attribute = attribute;
			this.min = min;
			this.max = max;
		}

		public Between(Attribute attribute, Object min, Object max) {
			this.entity = attribute.entity;
			this.attribute = attribute.attribute;
			this.min = min;
			this.max = max;
		}
		
		public Between clone() { return new Between(entity, attribute, min, max); }
	}
	
	
	public static class NullCheck extends Criterion{
		
		public boolean isNull;
		
		public NullCheck(Entity<?> entity, String attribute, boolean isNull) {
			this.entity = entity;
			this.attribute = attribute;
			this.isNull = isNull;
		}

		public NullCheck(Attribute attribute, boolean isNull) {
			this(attribute.entity, attribute.attribute, isNull);
		}

		public NullCheck clone() { return new NullCheck(entity, attribute, isNull); }

	}
	
	
	// MatchAgainst Criterion
	public static class MatchAgainst extends Criterion{
		
		public List<String> attributes;
		public Object value;
		public boolean inclusive;
		
		public MatchAgainst(Entity<?> entity, List<String> attributes, Object value, boolean inclusive) {
			this.entity = entity;
			this.attributes = attributes;
			this.value = value;
			this.inclusive = inclusive;
		}

		public MatchAgainst(Attribute attribute, Object value, boolean inclusive) {
			this(attribute.entity, Arrays.asList(attribute.attribute), value, inclusive);
		}
		
		public MatchAgainst clone() { return new MatchAgainst(entity, attributes, value, inclusive); } 
		
	}
	
	public static class NativeCriterion extends Criterion{
		
		public String criterion;

		public NativeCriterion(Entity<?> entity, String attribute, String criterion) {
			this.entity = entity;
			this.attribute = attribute;
			this.criterion = criterion;
		}

		public NativeCriterion(Attribute attribute, String criterion) {
			this(attribute.entity, attribute.attribute, criterion);
		}
		
		public NativeCriterion clone() { return new NativeCriterion(entity, attribute, criterion); } 
		
	}

	public static class InSubQuery extends Criterion{

		public Query subQuery;
		public boolean include;

		public InSubQuery(Entity<?> entity, String attribute, Query subQuery, boolean include){
			this.entity = entity;
			this.attribute = attribute;
			this.subQuery = subQuery;
			this.include = include;
		}

		public InSubQuery(Attribute attribute, Query subQuery, boolean include){
			this(attribute.entity, attribute.attribute, subQuery, include);
		}

		public InSubQuery clone() {return new InSubQuery(entity, attribute, subQuery, include);}

	}

	public static class ExistsSubQuery extends Criterion{

		public Query subQuery;
		public boolean include;

		public ExistsSubQuery(Query subQuery, boolean include){
			this.subQuery = subQuery;
			this.include = include;
		}

		public ExistsSubQuery clone(){return new ExistsSubQuery(subQuery, include);}


	}

	

}
