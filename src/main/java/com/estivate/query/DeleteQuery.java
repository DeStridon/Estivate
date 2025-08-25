package com.estivate.query;

import java.util.Collection;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;

import lombok.Getter;

public class DeleteQuery <T> extends Aggregator{

    @Getter
	final Entity<T> entity;

    public DeleteQuery(Class<T> baseClass) {
        super(GroupType.AND);
        this.entity = new Entity<T>(baseClass);
    }

    public DeleteQuery(Entity<T> entity) {
        super(GroupType.AND);
        this.entity = entity;
    }

    public DeleteQuery<T> eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return this; }
	public DeleteQuery<T> eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return this; }
	public DeleteQuery<T> eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return this; }
	public DeleteQuery<T> notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return this; }
	public DeleteQuery<T> notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return this; }
	public DeleteQuery<T> notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return this; }
	
	public DeleteQuery<T> lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return this; }
	public DeleteQuery<T> ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return this; }
	
	public DeleteQuery<T> lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return this; }
	public DeleteQuery<T> lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return this; }
	
	
	public DeleteQuery<T> gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return this; }
	public DeleteQuery<T> gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return this; }
	public DeleteQuery<T> gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return this; }
	public DeleteQuery<T> gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return this; }
	
	public DeleteQuery<T> between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return this; }
	public DeleteQuery<T> betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return this; }

	public DeleteQuery<T> in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return this; }
	public DeleteQuery<T> inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return this; }
	public DeleteQuery<T> notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return this; }
	public DeleteQuery<T> notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return this; }
	public DeleteQuery<T> notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return this; }


	public DeleteQuery<T> like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return this; }
	public DeleteQuery<T> likeIn 		(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return this; }
	public DeleteQuery<T> notLike		(Attribute attribute, String value)				{ super.notLike(attribute, value);  return this; }
	public DeleteQuery<T> notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return this; }
	
	
	public DeleteQuery<T> likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return this; }
	
	public DeleteQuery<T> likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return this; }
	
	public DeleteQuery<T> likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return this; }
	public DeleteQuery<T> notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return this; }

	public DeleteQuery<T> likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return this; }

	public DeleteQuery<T> nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return this; }
	
	public DeleteQuery<T> inSubQuery			(Attribute attribute, Query<?> subQuery){ super.inSubQuery(attribute, subQuery); return this; }
	public DeleteQuery<T> notInSubQuery		(Attribute attribute, Query<?> subQuery){ super.notInSubQuery(attribute, subQuery); return this; }
	public DeleteQuery<T> exists		(Query<?> subQuery){ super.exists(subQuery); return this; }
	public DeleteQuery<T> notExists	(Query<?> subQuery){ super.notExists(subQuery); return this; }

	
	public DeleteQuery<T> notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return this; }
	public DeleteQuery<T> inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return this; }
	public DeleteQuery<T> notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return this; }
	
	
	public DeleteQuery<T> likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return this; }

	public DeleteQuery<T> likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return this; }
	public DeleteQuery<T> likeStartsWithInIfNotEmpty  (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return this; }
	public DeleteQuery<T> likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return this; }
	public DeleteQuery<T> likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return this; }
	
	public DeleteQuery<T> notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return this; }

	public DeleteQuery<T> matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return this; }

	public DeleteQuery<T> matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return this; }

	public DeleteQuery<T> notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return this; }

	public DeleteQuery<T> notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return this; }


	public DeleteQuery<T> matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return this; }
	
	public DeleteQuery<T> isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return this;}
	public DeleteQuery<T> isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return this;}

	public DeleteQuery<T> eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return this;	}
	public DeleteQuery<T> ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return this;	}
	public DeleteQuery<T> gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return this;	}
	public DeleteQuery<T> lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return this;	}
	public DeleteQuery<T> gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return this;	}


	// Attribute only wrappers

	public DeleteQuery<T> eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return this; }
	public DeleteQuery<T> notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return this; }
	
	public DeleteQuery<T> lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return this; }
	
	public DeleteQuery<T> lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return this; }
	
	
	public DeleteQuery<T> gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return this; }
	
	public DeleteQuery<T> between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return this; }
	public DeleteQuery<T> betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return this; }

	public DeleteQuery<T> in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return this; }
	public DeleteQuery<T> inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return this; }
	public DeleteQuery<T> notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return this; }
	public DeleteQuery<T> notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return this; }
	public DeleteQuery<T> notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return this; }


	public DeleteQuery<T> like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return this; }
	
	
	public DeleteQuery<T> likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return this; }
	
	public DeleteQuery<T> likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return this; }
	
	public DeleteQuery<T> likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return this; }

	public DeleteQuery<T> likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return this; }

	public DeleteQuery<T> nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return this; }
	
	public DeleteQuery<T> inSubQuery			(String attribute, Query<?> subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return this; }
	public DeleteQuery<T> notInSubQuery		(String attribute, Query<?> subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return this; }

	
	public DeleteQuery<T> notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return this; }
	public DeleteQuery<T> inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return this; }
	public DeleteQuery<T> notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return this; }
	
	
	public DeleteQuery<T> likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public DeleteQuery<T> likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return this; }
	public DeleteQuery<T> likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public DeleteQuery<T> likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public DeleteQuery<T> likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public DeleteQuery<T> notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public DeleteQuery<T> matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return this; }

	public DeleteQuery<T> matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return this; }

	public DeleteQuery<T> notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return this; }

	public DeleteQuery<T> notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return this; }


	public DeleteQuery<T> matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public DeleteQuery<T> isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return this;}
	public DeleteQuery<T> isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return this;}

	public DeleteQuery<T> eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return this;	}
	public DeleteQuery<T> ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return this;	}
	public DeleteQuery<T> gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return this;	}
	public DeleteQuery<T> lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return this;	}
	public DeleteQuery<T> gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return this;	}


	// Class wrappers
	public DeleteQuery<T> eq   			(Class<?> entity, String attribute, Object value)	{ super.eq    (entity, attribute, value);  return this; }
	public DeleteQuery<T> eqIfNotNull   	(Class<?> entity, String attribute, Object value)   { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public DeleteQuery<T> eqNullable		(Class<?> entity, String attribute, Object value)   { super.eqNullable (entity, attribute, value); return this; }
	public DeleteQuery<T> notEq			(Class<?> entity, String attribute, Object value)   { super.notEq (entity, attribute, value);  return this; }
	public DeleteQuery<T> notEqIfNotNull	(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public DeleteQuery<T> notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return this; }
	
	public DeleteQuery<T> lt   			(Class<?> entity, String attribute, Object value)   { super.lt    (entity, attribute, value);  return this; }
	public DeleteQuery<T> ltIfNotNull		(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull   (entity, attribute, value);  return this; }
	
	public DeleteQuery<T> lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return this; }
	public DeleteQuery<T> lteIfNotNull  	(Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return this; }
	
	
	public DeleteQuery<T> gt   			(Class<?> entity, String attribute, Object value)   { super.gt    (entity, attribute, value);  return this; }
	public DeleteQuery<T> gtIfNotNull 	(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public DeleteQuery<T> gte  			(Class<?> entity, String attribute, Object value)   { super.gte   (entity, attribute, value);  return this; }
	public DeleteQuery<T> gteIfNotNull	(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return this; }
	
	public DeleteQuery<T> between			(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return this; }
	public DeleteQuery<T> betweenIfNotNull(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return this; }

	public DeleteQuery<T> in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmpty  			(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyNullable  	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return this; }
	public DeleteQuery<T> inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyOrNull		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return this; }
	public DeleteQuery<T> notIn  					(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }
	public DeleteQuery<T> notInOrNull		  		(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return this; }
	public DeleteQuery<T> notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return this; }
	


	public DeleteQuery<T> like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return this; }
	public DeleteQuery<T> likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return this; }
	
	
	public DeleteQuery<T> likeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return this; }
	
	public DeleteQuery<T> likeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return this; }
	
	public DeleteQuery<T> likeContains 		(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContains 	(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return this; }

	public DeleteQuery<T> likeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return this; }

	public DeleteQuery<T> nativeCriterion 	(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }
	
	public DeleteQuery<T> inSubQuery			(Class<?> entity, String attribute, Query<?> subQuery){ super.inSubQuery(entity, attribute, subQuery); return this; }
	public DeleteQuery<T> notInSubQuery		(Class<?> entity, String attribute, Query<?> subQuery){ super.notInSubQuery(entity, attribute, subQuery); return this; }

	
	public DeleteQuery<T> notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public DeleteQuery<T> inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public DeleteQuery<T> notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	
	public DeleteQuery<T> likeIfNotNull 				(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> likeEndsWithIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> likeContainsIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return this; }

	public DeleteQuery<T> likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeStartsWithInIfNotEmpty 	(Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }
	
	public DeleteQuery<T> notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return this; }

	public DeleteQuery<T> matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }
	public DeleteQuery<T> matchAgainst(Class<?> entity, Collection<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public DeleteQuery<T> matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public DeleteQuery<T> matchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public DeleteQuery<T> notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public DeleteQuery<T> notMatchAgainst(Class<?> entity, Collection<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public DeleteQuery<T> notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public DeleteQuery<T> notMatchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }

	public DeleteQuery<T> matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }
	public DeleteQuery<T> matchAgainstIn(Class<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(Class<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	
	public DeleteQuery<T> isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return this;}
	public DeleteQuery<T> isNull		(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return this;}

	public DeleteQuery<T> eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return this;	}


	public DeleteQuery<T> eq   	(Entity<?> entity, String attribute, Object value)        		{ super.eq    	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> eqNullable(Entity<?> entity, String attribute, Object value)			{ super.eqNullable(entity, attribute, value); 		return this; }
	public DeleteQuery<T> notEq	(Entity<?> entity, String attribute, Object value)        		{ super.notEq 	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return this; }
	public DeleteQuery<T> notEqNullable(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return this; }
	

	public DeleteQuery<T> lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return this; }
	
	public DeleteQuery<T> gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return this; }

	public DeleteQuery<T> in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return this; }
	public DeleteQuery<T> inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return this; }
	public DeleteQuery<T> inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return this; }
	public DeleteQuery<T> inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return this; }
	public DeleteQuery<T> notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }
	public DeleteQuery<T> notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return this; }
	public DeleteQuery<T> notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return this; }
	
	public DeleteQuery<T> like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return this; }
	public DeleteQuery<T> likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return this; }
	public DeleteQuery<T> likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return this; }
	public DeleteQuery<T> likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return this; }

	public DeleteQuery<T> notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return this; }
	public DeleteQuery<T> notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return this; }
	public DeleteQuery<T> notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return this; }
	public DeleteQuery<T> notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return this; }

	public DeleteQuery<T> likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return this; }
	public DeleteQuery<T> likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return this; }
	public DeleteQuery<T> likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return this; }
	public DeleteQuery<T> likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return this; }

	public DeleteQuery<T> notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return this; }
	public DeleteQuery<T> notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return this; }
	public DeleteQuery<T> notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return this; }
	public DeleteQuery<T> notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return this; }

	public DeleteQuery<T> eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return this; }
	public DeleteQuery<T> ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return this; }
	public DeleteQuery<T> gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return this; }
	public DeleteQuery<T> lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return this; }
	public DeleteQuery<T> gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return this; }
	public DeleteQuery<T> betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return this; }
	
	public DeleteQuery<T> notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public DeleteQuery<T> inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public DeleteQuery<T> notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	public DeleteQuery<T> likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return this; }
	public DeleteQuery<T> likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return this; }
	public DeleteQuery<T> likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return this; }

	public DeleteQuery<T> likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }

	
	public DeleteQuery<T> notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public DeleteQuery<T> notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return this; }
	public DeleteQuery<T> notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return this; }
	public DeleteQuery<T> notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return this; }
	
	public DeleteQuery<T> isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public DeleteQuery<T> isNull			(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return this;}
	public DeleteQuery<T> eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return this;	}
	public DeleteQuery<T> lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return this;}
	public DeleteQuery<T> gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return this;}
	
	public DeleteQuery<T> matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }	
	public DeleteQuery<T> matchAgainst(Entity<?> entity, Collection<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public DeleteQuery<T> matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public DeleteQuery<T> matchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public DeleteQuery<T> notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public DeleteQuery<T> notMatchAgainst(Entity<?> entity, Collection<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public DeleteQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public DeleteQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }
	
	public DeleteQuery<T> matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }	
	public DeleteQuery<T> matchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public DeleteQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }



	public DeleteQuery<T> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	public DeleteQuery<T> add(EstivateNode node) { super.add(node); return this; }
	public DeleteQuery<T> addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return this; }
	public DeleteQuery<T> and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return this; }
	public DeleteQuery<T> or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return this; }


	@SuppressWarnings("unchecked")
	public DeleteQuery<T> clone() {
		DeleteQuery<T> queryClone = new DeleteQuery<T>(entity);
		
		queryClone.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		return queryClone;
	}

}
