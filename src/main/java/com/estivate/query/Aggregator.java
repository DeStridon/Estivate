package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Estivate;
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
	
	public Aggregator(GroupType groupType, List<EstivateNode> criterions) {
		this.groupType = groupType;
		this.criterions = new ArrayList<>(criterions);
	}
	
	
	

	
	public Aggregator eq    		(Entity<?> entity, String attribute, Object value) { add(Estivate.eq(entity, attribute, value)); return this; }
	public Aggregator eqIfNotNull   (Entity<?> entity, String attribute, Object value) { add(Estivate.eqIfNotNull(entity, attribute, value)); return this; }
	public Aggregator eqNullable    (Entity<?> entity, String attribute, Object value) { add(Estivate.eqNullable(entity, attribute, value)); return this; }
	public Aggregator eqOrNull		(Entity<?> entity, String attribute, Object value) { add(Estivate.eqOrNull(entity, attribute, value)); return this; }
	
	public Aggregator notEq 		(Entity<?> entity, String attribute, Object value) { add(Estivate.notEq(entity, attribute, value)); return this; }
	public Aggregator notEqIfNotNull(Entity<?> entity, String attribute, Object value) { add(Estivate.notEqIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notEqNullable (Entity<?> entity, String attribute, Object value) { add(Estivate.notEqNullable(entity, attribute, value)); return this; }
	public Aggregator notEqOrNull	(Entity<?> entity, String attribute, Object value) { add(Estivate.notEqOrNull(entity, attribute, value)); return this; }
	
	public Aggregator lt    	(Entity<?> entity, String attribute, Object value)     { add(Estivate.lt(entity, attribute, value)); return this; }
	public Aggregator ltIfNotNull   	(Entity<?> entity, String attribute, Object value) { add(Estivate.ltIfNotNull(entity, attribute, value)); return this; }
	public Aggregator ltOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).lt(entity, attribute, value).isNull(entity, attribute)); return this; }

	public Aggregator lte   	(Entity<?> entity, String attribute, Object value)     { add(Estivate.lte(entity, attribute, value)); return this; }
	public Aggregator lteIfNotNull  	(Entity<?> entity, String attribute, Object value) { add(Estivate.lteIfNotNull(entity, attribute, value)); return this; }
	public Aggregator lteOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).lte(entity, attribute, value).isNull(entity, attribute)); return this; }
	
	public Aggregator gt    	(Entity<?> entity, String attribute, Object value)     { add(Estivate.gt(entity, attribute, value)); return this; }
	public Aggregator gtIfNotNull   	(Entity<?> entity, String attribute, Object value) { add(Estivate.gtIfNotNull(entity, attribute, value)); return this; }
	public Aggregator gtOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).gt(entity, attribute, value).isNull(entity, attribute)); return this; }
	
	public Aggregator gte   	(Entity<?> entity, String attribute, Object value)     { add(Estivate.gte(entity, attribute, value)); return this; }
	public Aggregator gteIfNotNull  	(Entity<?> entity, String attribute, Object value) { add(Estivate.gteIfNotNull(entity, attribute, value)); return this; }
	public Aggregator gteOrNull(Entity<?> entity, String attribute, Object value) { criterions.add(new Aggregator(GroupType.OR).gte(entity, attribute, value).isNull(entity, attribute)); return this; }

	public Aggregator between 	(Entity<?> entity, String attribute, Object min, Object max){ add(Estivate.between(entity, attribute, min, max)); return this; }
	public Aggregator betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max) {add(Estivate.betweenIfNotNull(entity, attribute, min, max)); return this; }
	public Aggregator betweenOrNull	(Entity<?> entity, String attribute, Object min, Object max) {add(Estivate.betweenOrNull(entity, attribute, min, max)); return this; }
	
	
	public Aggregator in    	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.in(entity, attribute, values)); return this; }
	public Aggregator inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.inIfNotEmptyNullable(entity, attribute, values)); return this; }
	public Aggregator notIn    	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.notIn(entity, attribute, values)); return this; }

	public Aggregator like				(Entity<?> entity, String attribute, String value)     { add(Estivate.like(entity, attribute, value)); return this; }
	public Aggregator likeStartsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.likeStartsWith(entity, attribute, value)); return this; }
	public Aggregator likeEndsWith		(Entity<?> entity, String attribute, String value)     { add(Estivate.likeEndsWith(entity, attribute, value)); return this; }
	public Aggregator likeContains		(Entity<?> entity, String attribute, String value)     { add(Estivate.likeContains(entity, attribute, value)); return this; }
	public Aggregator notLike			(Entity<?> entity, String attribute, String value)     { add(Estivate.notLike(entity, attribute, value)); return this; }
	public Aggregator notLikeStartsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeStartsWith(entity, attribute, value)); return this; }
	public Aggregator notLikeEndsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeEndsWith(entity, attribute, value)); return this; }
	public Aggregator notLikeContains	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeContains(entity, attribute, value)); return this; }

	public Aggregator likeIn			(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeIn(entity, attribute, value)); return this; }
	public Aggregator likeStartsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeStartsWithIn(entity, attribute, value)); return this; }
	public Aggregator likeEndsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeEndsWithIn(entity, attribute, value)); return this; }
	public Aggregator likeContainsIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeContainsIn(entity, attribute, value)); return this; }
	public Aggregator notLikeIn			(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeIn(entity, attribute, value)); return this; }
	public Aggregator notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> value){ add(Estivate.notLikeStartsWithIn(entity, attribute, value)); return this; }
	public Aggregator notLikeEndsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeEndsWithIn(entity, attribute, value)); return this; }
	public Aggregator notLikeContainsIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeContainsIn(entity, attribute, value)); return this; }

	
	

	public Aggregator inIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.inIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator notInIfNotEmpty	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.notInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.inOrFalseIfEmpty(entity, attribute, values)); return this; }
	public Aggregator notInOrTrueIfEmpty(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.notInOrTrueIfEmpty(entity, attribute, values)); return this; }
	
	
	public Aggregator likeIfNotNull 			(Entity<?> entity, String attribute, String value) { add(Estivate.likeIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeStartsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeStartsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeEndsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeIfNotNull(entity, attribute, value)); return this; }

	
	public Aggregator likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeStartsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeStartsWithInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeEndsWithInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeContainsInIfNotEmpty(entity, attribute, values)); return this; }

	
	
	public Aggregator notLikeIfNotNull 			(Entity<?> entity, String attribute, String value) { if(value != null) {return notLike(entity, attribute, value);} return this; }
	public Aggregator notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeStartsWith(entity, attribute, value);} return this; }
	public Aggregator notLikeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeEndsWith(entity, attribute, value);} return this; }
	public Aggregator notLikeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeContains(entity, attribute, value);} return this; }

	public Aggregator isNull(Entity<?> entity, String attribute) { 		add(Estivate.isNull(entity, attribute)); 	return this; }
	public Aggregator isNotNull(Entity<?> entity, String attribute) { 	add(Estivate.isNotNull(entity, attribute)); return this; }

	public Aggregator inSubQuery		(Entity<?> entity, String attribute, Query subQuery)	{ criterions.add(new Criterion.InSubQuery(entity, attribute, subQuery, true)); return this; }
	public Aggregator existsSubQuery	(Query subQuery)	{ criterions.add(new Criterion.ExistsSubQuery(subQuery, true)); return this; }
	public Aggregator notInSubQuery		(Entity<?> entity, String attribute, Query subQuery){ criterions.add(new Criterion.InSubQuery(entity, attribute, subQuery, false)); return this; }
	public Aggregator notExistsSubQuery	(Query subQuery)	{ criterions.add(new Criterion.ExistsSubQuery(subQuery, false)); return this; }

	
	/* Wrappers for Class */
	public Aggregator eq    	(Class<?> entity, String attribute, Object value)        { return eq(new Entity<>(entity), attribute, value); }
	public Aggregator eqIfNotNull    	(Class<?> entity, String attribute, Object value){ return eqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator eqNullable(Class<?> entity, String attribute, Object value)		 { return eqNullable(new Entity<>(entity), attribute, value); }
	public Aggregator eqOrNull(Class<?> entity, String attribute, Object value) 		 { return eqOrNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator notEq 	(Class<?> entity, String attribute, Object value)       { return notEq(new Entity<>(entity), attribute, value); }
	public Aggregator notEqIfNotNull (Class<?> entity, String attribute, Object value)	{ return notEqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notEqNullable(Class<?> entity, String attribute, Object value)	{ return notEqNullable(new Entity<>(entity), attribute, value); }
	public Aggregator notEqOrNull(Class<?> entity, String attribute, Object value)		{ return notEqOrNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator lt    	(Class<?> entity, String attribute, Object value)        { return lt(new Entity<>(entity), attribute, value); }
	public Aggregator ltIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return ltIfNotNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator lte   	(Class<?> entity, String attribute, Object value)        { return lte(new Entity<>(entity), attribute, value); }
	
	public Aggregator gt    	(Class<?> entity, String attribute, Object value)        { return gt(new Entity<>(entity), attribute, value); }
	public Aggregator gte   	(Class<?> entity, String attribute, Object value)        { return gte(new Entity<>(entity), attribute, value); }
	
	public Aggregator between	(Class<?> entity, String attribute, Object left, Object right) { return between(new Entity<>(entity), attribute, left, right); }
	
	public Aggregator in    	(Class<?> entity, String attribute, Collection<?> values) { return in(new Entity<>(entity), attribute, values); }
	public Aggregator inIfNotEmptyNullable(Class<?> entity, String attribute, Collection<?> values) { return inIfNotEmptyNullable(new Entity<>(entity), attribute, values); }
	public Aggregator notIn    	(Class<?> entity, String attribute, Collection<?> values) { return notIn(new Entity<>(entity), attribute, values); }

	public Aggregator like		(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public Aggregator likeIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLike	(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeIn(new Entity<>(entity), attribute, value); }
	

	public Aggregator likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWith	(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	
	public Aggregator likeEndsWith		(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	
	public Aggregator likeContains		(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }

	public Aggregator likeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeStartsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWithIn		(Class<?> entity, String attribute, Collection<String> value)	{ return likeEndsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator likeContainsIn		(Class<?> entity, String attribute, Collection<String> value)	{ return likeContainsIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeStartsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeEndsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContainsIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeContainsIn(new Entity<>(entity), attribute, value); }

	
	public Aggregator inSubQuery(Class<?> entity, String attribute, Query subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public Aggregator notInSubQuery(Class<?> entity, String attribute, Query subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }

	public Aggregator gtIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return gtIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return lteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return gteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max)	{ return betweenIfNotNull(new Entity<>(entity), attribute, min, max); }

	public Aggregator inIfNotEmpty    	(Class<?> entity, String attribute, Collection<?> values) 	{ return inIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator notInIfNotEmpty   (Class<?> entity, String attribute, Collection<?> values) 	{ return notInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ return inOrFalseIfEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator notInOrTrueIfEmpty(Class<?> entity, String attribute, Collection<?> values)	{ return notInOrTrueIfEmpty(new Entity<>(entity), attribute, values); }
	
	public Aggregator likeIfNotNull				(Class<?> entity, String attribute, String value)	{ return likeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeStartsWithIfNotNull	(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWithIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeContainsIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	public Aggregator likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { return likeInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { return likeStartsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { return likeEndsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { return likeContainsInIfNotEmpty(new Entity<>(entity), attribute, values); }

	
	public Aggregator notLikeIfNotNull			(Class<?> entity, String attribute, String value)  { return notLikeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWithIfNotNull	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContainsIfNotNull	(Class<?> entity, String attribute, String value)	{ return notLikeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	
	public Aggregator isNotNull(Class<?> entity, String attribute) { return isNotNull(new Entity<>(entity), attribute); }
	public Aggregator isNull(Class<?> entity, String attribute) { return isNull(new Entity<>(entity), attribute); }

	public Aggregator ltOrNull(Class<?> entity, String attribute, Object value) 	{ return ltOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gtOrNull(Class<?> entity, String attribute, Object value) 	{ return gtOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteOrNull(Class<?> entity, String attribute, Object value) 	{ return lteOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteOrNull(Class<?> entity, String attribute, Object value) 	{ return gteOrNull(new Entity<>(entity), attribute, value); }
	
	
	public Aggregator add(EstivateNode joinNode) { if(joinNode != null) { criterions.add(joinNode); } return this; }
	public Aggregator addIf(boolean condition, EstivateNode joinNode) { if(condition && joinNode != null) { criterions.add(joinNode); } return this; }
	
	
	

	public Aggregator clone() {
		Aggregator joinAggregator = new Aggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		return joinAggregator;
	}





	
	
	

}
