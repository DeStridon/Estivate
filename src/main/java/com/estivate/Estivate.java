package com.estivate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Aggregator;
import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Criterion;
import com.estivate.query.Criterion.Between;
import com.estivate.query.Criterion.ExistsSubQuery;
import com.estivate.query.Criterion.In;
import com.estivate.query.Criterion.InSubQuery;
import com.estivate.query.Criterion.NativeCriterion;
import com.estivate.query.Criterion.NotIn;
import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.EstivateNode;
import com.estivate.query.Keyword;
import com.estivate.query.Keyword.KeywordValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;

public class Estivate {

	// Query
	public static Query query(Entity<?> entity) { return new Query(entity); }	
	public static Query query(Class<?> entity) 	{ return new Query(entity); }
	
	
	
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
	
	// Like In
	public static Aggregator likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList())); }
	public static Aggregator likeInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 			{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ return and(values.stream().map(x -> notLike(entity, attribute, x)).collect(Collectors.toList()));  		}
	
	
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
	
	// exists
	public static Criterion existsSubQuery(Query subQuery)		{ return new ExistsSubQuery(subQuery, true); }
	public static Criterion notExistsSubQuery(Query subQuery)	{ return new ExistsSubQuery(subQuery, false); }
	

	public static Keyword keywordTrue() { return new Keyword(KeywordValue.TRUE); }
	public static Keyword keywordFalse() { return new Keyword(KeywordValue.FALSE); }
	
	
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
	
	public static Criterion nativeCriterion (Class<?> entity, String attribute, String criterion)	{ return nativeCriterion(new Entity<>(entity), attribute, criterion); }
	
	public static Criterion inSubQuery(Class<?> entity, String attribute, Query subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public static Criterion notInSubQuery(Class<?> entity, String attribute, Query subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }
	
	
	
	public static Aggregator or(EstivateNode... criterions) { return or(Arrays.asList(criterions));}
	public static Aggregator or(List<EstivateNode> criterions) { return new Aggregator(GroupType.OR, criterions); }
	public static Aggregator and(EstivateNode... criterions) { return and(Arrays.asList(criterions)); }
	public static Aggregator and(List<EstivateNode> criterions) { return new Aggregator(GroupType.AND, criterions); }
}
