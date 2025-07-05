package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Attribute.Function;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class Query extends Aggregator{
	


	public Query eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return this; }
	public Query eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return this; }
	public Query eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return this; }
	public Query notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return this; }
	public Query notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return this; }
	public Query notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return this; }
	
	public Query lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return this; }
	public Query ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return this; }
	
	public Query lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return this; }
	public Query lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return this; }
	
	
	public Query gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return this; }
	public Query gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return this; }
	public Query gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return this; }
	public Query gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return this; }
	
	public Query between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return this; }
	public Query betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return this; }

	public Query in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return this; }
	public Query inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return this; }
	public Query inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return this; }
	public Query inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return this; }
	public Query inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return this; }
	public Query notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return this; }
	public Query notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return this; }
	public Query notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return this; }


	public Query like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return this; }
	public Query likeIn 	(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return this; }
	public Query notLike	(Attribute attribute, String value)				{ super.notLike(attribute, value);  return this; }
	public Query notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return this; }
	
	
	public Query likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return this; }
	public Query likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return this; }
	public Query notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return this; }
	
	public Query likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return this; }
	public Query notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return this; }
	
	public Query likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return this; }
	public Query notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return this; }

	public Query likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return this; }
	public Query likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return this; }
	public Query notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return this; }
	public Query notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return this; }
	public Query notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return this; }

	public Query nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return this; }
	
	public Query inSubQuery			(Attribute attribute, Query subQuery){ super.inSubQuery(attribute, subQuery); return this; }
	public Query notInSubQuery		(Attribute attribute, Query subQuery){ super.notInSubQuery(attribute, subQuery); return this; }
	public Query existsSubQuery		(Query subQuery){ super.existsSubQuery(subQuery); return this; }
	public Query notExistsSubQuery	(Query subQuery){ super.notExistsSubQuery(subQuery); return this; }

	
	public Query notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return this; }
	public Query inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return this; }
	public Query notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return this; }
	
	
	public Query likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return this; }
	public Query likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return this; }
	public Query likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return this; }
	public Query likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return this; }

	public Query likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return this; }
	public Query likeStartsWithInIfNotEmpty (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return this; }
	public Query likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return this; }
	public Query likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return this; }
	
	public Query notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return this; }
	public Query notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return this; }
	public Query notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return this; }
	public Query notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return this; }

	public Query matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return this; }

	public Query matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return this; }

	public Query notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return this; }

	public Query notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return this; }


	public Query matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return this; }
	public Query matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return this; }
	public Query notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return this; }
	
	public Query isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return this;}
	public Query isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return this;}

	public Query eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return this;	}
	public Query ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return this;	}
	public Query gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return this;	}
	public Query lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return this;	}
	public Query gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return this;	}


	// Attribute only wrappers

	public Query eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return this; }
	public Query eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return this; }
	public Query eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return this; }
	public Query notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return this; }
	public Query notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return this; }
	public Query notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return this; }
	
	public Query lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return this; }
	public Query ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return this; }
	
	public Query lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return this; }
	public Query lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return this; }
	
	
	public Query gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return this; }
	public Query gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return this; }
	public Query gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return this; }
	public Query gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return this; }
	
	public Query between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return this; }
	public Query betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return this; }

	public Query in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return this; }
	public Query inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return this; }
	public Query inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return this; }
	public Query inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return this; }
	public Query inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return this; }
	public Query notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return this; }
	public Query notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return this; }
	public Query notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return this; }


	public Query like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return this; }
	public Query likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return this; }
	public Query notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return this; }
	public Query notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return this; }
	
	
	public Query likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return this; }
	public Query likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return this; }
	public Query notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return this; }
	
	public Query likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return this; }
	public Query notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return this; }
	
	public Query likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return this; }
	public Query notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return this; }

	public Query likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return this; }
	public Query likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return this; }
	public Query notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return this; }
	public Query notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return this; }
	public Query notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return this; }

	public Query nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return this; }
	
	public Query inSubQuery			(String attribute, Query subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return this; }
	public Query notInSubQuery		(String attribute, Query subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return this; }

	
	public Query notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return this; }
	public Query inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return this; }
	public Query notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return this; }
	
	
	public Query likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return this; }
	public Query likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public Query likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public Query notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return this; }
	public Query notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public Query matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return this; }

	public Query matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return this; }

	public Query notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return this; }

	public Query notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return this; }


	public Query matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return this; }
	public Query matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public Query isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return this;}
	public Query isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return this;}

	public Query eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return this;	}
	public Query ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return this;	}
	public Query gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return this;	}
	public Query lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return this;	}
	public Query gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return this;	}


	// Class wrappers
	public Query eq   			(Class<?> entity, String attribute, Object value)	{ super.eq    (entity, attribute, value);  return this; }
	public Query eqIfNotNull   	(Class<?> entity, String attribute, Object value)   { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query eqNullable		(Class<?> entity, String attribute, Object value)   { super.eqNullable (entity, attribute, value); return this; }
	public Query notEq			(Class<?> entity, String attribute, Object value)   { super.notEq (entity, attribute, value);  return this; }
	public Query notEqIfNotNull	(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public Query notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return this; }
	
	public Query lt   			(Class<?> entity, String attribute, Object value)   { super.lt    (entity, attribute, value);  return this; }
	public Query ltIfNotNull	(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull   (entity, attribute, value);  return this; }
	
	public Query lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return this; }
	public Query lteIfNotNull  	(Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return this; }
	
	
	public Query gt   	(Class<?> entity, String attribute, Object value)        		{ super.gt    (entity, attribute, value);  return this; }
	public Query gtIfNotNull   		(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query gte  	(Class<?> entity, String attribute, Object value)        		{ super.gte   (entity, attribute, value);  return this; }
	public Query gteIfNotNull  		(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return this; }
	
	public Query between(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return this; }
	public Query betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return this; }

	public Query in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return this; }
	public Query inIfNotEmpty  			(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return this; }
	public Query inIfNotEmptyNullable  	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return this; }
	public Query inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return this; }
	public Query inIfNotEmptyOrNull		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return this; }
	public Query notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }
	public Query notInOrNull		  	(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return this; }
	public Query notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return this; }
	


	public Query like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return this; }
	public Query likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return this; }
	public Query notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return this; }
	public Query notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return this; }
	
	
	public Query likeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return this; }
	public Query likeStartsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return this; }
	public Query notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return this; }
	
	public Query likeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return this; }
	public Query notLikeEndsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return this; }
	
	public Query likeContains 		(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return this; }
	public Query notLikeContains 	(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return this; }

	public Query likeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return this; }
	public Query likeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return this; }
	public Query notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return this; }
	public Query notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return this; }
	public Query notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return this; }

	public Query nativeCriterion 	(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }
	
	public Query inSubQuery			(Class<?> entity, String attribute, Query subQuery){ super.inSubQuery(entity, attribute, subQuery); return this; }
	public Query notInSubQuery		(Class<?> entity, String attribute, Query subQuery){ super.notInSubQuery(entity, attribute, subQuery); return this; }

	
	public Query notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public Query inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public Query notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	
	public Query likeIfNotNull 				(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public Query likeEndsWithIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public Query likeContainsIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return this; }

	public Query likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeStartsWithInIfNotEmpty (Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }
	
	public Query notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeStartsWithIfNotNull (Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return this; }

	public Query matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }
	public Query matchAgainst(Class<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public Query matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query matchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public Query notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public Query notMatchAgainst(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public Query notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query notMatchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }

	public Query matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }
	public Query matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public Query matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public Query notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public Query notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	
	public Query isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return this;}
	public Query isNull		(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return this;}

	public Query eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return this;	}
	public Query ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return this;	}
	public Query gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return this;	}
	public Query lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return this;	}
	public Query gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return this;	}


	public Query eq   	(Entity<?> entity, String attribute, Object value)        		{ super.eq    	(entity, attribute, value);  		return this; }
	public Query eqNullable(Entity<?> entity, String attribute, Object value)			{ super.eqNullable(entity, attribute, value); 		return this; }
	public Query notEq	(Entity<?> entity, String attribute, Object value)        		{ super.notEq 	(entity, attribute, value);  		return this; }
	public Query notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return this; }
	public Query notEqNullable(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return this; }
	

	public Query lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return this; }
	public Query lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return this; }
	
	public Query gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return this; }
	public Query gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return this; }
	public Query between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return this; }

	public Query in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return this; }
	public Query inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return this; }
	public Query inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return this; }
	public Query inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return this; }
	public Query inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return this; }
	public Query notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }
	public Query notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return this; }
	public Query notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return this; }
	
	public Query like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return this; }
	public Query likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return this; }
	public Query likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return this; }
	public Query likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return this; }

	public Query notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return this; }
	public Query notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return this; }
	public Query notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return this; }
	public Query notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return this; }

	public Query likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return this; }
	public Query likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return this; }
	public Query likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return this; }
	public Query likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return this; }

	public Query notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return this; }
	public Query notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return this; }
	public Query notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return this; }
	public Query notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return this; }

	public Query eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return this; }
	public Query gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return this; }
	public Query gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return this; }
	public Query betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return this; }
	
	public Query notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public Query inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public Query notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	public Query likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return this; }
	public Query likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return this; }
	public Query likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return this; }

	public Query likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }

	
	public Query notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return this; }
	public Query notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return this; }
	public Query notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return this; }
	
	public Query isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public Query isNull				(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return this;}
	public Query eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	public Query ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return this;	}
	public Query gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return this;	}
	public Query lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return this;}
	public Query gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return this;}
	
	public Query matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }	
	public Query matchAgainst(Entity<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public Query matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query matchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public Query notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public Query notMatchAgainst(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public Query notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query notMatchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }
	
	public Query matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }	
	public Query matchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public Query matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query matchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public Query notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public Query notMatchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query notMatchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }



	public Query nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	public Query add(EstivateNode node) { super.add(node); return this; }
	public Query addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return this; }
	public Query and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return this; }
	public Query or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return this; }
	
	@Getter
	final Entity<?> entity;

	@Getter
	List<String> comments = new ArrayList<>();
	
	@Getter
	Set<Select> selects = new LinkedHashSet<>();
	
	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	@Getter
	Set<Join> joins = new LinkedHashSet<>();
	
	@Getter
	List<Order> orders = new ArrayList<>();
	
	@Getter
	List<Group> groupBys = new ArrayList<>();

	@Getter
	EstivateNode having;
	
	@Getter
	Integer offset;

	@Getter
	Integer limit;

	@Getter
	IndexHint indexHint;

	@Getter
	Set<String> indexNames = new LinkedHashSet<>();
	
	public <U> Query(Class<U> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<U>(baseClass);
	}
	
	public Query(Entity<?> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}
	
	public Query comment(String comment) {
		comments.add(comment);
		return this;
	}
	
	public Query join(Join join) { 
		if(joins.stream().noneMatch(x -> x.leftEntity.equals(join.leftEntity) && x.rightEntity.equals(join.rightEntity) && x.joinType == join.joinType)) {
			joins.add(join);
		}
		return this;
	}
		
	public Query joinInner(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return join(Estivate.joinInner(leftEntity, rightEntity)); }
	public Query joinInner(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinInner(leftEntity, new Query.Entity<>(rightClass)));}
	public Query joinInner(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return join(Estivate.joinInner(new Query.Entity<>(leftClass), rightEntity));}
	public Query joinInner(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinInner(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity))); }

	public Query joinOuter(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return join(Estivate.joinOuter(leftEntity, rightEntity)); }
	public Query joinOuter(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinOuter(leftEntity, new Query.Entity<>(rightClass))); }
	public Query joinOuter(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return join(Estivate.joinOuter(new Query.Entity<>(leftClass), rightEntity)); }
	public Query joinOuter(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinOuter(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity))); }

	public Query joinLeft(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return join(Estivate.joinLeft(leftEntity, rightEntity)); }
	public Query joinLeft(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinLeft(leftEntity, new Query.Entity<>(rightClass))); }
	public Query joinLeft(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return join(Estivate.joinLeft(new Query.Entity<>(leftClass), rightEntity)); }
	public Query joinLeft(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinLeft(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity))); }

	public Query joinRight(Query.Entity<?> leftEntity, 	Query.Entity<?> rightEntity)	{ return join(Estivate.joinRight(leftEntity, rightEntity)); }
	public Query joinRight(Query.Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinRight(leftEntity, new Query.Entity<>(rightClass))); }
	public Query joinRight(Class<?> leftClass, 			Query.Entity<?> rightEntity)	{ return join(Estivate.joinRight(new Query.Entity<>(leftClass), rightEntity)); }
	public Query joinRight(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinRight(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity))); }


	public Query joinInner(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query joinInner(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query joinInner(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query joinInner(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinInner(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }

	public Query joinOuter(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query joinOuter(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query joinOuter(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query joinOuter(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinOuter(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }

	public Query joinLeft(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query joinLeft(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query joinLeft(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query joinLeft(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinLeft(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }

	public Query joinRight(Query.Entity<?> leftEntity, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query joinRight(Query.Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, new Query.Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query joinRight(Class<?> leftClass, Query.Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Query.Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query joinRight(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinRight(new Query.Entity<>(joinerEntity), new Query.Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }


	public Query order(Order order) { orders.add(order); return this; }
	public Query order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
		orders.add(Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build()); 
		return this; 
	}
	public Query order(Class<?> entity, String attribute, Order.Direction direction, Function function) { return order(new Entity<>(entity), attribute, direction, function); }
	public Query order(String attribute, Order.Direction direction, Function function) { return order(this.entity, attribute, direction, function); }
	public Query order(Entity<?> entity, String attribute, Order.Direction direction) { return order(entity, attribute, direction, null); }
	public Query order(Class<?> entity, String attribute, Order.Direction direction) { return order(new Entity<>(entity), attribute, direction, null); }
	public Query order(String attribute, Order.Direction direction) { return order(this.entity, attribute, direction, null); }
	public Query order(Attribute attribute, Order.Direction direction) { return order(attribute.entity, attribute.attribute, direction, attribute.function); }

	public Query orderAsc(Entity<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Asc); }
	public Query orderAsc(Entity<?> c, String attribute, Function function) { return order(c, attribute, Order.Direction.Asc, function); }
	public Query orderAsc(Class<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Asc); }
	public Query orderAsc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Query orderAsc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Asc); }
	public Query orderAsc(String attribute, Function function) { return order(this.entity, attribute, Order.Direction.Asc, function); }
	public Query orderAsc(Attribute attribute) { return order(attribute.entity, attribute.attribute, Order.Direction.Asc, attribute.function); }
	
	public Query orderDesc(Entity<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Desc); }
	public Query orderDesc(Entity<?> c, String attribute, Function function){ return order(c, attribute, Order.Direction.Desc, function); }
	public Query orderDesc(Class<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Desc); }
	public Query orderDesc(Class<?> c, String attribute, Function function) { return order(c, attribute, Order.Direction.Desc, function); }
	public Query orderDesc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Desc); }
	public Query orderDesc(String attribute, Function function) { return order(this.entity, attribute, Order.Direction.Desc, function); }
	public Query orderDesc(Attribute attribute) { return order(attribute.entity, attribute.attribute, Order.Direction.Desc, attribute.function); }
	
	public Query limit(Integer limit) 		{ this.limit = limit; return this; }
	public Query offset(Integer offset) 	{ this.offset = offset; return this;}
	

	
	@SuperBuilder
	@Data
	@AllArgsConstructor
	public static class Order extends Attribute{
		
		public Direction direction;

		public enum Direction{
			Asc,
			Desc
		}
	}
	
	@AllArgsConstructor
	public static class Group{
		public Entity<?> entity;
		public String attribute;
	}
	
	
	public Query selectFunctionAs(Entity<?> c, String attribute, Attribute.Function function, String alias) {
		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}

		selects.add(Select.builder().entity(c).attribute(attribute).alias(alias).function(function).build());
		return this;
	}

	public Query selectFunctionAs(Class<?> c, String attribute, Attribute.Function function, String alias) { return selectFunctionAs(new Entity<>(c), attribute, function, alias); }

	/* Wrappers */
	public Query select(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, null, null); }
	public Query select(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, null, null); }
	public Query selectAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, null, alias); }
	public Query selectAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, null, alias); }	
	public Query selectFunction(Class<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(new Entity<>(c), attribute, function, null); }
	public Query selectFunction(Entity<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(c, attribute, function, null); }
	public Query selectAttribute(Attribute attribute) { return selectFunctionAs(attribute.entity, attribute.attribute, attribute.function, null); }
	
	public Query selectAll(Class<?> entity, String...fields) { return selectAll(new Entity<>(entity), fields); }	
	public Query selectAll(Entity<?> c, String... fields) {
		
		Class<?> currentClazz = c.entity;
		while(currentClazz != Object.class) {
			
			String[] classFields = fields.length == 0 ? FieldUtils.getEntityFields(currentClazz).stream().map( x -> x.getName() ).toArray(String[]::new) : fields;
			
			for(String field : classFields){
				if(selects.stream().noneMatch(x -> x.entity.equals(c) && x.attribute.equals(field))) {
					select(c, field);
				}
			}
			currentClazz = currentClazz.getSuperclass();
		}
		return this;
	}

	
	public Query selectDistinct(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, null); }
	public Query selectDistinct(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, null); }
	public Query selectDistinct(String attribute) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, null); }
	public Query selectDistinctAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, alias); }
	public Query selectDistinctAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, alias); }
	public Query selectDistinctAs(String attribute, String alias) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, alias); }

	// Select count
	public Query selectCount() { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, null); }
	public Query selectCountAs(String alias) { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count field 
	public Query selectCount(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, null); }
	public Query selectCount(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.count, null); }
	public Query selectCount(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, null); }
	public Query selectCountAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public Query selectCountAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.count, alias); }
	public Query selectCountAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, alias); }
	
	// Select count distinct field
	public Query selectCountDistinct(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, null); }
	public Query selectCountDistinct(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, null); }
	public Query selectCountDistinct(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, null); }
	public Query selectCountDistinctAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public Query selectCountDistinctAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, alias); }
	public Query selectCountDistinctAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	
	// Select min
	public Query selectMin(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, null); }
	public Query selectMin(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.min, null); }
	public Query selectMin(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, null); }
	public Query selectMinAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public Query selectMinAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.min, alias); }
	public Query selectMinAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, alias); }
	
	// Select max
	public Query selectMax(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, null); }
	public Query selectMax(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.max, null); }
	public Query selectMax(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, null); }
	public Query selectMaxAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public Query selectMaxAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.max, alias); }
	public Query selectMaxAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, alias); }

	// Select Sum
	public Query selectSum(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, null); }
	public Query selectSum(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, null); }
	public Query selectSum(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, null); }
	public Query selectSumAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public Query selectSumAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, alias); }
	public Query selectSumAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, alias); }
	
	// Select Group Concat
	public Query selectGroupConcat(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, null); }
	public Query selectGroupConcat(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, null); }
	public Query selectGroupConcat(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, null); }
	public Query selectGroupConcatAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public Query selectGroupConcatAs(Entity<?> c, String attribute, String alias){ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, alias); }
	public Query selectGroupConcatAs(String attribute, String alias) 			{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, alias); }


	// Having
	public Query having(EstivateNode node) { this.having = node; return this; }
	
	public Query clone() {
		Query queryClone = new Query(entity);
		
		queryClone.comments = new ArrayList<>(this.comments);
		
		queryClone.selects = new LinkedHashSet<>(this.selects);
		queryClone.joins = new LinkedHashSet<>(this.joins);

		queryClone.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		queryClone.indexHint = this.indexHint;
		queryClone.indexNames = new LinkedHashSet<>(this.indexNames);

		queryClone.orders = new ArrayList<>(this.orders);
		queryClone.groupBys = new ArrayList<>(this.groupBys);

		queryClone.limit = this.limit;
		queryClone.offset = this.offset;
		
		return queryClone;
	}

	public Query groupBy(Entity<?> entity, String field) { groupBys.add(new Group(entity, field)); return this; }
	public Query groupBy(Class<?> c, String field) { return groupBy(new Entity<>(c), field); }
	
	public Query setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}



	public <U> U fetchSingleAs(Context context, Class<U> clazz) {
		return context.fetchSingleAs(this, clazz);
	}

	public <U> List<U> fetchListAs(Context context, Class<U> clazz) {
		return context.fetchListAs(this, clazz);
	}

	public List<Result> fetchList(Context context) {
		return context.fetchList(this);
	}

	public Result fetchSingle(Context context) {
		return context.fetchSingle(this);
	}
	

	@EqualsAndHashCode
	@AllArgsConstructor
	public static class Entity<U>{
		public final Class<U> entity;
		public final String alias;
		
		public Entity(Class<U> entity) {
			this(entity, null);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Entity[");
			if(alias != null) {
				sb.append("alias = ").append(alias).append(", ");
			}
			sb.append("entity = ").append(entity.getSimpleName()).append("]");
			
			return sb.toString();
		}
		
	}


	
}
