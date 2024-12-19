package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.Query.Entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Criterion implements EstivateNode{
	
	public Entity<?> entity;
	public String attribute;
	
	public abstract Criterion clone();
	

	public static Criterion eq   	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Eq, value); }
	public static Criterion notEq	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.NotEq, value); }
	public static Criterion lt   	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Lt, value); }
	public static Criterion gt   	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Gt, value); }
	public static Criterion lte  	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Lte, value); }
	public static Criterion gte  	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Gte, value); }
	public static Criterion between (Entity<?> entity, String attribute, Object min, Object max) 	{ return new Between(entity, attribute, min, max); }

	public static Criterion in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ return new In(entity, attribute, values); 		}
	public static Criterion notIn   (Entity<?> entity, String attribute, Collection<?> values) 		{ return new NotIn(entity, attribute, values); 		}

	public static Criterion like 	(Entity<?> entity, String attribute, String value)	    		{ return new Operator(entity, attribute, OperatorType.Like, value); }
	public static Criterion likeStartsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.Like, value+"%");	}
	public static Criterion likeEndsWith(Entity<?> entity, String attribute, String value)			{ return new Operator(entity, attribute, OperatorType.Like, "%"+value);		}
	public static Criterion likeContains(Entity<?> entity, String attribute, String value)			{ return new Operator(entity, attribute, OperatorType.Like, "%"+value+"%");	}

	public static Criterion notLike(Entity<?> entity, String attribute, String value)	    		{ return new Operator(entity, attribute, OperatorType.NotLike, value);  		}
	public static Criterion notLikeStartsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, value+"%");}
	public static Criterion notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, "%"+value);	}
	public static Criterion notLikeContains(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, "%"+value+"%");	}

	public static Criterion inSubQuery(Entity<?> entity, String attribute, Query subQuery)	  	{ return new InSubQuery(entity, attribute, subQuery, true); }
	public static Criterion notInSubQuery(Entity<?> entity, String attribute, Query subQuery)	{ return new InSubQuery(entity, attribute, subQuery, false); }
	
	public static Criterion existsSubQuery(Query subQuery)	{ return new ExistsSubQuery(subQuery, true); }
	public static Criterion notExistsSubQuery(Query subQuery)	{ return new ExistsSubQuery(subQuery, false); }
	

	
	/* Wrappers for Class */
	public static Criterion eq    	(Class<?> entity, String attribute, Object value)        { return eq(new Entity<>(entity), attribute, value); }
	public static Criterion notEq 	(Class<?> entity, String attribute, Object value)        { return notEq(new Entity<>(entity), attribute, value); }
	public static Criterion lt    	(Class<?> entity, String attribute, Object value)        { return lt(new Entity<>(entity), attribute, value); }
	public static Criterion gt    	(Class<?> entity, String attribute, Object value)        { return gt(new Entity<>(entity), attribute, value); }
	public static Criterion lte   	(Class<?> entity, String attribute, Object value)        { return lte(new Entity<>(entity), attribute, value); }
	public static Criterion gte   	(Class<?> entity, String attribute, Object value)        { return gte(new Entity<>(entity), attribute, value); }
	public static Criterion between	(Class<?> entity, String attribute, Object left, Object right) { return between(new Entity<>(entity), attribute, left, right); }
	public static Criterion in    	(Class<?> entity, String attribute, Collection<?> values) { return in(new Entity<>(entity), attribute, values); }
	public static Criterion notIn   (Class<?> entity, String attribute, Collection<?> values) { return notIn(new Entity<>(entity), attribute, values); }

	public static Criterion like			(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public static Criterion likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public static Criterion likeEndsWith	(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public static Criterion likeContains	(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public static Criterion notLike			(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWith(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }

	public static Criterion inSubQuery(Class<?> entity, String attribute, Query subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public static Criterion notInSubQuery(Class<?> entity, String attribute, Query subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }
	
	
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
		
		public Between clone() { return new Between(entity, attribute, min, max); }
	}
	
	
	public static class NullCheck extends Criterion{
		
		public boolean isNull;
		
		public NullCheck(Entity<?> entity, String attribute, boolean isNull) {
			this.entity = entity;
			this.attribute = attribute;
			this.isNull = isNull;
		}

		public NullCheck clone() { return new NullCheck(entity, attribute, isNull); }

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
