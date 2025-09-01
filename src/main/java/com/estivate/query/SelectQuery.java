package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class SelectQuery<T> extends Query<T>{
	


	public SelectQuery<T> eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return this; }
	public SelectQuery<T> eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return this; }
	public SelectQuery<T> eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return this; }
	public SelectQuery<T> notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return this; }
	public SelectQuery<T> notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return this; }
	public SelectQuery<T> notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return this; }
	
	public SelectQuery<T> lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return this; }
	public SelectQuery<T> ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return this; }
	
	public SelectQuery<T> lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return this; }
	public SelectQuery<T> lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return this; }
	
	
	public SelectQuery<T> gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return this; }
	public SelectQuery<T> gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return this; }
	public SelectQuery<T> gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return this; }
	public SelectQuery<T> gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return this; }
	
	public SelectQuery<T> between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return this; }
	public SelectQuery<T> betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return this; }

	public SelectQuery<T> in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return this; }
	public SelectQuery<T> inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return this; }
	public SelectQuery<T> inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return this; }
	public SelectQuery<T> notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return this; }
	public SelectQuery<T> notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return this; }
	public SelectQuery<T> notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return this; }


	public SelectQuery<T> like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return this; }
	public SelectQuery<T> likeIn 		(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return this; }
	public SelectQuery<T> notLike		(Attribute attribute, String value)				{ super.notLike(attribute, value);  return this; }
	public SelectQuery<T> notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return this; }
	
	
	public SelectQuery<T> likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return this; }
	
	public SelectQuery<T> likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return this; }
	
	public SelectQuery<T> likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return this; }
	public SelectQuery<T> notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return this; }

	public SelectQuery<T> likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return this; }
	public SelectQuery<T> likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return this; }

	public SelectQuery<T> nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return this; }
	
	public SelectQuery<T> inSubQuery			(Attribute attribute, SelectQuery<?> subQuery){ super.inSubQuery(attribute, subQuery); return this; }
	public SelectQuery<T> notInSubQuery		(Attribute attribute, SelectQuery<?> subQuery){ super.notInSubQuery(attribute, subQuery); return this; }
	public SelectQuery<T> exists		(SelectQuery<?> subQuery){ super.exists(subQuery); return this; }
	public SelectQuery<T> notExists	(SelectQuery<?> subQuery){ super.notExists(subQuery); return this; }

	
	public SelectQuery<T> notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return this; }
	public SelectQuery<T> inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return this; }
	public SelectQuery<T> notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return this; }
	
	
	public SelectQuery<T> likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return this; }

	public SelectQuery<T> likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return this; }
	public SelectQuery<T> likeStartsWithInIfNotEmpty  (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return this; }
	public SelectQuery<T> likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return this; }
	public SelectQuery<T> likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return this; }
	
	public SelectQuery<T> notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return this; }

	public SelectQuery<T> matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return this; }

	public SelectQuery<T> matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return this; }

	public SelectQuery<T> notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return this; }

	public SelectQuery<T> notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return this; }


	public SelectQuery<T> matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return this; }
	
	public SelectQuery<T> isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return this;}
	public SelectQuery<T> isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return this;}

	public SelectQuery<T> eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return this;	}
	public SelectQuery<T> ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return this;	}
	public SelectQuery<T> gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return this;	}
	public SelectQuery<T> lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return this;	}
	public SelectQuery<T> gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return this;	}


	// Attribute only wrappers

	public SelectQuery<T> eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return this; }
	public SelectQuery<T> eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return this; }
	public SelectQuery<T> eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return this; }
	public SelectQuery<T> notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return this; }
	
	public SelectQuery<T> lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return this; }
	public SelectQuery<T> ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return this; }
	
	public SelectQuery<T> lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return this; }
	public SelectQuery<T> lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return this; }
	
	
	public SelectQuery<T> gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return this; }
	public SelectQuery<T> gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return this; }
	public SelectQuery<T> gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return this; }
	public SelectQuery<T> gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return this; }
	
	public SelectQuery<T> between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return this; }
	public SelectQuery<T> betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return this; }

	public SelectQuery<T> in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return this; }
	public SelectQuery<T> inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return this; }
	public SelectQuery<T> notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return this; }
	public SelectQuery<T> notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return this; }
	public SelectQuery<T> notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return this; }


	public SelectQuery<T> like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return this; }
	
	
	public SelectQuery<T> likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return this; }
	
	public SelectQuery<T> likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return this; }
	
	public SelectQuery<T> likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return this; }

	public SelectQuery<T> likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return this; }

	public SelectQuery<T> nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return this; }
	
	public SelectQuery<T> inSubQuery			(String attribute, SelectQuery subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return this; }
	public SelectQuery<T> notInSubQuery		(String attribute, SelectQuery subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return this; }

	
	public SelectQuery<T> notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return this; }
	public SelectQuery<T> inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return this; }
	public SelectQuery<T> notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return this; }
	
	
	public SelectQuery<T> likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public SelectQuery<T> likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return this; }
	public SelectQuery<T> likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public SelectQuery<T> likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public SelectQuery<T> likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public SelectQuery<T> notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public SelectQuery<T> matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return this; }

	public SelectQuery<T> matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return this; }

	public SelectQuery<T> notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return this; }

	public SelectQuery<T> notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return this; }


	public SelectQuery<T> matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public SelectQuery<T> isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return this;}
	public SelectQuery<T> isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return this;}

	public SelectQuery<T> eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return this;	}
	public SelectQuery<T> ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return this;	}
	public SelectQuery<T> gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return this;	}
	public SelectQuery<T> lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return this;	}
	public SelectQuery<T> gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return this;	}


	// Class wrappers
	public SelectQuery<T> eq   			(Class<?> entity, String attribute, Object value)	{ super.eq(entity, attribute, value);  return this; }
	public SelectQuery<T> eqIfNotNull   (Class<?> entity, String attribute, Object value)   { super.eqIfNotNull(entity, attribute, value);  return this; }
	public SelectQuery<T> eqNullable	(Class<?> entity, String attribute, Object value)   { super.eqNullable(entity, attribute, value); return this; }
	public SelectQuery<T> notEq			(Class<?> entity, String attribute, Object value)   { super.notEq(entity, attribute, value);  return this; }
	public SelectQuery<T> notEqIfNotNull(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public SelectQuery<T> notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return this; }
	
	public SelectQuery<T> lt   			(Class<?> entity, String attribute, Object value)   { super.lt(entity, attribute, value);  return this; }
	public SelectQuery<T> ltIfNotNull	(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull(entity, attribute, value);  return this; }
	
	public SelectQuery<T> lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return this; }
	public SelectQuery<T> lteIfNotNull  (Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return this; }
	
	
	public SelectQuery<T> gt   			(Class<?> entity, String attribute, Object value)   { super.gt    (entity, attribute, value);  return this; }
	public SelectQuery<T> gtIfNotNull 	(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public SelectQuery<T> gte  			(Class<?> entity, String attribute, Object value)   { super.gte   (entity, attribute, value);  return this; }
	public SelectQuery<T> gteIfNotNull	(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return this; }
	
	public SelectQuery<T> between			(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return this; }
	public SelectQuery<T> betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return this; }

	public SelectQuery<T> in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmpty  		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyNullable  (Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return this; }
	public SelectQuery<T> inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return this; }
	public SelectQuery<T> notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }
	public SelectQuery<T> notInOrNull		  	(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return this; }
	public SelectQuery<T> notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return this; }
	


	public SelectQuery<T> like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return this; }
	public SelectQuery<T> likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return this; }
	public SelectQuery<T> notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return this; }
	
	
	public SelectQuery<T> likeStartsWith 		(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return this; }
	
	public SelectQuery<T> likeEndsWith 			(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return this; }
	
	public SelectQuery<T> likeContains 			(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContains 		(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return this; }

	public SelectQuery<T> likeEndsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return this; }
	public SelectQuery<T> likeContainsIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return this; }

	public SelectQuery<T> nativeCriterion 		(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }
	
	public SelectQuery<T> inSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.inSubQuery(entity, attribute, subQuery); return this; }
	public SelectQuery<T> notInSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(entity, attribute, subQuery); return this; }

	
	public SelectQuery<T> notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public SelectQuery<T> inOrFalseIfEmpty		(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public SelectQuery<T> notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	
	public SelectQuery<T> likeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> likeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> likeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return this; }

	public SelectQuery<T> likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }
	
	public SelectQuery<T> notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return this; }

	public SelectQuery<T> matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }
	public SelectQuery<T> matchAgainst(Class<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public SelectQuery<T> matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public SelectQuery<T> matchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public SelectQuery<T> notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public SelectQuery<T> notMatchAgainst(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public SelectQuery<T> notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public SelectQuery<T> notMatchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }

	public SelectQuery<T> matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }
	public SelectQuery<T> matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	
	public SelectQuery<T> isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return this;}
	public SelectQuery<T> isNull	(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return this;}

	public SelectQuery<T> eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return this;	}


	public SelectQuery<T> eq   			(Entity<?> entity, String attribute, Object value)      { super.eq    	(entity, attribute, value);  		return this; }
	public SelectQuery<T> eqNullable	(Entity<?> entity, String attribute, Object value)		{ super.eqNullable(entity, attribute, value); 		return this; }
	public SelectQuery<T> notEq			(Entity<?> entity, String attribute, Object value)      { super.notEq 	(entity, attribute, value);  		return this; }
	public SelectQuery<T> notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return this; }
	public SelectQuery<T> notEqNullable	(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return this; }
	

	public SelectQuery<T> lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return this; }
	public SelectQuery<T> lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return this; }
	
	public SelectQuery<T> gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return this; }
	public SelectQuery<T> gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return this; }
	public SelectQuery<T> between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return this; }

	public SelectQuery<T> in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return this; }
	public SelectQuery<T> inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return this; }
	public SelectQuery<T> inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return this; }
	public SelectQuery<T> inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return this; }
	public SelectQuery<T> notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }
	public SelectQuery<T> notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return this; }
	public SelectQuery<T> notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return this; }
	
	public SelectQuery<T> like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return this; }
	public SelectQuery<T> likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return this; }
	public SelectQuery<T> likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return this; }
	public SelectQuery<T> likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return this; }

	public SelectQuery<T> notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return this; }
	public SelectQuery<T> notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return this; }
	public SelectQuery<T> notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return this; }
	public SelectQuery<T> notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return this; }

	public SelectQuery<T> likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return this; }
	public SelectQuery<T> likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return this; }
	public SelectQuery<T> likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return this; }
	public SelectQuery<T> likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return this; }

	public SelectQuery<T> notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return this; }
	public SelectQuery<T> notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return this; }
	public SelectQuery<T> notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return this; }
	public SelectQuery<T> notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return this; }

	public SelectQuery<T> eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return this; }
	public SelectQuery<T> ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return this; }
	public SelectQuery<T> gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return this; }
	public SelectQuery<T> lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return this; }
	public SelectQuery<T> gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return this; }
	public SelectQuery<T> betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return this; }
	
	public SelectQuery<T> notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public SelectQuery<T> inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public SelectQuery<T> notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	public SelectQuery<T> likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return this; }
	public SelectQuery<T> likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return this; }
	public SelectQuery<T> likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return this; }

	public SelectQuery<T> likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }

	
	public SelectQuery<T> notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public SelectQuery<T> notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return this; }
	public SelectQuery<T> notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return this; }
	public SelectQuery<T> notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return this; }
	
	public SelectQuery<T> isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public SelectQuery<T> isNull				(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return this;}
	public SelectQuery<T> eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return this;	}
	public SelectQuery<T> lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return this;}
	public SelectQuery<T> gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return this;}
	
	public SelectQuery<T> matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }	
	public SelectQuery<T> matchAgainst(Entity<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public SelectQuery<T> matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public SelectQuery<T> matchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public SelectQuery<T> notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public SelectQuery<T> notMatchAgainst(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public SelectQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public SelectQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }
	
	public SelectQuery<T> matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }	
	public SelectQuery<T> matchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public SelectQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }



	public SelectQuery<T> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	public SelectQuery<T> add(EstivateNode node) { super.add(node); return this; }
	public SelectQuery<T> addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return this; }
	public SelectQuery<T> and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return this; }
	public SelectQuery<T> or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return this; }
	
	

	
	@Getter
	Set<Select> selects = new LinkedHashSet<>();
	
	
	
	@Getter
	List<Group> groupBys = new ArrayList<>();

	@Getter
	EstivateNode having;
	

	@Getter
	IndexHint indexHint;

	@Getter
	Set<String> indexNames = new LinkedHashSet<>();
	
	public SelectQuery(Class<T> baseClass) {
		super(baseClass);
	}
	
	public SelectQuery(Entity<T> entity) {
		super(entity);
	}

	
	public SelectQuery<T> comment(String comment) {
		super.comment(comment);
		return this;
	}
	
	public SelectQuery<T> join(Join join) { 
		super.join(join);
		return this;
	}
		
	public SelectQuery<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinInner(leftEntity, rightEntity); return this; }
	public SelectQuery<T> joinInner(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinInner(leftEntity, rightClass); return this;}
	public SelectQuery<T> joinInner(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinInner(leftClass, rightEntity); return this; }
	public SelectQuery<T> joinInner(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinInner(leftClass, rightClass); return this; }

	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinOuter(leftEntity, rightEntity); return this; }
	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinOuter(leftEntity, rightClass); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinOuter(leftClass, rightEntity); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinOuter(leftClass, rightClass); return this; }

	public SelectQuery<T> joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)			{ super.joinLeft(leftEntity, rightEntity); return this; }
	public SelectQuery<T> joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)			{ super.joinLeft(leftEntity, rightClass); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinLeft(leftClass, rightEntity); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinLeft(leftClass, rightClass); return this; }

	public SelectQuery<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinRight(leftEntity, rightEntity); return this; }
	public SelectQuery<T> joinRight(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinRight(leftEntity, rightClass); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinRight(leftClass, rightEntity); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinRight(leftClass, rightClass); return this; }


	public SelectQuery<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }

	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }

	public SelectQuery<T> joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }

	public SelectQuery<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
	public SelectQuery<T> joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }

	public SelectQuery<T> order(Order order) { super.order(order); return this; }
	public SelectQuery<T> order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
		super.order(entity, attribute, direction, function);
		return this;
	}
	public SelectQuery<T> order(Class<?> entity, String attribute, Order.Direction direction, Function function) { super.order(entity, attribute, direction, function); return this; }
	public SelectQuery<T> order(String attribute, Order.Direction direction, Function function) { super.order(this.entity, attribute, direction, function); return this; }
	public SelectQuery<T> order(Entity<?> entity, String attribute, Order.Direction direction) { super.order(entity, attribute, direction, null); return this; }
	public SelectQuery<T> order(Class<?> entity, String attribute, Order.Direction direction) { super.order(new Entity<>(entity), attribute, direction, null); return this; }
	public SelectQuery<T> order(String attribute, Order.Direction direction) { super.order(this.entity, attribute, direction, null); return this; }
	public SelectQuery<T> order(Attribute attribute, Order.Direction direction) { super.order(attribute.entity, attribute.attribute, direction, attribute.function); return this; }
	public SelectQuery<T> orderAlias(String alias, Order.Direction direction) { super.orderAlias(alias, direction); return this; }

	public SelectQuery<T> orderAsc(Entity<?> c, String attribute) { super.orderAsc(c, attribute); return this; }
	public SelectQuery<T> orderAsc(Entity<?> c, String attribute, Function function) { super.orderAsc(c, attribute, function); return this; }
	public SelectQuery<T> orderAsc(Class<?> c, String attribute) { super.orderAsc(c, attribute); return this; }
	public SelectQuery<T> orderAsc(Class<?> c, String attribute, Function function) { super.orderAsc(c, attribute, function); return this; }
	public SelectQuery<T> orderAsc(String attribute) { super.orderAsc(attribute); return this; }
	public SelectQuery<T> orderAsc(String attribute, Function function) { super.orderAsc(attribute, function); return this; }
	public SelectQuery<T> orderAsc(Attribute attribute) { super.orderAsc(attribute); return this; }
	public SelectQuery<T> orderAscAlias(String alias){ super.orderAscAlias(alias); return this; }
	
	public SelectQuery<T> orderDesc(Entity<?> c, String attribute) { super.orderDesc(c, attribute); return this; }
	public SelectQuery<T> orderDesc(Entity<?> c, String attribute, Function function){ super.orderDesc(c, attribute, function); return this; }
	public SelectQuery<T> orderDesc(Class<?> c, String attribute) { super.orderDesc(c, attribute); return this; }
	public SelectQuery<T> orderDesc(Class<?> c, String attribute, Function function) { super.orderDesc(c, attribute, function); return this; }
	public SelectQuery<T> orderDesc(String attribute) { super.orderDesc(attribute); return this; }
	public SelectQuery<T> orderDesc(String attribute, Function function) { super.orderDesc(attribute, function); return this; }
	public SelectQuery<T> orderDesc(Attribute attribute) { super.orderDesc(attribute); return this; }
	public SelectQuery<T> orderDescAlias(String alias) { super.orderDescAlias(alias); return this; }
	
	public SelectQuery<T> limit(Integer limit) { this.limit = limit; return this; }
	public SelectQuery<T> limitIfNotNull(Integer limit) { if(limit != null) { this.limit = limit; } return this; }
	public SelectQuery<T> limitIfNotNullOr(Integer limit, Integer fallbackLimit) { if(limit != null) { this.limit = limit; } else { this.limit = fallbackLimit; } return this; }
	public SelectQuery<T> offset(Integer offset) { this.offset = offset; return this;}
	public SelectQuery<T> offsetIfNotNull(Integer offset) { if(offset != null) { this.offset = offset; } return this; }
	public SelectQuery<T> offsetIfNotNullOr(Integer offset, Integer fallbackOffset) { if(offset != null) { this.offset = offset; } else { this.offset = fallbackOffset; } return this; }
	
	
	
	@AllArgsConstructor
	public static class Group{
		public Entity<?> entity;
		public String attribute;
	}
	
	
	public SelectQuery<T> selectFunctionAs(Entity<?> c, String attribute, Attribute.Function function, String alias) {
		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}

		selects.add(Select.builder().entity(c).attribute(attribute).alias(alias).function(function).build());
		return this;
	}

	public SelectQuery<T> selectFunctionAs(Class<?> c, String attribute, Attribute.Function function, String alias) { return selectFunctionAs(new Entity<>(c), attribute, function, alias); }

	/* Wrappers */
	public SelectQuery<T> select(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, null, null); }
	public SelectQuery<T> select(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, null, null); }
	public SelectQuery<T> selectAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, null, alias); }
	public SelectQuery<T> selectAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, null, alias); }	
	public SelectQuery<T> selectFunction(Class<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(new Entity<>(c), attribute, function, null); }
	public SelectQuery<T> selectFunction(Entity<?> c, String attribute, Attribute.Function function) { return selectFunctionAs(c, attribute, function, null); }
	public SelectQuery<T> selectAttribute(Attribute attribute) { return selectFunctionAs(attribute.entity, attribute.attribute, attribute.function, null); }
	
	public SelectQuery<T> selectAll(Class<?> entity, String...fields) { return selectAll(new Entity<>(entity), fields); }	
	public SelectQuery<T> selectAll(Entity<?> c, String... fields) {
		
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

	
	public SelectQuery<T> selectDistinct(Class<?> c, String attribute) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, null); }
	public SelectQuery<T> selectDistinct(Entity<?> c, String attribute) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, null); }
	public SelectQuery<T> selectDistinct(String attribute) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, null); }
	public SelectQuery<T> selectDistinctAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.distinct, alias); }
	public SelectQuery<T> selectDistinctAs(Entity<?> c, String attribute, String alias) { return selectFunctionAs(c, attribute, Estivate.Functions.distinct, alias); }
	public SelectQuery<T> selectDistinctAs(String attribute, String alias) { return selectFunctionAs(this.entity, attribute, Estivate.Functions.distinct, alias); }

	// Select count
	public SelectQuery<T> selectCount() { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, null); }
	public SelectQuery<T> selectCountAs(String alias) { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count field 
	public SelectQuery<T> selectCount(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, null); }
	public SelectQuery<T> selectCount(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.count, null); }
	public SelectQuery<T> selectCount(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, null); }
	public SelectQuery<T> selectCountAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, alias); }
	
	// Select count distinct field
	public SelectQuery<T> selectCountDistinct(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, null); }
	public SelectQuery<T> selectCountDistinct(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, null); }
	public SelectQuery<T> selectCountDistinct(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, null); }
	public SelectQuery<T> selectCountDistinctAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	
	// Select min
	public SelectQuery<T> selectMin(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, null); }
	public SelectQuery<T> selectMin(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.min, null); }
	public SelectQuery<T> selectMin(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, null); }
	public SelectQuery<T> selectMinAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, alias); }
	
	// Select max
	public SelectQuery<T> selectMax(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, null); }
	public SelectQuery<T> selectMax(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.max, null); }
	public SelectQuery<T> selectMax(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, null); }
	public SelectQuery<T> selectMaxAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, alias); }

	// Select Sum
	public SelectQuery<T> selectSum(Class<?> c, String attribute) 					{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, null); }
	public SelectQuery<T> selectSum(Entity<?> c, String attribute) 					{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, null); }
	public SelectQuery<T> selectSum(String attribute) 								{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, null); }
	public SelectQuery<T> selectSumAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, alias); }
	
	// Select Group Concat
	public SelectQuery<T> selectGroupConcat(Class<?> c, String attribute) 				{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, null); }
	public SelectQuery<T> selectGroupConcat(Entity<?> c, String attribute) 				{ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, null); }
	public SelectQuery<T> selectGroupConcat(String attribute) 							{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, null); }
	public SelectQuery<T> selectGroupConcatAs(Class<?> c, String attribute, String alias) { return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(Entity<?> c, String attribute, String alias){ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(String attribute, String alias) 			{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, alias); }

	public SelectQuery<T> clearSelects(){ selects.clear(); return this; }
	
	// Having
	public SelectQuery<T> having(EstivateNode node) { this.having = node; return this; }
	
	@SuppressWarnings("unchecked")
	public SelectQuery<T> clone() {
		SelectQuery<T> queryClone = new SelectQuery<T>(entity);
		
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

	public SelectQuery<T> groupBy(Entity<?> entity, String field) { groupBys.add(new Group(entity, field)); return this; }
	public SelectQuery<T> groupBy(Class<?> c, String field) { return groupBy(new Entity<>(c), field); }
	public SelectQuery<T> groupBy(String attribute) { return groupBy(this.entity, attribute); }
	public SelectQuery<T> groupByAlias(String alias) { groupBys.add(new Group(null, alias)); return this; }
	public SelectQuery<T> clearGroupBys(){ groupBys.clear(); return this; }
	
	public SelectQuery<T> setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}


	public T 		fetchSingle(Context context) 					{ return context.fetchSingle(this); }
	public Result 	fetchSingleAsResult(Context context)			{ return context.fetchSingleAsResult(this); }
	public <U> U 	fetchSingleAs(Context context, Class<U> clazz) 	{ return context.fetchSingleAs(this, clazz); }
	public String 	fetchSingleAsString(Context context) 			{ return context.fetchSingleAsString(this); }
	public Short 	fetchSingleAsShort(Context context) 			{ return context.fetchSingleAsShort(this); }
	public Integer 	fetchSingleAsInteger(Context context) 			{ return context.fetchSingleAsInteger(this); }
	public Long 	fetchSingleAsLong(Context context) 				{ return context.fetchSingleAsLong(this); }
	public Float 	fetchSingleAsFloat(Context context) 			{ return context.fetchSingleAsFloat(this); }
	public Double 	fetchSingleAsDouble(Context context) 			{ return context.fetchSingleAsDouble(this); }
	public Date 	fetchSingleAsDate(Context context) 				{ return context.fetchSingleAsDate(this); }
	public Boolean 	fetchSingleAsBoolean(Context context) 			{ return context.fetchSingleAsBoolean(this); }
	
	public Optional<T> 			fetchSingleOptional(Context context) 			{ return context.fetchSingleOptional(this); }
	public Optional<Result> 	fetchSingleAsResultOptional(Context context) 	{ return context.fetchSingleAsResultOptional(this); }
	public Optional<T> 			fetchSingleAsOptional(Context context) 			{ return context.fetchSingleAsOptional(this, (Class<T>) entity.entity); }
	public Optional<String>		fetchSingleAsStringOptional(Context context) 	{ return context.fetchSingleAsStringOptional(this); }
	public Optional<Short>		fetchSingleAsShortOptional(Context context) 	{ return context.fetchSingleAsShortOptional(this); }
	public Optional<Integer>	fetchSingleAsIntegerOptional(Context context) 	{ return context.fetchSingleAsIntegerOptional(this); }
	public Optional<Long>		fetchSingleAsLongOptional(Context context) 		{ return context.fetchSingleAsLongOptional(this); }
	public Optional<Float>		fetchSingleAsFloatOptional(Context context) 	{ return context.fetchSingleAsFloatOptional(this); }
	public Optional<Double>		fetchSingleAsDoubleOptional(Context context) 	{ return context.fetchSingleAsDoubleOptional(this); }
	public Optional<Date>		fetchSingleAsDateOptional(Context context) 		{ return context.fetchSingleAsDateOptional(this); }
	public Optional<Boolean>	fetchSingleAsBooleanOptional(Context context) 	{ return context.fetchSingleAsBooleanOptional(this); }

	public List<T> fetchList(Context context){ return context.fetchListAs(this, (Class<T>) entity.entity); }
	public <U> List<U> fetchListAs(Context context, Class<U> clazz) { return context.fetchListAs(this, clazz); }
	public List<Result> fetchListAsResults(Context context) { return context.fetchListAsResults(this); }
	public List<String> fetchListAsString(Context context) { return context.fetchListAsString(this); }
	public List<Short> fetchListAsShort(Context context) { return context.fetchListAsShort(this); }
	public List<Integer> fetchListAsInteger(Context context) { return context.fetchListAsInteger(this); }
	public List<Long> fetchListAsLong(Context context) { return context.fetchListAsLong(this); }
	public List<Float> fetchListAsFloat(Context context) { return context.fetchListAsFloat(this); }
	public List<Double> fetchListAsDouble(Context context) { return context.fetchListAsDouble(this); }
	public List<Date> fetchListAsDate(Context context) { return context.fetchListAsDate(this); }
	public List<Boolean> fetchListAsBoolean(Context context) { return context.fetchListAsBoolean(this); }
	
	

	public <U, V> Map<U, V> aggregateToMap(Context context, java.util.function.Function<Result,U> uType, java.util.function.Function<Result,V> vType){
		return context.aggregateToMap(this, uType, vType);
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(Context context, java.util.function.Function<Result,U> uType, java.util.function.Function<Result,V> vType){
		return context.aggregateToMapList(this, uType, vType);
	}



	public SubQueryEntity<T> asSubQueryEntity(String alias){
		return Estivate.subQueryEntity(this, alias);
	}


	




}
