package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.Query.Entity;

import lombok.Getter;

@Getter
public class Aggregator implements EstivateNode {
	
	final GroupType groupType;
	
	List<EstivateNode> criterions = new ArrayList<>();
	
	public static enum GroupType{AND, OR}
	
	public Aggregator(GroupType groupType) {
		this.groupType = groupType;
	}
	
	
	

	
	public Aggregator eq    	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.Eq,     value)); return this; }
	public Aggregator notEq 	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.NotEq,  value)); return this; }
	public Aggregator lt    	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.Lt,     value)); return this; }
	public Aggregator gt    	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.Gt,     value)); return this; }
	public Aggregator lte   	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.Lte,    value)); return this; }
	public Aggregator gte   	(Entity<?> entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, OperatorType.Gte,    value)); return this; }
	public Aggregator between 	(Entity<?> entity, String attribute, Object min, Object max) { criterions.add(new Criterion.Between(entity, attribute, min, max )); return this; }
	public Aggregator in    	(Entity<?> entity, String attribute, Collection<?> values) { criterions.add(new Criterion.In(entity, attribute, values)); return this; }
	public Aggregator notIn    	(Entity<?> entity, String attribute, Collection<?> values) { criterions.add(new Criterion.NotIn(entity, attribute, values)); return this; }

	public Aggregator like				(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.Like,   value)); return this; }
	public Aggregator likeStartsWith	(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.Like,   value+"%")); return this; }
	public Aggregator likeEndsWith		(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.Like,   "%"+value)); return this; }
	public Aggregator likeContains		(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.Like,   "%"+value+"%")); return this; }
	public Aggregator notLike			(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.NotLike,   value)); return this; }
	public Aggregator notLikeStartsWith	(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.NotLike,   value+"%")); return this; }
	public Aggregator notLikeEndsWith	(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.NotLike,   "%"+value)); return this; }
	public Aggregator notLikeContains	(Entity<?> entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, OperatorType.NotLike,   "%"+value+"%")); return this; }

	public Aggregator eqIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return eq(entity, attribute, value);} return this; }
	public Aggregator notEqIfNotNull	(Entity<?> entity, String attribute, Object value) { if(value != null) {return notEq(entity, attribute, value);} return this; }
	public Aggregator ltIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lt(entity, attribute, value);} return this; }
	public Aggregator gtIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gt(entity, attribute, value);} return this; }
	public Aggregator lteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lte(entity, attribute, value);} return this; }
	public Aggregator gteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gte(entity, attribute, value);} return this; }
	public Aggregator betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max) {if(min != null && max != null) { return between(entity, attribute, min, max);} return this; }

	public Aggregator inIfNotNull   	(Entity<?> entity, String attribute, Collection<?> values) { if(values != null) {return in(entity, attribute, values);} return this; }
	public Aggregator notInIfNotNull   	(Entity<?> entity, String attribute, Collection<?> values) { if(values != null) {return notIn(entity, attribute, values);} return this; }

	public Aggregator likeIfNotNull 			(Entity<?> entity, String attribute, String value) { if(value != null) {return like(entity, attribute, value);} return this; }
	public Aggregator likeStartsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeStartsWith(entity, attribute, value);} return this; }
	public Aggregator likeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeEndsWith(entity, attribute, value);} return this; }
	public Aggregator likeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeContains(entity, attribute, value);} return this; }

	public Aggregator notLikeIfNotNull 			(Entity<?> entity, String attribute, String value) { if(value != null) {return notLike(entity, attribute, value);} return this; }
	public Aggregator notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeStartsWith(entity, attribute, value);} return this; }
	public Aggregator notLikeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeEndsWith(entity, attribute, value);} return this; }
	public Aggregator notLikeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeContains(entity, attribute, value);} return this; }

	public Aggregator isNull(Entity<?> entity, String attribute) { criterions.add(new NullCheck(entity, attribute, true)); return this; }
	public Aggregator isNotNull(Entity<?> entity, String attribute) { criterions.add(new NullCheck(entity, attribute, false)); return this; }

	public Aggregator inSubQuery		(Entity<?> entity, String attribute, Query subQuery)	{ criterions.add(new Criterion.InSubQuery(entity, attribute, subQuery, true)); return this; }
	public Aggregator existsSubQuery	(Query subQuery)	{ criterions.add(new Criterion.ExistsSubQuery(subQuery, true)); return this; }
	public Aggregator notInSubQuery		(Entity<?> entity, String attribute, Query subQuery){ criterions.add(new Criterion.InSubQuery(entity, attribute, subQuery, false)); return this; }
	public Aggregator notExistsSubQuery	(Query subQuery)	{ criterions.add(new Criterion.ExistsSubQuery(subQuery, false)); return this; }

	public Aggregator eqOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).eq(entity, attribute, value).isNull(entity, attribute)); return this; }
	public Aggregator ltOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).lt(entity, attribute, value).isNull(entity, attribute)); return this; }
	public Aggregator gtOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).gt(entity, attribute, value).isNull(entity, attribute)); return this; }
	public Aggregator lteOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).lte(entity, attribute, value).isNull(entity, attribute)); return this; }
	public Aggregator gteOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).gte(entity, attribute, value).isNull(entity, attribute)); return this; }

	/* Wrappers for Class */
	public Aggregator eq    	(Class<?> entity, String attribute, Object value)        { return eq(new Entity<>(entity), attribute, value); }
	public Aggregator notEq 	(Class<?> entity, String attribute, Object value)        { return notEq(new Entity<>(entity), attribute, value); }
	public Aggregator lt    	(Class<?> entity, String attribute, Object value)        { return lt(new Entity<>(entity), attribute, value); }
	public Aggregator gt    	(Class<?> entity, String attribute, Object value)        { return gt(new Entity<>(entity), attribute, value); }
	public Aggregator lte   	(Class<?> entity, String attribute, Object value)        { return lte(new Entity<>(entity), attribute, value); }
	public Aggregator gte   	(Class<?> entity, String attribute, Object value)        { return gte(new Entity<>(entity), attribute, value); }
	public Aggregator between	(Class<?> entity, String attribute, Object left, Object right) { return between(new Entity<>(entity), attribute, left, right); }
	public Aggregator in    	(Class<?> entity, String attribute, Collection<?> values) { return in(new Entity<>(entity), attribute, values); }
	public Aggregator notIn    	(Class<?> entity, String attribute, Collection<?> values) { return notIn(new Entity<>(entity), attribute, values); }

	public Aggregator like				(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public Aggregator likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWith		(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public Aggregator likeContains		(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public Aggregator notLike			(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWith	(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }

	public Aggregator inSubQuery(Class<?> entity, String attribute, Query subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public Aggregator notInSubQuery(Class<?> entity, String attribute, Query subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }

	public Aggregator eqIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return eqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notEqIfNotNull 	(Class<?> entity, String attribute, Object value)        	{ return notEqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator ltIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return ltIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator gtIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return gtIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return lteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return gteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max)	{ return betweenIfNotNull(new Entity<>(entity), attribute, min, max); }
	public Aggregator inIfNotNull    	(Class<?> entity, String attribute, Collection<?> values) 	{ return inIfNotNull(new Entity<>(entity), attribute, values); }
	public Aggregator notInIfNotNull    (Class<?> entity, String attribute, Collection<?> values) 	{ return notInIfNotNull(new Entity<>(entity), attribute, values); }

	public Aggregator likeIfNotNull				(Class<?> entity, String attribute, String value)	{ return likeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeStartsWithIfNotNull	(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWithIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeContainsIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	public Aggregator notLikeIfNotNull			(Class<?> entity, String attribute, String value)  { return notLikeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWithIfNotNull	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContainsIfNotNull	(Class<?> entity, String attribute, String value)	{ return notLikeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	
	public Aggregator isNotNull(Class<?> entity, String attribute) { return isNotNull(new Entity<>(entity), attribute); }
	public Aggregator isNull(Class<?> entity, String attribute) { return isNull(new Entity<>(entity), attribute); }

	public Aggregator eqOrNull(Class<?> entity, String attribute, Object value) 	{ return eqOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator ltOrNull(Class<?> entity, String attribute, Object value) 	{ return ltOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gtOrNull(Class<?> entity, String attribute, Object value) 	{ return gtOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteOrNull(Class<?> entity, String attribute, Object value) 	{ return lteOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteOrNull(Class<?> entity, String attribute, Object value) 	{ return gteOrNull(new Entity<>(entity), attribute, value); }
	
	
	public Aggregator add(EstivateNode joinNode) {
		criterions.add(joinNode);
		return this;
	}
	
	

	public Aggregator clone() {
		Aggregator joinAggregator = new Aggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		return joinAggregator;
	}
	
	

}
