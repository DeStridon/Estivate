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
public abstract class Criterion implements EstivateNode{

	
	public abstract Criterion clone();

	@Override
	public boolean isEmpty() { return false; }

	public Attribute attribute;
	
		
	
	@ToString(callSuper = true)
	public static class Operator extends Criterion{
		
		public enum OperatorType{
			Eq("="),
			NotEq("!="),
			Lt("<"),
			Lte("<="),
			Gt(">"),
			Gte(">="),
			Like("LIKE"),
			NotLike("NOT LIKE");
			
			public String symbol;
			
			OperatorType(String symbol) {
				this.symbol = symbol;
			}
		}
		
		public OperatorType type;

		public Object value;
		
		public Operator(Attribute attribute, OperatorType type, Object value) {
			this.attribute = attribute;
			this.type = type;
			this.value = value;
		}

		public Operator clone() { return new Operator(attribute, type, value); }

	}
	
	
	@ToString(callSuper = true)
	public static class In extends Criterion {
		
		@Getter
		List<?> values;

		public In(Attribute attribute, Collection<?> values) {
			this.attribute = attribute;
			this.values = new ArrayList<>(values) ; 
		}

		public In clone() { return new In(attribute, values.stream().collect(Collectors.toList())); }

	}
	
	
	@ToString(callSuper = true)
	public static class NotIn extends Criterion {
		
		@Getter
		List<?> values;

		public NotIn(Attribute attribute, Collection<?> values) {
			this.attribute = attribute;
			this.values = new ArrayList<>(values);
		}

		public NotIn clone() { return new NotIn(attribute, values.stream().collect(Collectors.toList())); }

	}
	
	
	@ToString(callSuper = true)
	public static class Between extends Criterion{

		public Object min;
		public Object max;
		
		public Between(Attribute attribute, Object min, Object max) {
			this.attribute = attribute;
			this.min = min;
			this.max = max;
		}
		
		public Between clone() { return new Between(attribute, min, max); }
	}
	
	
	@ToString(callSuper = true)
	public static class NullCheck extends Criterion{
		
		public boolean isNull;
		
		public NullCheck(Attribute attribute, boolean isNull) {
			this.attribute = attribute;
			this.isNull = isNull;
		}

		public NullCheck clone() { return new NullCheck(attribute, isNull); }

	}
	
	
	// MatchAgainst Criterion
	@ToString(callSuper = true)
	public static class MatchAgainst extends Criterion{
		
		public Object value;
		public boolean inclusive;
		
		public MatchAgainst(Attribute attribute, Object value, boolean inclusive) {
			this.attribute = attribute;
			this.value = value;
			this.inclusive = inclusive;
		}

		public MatchAgainst clone() { return new MatchAgainst(attribute, value, inclusive); } 
		
	}
	
	@ToString(callSuper=true)
	public static class Regexp extends Criterion{
		public String pattern;
		public Regexp(Attribute attribute, String pattern) {
			this.attribute = attribute;
			this.pattern = pattern;
		}
		
		public Regexp clone() { return new Regexp(attribute, pattern); }
	}
	
	
	@ToString(callSuper = true)
	public static class NativeCriterion extends Criterion{
		
		public String criterion;

		public NativeCriterion(Attribute attribute, String criterion) {
			this.attribute = attribute;
			this.criterion = criterion;
		}

		public NativeCriterion clone() { return new NativeCriterion(attribute, criterion); } 
		
	}

	
	@ToString(callSuper = true)
	public static class InSubQuery extends Criterion{

		public SelectQuery<?> subQuery;
		public boolean include;

		public InSubQuery(Attribute attribute, SelectQuery<?> subQuery, boolean include){
			this.attribute = attribute;
			this.subQuery = subQuery;
			this.include = include;
		}


		public InSubQuery clone() {return new InSubQuery(attribute, subQuery, include);}

	}

	
	@ToString(callSuper = true)
	public static class ExistsSubQuery extends Criterion{

		public SelectQuery<?> subQuery;
		public boolean include;

		public ExistsSubQuery(SelectQuery<?> subQuery, boolean include){
			this.subQuery = subQuery;
			this.include = include;
		}

		public ExistsSubQuery clone(){return new ExistsSubQuery(subQuery, include);}

	}

	

}
