package com.estivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Aggregator;
import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Attribute;
import com.estivate.query.Criterion;
import com.estivate.query.Criterion.Between;
import com.estivate.query.Criterion.ExistsSubQuery;
import com.estivate.query.Criterion.In;
import com.estivate.query.Criterion.InSubQuery;
import com.estivate.query.Criterion.MatchAgainst;
import com.estivate.query.Criterion.NativeCriterion;
import com.estivate.query.Criterion.NotIn;
import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.Keyword;
import com.estivate.query.Keyword.KeywordValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.query.Query.Order;

public class Estivate {

	// Entities Factory
	public static Query query(Entity<?> entity) { return new Query(entity); }	
	public static Query query(Class<?> entity) 	{ return new Query(entity); }
	
	public static Attribute attribute(Entity<?> entity, String field, Attribute.Function... functions) { return new Attribute(entity, field, Arrays.asList(functions)); }
	public static Attribute attribute(Class<?> entity, String field, Attribute.Function... functions) { return attribute(new Entity<>(entity), field, functions); }

	public static Aggregator or(EstivateNode... criterions) { return or(new ArrayList<>(Arrays.asList(criterions)));}
	public static Aggregator or(List<EstivateNode> criterions) { return new Aggregator(GroupType.OR, criterions); }
	public static Aggregator and(EstivateNode... criterions) { return and(new ArrayList<>(Arrays.asList(criterions))); }
	public static Aggregator and(List<EstivateNode> criterions) { return new Aggregator(GroupType.AND, criterions); }
	
	public static Attribute.Function function(String before, String after) { return new Attribute.Function(before, after); }
	

	public static Order order(Entity<?> entity, String attribute){ return new Order(entity, attribute, null, ""); }
	public static Order order(Entity<?> entity, String attribute, Order.Direction direction){ return new Order(entity, attribute, direction, ""); }
	public static Order order(Entity<?> entity, String attribute, Order.Direction direction, String option){ return new Order(entity, attribute, direction, option); }
	public static Order order(Class<?> entity, String attribute){ return new Order(new Entity<>(entity), attribute, null, ""); }
	public static Order order(Class<?> entity, String attribute, Order.Direction direction){ return new Order(new Entity<>(entity), attribute, direction, ""); }
	public static Order order(Class<?> entity, String attribute, Order.Direction direction, String option){ return new Order(new Entity<>(entity), attribute, direction, option); }


	/*
	 * Wrappers for Joins
	 */
	public static Join joinInner(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.INNER); }
	public static Join joinInner(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return joinInner(leftEntity, new Query.Entity<>(rightClass)); }
	public static Join joinInner(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return joinInner(new Query.Entity<>(leftClass), rightEntity); }
	public static Join joinInner(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return joinInner(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity)); }

	public static Join joinOuter(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.OUTER); }
	public static Join joinOuter(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return joinOuter(leftEntity, new Query.Entity<>(rightClass)); }
	public static Join joinOuter(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return joinOuter(new Query.Entity<>(leftClass), rightEntity); }
	public static Join joinOuter(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return joinOuter(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity)); }

	public static Join joinLeft	(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.LEFT); }
	public static Join joinLeft	(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return joinLeft(leftEntity, new Query.Entity<>(rightClass)); }
	public static Join joinLeft	(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return joinLeft(new Query.Entity<>(leftClass), rightEntity); }
	public static Join joinLeft	(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return joinLeft(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity)); }

	public static Join joinRight(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.RIGHT); }
	public static Join joinRight(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return joinRight(leftEntity, new Query.Entity<>(rightClass)); }
	public static Join joinRight(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return joinRight(new Query.Entity<>(leftClass), rightEntity); }
	public static Join joinRight(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return joinRight(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity)); }


	 public static Join joinInner(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	 public static Join joinInner(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.INNER); }
	 public static Join joinInner(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	 public static Join joinInner(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute, JoinType.INNER); }
 
	 public static Join joinOuter(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	 public static Join joinOuter(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.OUTER); }
	 public static Join joinOuter(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	 public static Join joinOuter(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute, JoinType.OUTER); }
 
	 public static Join joinLeft(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	 public static Join joinLeft(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.LEFT); }
	 public static Join joinLeft(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	 public static Join joinLeft(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute, JoinType.LEFT); }
 
	 public static Join joinRight(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	 public static Join joinRight(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.RIGHT); }
	 public static Join joinRight(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	 public static Join joinRight(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute, JoinType.RIGHT); }
 
	
	// Eq methods
	public static Criterion eq   			(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.Eq, value); }
	public static Criterion eqIfNotNull		(Attribute attribute, Object value) { if(value != null) {return eq(attribute, value);} return null; }
	public static Criterion eqNullable		(Attribute attribute, Object value) { if(value != null) {return eq(attribute, value);} return isNull(attribute); }
	public static Aggregator eqOrNull		(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).eq(attribute, value).isNull(attribute); }
	public static Criterion notEq			(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.NotEq, value); }
	public static Criterion notEqIfNotNull	(Attribute attribute, Object value) { if(value != null) {return notEq(attribute, value);} return null; }
	public static Criterion notEqNullable	(Attribute attribute, Object value) { if(value != null) {return notEq(attribute, value);} return isNotNull(attribute); }
	public static Aggregator notEqOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).notEq(attribute, value).isNull(attribute); }
	
	// Lt 
	public static Criterion lt   	(Attribute attribute, Object value)        		{ return new Operator(attribute, OperatorType.Lt, value); }
	public static Criterion ltIfNotNull   	(Attribute attribute, Object value) { if(value != null) {return lt(attribute, value);} return null; }
	public static Aggregator ltOrNull(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).lt(attribute, value).isNull(attribute); }
	
	// Lte
	public static Criterion lte  	(Attribute attribute, Object value)        		{ return new Operator(attribute, OperatorType.Lte, value); }
	public static Criterion lteIfNotNull  	(Attribute attribute, Object value) { if(value != null) {return lte(attribute, value);} return null; }
	public static Aggregator lteOrNull(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).lte(attribute, value).isNull(attribute); }
	
	
	// Gt 
	public static Criterion gt   	(Attribute attribute, Object value)        		{ return new Operator(attribute, OperatorType.Gt, value); }
	public static Criterion gtIfNotNull   	(Attribute attribute, Object value) { if(value != null) {return gt(attribute, value);} return null; }
	public static Aggregator gtOrNull(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).gt(attribute, value).isNull(attribute); }
	
	// Gte
	public static Criterion gte  	(Attribute attribute, Object value)        		{ return new Operator(attribute, OperatorType.Gte, value); }
	public static Criterion gteIfNotNull  	(Attribute attribute, Object value) { if(value != null) {return gte(attribute, value);} return null; }
	public static Aggregator gteOrNull(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).gte(attribute, value).isNull(attribute); }
	
	
	// Between
	public static Criterion between (Attribute attribute, Object min, Object max) 	{ return new Between(attribute, min, max); }
	public static Criterion betweenIfNotNull(Attribute attribute, Object min, Object max) {if(min != null && max != null) { return between(attribute, min, max);} return null; }
	public static Aggregator betweenOrNull(Attribute attribute, Object min, Object max) { return new Aggregator(GroupType.OR).between(attribute, min, max).isNull(attribute); }
	
	// In
	public static Criterion in   					(Attribute attribute, Collection<?> values){ return new In(attribute, values); }
	public static Criterion inIfNotEmpty			(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(attribute, values);} return null; }
	public static Aggregator inIfNotEmptyNullable	(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return Estivate.or(inIfNotEmpty(attribute, values.stream().filter(x -> x != null).collect(Collectors.toList()))).addIf(values.stream().anyMatch(x -> x == null), Estivate.isNull(attribute)); } return null; }
	public static EstivateNode inOrFalseIfEmpty		(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(attribute, values);} return keywordFalse(); }
	
	public static Criterion notIn   				(Attribute attribute, Collection<?> values){ return new NotIn(attribute, values); }
	public static Criterion notInIfNotEmpty			(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(attribute, values);} return null; }
	public static EstivateNode notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(attribute, values);} return keywordTrue(); }

	// Like
	public static Criterion like 			(Attribute attribute, String value) { return new Operator(attribute, OperatorType.Like, value); }
	public static Criterion notLike			(Attribute attribute, String value) { return new Operator(attribute, OperatorType.NotLike, value); }
	public static Criterion likeIfNotNull 	(Attribute attribute, String value) { if(value != null) {return like(attribute, value);} return null;  }
	public static Criterion notLikeIfNotNull(Attribute attribute, String value) { if(value != null) {return notLike(attribute, value);} return null;  }
	
	// Match Against
	public static Criterion matchAgainst			(Attribute attribute, String value) { return new MatchAgainst(attribute, value, true); }
	public static Criterion notMatchAgainst 		(Attribute attribute, String value) { return new MatchAgainst(attribute, value, false); }
	public static Criterion matchAgainstIfNotNull 	(Attribute attribute, String value) { if(value != null) {return new MatchAgainst(attribute, value, true);} return null;  }
	public static Criterion notMatchAgainstIfNotNull(Attribute attribute, String value) { if(value != null) {return new MatchAgainst(attribute, value, false);} return null;  }
	
	// Like In
	public static Aggregator likeIn 			(Attribute attribute, Collection<String> values) { return or(values.stream().map(x -> like(attribute, x)).collect(Collectors.toList())); }
	public static Aggregator likeInIfNotEmpty	(Attribute attribute, Collection<String> values) { if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> like(attribute, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator notLikeIn			(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLike(attribute, x)).collect(Collectors.toList()));  		}
	public static Aggregator notLikeInIfNotEmpty(Attribute attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notLike(attribute, x)).collect(Collectors.toList()));} return null; }

	// Match Against In
	public static Aggregator matchAgainstIn 			(Attribute attribute, Collection<String> values)	{ return matchAgainstIn(attribute, values); }
	public static Aggregator matchAgainstInIfNotEmpty	(Attribute attribute, Collection<String> values) { return matchAgainstInIfNotEmpty(attribute, values); } 
	public static Aggregator notMatchAgainstIn			(Attribute attribute, Collection<String> values)	{ return notMatchAgainstIn(attribute, values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values)	{ return notMatchAgainstInIfNotEmpty(attribute, values); }

	// Like starts
	public static Criterion likeStartsWith(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.Like, value+"%");	}
	public static Criterion notLikeStartsWith(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.NotLike, value+"%");}
	public static Criterion likeStartsWithIfNotNull (Attribute attribute, String value) 	{ if(value != null) {return likeStartsWith(attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotNull(Attribute attribute, String value){ if(value != null) {return notLikeStartsWith(attribute, value);} return null; }
	
	// Like starts in
	public static Aggregator likeStartsWithIn(Attribute attribute, Collection<String> values)	{ return or(values.stream().map(x -> likeStartsWith(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeStartsWithInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeStartsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeStartsWithIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeStartsWith(attribute, x)).collect(Collectors.toList()));}
	
	
	// Like ends
	public static Criterion likeEndsWith(Attribute attribute, String value)			{ return new Operator(attribute, OperatorType.Like, "%"+value);		}
	public static Criterion notLikeEndsWith(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.NotLike, "%"+value);	}
	public static Criterion likeEndsWithIfNotNull 	(Attribute attribute, String value) { if(value != null) {return likeEndsWith(attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotNull(Attribute attribute, String value) { if(value != null) {return notLikeEndsWith(attribute, value);} return null; }
	
	// Like ends in
	public static Aggregator likeEndsWithIn(Attribute attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeEndsWith(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeEndsWithInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeEndsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeEndsWithIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeEndsWith(attribute, x)).collect(Collectors.toList()));	}
	
	// Like contains
	public static Criterion likeContains(Attribute attribute, String value)			{ return new Operator(attribute, OperatorType.Like, "%"+value+"%");	}
	public static Criterion notLikeContains(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.NotLike, "%"+value+"%");	}
	public static Criterion likeContainsIfNotNull 	(Attribute attribute, String value) { if(value != null) {return likeContains(attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotNull(Attribute attribute, String value) { if(value != null) {return notLikeContains(attribute, value);} return null; }


	// Like contains in
	public static Aggregator likeContainsIn(Attribute attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeContains(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeContainsInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeContains(attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeContainsIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeContains(attribute, x)).collect(Collectors.toList()));	}

	// isNull
	public static Criterion isNull		(Attribute attribute) 						{ return new NullCheck(attribute, true);}
	public static Criterion isNotNull	(Attribute attribute) 						{ return new NullCheck(attribute, false);}
	
	// natively
	public static Criterion nativeCriterion (Attribute attribute, String criterion) { return new NativeCriterion(attribute, criterion); }
	
	// subQuery
	public static Criterion inSubQuery(Attribute attribute, Query subQuery)	  	{ return new InSubQuery(attribute, subQuery, true); }
	public static Criterion notInSubQuery(Attribute attribute, Query subQuery)	{ return new InSubQuery(attribute, subQuery, false); }
	
	// exists
	public static Criterion existsSubQuery(Query subQuery)		{ return new ExistsSubQuery(subQuery, true); }
	public static Criterion notExistsSubQuery(Query subQuery)	{ return new ExistsSubQuery(subQuery, false); }
	

	public static Keyword keywordTrue() { return new Keyword(KeywordValue.TRUE); }
	public static Keyword keywordFalse() { return new Keyword(KeywordValue.FALSE); }


	/* Wrapper for Entity */
	
	// Eq methods
	public static Criterion eq   			(Entity<?> entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Eq, value); }
	public static Criterion eqIfNotNull		(Entity<?> entity, String attribute, Object value) { if(value != null) {return eq(entity, attribute, value);} return null; }
	public static Criterion eqNullable		(Entity<?> entity, String attribute, Object value) { if(value != null) {return eq(entity, attribute, value);} return isNull(entity, attribute); }
	public static Aggregator eqOrNull		(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).eq(entity, attribute, value).isNull(entity, attribute); }
	public static Criterion notEq			(Entity<?> entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.NotEq, value); }
	public static Criterion notEqIfNotNull	(Entity<?> entity, String attribute, Object value) { if(value != null) {return notEq(entity, attribute, value);} return null; }
	public static Criterion notEqNullable	(Entity<?> entity, String attribute, Object value) { if(value != null) {return notEq(entity, attribute, value);} return isNotNull(entity, attribute); }
	public static Aggregator notEqOrNull	(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).notEq(entity, attribute, value).isNull(entity, attribute); }
	
	// Lt 
	public static Criterion lt   	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Lt, value); }
	public static Criterion ltIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lt(entity, attribute, value);} return null; }
	public static Aggregator ltOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).lt(entity, attribute, value).isNull(entity, attribute); }
	
	// Lte
	public static Criterion lte  	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Lte, value); }
	public static Criterion lteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lte(entity, attribute, value);} return null; }
	public static Aggregator lteOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).lte(entity, attribute, value).isNull(entity, attribute); }
	
	
	// Gt 
	public static Criterion gt   	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Gt, value); }
	public static Criterion gtIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gt(entity, attribute, value);} return null; }
	public static Aggregator gtOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).gt(entity, attribute, value).isNull(entity, attribute); }
	
	// Gte
	public static Criterion gte  	(Entity<?> entity, String attribute, Object value)        		{ return new Operator(entity, attribute, OperatorType.Gte, value); }
	public static Criterion gteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gte(entity, attribute, value);} return null; }
	public static Aggregator gteOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).gte(entity, attribute, value).isNull(entity, attribute); }
	
	
	// Between
	public static Criterion between (Entity<?> entity, String attribute, Object min, Object max) 	{ return new Between(entity, attribute, min, max); }
	public static Criterion betweenIfNotNull(Entity<?> entity, String attribute, Object min, Object max) {if(min != null && max != null) { return between(entity, attribute, min, max);} return null; }
	public static Aggregator betweenOrNull(Entity<?> entity, String attribute, Object min, Object max) { return new Aggregator(GroupType.OR).between(entity, attribute, min, max).isNull(entity, attribute); }
	
	// In
	public static Criterion in   					(Entity<?> entity, String attribute, Collection<?> values){ return new In(entity, attribute, values); }
	public static Criterion inIfNotEmpty			(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(entity, attribute, values);} return null; }
	public static Aggregator inIfNotEmptyNullable	(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return Estivate.or(inIfNotEmpty(entity, attribute, values.stream().filter(x -> x != null).collect(Collectors.toList()))).addIf(values.stream().anyMatch(x -> x == null), Estivate.isNull(entity, attribute)); } return null; }
	public static EstivateNode inOrFalseIfEmpty		(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(entity, attribute, values);} return keywordFalse(); }
	
	public static Criterion notIn   				(Entity<?> entity, String attribute, Collection<?> values){ return new NotIn(entity, attribute, values); }
	public static Criterion notInIfNotEmpty			(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(entity, attribute, values);} return null; }
	public static EstivateNode notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(entity, attribute, values);} return keywordTrue(); }

	// Like
	public static Criterion like 			(Entity<?> entity, String attribute, String value) { return new Operator(entity, attribute, OperatorType.Like, value); }
	public static Criterion notLike			(Entity<?> entity, String attribute, String value) { return new Operator(entity, attribute, OperatorType.NotLike, value); }
	public static Criterion likeIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return like(entity, attribute, value);} return null;  }
	public static Criterion notLikeIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLike(entity, attribute, value);} return null;  }
	
	// Match Against
	public static Criterion matchAgainst			(Entity<?> entity, List<String> attributes, String value) { return new MatchAgainst(entity, attributes, value, true); }
	public static Criterion notMatchAgainst 		(Entity<?> entity, List<String> attributes, String value) { return new MatchAgainst(entity, attributes, value, false); }
	public static Criterion matchAgainstIfNotNull 	(Entity<?> entity, List<String> attributes, String value) { if(value != null) {return matchAgainst(entity, attributes, value);} return null;  }
	public static Criterion notMatchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { if(value != null) {return notMatchAgainst(entity, attributes, value);} return null;  }
	public static Criterion matchAgainst			(Entity<?> entity, String attribute, String value) { return matchAgainst(entity, Arrays.asList(attribute), value); }
	public static Criterion notMatchAgainst 		(Entity<?> entity, String attribute, String value) { return notMatchAgainst(entity, Arrays.asList(attribute), value); }
	public static Criterion matchAgainstIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return matchAgainst(entity, attribute, value);} return null;  }
	public static Criterion notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notMatchAgainst(entity, attribute, value);} return null;  }
	
	// Like In
	public static Aggregator likeIn 			(Entity<?> entity, String attribute, Collection<String> values) { return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList())); }
	public static Aggregator likeInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator notLikeIn			(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLike(entity, attribute, x)).collect(Collectors.toList()));  		}
	public static Aggregator notLikeInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notLike(entity, attribute, x)).collect(Collectors.toList()));} return null; }

	// Match Against In
	public static Aggregator matchAgainstIn 			(Entity<?> entity, List<String> attributes, Collection<String> values)	{ return or(values.stream().map(x -> matchAgainst(entity, attributes, x)).collect(Collectors.toList())); }
	public static Aggregator matchAgainstInIfNotEmpty	(Entity<?> entity, List<String> attributes, Collection<String> values) { if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> matchAgainst(entity, attributes, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator notMatchAgainstIn			(Entity<?> entity, List<String> attributes, Collection<String> values)	{ return and(values.stream().map(x -> notMatchAgainst(entity, attributes, x)).collect(Collectors.toList()));  		}
	public static Aggregator notMatchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notMatchAgainst(entity, attributes, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator matchAgainstIn 			(Entity<?> entity, String attribute, Collection<String> values)	{ return matchAgainstIn(entity, Arrays.asList(attribute), values); }
	public static Aggregator matchAgainstInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { return matchAgainstInIfNotEmpty(entity, Arrays.asList(attribute), values); } 
	public static Aggregator notMatchAgainstIn			(Entity<?> entity, String attribute, Collection<String> values)	{ return notMatchAgainstIn(entity, Arrays.asList(attribute), values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values)	{ return notMatchAgainstInIfNotEmpty(entity, Arrays.asList(attribute), values); }

	// Like starts
	public static Criterion likeStartsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.Like, value+"%");	}
	public static Criterion notLikeStartsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, value+"%");}
	public static Criterion likeStartsWithIfNotNull (Entity<?> entity, String attribute, String value) 	{ if(value != null) {return likeStartsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value){ if(value != null) {return notLikeStartsWith(entity, attribute, value);} return null; }
	
	// Like starts in
	public static Aggregator likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return or(values.stream().map(x -> likeStartsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeStartsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeStartsWith(entity, attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeStartsWith(entity, attribute, x)).collect(Collectors.toList()));}
	
	
	// Like ends
	public static Criterion likeEndsWith(Entity<?> entity, String attribute, String value)			{ return new Operator(entity, attribute, OperatorType.Like, "%"+value);		}
	public static Criterion notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, "%"+value);	}
	public static Criterion likeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeEndsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeEndsWith(entity, attribute, value);} return null; }
	
	// Like ends in
	public static Aggregator likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeEndsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeEndsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeEndsWith(entity, attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeEndsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	
	// Like contains
	public static Criterion likeContains(Entity<?> entity, String attribute, String value)			{ return new Operator(entity, attribute, OperatorType.Like, "%"+value+"%");	}
	public static Criterion notLikeContains(Entity<?> entity, String attribute, String value)		{ return new Operator(entity, attribute, OperatorType.NotLike, "%"+value+"%");	}
	public static Criterion likeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeContains(entity, attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeContains(entity, attribute, value);} return null; }


	// Like contains in
	public static Aggregator likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeContains(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeContainsInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeContains(entity, attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeContains(entity, attribute, x)).collect(Collectors.toList()));	}

	// isNull
	public static Criterion isNull		(Entity<?> entity, String attribute) 						{ return new NullCheck(entity, attribute, true);}
	public static Criterion isNotNull	(Entity<?> entity, String attribute) 						{ return new NullCheck(entity, attribute, false);}
	
	// natively
	public static Criterion nativeCriterion (Entity<?> entity, String attribute, String criterion) { return new NativeCriterion(entity, attribute, criterion); }
	
	// subQuery
	public static Criterion inSubQuery(Entity<?> entity, String attribute, Query subQuery)	  	{ return new InSubQuery(entity, attribute, subQuery, true); }
	public static Criterion notInSubQuery(Entity<?> entity, String attribute, Query subQuery)	{ return new InSubQuery(entity, attribute, subQuery, false); }
	
	
	

	
	/* Wrappers for Class */
	
	public static Criterion eq    			(Class<?> entity, String attribute, Object value) { return eq(new Entity<>(entity), attribute, value); }
	public static Criterion eqIfNotNull		(Class<?> entity, String attribute, Object value) { return eqIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion eqNullable		(Class<?> entity, String attribute, Object value) { return eqNullable(new Entity<>(entity), attribute, value); }
	public static Aggregator eqOrNull		(Class<?> entity, String attribute, Object value) { return eqOrNull(new Entity<>(entity), attribute, value); }

	public static Criterion notEq 			(Class<?> entity, String attribute, Object value) { return notEq(new Entity<>(entity), attribute, value); }
	public static Criterion notEqIfNotNull	(Class<?> entity, String attribute, Object value) { return notEqIfNotNull(new Entity<>(entity), attribute, value); }
	public static Aggregator notEqOrNull	(Class<?> entity, String attribute, Object value) { return notEqOrNull(new Entity<>(entity), attribute, value); }

	
	public static Criterion lt    	(Class<?> entity, String attribute, Object value)        { return lt(new Entity<>(entity), attribute, value); }
	public static Aggregator ltOrNull(Class<?> entity, String attribute, Object value) { return ltOrNull(new Entity<>(entity), attribute, value); }
	
	public static Criterion lte   	(Class<?> entity, String attribute, Object value)        { return lte(new Entity<>(entity), attribute, value); }
	public static Aggregator lteOrNull(Class<?> entity, String attribute, Object value) { return lteOrNull(new Entity<>(entity), attribute, value); }
	
	public static Criterion gt    	(Class<?> entity, String attribute, Object value)        { return gt(new Entity<>(entity), attribute, value); }
	public static Aggregator gtOrNull(Class<?> entity, String attribute, Object value) { return gtOrNull(new Entity<>(entity), attribute, value); }
	public static Criterion gtIfNotNull   	(Class<?> entity, String attribute, Object value) { return gtIfNotNull(new Entity<>(entity), attribute, value); }
	
	public static Criterion gte   	(Class<?> entity, String attribute, Object value)        { return gte(new Entity<>(entity), attribute, value); }
	public static Aggregator gteOrNull(Class<?> entity, String attribute, Object value) { return gteOrNull(new Entity<>(entity), attribute, value); }
	
	public static Criterion between	(Class<?> entity, String attribute, Object left, Object right) { return between(new Entity<>(entity), attribute, left, right); }
	
	public static Criterion in    				(Class<?> entity, String attribute, Collection<?> values) { return in(new Entity<>(entity), attribute, values); }
	public static Criterion inIfNotEmpty		(Class<?> entity, String attribute, Collection<?> values) { return inIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static EstivateNode inIfNotEmptyNullable  (Class<?> entity, String attribute, Collection<?> values) { return inIfNotEmptyNullable(new Entity<>(entity), attribute, values); }
	public static EstivateNode inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values) { return inOrFalseIfEmpty(new Entity<>(entity), attribute, values); }

	public static Criterion notIn   (Class<?> entity, String attribute, Collection<?> values) { return notIn(new Entity<>(entity), attribute, values); }
	public static Criterion notInIfNotEmpty		(Class<?> entity, String attribute, Collection<?> values){ return notInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static EstivateNode notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values){ return notInOrTrueIfEmpty(new Entity<>(entity), attribute, values); }
	
	
	public static Criterion like			(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public static Criterion notLike			(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWith(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeEndsWith	(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeContains	(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }


	
	public static Criterion ltIfNotNull   	(Class<?> entity, String attribute, Object value) { return ltIfNotNull(new Entity<>(entity), attribute, value); }

	public static Criterion lteIfNotNull  	(Class<?> entity, String attribute, Object value) { return lteIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion gteIfNotNull  	(Class<?> entity, String attribute, Object value) { return gteIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion betweenIfNotNull(Class<?> entity, String attribute, Object min, Object max) { return betweenIfNotNull(new Entity<>(entity), attribute, min, max); }

	public static Aggregator likeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return likeStartsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeStartsWithInIfNotEmpty(new Entity<>(entity), attribute, values);}
	public static Aggregator notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeStartsWithIn(new Entity<>(entity), attribute, value); }
	
	public static Aggregator likeEndsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeEndsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeEndsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeEndsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notLikeEndsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeEndsWithIn(new Entity<>(entity), attribute, value); }
	
	public static Aggregator likeContainsIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeContainsIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeContainsInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeContainsInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notLikeContainsIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeContainsIn(new Entity<>(entity), attribute, value); }

	 
	public static Aggregator likeIn				(Class<?> entity, String attribute, Collection<String> value)	{ return likeIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> value)  	{ return likeInIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeIn			(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeIn(new Entity<>(entity), attribute, value); }
	

	public static Criterion isNull		(Class<?> entity, String attribute) 						{ return isNull(new Entity<>(entity), attribute);}
	public static Criterion isNotNull	(Class<?> entity, String attribute) 						{ return isNotNull(new Entity<>(entity), attribute);}
	

	public static Criterion matchAgainst(Class<?> entity, String attribute, String value) 			{ return matchAgainst(new Entity<>(entity), attribute, value); }	
	public static Criterion matchAgainst(Class<?> entity, List<String> attributes, String value) 	{ return matchAgainst(new Entity<>(entity), attributes, value); }
	public static Criterion matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return matchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion matchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { return matchAgainstIfNotNull(new Entity<>(entity), attributes, value); }
	public static Criterion notMatchAgainst(Class<?> entity, String attribute, String value) 			{ return notMatchAgainst(new Entity<>(entity), attribute, value); }
	public static Criterion notMatchAgainst(Class<?> entity, List<String> attributes, String value) 	{ return notMatchAgainst(new Entity<>(entity), attributes, value); }
	public static Criterion notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return notMatchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notMatchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { return notMatchAgainstIfNotNull(new Entity<>(entity), attributes, value); }
	
	public static Aggregator matchAgainstIn(Class<?> entity, String attribute, Collection<String> values)			{ return matchAgainstIn(new Entity<>(entity), attribute, values); }
	public static Aggregator matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values)	{ return matchAgainstIn(new Entity<>(entity), attributes, values); }
	public static Aggregator matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 		{ return matchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { return matchAgainstInIfNotEmpty(new Entity<>(entity), attributes, values); }
	public static Aggregator notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values)			{ return notMatchAgainstIn(new Entity<>(entity), attribute, values); }
	public static Aggregator notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values)				{ return notMatchAgainstIn(new Entity<>(entity), attributes, values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 		{ return notMatchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { return notMatchAgainstInIfNotEmpty(new Entity<>(entity), attributes, values); }


	public static Criterion nativeCriterion (Class<?> entity, String attribute, String criterion)	{ return nativeCriterion(new Entity<>(entity), attribute, criterion); }

	
	public static Criterion inSubQuery(Class<?> entity, String attribute, Query subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public static Criterion notInSubQuery(Class<?> entity, String attribute, Query subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }
	
	
	
	


	
	 
	 public static class Functions{
		 

		/* Math Functions */
		public static Attribute.Function abs(){ return new Attribute.Function("abs(", ")"); }
		public static Attribute.Function round(){ return new Attribute.Function("round(", ")"); }
		public static Attribute.Function ceil(){ return new Attribute.Function("ceil(", ")"); }
		public static Attribute.Function floor(){ return new Attribute.Function("floor(", ")"); }
		public static Attribute.Function mod(int mod) {return new Attribute.Function("mod(", mod+")"); }
		public static Attribute.Function pow(){ return new Attribute.Function("pow(", ")"); }
		public static Attribute.Function sqrt(){ return new Attribute.Function("sqrt(", ")"); }
		public static Attribute.Function log(){ return new Attribute.Function("log(", ")"); }
		public static Attribute.Function exp(){ return new Attribute.Function("exp(", ")"); }
		public static Attribute.Function sin(){ return new Attribute.Function("sin(", ")"); }
		public static Attribute.Function cos(){ return new Attribute.Function("cos(", ")"); }
		public static Attribute.Function tan(){ return new Attribute.Function("tan(", ")"); }
		
		/* Date Functions */
		public static Attribute.Function date(){ return new Attribute.Function("date(", ")"); }
		public static Attribute.Function time(){ return new Attribute.Function("time(", ")"); }
		public static Attribute.Function timestamp(){ return new Attribute.Function("timestamp(", ")"); }
		public static Attribute.Function now(){ return new Attribute.Function("now(", ")"); }
		public static Attribute.Function month() { return new Attribute.Function("month(", ")"); }
		public static Attribute.Function year() { return new Attribute.Function("year(", ")"); }

		/* String Functions */
		 public static Attribute.Function lower(){ 	return new Attribute.Function("lower(", ")"); }
		 public static Attribute.Function upper(){ 	return new Attribute.Function("upper(", ")"); }
		 public static Attribute.Function length(){ return new Attribute.Function("length(", ")"); }
		 public static Attribute.Function trim(){ 	return new Attribute.Function("trim(", ")"); }

		/* Aggregate Functions : cannot be used in where clause */
		 public static Attribute.Function count(){ return new Attribute.Function("count(", ")"); }
		 public static Attribute.Function countDistinct(){ return new Attribute.Function("count(distinct ", ")"); }
		 public static Attribute.Function sum(){ return new Attribute.Function("sum(", ")"); }
		 public static Attribute.Function avg(){ return new Attribute.Function("avg(", ")"); }
		 public static Attribute.Function min(){ return new Attribute.Function("min(", ")"); }
		 public static Attribute.Function max(){ return new Attribute.Function("max(", ")"); }
		 public static Attribute.Function groupConcat(){ return new Attribute.Function("group_concat(", ")"); }

		/* JSON Functions */
		public static Attribute.Function json_extract(String path){ return new Attribute.Function("JSON_EXTRACT(", ", \""+path+"\")"); }

		 
	 }
	 
}
