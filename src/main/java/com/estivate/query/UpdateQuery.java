package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;

import lombok.Getter;

public class UpdateQuery<T> extends Query<T>{

    @Getter
    LinkedHashMap<Attribute, Object> updates = new LinkedHashMap<>();

    public UpdateQuery(Class<T> baseClass) {
    	super(baseClass);
    }

    public UpdateQuery(Entity<T> entity) {
        super(entity);
    }

    public UpdateQuery<T> set(Attribute attribute, Object value) {
        updates.put(attribute, value);
        return this;
    }

    public UpdateQuery<T> set(String attribute, Object value) {
        return set(new Attribute(entity, attribute, null), value);
    }



    public UpdateQuery<T> eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return this; }
	public UpdateQuery<T> eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return this; }
	public UpdateQuery<T> eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return this; }
	public UpdateQuery<T> notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return this; }
	public UpdateQuery<T> notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return this; }
	public UpdateQuery<T> notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return this; }
	
	public UpdateQuery<T> lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return this; }
	public UpdateQuery<T> ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return this; }
	
	public UpdateQuery<T> lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return this; }
	public UpdateQuery<T> lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return this; }
	
	
	public UpdateQuery<T> gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return this; }
	public UpdateQuery<T> gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return this; }
	public UpdateQuery<T> gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return this; }
	public UpdateQuery<T> gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return this; }
	
	public UpdateQuery<T> between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return this; }
	public UpdateQuery<T> betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return this; }

	public UpdateQuery<T> in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return this; }
	public UpdateQuery<T> inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return this; }
	public UpdateQuery<T> notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return this; }
	public UpdateQuery<T> notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return this; }
	public UpdateQuery<T> notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return this; }


	public UpdateQuery<T> like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return this; }
	public UpdateQuery<T> likeIn 		(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return this; }
	public UpdateQuery<T> notLike		(Attribute attribute, String value)				{ super.notLike(attribute, value);  return this; }
	public UpdateQuery<T> notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return this; }
	
	
	public UpdateQuery<T> likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return this; }
	
	public UpdateQuery<T> likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return this; }
	
	public UpdateQuery<T> likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return this; }
	public UpdateQuery<T> notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return this; }

	public UpdateQuery<T> likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return this; }

	public UpdateQuery<T> nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return this; }
	
	public UpdateQuery<T> inSubQuery			(Attribute attribute, SelectQuery<?> subQuery){ super.inSubQuery(attribute, subQuery); return this; }
	public UpdateQuery<T> notInSubQuery		(Attribute attribute, SelectQuery<?> subQuery){ super.notInSubQuery(attribute, subQuery); return this; }
	public UpdateQuery<T> exists		(SelectQuery<?> subQuery){ super.exists(subQuery); return this; }
	public UpdateQuery<T> notExists	(SelectQuery<?> subQuery){ super.notExists(subQuery); return this; }

	
	public UpdateQuery<T> notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return this; }
	public UpdateQuery<T> inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return this; }
	public UpdateQuery<T> notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return this; }
	
	
	public UpdateQuery<T> likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return this; }

	public UpdateQuery<T> likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return this; }
	public UpdateQuery<T> likeStartsWithInIfNotEmpty  (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return this; }
	public UpdateQuery<T> likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return this; }
	public UpdateQuery<T> likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return this; }
	
	public UpdateQuery<T> notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return this; }

	public UpdateQuery<T> matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return this; }

	public UpdateQuery<T> matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return this; }

	public UpdateQuery<T> notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return this; }

	public UpdateQuery<T> notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return this; }


	public UpdateQuery<T> matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return this; }
	
	public UpdateQuery<T> isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return this;}
	public UpdateQuery<T> isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return this;}

	public UpdateQuery<T> eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return this;	}
	public UpdateQuery<T> ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return this;	}
	public UpdateQuery<T> gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return this;	}
	public UpdateQuery<T> lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return this;	}
	public UpdateQuery<T> gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return this;	}


	// Attribute only wrappers

	public UpdateQuery<T> eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return this; }
	public UpdateQuery<T> notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return this; }
	
	public UpdateQuery<T> lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return this; }
	
	public UpdateQuery<T> lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return this; }
	
	
	public UpdateQuery<T> gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return this; }
	
	public UpdateQuery<T> between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return this; }
	public UpdateQuery<T> betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return this; }

	public UpdateQuery<T> in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return this; }
	public UpdateQuery<T> inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return this; }
	public UpdateQuery<T> notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return this; }
	public UpdateQuery<T> notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return this; }
	public UpdateQuery<T> notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return this; }


	public UpdateQuery<T> like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return this; }
	
	
	public UpdateQuery<T> likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return this; }
	
	public UpdateQuery<T> likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return this; }
	
	public UpdateQuery<T> likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return this; }

	public UpdateQuery<T> likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return this; }

	public UpdateQuery<T> nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return this; }
	
	public UpdateQuery<T> inSubQuery			(String attribute, SelectQuery<?> subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return this; }
	public UpdateQuery<T> notInSubQuery		(String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return this; }

	
	public UpdateQuery<T> notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return this; }
	public UpdateQuery<T> inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return this; }
	public UpdateQuery<T> notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return this; }
	
	
	public UpdateQuery<T> likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public UpdateQuery<T> likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return this; }
	public UpdateQuery<T> likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public UpdateQuery<T> likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return this; }
	public UpdateQuery<T> likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public UpdateQuery<T> notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return this; }

	public UpdateQuery<T> matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return this; }

	public UpdateQuery<T> matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return this; }

	public UpdateQuery<T> notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return this; }

	public UpdateQuery<T> notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return this; }


	public UpdateQuery<T> matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return this; }
	
	public UpdateQuery<T> isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return this;}
	public UpdateQuery<T> isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return this;}

	public UpdateQuery<T> eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return this;	}
	public UpdateQuery<T> ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return this;	}
	public UpdateQuery<T> gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return this;	}
	public UpdateQuery<T> lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return this;	}
	public UpdateQuery<T> gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return this;	}


	// Class wrappers
	public UpdateQuery<T> eq   			(Class<?> entity, String attribute, Object value)	{ super.eq    (entity, attribute, value);  return this; }
	public UpdateQuery<T> eqIfNotNull   	(Class<?> entity, String attribute, Object value)   { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public UpdateQuery<T> eqNullable		(Class<?> entity, String attribute, Object value)   { super.eqNullable (entity, attribute, value); return this; }
	public UpdateQuery<T> notEq			(Class<?> entity, String attribute, Object value)   { super.notEq (entity, attribute, value);  return this; }
	public UpdateQuery<T> notEqIfNotNull	(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public UpdateQuery<T> notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return this; }
	
	public UpdateQuery<T> lt   			(Class<?> entity, String attribute, Object value)   { super.lt    (entity, attribute, value);  return this; }
	public UpdateQuery<T> ltIfNotNull		(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull   (entity, attribute, value);  return this; }
	
	public UpdateQuery<T> lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return this; }
	public UpdateQuery<T> lteIfNotNull  	(Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return this; }
	
	
	public UpdateQuery<T> gt   			(Class<?> entity, String attribute, Object value)   { super.gt    (entity, attribute, value);  return this; }
	public UpdateQuery<T> gtIfNotNull 	(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public UpdateQuery<T> gte  			(Class<?> entity, String attribute, Object value)   { super.gte   (entity, attribute, value);  return this; }
	public UpdateQuery<T> gteIfNotNull	(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return this; }
	
	public UpdateQuery<T> between			(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return this; }
	public UpdateQuery<T> betweenIfNotNull(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return this; }

	public UpdateQuery<T> in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmpty  			(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyNullable  	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return this; }
	public UpdateQuery<T> inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyOrNull		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return this; }
	public UpdateQuery<T> notIn  					(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }
	public UpdateQuery<T> notInOrNull		  		(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return this; }
	public UpdateQuery<T> notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return this; }
	


	public UpdateQuery<T> like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return this; }
	public UpdateQuery<T> likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return this; }
	
	
	public UpdateQuery<T> likeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return this; }
	
	public UpdateQuery<T> likeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return this; }
	
	public UpdateQuery<T> likeContains 		(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContains 	(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return this; }

	public UpdateQuery<T> likeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return this; }

	public UpdateQuery<T> nativeCriterion 	(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }
	
	public UpdateQuery<T> inSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.inSubQuery(entity, attribute, subQuery); return this; }
	public UpdateQuery<T> notInSubQuery		(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(entity, attribute, subQuery); return this; }

	
	public UpdateQuery<T> notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public UpdateQuery<T> inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public UpdateQuery<T> notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	
	public UpdateQuery<T> likeIfNotNull 				(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> likeEndsWithIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> likeContainsIfNotNull 		(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return this; }

	public UpdateQuery<T> likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeStartsWithInIfNotEmpty 	(Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }
	
	public UpdateQuery<T> notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return this; }

	public UpdateQuery<T> matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }
	public UpdateQuery<T> matchAgainst(Class<?> entity, Collection<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public UpdateQuery<T> matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public UpdateQuery<T> matchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public UpdateQuery<T> notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public UpdateQuery<T> notMatchAgainst(Class<?> entity, Collection<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public UpdateQuery<T> notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public UpdateQuery<T> notMatchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }

	public UpdateQuery<T> matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }
	public UpdateQuery<T> matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	
	public UpdateQuery<T> isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return this;}
	public UpdateQuery<T> isNull		(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return this;}

	public UpdateQuery<T> eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return this;	}


	public UpdateQuery<T> eq   	(Entity<?> entity, String attribute, Object value)        		{ super.eq    	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> eqNullable(Entity<?> entity, String attribute, Object value)			{ super.eqNullable(entity, attribute, value); 		return this; }
	public UpdateQuery<T> notEq	(Entity<?> entity, String attribute, Object value)        		{ super.notEq 	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return this; }
	public UpdateQuery<T> notEqNullable(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return this; }
	

	public UpdateQuery<T> lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return this; }
	
	public UpdateQuery<T> gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return this; }

	public UpdateQuery<T> in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return this; }
	public UpdateQuery<T> inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return this; }
	public UpdateQuery<T> inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return this; }
	public UpdateQuery<T> inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return this; }
	public UpdateQuery<T> notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }
	public UpdateQuery<T> notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return this; }
	public UpdateQuery<T> notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return this; }
	
	public UpdateQuery<T> like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return this; }
	public UpdateQuery<T> likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return this; }
	public UpdateQuery<T> likeEndsWith(Entity<?> entity, String attribute, String value)		{ super.likeEndsWith(entity, attribute, value);		return this; }
	public UpdateQuery<T> likeContains(Entity<?> entity, String attribute, String value)		{ super.likeContains(entity, attribute, value);		return this; }

	public UpdateQuery<T> notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return this; }
	public UpdateQuery<T> notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return this; }
	public UpdateQuery<T> notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return this; }
	public UpdateQuery<T> notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return this; }

	public UpdateQuery<T> likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return this; }
	public UpdateQuery<T> likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return this; }
	public UpdateQuery<T> likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return this; }
	public UpdateQuery<T> likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return this; }

	public UpdateQuery<T> notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return this; }
	public UpdateQuery<T> notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return this; }
	public UpdateQuery<T> notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return this; }
	public UpdateQuery<T> notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return this; }

	public UpdateQuery<T> eqIfNotNull   	(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return this; }
	public UpdateQuery<T> ltIfNotNull   	(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return this; }
	public UpdateQuery<T> gtIfNotNull   	(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return this; }
	public UpdateQuery<T> lteIfNotNull  	(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return this; }
	public UpdateQuery<T> gteIfNotNull  	(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return this; }
	public UpdateQuery<T> betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return this; }
	
	public UpdateQuery<T> notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return this; }
	public UpdateQuery<T> inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return this; }
	public UpdateQuery<T> notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return this; }
	
	public UpdateQuery<T> likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return this; }
	public UpdateQuery<T> likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return this; }
	public UpdateQuery<T> likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return this; }

	public UpdateQuery<T> likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return this; }

	
	public UpdateQuery<T> notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return this; }
	public UpdateQuery<T> notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return this; }
	public UpdateQuery<T> notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return this; }
	public UpdateQuery<T> notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return this; }
	
	public UpdateQuery<T> isNotNull		(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public UpdateQuery<T> isNull		(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return this;}
	public UpdateQuery<T> eqOrNull		(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> ltOrNull		(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> gtOrNull		(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return this;	}
	public UpdateQuery<T> lteOrNull		(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return this;}
	public UpdateQuery<T> gteOrNull		(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return this;}
	
	public UpdateQuery<T> matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return this; }	
	public UpdateQuery<T> matchAgainst(Entity<?> entity, Collection<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return this; }
	public UpdateQuery<T> matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return this; }
	public UpdateQuery<T> matchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return this; }
	public UpdateQuery<T> notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return this; }
	public UpdateQuery<T> notMatchAgainst(Entity<?> entity, Collection<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return this; }
	public UpdateQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return this; }
	public UpdateQuery<T> notMatchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return this; }
	
	public UpdateQuery<T> matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return this; }	
	public UpdateQuery<T> matchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> matchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return this; }
	public UpdateQuery<T> notMatchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return this; }

	
	public UpdateQuery<T> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	public UpdateQuery<T> add(EstivateNode node) { super.add(node); return this; }
	public UpdateQuery<T> addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return this; }
	public UpdateQuery<T> and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return this; }
	public UpdateQuery<T> or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return this; }
		

	@SuppressWarnings("unchecked")
	public UpdateQuery<T> clone() {
		UpdateQuery<T> queryClone = new UpdateQuery<T>(entity);
		
		queryClone.updates = new LinkedHashMap<>(this.updates);
		queryClone.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		return queryClone;
	}


}
