package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.query.Query.Entity;
import com.estivate.query.Select.SelectMethod;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Query extends Aggregator{
	
	// Method wrappers
	
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
	public Query notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return this; }


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
	public Query existsSubQuery		(Query subQuery){ super.existsSubQuery(subQuery); return this; }
	public Query notExistsSubQuery	(Query subQuery){ super.notExistsSubQuery(subQuery); return this; }

	
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
	public Query notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return this; }

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
	String name;
	
	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	@Getter
	Set<Join> joins = new LinkedHashSet<>();
	
	@Getter
	Set<Select> selects = new LinkedHashSet<>();
	
	@Getter
	List<Order> orders = new ArrayList<>();
	
	@Getter
	List<Group> groupBys = new ArrayList<>();
	
	@Getter
	Integer offset;

	@Getter
	Integer limit;

	@Getter
	IndexHint indexHint;

	@Getter
	Set<String> indexNames = new LinkedHashSet<>();
	
	public Query(Class<?> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<>(baseClass);
	}
	
	public Query(Entity<?> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}
	
	public Query name(String name) {
		this.name = name;
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



	public Query orderAsc(Entity<?> c, String attribute) 				{ orders.add(new Order(c, attribute, "", true)); return this; }
	public Query orderAsc(Entity<?> c, String attribute, String option) { orders.add(new Order(c, attribute, option, true)); return this; }
	public Query orderAsc(Class<?> c, String attribute) 				{ return orderAsc(new Entity<>(c), attribute); }
	public Query orderAsc(Class<?> c, String attribute, String option) 	{ return orderAsc(new Entity<>(c), attribute, option); }
	public Query orderDesc(Entity<?> c, String attribute) 				{ orders.add(new Order(c, attribute, "", false)); return this; }
	public Query orderDesc(Entity<?> c, String attribute, String option){ orders.add(new Order(c, attribute, option, false)); return this; }
	public Query orderDesc(Class<?> c, String attribute) 				{ return orderDesc(new Entity<>(c), attribute); }
	public Query orderDesc(Class<?> c, String attribute, String option) { return orderDesc(new Entity<>(c), attribute, option); }
	
	public Query limit(Integer limit) 		{ this.limit = limit; return this; }
	public Query offset(Integer offset) 	{ this.offset = offset; return this;}
	

	@AllArgsConstructor
	public static class Order{
		public Entity<?> entity;
		public String attribute;
		public String option;
		public Boolean asc;
	}
	
	@AllArgsConstructor
	public static class Group{
		public Entity<?> entity;
		public String attribute;
	}
	

	
	
	public Query select(Class<?> c, String attribute) { return select(new Entity<>(c), attribute); }
	
	public Query select(Entity<?> c, String attribute) {
		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}
		selects.add(Select.builder().entity(c).attribute(attribute).build());
		return this;
	}
	
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

	
	public Query selectAs(Class<?> c, String field, String alias) { return selectAs(new Entity<>(c), field, alias); }
	public Query selectAs(Entity<?> c, String field, String alias) { 

		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(field)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}
		selects.add(Select.builder().entity(c).attribute(field).alias(alias).build());
		return this;
	}
	
	
	
	
	public Query selectDistinct(Class<?> c, String attribute) {
		
		Select select = selects.stream().filter(x -> x.entity.equals(new Entity(c)) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}
		
		
		selects.add(Select.builder().method(SelectMethod.Distinct).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	
	// Select count
	public Query selectCount() { selects.add(Select.builder().method(SelectMethod.Count).build()); return this; }
	public Query selectCountAs(String alias) { selects.add(Select.builder().method(SelectMethod.Count).alias(alias).build()); return this; }
	// Select count field
	public Query selectCount(Class<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.Count).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectCount(Entity<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.Count).entity(c).attribute(attribute).build()); return this; }
	public Query selectCountAs(Class<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Count).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectCountAs(Entity<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Count).entity(c).attribute(attribute).alias(alias).build()); return this; }
	
	// Select count distinct field
	public Query selectDistinctCount(Class<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.CountDistinct).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectDistinctCount(Entity<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.CountDistinct).entity(c).attribute(attribute).build()); return this; }
	public Query selectDistinctCountAs(Class<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.CountDistinct).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectDistinctCountAs(Entity<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.CountDistinct).entity(c).attribute(attribute).alias(alias).build()); return this; }
	
	
	// Select min
	public Query selectMin(Class<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Min).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectMin(Entity<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Min).entity(c).attribute(attribute).build()); return this; }
	public Query selectMinAs(Class<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Min).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectMinAs(Entity<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Min).entity(c).attribute(attribute).alias(alias).build()); return this; }
	// Select max
	public Query selectMax(Class<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Max).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectMax(Entity<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Max).entity(c).attribute(attribute).build()); return this; }
	public Query selectMaxAs(Class<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Max).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectMaxAs(Entity<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Max).entity(c).attribute(attribute).alias(alias).build()); return this; }
	// Select Sum
	public Query selectSum(Class<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Sum).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectSum(Entity<?> c, String attribute) 					{ selects.add(Select.builder().method(SelectMethod.Sum).entity(c).attribute(attribute).build()); return this; }
	public Query selectSumAs(Class<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Sum).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectSumAs(Entity<?> c, String attribute, String alias) 	{ selects.add(Select.builder().method(SelectMethod.Sum).entity(c).attribute(attribute).alias(alias).build()); return this; }
	// Select Group Concat
	public Query selectGroupConcat(Class<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.GroupConcat).entity(new Entity<>(c)).attribute(attribute).build()); return this; }
	public Query selectGroupConcat(Entity<?> c, String attribute) 				{ selects.add(Select.builder().method(SelectMethod.GroupConcat).entity(c).attribute(attribute).build()); return this; }
	public Query selectGroupConcatAs(Class<?> c, String attribute, String alias) { selects.add(Select.builder().method(SelectMethod.GroupConcat).entity(new Entity<>(c)).attribute(attribute).alias(alias).build()); return this; }
	public Query selectGroupConcatAs(Entity<?> c, String attribute, String alias){ selects.add(Select.builder().method(SelectMethod.GroupConcat).entity(c).attribute(attribute).alias(alias).build()); return this; }
	
	
	public Query clone() {
		Query queryClone = new Query(entity);
		
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

	public Query groupBy(Class<?> c, String field) {
		return groupBy(new Entity<>(c), field);
	}
	
	public Query groupBy(Entity<?> entity, String field) {
		
		// Add to select
		select(entity, field);
		groupBys.add(new Group(entity, field));
		
		return this;
	}
	
	public Query setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
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
