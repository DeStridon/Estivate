package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Attribute.Function;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class Query<T> extends Aggregator{
	


	public Query<T> eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return this; }
	public Query<T> eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return this; }
	public Query<T> eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return this; }
	public Query<T> notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return this; }
	public Query<T> notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return this; }
	public Query<T> notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return this; }
	
	public Query<T> lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return this; }
	public Query<T> ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return this; }
	
	public Query<T> lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return this; }
	public Query<T> lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return this; }
	
	
	public Query<T> gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return this; }
	public Query<T> gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return this; }
	public Query<T> gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return this; }
	public Query<T> gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return this; }
	
	public Query<T> between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return this; }
	public Query<T> betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return this; }

	public Query<T> in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return this; }
	public Query<T> inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return this; }
	public Query<T> inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return this; }
	public Query<T> inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return this; }
	public Query<T> inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return this; }
	public Query<T> notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return this; }
	public Query<T> notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return this; }
	public Query<T> notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return this; }


	public Query<T> like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return this; }
	public Query<T> likeIn 	(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return this; }
	public Query<T> notLike	(Attribute attribute, String value)				{ super.notLike(attribute, value);  return this; }
	public Query<T> notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return this; }
	
	
	public Query<T> likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return this; }
	public Query<T> likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return this; }
	public Query<T> notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return this; }
	
	public Query<T> likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return this; }
	public Query<T> notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return this; }
	
	public Query<T> likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return this; }
	public Query<T> notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return this; }

	public Query<T> likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return this; }
	public Query<T> likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return this; }
	public Query<T> notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return this; }
	public Query<T> notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return this; }
	public Query<T> notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return this; }

	public Query<T> nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return this; }
	
	public Query<T> inSubQuery			(Attribute attribute, Query subQuery){ super.inSubQuery(attribute, subQuery); return this; }
	public Query<T> notInSubQuery		(Attribute attribute, Query subQuery){ super.notInSubQuery(attribute, subQuery); return this; }
	public Query<T> existsSubQuery		(Query subQuery){ super.existsSubQuery(subQuery); return this; }
	public Query<T> notExistsSubQuery	(Query subQuery){ super.notExistsSubQuery(subQuery); return this; }

	
	public Query<T> notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return this; }
	public Query<T> inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return this; }
	public Query<T> notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return this; }
	
	
	public Query<T> likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return this; }
	public Query<T> likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return this; }
	public Query<T> likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return this; }
	public Query<T> likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return this; }

	public Query<T> likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return this; }
	public Query<T> likeStartsWithInIfNotEmpty (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return this; }
	public Query<T> likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return this; }
	public Query<T> likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return this; }
	
	public Query<T> notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return this; }
	public Query<T> notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return this; }
	public Query<T> notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return this; }
	public Query<T> notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return this; }

	public Query<T> matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return this; }

	public Query<T> matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return this; }

	public Query<T> notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return this; }

	public Query<T> notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return this; }


	public Query<T> matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return this; }
	public Query<T> notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return this; }
	
	public Query<T> isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return this;}
	public Query<T> isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return this;}

	public Query<T> eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return this;	}
	public Query<T> ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return this;	}
	public Query<T> gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return this;	}
	public Query<T> lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return this;	}
	public Query<T> gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return this;	}


	// Attribute only wrappers

	public Query<T> eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return this; }
	public Query<T> eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return this; }
	public Query<T> eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return this; }
	public Query<T> notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return this; }
	public Query<T> notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return this; }
	public Query<T> notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return this; }
	
	public Query<T> lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return this; }
	public Query<T> ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return this; }
	
	public Query<T> lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return this; }
	public Query<T> lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return this; }
	
	
	public Query<T> gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return this; }
	public Query<T> gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return this; }
	public Query<T> gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return this; }
	public Query<T> gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return this; }
	
	public Query<T> between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return this; }
	public Query<T> betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return this; }

	public Query<T> in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return this; }
	public Query<T> inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return this; }
	public Query<T> inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return this; }
	public Query<T> notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return this; }
	public Query<T> notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return this; }
	public Query<T> notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return this; }


	public Query<T> like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return this; }
	public Query<T> likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return this; }
	public Query<T> notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return this; }
	public Query<T> notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return this; }
	
	
	public Query<T> likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return this; }
	public Query<T> likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return this; }
	
	public Query<T> likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return this; }
	
	public Query<T> likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return this; }
	public Query<T> notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return this; }

	public Query<T> likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return this; }
	public Query<T> likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return this; }
	public Query<T> notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return this; }

	public Query<T> nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return this; }
	
	public Query<T> inSubQuery			(String attribute, Query subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return this; }
	public Query<T> notInSubQuery		(String attribute, Query subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return this; }

	
	public Query<T> notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return this; }
	public Query<T> inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return this; }
	public Query<T> notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return this; }
	
	
	public Query<T> likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public Query<T> likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query<T> likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query<T> likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query<T> likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public Query<T> notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public Query<T> notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public Query<T> matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return this; }

	public Query<T> matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return this; }

	public Query<T> notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return this; }

	public Query<T> notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return this; }


	public Query<T> matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	public Query<T> notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public Query<T> isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return this;}
	public Query<T> isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return this;}

	public Query<T> eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return this;	}
	public Query<T> ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return this;	}
	public Query<T> gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return this;	}
	public Query<T> lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return this;	}
	public Query<T> gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return this;	}


	// Class wrappers
	public Query<T> eq   			(Class<?> entity, String attribute, Object value)	{ super.eq    (entity, attribute, value);  return this; }
	public Query<T> eqIfNotNull   	(Class<?> entity, String attribute, Object value)   { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query<T> eqNullable		(Class<?> entity, String attribute, Object value)   { super.eqNullable (entity, attribute, value); return this; }
	public Query<T> notEq			(Class<?> entity, String attribute, Object value)   { super.notEq (entity, attribute, value);  return this; }
	public Query<T> notEqIfNotNull	(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public Query<T> notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return this; }
	
	public Query<T> lt   			(Class<?> entity, String attribute, Object value)   { super.lt    (entity, attribute, value);  return this; }
	public Query<T> ltIfNotNull	(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull   (entity, attribute, value);  return this; }
	
	public Query<T> lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return this; }
	public Query<T> lteIfNotNull  	(Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return this; }
	
	
	public Query<T> gt   	(Class<?> entity, String attribute, Object value)        		{ super.gt    (entity, attribute, value);  return this; }
	public Query<T> gtIfNotNull   		(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query<T> gte  	(Class<?> entity, String attribute, Object value)        		{ super.gte   (entity, attribute, value);  return this; }
	public Query<T> gteIfNotNull  		(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return this; }
	
	public Query<T> between(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return this; }
	public Query<T> betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return this; }

	public Query<T> in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return this; }
	public Query<T> inIfNotEmpty  			(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyNullable  	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return this; }
	public Query<T> inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyOrNull		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return this; }
	public Query<T> notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }
	public Query<T> notInOrNull		  	(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return this; }
	public Query<T> notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return this; }
	


	public Query<T> like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return this; }
	public Query<T> likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return this; }
	public Query<T> notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return this; }
	public Query<T> notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return this; }
	
	
	public Query<T> likeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return this; }
	public Query<T> likeStartsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return this; }
	
	public Query<T> likeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return this; }
	
	public Query<T> likeContains 		(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return this; }
	public Query<T> notLikeContains 	(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return this; }

	public Query<T> likeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return this; }
	public Query<T> likeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return this; }
	public Query<T> notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return this; }

	public Query<T> nativeCriterion 	(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }
	
	public Query<T> inSubQuery			(Class<?> entity, String attribute, Query subQuery){ super.inSubQuery(entity, attribute, subQuery); return this; }
	public Query<T> notInSubQuery		(Class<?> entity, String attribute, Query subQuery){ super.notInSubQuery(entity, attribute, subQuery); return this; }

	
	public Query<T> notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public Query<T> inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public Query<T> notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	
	public Query<T> likeIfNotNull 				(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query<T> likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public Query<T> likeEndsWithIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public Query<T> likeContainsIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return this; }

	public Query<T> likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeStartsWithInIfNotEmpty (Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }
	
	public Query<T> notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWithIfNotNull (Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public Query<T> notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public Query<T> notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return this; }

	public Query<T> matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }
	public Query<T> matchAgainst(Class<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public Query<T> matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query<T> matchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public Query<T> notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public Query<T> notMatchAgainst(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public Query<T> notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query<T> notMatchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }

	public Query<T> matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }
	public Query<T> matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public Query<T> notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public Query<T> notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	
	public Query<T> isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return this;}
	public Query<T> isNull		(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return this;}

	public Query<T> eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return this;	}
	public Query<T> ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return this;	}
	public Query<T> gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return this;	}
	public Query<T> lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return this;	}
	public Query<T> gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return this;	}


	public Query<T> eq   	(Entity<?> entity, String attribute, Object value)        		{ super.eq    	(entity, attribute, value);  		return this; }
	public Query<T> eqNullable(Entity<?> entity, String attribute, Object value)			{ super.eqNullable(entity, attribute, value); 		return this; }
	public Query<T> notEq	(Entity<?> entity, String attribute, Object value)        		{ super.notEq 	(entity, attribute, value);  		return this; }
	public Query<T> notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return this; }
	public Query<T> notEqNullable(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return this; }
	

	public Query<T> lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return this; }
	public Query<T> lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return this; }
	
	public Query<T> gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return this; }
	public Query<T> gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return this; }
	public Query<T> between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return this; }

	public Query<T> in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return this; }
	public Query<T> inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return this; }
	public Query<T> inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return this; }
	public Query<T> inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return this; }
	public Query<T> notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }
	public Query<T> notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return this; }
	public Query<T> notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return this; }
	
	public Query<T> like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return this; }
	public Query<T> likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return this; }
	public Query<T> likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return this; }
	public Query<T> likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return this; }

	public Query<T> notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return this; }
	public Query<T> notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return this; }
	public Query<T> notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return this; }
	public Query<T> notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return this; }

	public Query<T> likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return this; }
	public Query<T> likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return this; }
	public Query<T> likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return this; }
	public Query<T> likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return this; }

	public Query<T> notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return this; }
	public Query<T> notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return this; }
	public Query<T> notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return this; }
	public Query<T> notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return this; }

	public Query<T> eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query<T> ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return this; }
	public Query<T> gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query<T> lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return this; }
	public Query<T> gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return this; }
	public Query<T> betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return this; }
	
	public Query<T> notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public Query<T> inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public Query<T> notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	public Query<T> likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query<T> likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return this; }
	public Query<T> likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return this; }
	public Query<T> likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return this; }

	public Query<T> likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }

	
	public Query<T> notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public Query<T> notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return this; }
	public Query<T> notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return this; }
	public Query<T> notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return this; }
	
	public Query<T> isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public Query<T> isNull				(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return this;}
	public Query<T> eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	public Query<T> ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return this;	}
	public Query<T> gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return this;	}
	public Query<T> lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return this;}
	public Query<T> gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return this;}
	
	public Query<T> matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }	
	public Query<T> matchAgainst(Entity<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public Query<T> matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query<T> matchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public Query<T> notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public Query<T> notMatchAgainst(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public Query<T> notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public Query<T> notMatchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }
	
	public Query<T> matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }	
	public Query<T> matchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> matchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public Query<T> notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public Query<T> notMatchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public Query<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }



	public Query<T> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	public Query<T> add(EstivateNode node) { super.add(node); return this; }
	public Query<T> addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return this; }
	public Query<T> and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return this; }
	public Query<T> or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return this; }
	
	@Getter
	final Entity<T> entity;

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
	
	public Query(Class<T> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<T>(baseClass);
	}
	
	public Query(Entity<T> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}

	
	public Query<T> comment(String comment) {
		comments.add(comment);
		return this;
	}
	
	public Query<T> join(Join join) { 
		if(joins.stream().noneMatch(x -> x.leftEntity.equals(join.leftEntity) && x.rightEntity.equals(join.rightEntity) && x.joinType == join.joinType)) {
			joins.add(join);
		}
		return this;
	}
		
	public Query<T> joinInner(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinInner(leftEntity, rightEntity)); }
	public Query<T> joinInner(Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass)));}
	public Query<T> joinInner(Class<?> leftClass, 			Entity<?> rightEntity)	{ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity));}
	public Query<T> joinInner(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinInner(new Entity<>(joinerEntity), new Entity<>(joinedEntity))); }

	public Query<T> joinOuter(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinOuter(leftEntity, rightEntity)); }
	public Query<T> joinOuter(Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinOuter(Class<?> leftClass, 			Entity<?> rightEntity)	{ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinOuter(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinOuter(new Entity<>(joinerEntity), new Entity<>(joinedEntity))); }

	public Query<T> joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinLeft(leftEntity, rightEntity)); }
	public Query<T> joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinLeft(Class<?> leftClass, 			Entity<?> rightEntity)	{ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinLeft(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinLeft(new Entity<>(joinerEntity), new Entity<>(joinedEntity))); }

	public Query<T> joinRight(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinRight(leftEntity, rightEntity)); }
	public Query<T> joinRight(Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinRight(Class<?> leftClass, 			Entity<?> rightEntity)	{ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinRight(Class<?> joinerEntity, 		Class<?> joinedEntity)			{ return join(Estivate.joinRight(new Entity<>(joinerEntity), new Entity<>(joinedEntity))); }


	public Query<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinInner(new Entity<>(joinerEntity), new Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }
	public Query<T> joinInner(Class<?> joinerEntity, SubQueryEntity<?> joinedQuery, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinInner(new Entity<>(joinerEntity), joinedQuery, joinerAttribute, joinedAttribute)); }

	public Query<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinOuter(new Entity<>(joinerEntity), new Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }
	public Query<T> joinOuter(Class<?> joinerEntity, SubQueryEntity<?> joinedQuery, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinOuter(new Entity<>(joinerEntity), joinedQuery, joinerAttribute, joinedAttribute)); }

	public Query<T> joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinLeft(new Entity<>(joinerEntity), new Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }
	public Query<T> joinLeft(Class<?> joinerEntity, SubQueryEntity<?> joinedQuery, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinLeft(new Entity<>(joinerEntity), joinedQuery, joinerAttribute, joinedAttribute)); }

	public Query<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinRight(new Entity<>(joinerEntity), new Entity<>(joinedEntity), joinerAttribute, joinedAttribute)); }
	public Query<T> joinRight(Class<?> joinerEntity, SubQueryEntity<?> joinedQuery, String joinerAttribute, String joinedAttribute){ return join(Estivate.joinRight(new Entity<>(joinerEntity), joinedQuery, joinerAttribute, joinedAttribute)); }

	public Query<T> order(Order order) { orders.add(order); return this; }
	public Query<T> order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
		orders.add(Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build()); 
		return this; 
	}
	public Query<T> order(Class<?> entity, String attribute, Order.Direction direction, Function function) { return order(new Entity<>(entity), attribute, direction, function); }
	public Query<T> order(String attribute, Order.Direction direction, Function function) { return order(this.entity, attribute, direction, function); }
	public Query<T> order(Entity<?> entity, String attribute, Order.Direction direction) { return order(entity, attribute, direction, null); }
	public Query<T> order(Class<?> entity, String attribute, Order.Direction direction) { return order(new Entity<>(entity), attribute, direction, null); }
	public Query<T> order(String attribute, Order.Direction direction) { return order(this.entity, attribute, direction, null); }
	public Query<T> order(Attribute attribute, Order.Direction direction) { return order(attribute.entity, attribute.attribute, direction, attribute.function); }

	public Query<T> orderAsc(Entity<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(Entity<?> c, String attribute, Function function) { return order(c, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(Class<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(String attribute, Function function) { return order(this.entity, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(Attribute attribute) { return order(attribute.entity, attribute.attribute, Order.Direction.Asc, attribute.function); }
	
	public Query<T> orderDesc(Entity<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(Entity<?> c, String attribute, Function function){ return order(c, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(Class<?> c, String attribute) 				{ return order(c, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(Class<?> c, String attribute, Function function) { return order(c, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(String attribute, Function function) { return order(this.entity, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(Attribute attribute) { return order(attribute.entity, attribute.attribute, Order.Direction.Desc, attribute.function); }
	
	public Query<T> limit(Integer limit) 		{ this.limit = limit; return this; }
	public Query<T> offset(Integer offset) 	{ this.offset = offset; return this;}
	

	
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
	
	
	public Query<T> selectFunctionAs(Entity<?> c, String attribute, Attribute.Function function, String alias) {
		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}

		selects.add(Select.builder().entity(c).attribute(attribute).alias(alias).function(function).build());
		return this;
	}

	public Query<T> selectFunctionAs(Class<?> c, String attribute, Attribute.Function function, String alias) { return selectFunctionAs(new Entity<>(c), attribute, function, alias); }

	/* Wrappers */
	public Query<T> select(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, null, null); }
	public Query<T> select(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, null, null); }
	public Query<T> selectAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, null, alias); }
	public Query<T> selectAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, null, alias); }	
	public Query<T> selectFunction(Class<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(new Entity<>(c), attribute, function, null); }
	public Query<T> selectFunction(Entity<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(c, attribute, function, null); }
	public Query<T> selectAttribute(Attribute attribute) { return selectFunctionAs(attribute.entity, attribute.attribute, attribute.function, null); }
	
	public Query<T> selectAll(Class<?> entity, String...fields) { return selectAll(new Entity<>(entity), fields); }	
	public Query<T> selectAll(Entity<?> c, String... fields) {
		
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

	
	public Query<T> selectDistinct(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, null); }
	public Query<T> selectDistinct(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, null); }
	public Query<T> selectDistinct(String attribute) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, null); }
	public Query<T> selectDistinctAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, alias); }
	public Query<T> selectDistinctAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, alias); }
	public Query<T> selectDistinctAs(String attribute, String alias) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, alias); }

	// Select count
	public Query<T> selectCount() { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, null); }
	public Query<T> selectCountAs(String alias) { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count field 
	public Query<T> selectCount(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, null); }
	public Query<T> selectCount(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.count, null); }
	public Query<T> selectCount(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, null); }
	public Query<T> selectCountAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public Query<T> selectCountAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.count, alias); }
	public Query<T> selectCountAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, alias); }
	
	// Select count distinct field
	public Query<T> selectCountDistinct(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, null); }
	public Query<T> selectCountDistinct(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, null); }
	public Query<T> selectCountDistinct(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, null); }
	public Query<T> selectCountDistinctAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public Query<T> selectCountDistinctAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, alias); }
	public Query<T> selectCountDistinctAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	
	// Select min
	public Query<T> selectMin(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, null); }
	public Query<T> selectMin(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.min, null); }
	public Query<T> selectMin(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, null); }
	public Query<T> selectMinAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public Query<T> selectMinAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.min, alias); }
	public Query<T> selectMinAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, alias); }
	
	// Select max
	public Query<T> selectMax(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, null); }
	public Query<T> selectMax(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.max, null); }
	public Query<T> selectMax(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, null); }
	public Query<T> selectMaxAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public Query<T> selectMaxAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.max, alias); }
	public Query<T> selectMaxAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, alias); }

	// Select Sum
	public Query<T> selectSum(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, null); }
	public Query<T> selectSum(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, null); }
	public Query<T> selectSum(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, null); }
	public Query<T> selectSumAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public Query<T> selectSumAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, alias); }
	public Query<T> selectSumAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, alias); }
	
	// Select Group Concat
	public Query<T> selectGroupConcat(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, null); }
	public Query<T> selectGroupConcat(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, null); }
	public Query<T> selectGroupConcat(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, null); }
	public Query<T> selectGroupConcatAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public Query<T> selectGroupConcatAs(Entity<?> c, String attribute, String alias){ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, alias); }
	public Query<T> selectGroupConcatAs(String attribute, String alias) 			{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, alias); }


	// Having
	public Query<T> having(EstivateNode node) { this.having = node; return this; }
	
	@SuppressWarnings("unchecked")
	public Query<T> clone() {
		Query<T> queryClone = new Query<T>(entity);
		
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

	public Query<T> groupBy(Entity<?> entity, String field) { groupBys.add(new Group(entity, field)); return this; }
	public Query<T> groupBy(Class<?> c, String field) { return groupBy(new Entity<>(c), field); }
	
	public Query<T> setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}


	public T fetchSingle(Context context) {
		return context.fetchSingle(this);
	}

	public Optional<T> fetchSingleOptional(Context context) {
		return context.fetchSingleOptional(this);
	}

	public Result fetchSingleAsResult(Context context){
		return context.fetchSingleAsResult(this);
	}

	public Optional<Result> fetchSingleAsResultOptional(Context context) {
		return context.fetchSingleAsResultOptional(this);
	}

	public <U> U fetchSingleAs(Context context, Class<U> clazz) {
		return context.fetchSingleAs(this, clazz);
	}

	public Optional<T> fetchSingleAsOptional(Context context) {
		return context.fetchSingleAsOptional(this, (Class<T>) entity.entity);
	}


	public List<T> fetchList(Context context){
		return context.fetchListAs(this, (Class<T>) entity.entity);
	}

	public <U> List<U> fetchListAs(Context context, Class<U> clazz) {
		return context.fetchListAs(this, clazz);
	}

	public List<Result> fetchListAsResults(Context context) {
		return context.fetchListAsResults(this);
	}


	




}
