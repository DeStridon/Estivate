package com.estivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity.SubQueryEntity;
import com.estivate.query.Aggregator;
import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.AlterQuery;
import com.estivate.query.CreateQuery;
import com.estivate.query.Attribute;
import com.estivate.query.Attribute.Function;
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
import com.estivate.query.DeleteQuery;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.Keyword;
import com.estivate.query.Keyword.KeywordValue;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class Estivate {

	// Entities Factory
	public static <U> SelectQuery<U> selectQuery(Entity<U> entity) 	{ return new SelectQuery<>(entity); }
	public static <U> SelectQuery<U> selectQuery(Class<U> entity) 	{ return new SelectQuery<>(entity); }
	public static <U> SelectQuery<U> selectQuery(SelectQuery<U> subQuery) { return new SelectQuery<>(new SubQueryEntity<>(subQuery)); }
	public static <U> SelectQuery<U> selectQuery(SelectQuery<U> subQuery, String alias) { return new SelectQuery<>(new SubQueryEntity<>(subQuery, alias)); }
	public static <U> UpdateQuery<U> updateQuery(Entity<U> entity) 	{ return new UpdateQuery<>(entity); }
	public static <U> UpdateQuery<U> updateQuery(Class<U> entity) 	{ return new UpdateQuery<>(entity); }
	public static <U> DeleteQuery<U> deleteQuery(Entity<U> entity) 	{ return new DeleteQuery<>(entity); }
	public static <U> DeleteQuery<U> deleteQuery(Class<U> entity)	{ return new DeleteQuery<>(entity); }
	public static <U> AlterQuery<U> alterQuery(Entity<U> entity) { return new AlterQuery<>(entity); }
	public static <U> AlterQuery<U> alterQuery(Class<U> entity) { return new AlterQuery<>(entity); }
	public static <U> CreateQuery<U> createQuery(Entity<U> entity) { return new CreateQuery<>(entity); }
	public static <U> CreateQuery<U> createQuery(Class<U> entity) { return new CreateQuery<>(entity); }


	public static Attribute attribute(Entity<?> entity, String field, Attribute.Function function, String alias) { return new Attribute(entity, field, function, alias); }
	public static Attribute attribute(Class<?> entity, String field, Attribute.Function function, String alias) { return new Attribute(new Entity<>(entity), field, function, alias); }
	public static Attribute attribute(Entity<?> entity, String field, String alias) { return attribute(entity, field, null, alias); }
	public static Attribute attribute(Class<?> entity, String field, String alias) { return attribute(new Entity<>(entity), field, null, alias); }
	public static Attribute attribute(Entity<?> entity, String field, Attribute.Function function) { return attribute(entity, field, function, null); }
	public static Attribute attribute(Class<?> entity, String field, Attribute.Function function) { return attribute(new Entity<>(entity), field, function, null); }
	public static Attribute attribute(Entity<?> entity, String field) { return attribute(entity, field, null, null); }
	public static Attribute attribute(Class<?> entity, String field) { return attribute(new Entity<>(entity), field, null, null); }


	public static <E, P> Attribute attribute(com.estivate.util.FieldUtils.AttributeGetter<E, P> getter) { return FieldUtils.attributeFromLambda(getter, null, null); }
	public static <E, P> Attribute attribute(com.estivate.util.FieldUtils.AttributeGetter<E, P> getter, Attribute.Function function) { return FieldUtils.attributeFromLambda(getter, function, null); }
	public static <E, P> Attribute attribute(com.estivate.util.FieldUtils.AttributeGetter<E, P> getter, Attribute.Function function, String alias) { return FieldUtils.attributeFromLambda(getter, function, alias); }
	public static <E, P> Attribute attribute(com.estivate.util.FieldUtils.AttributeGetter<E, P> getter, String alias) { return FieldUtils.attributeFromLambda(getter, null, alias); }
	
	public static Attribute attributeOfAlias(String alias, Attribute.Function function) { return attribute(new Entity<>(null), null, function, alias); }


	public static Attribute.AttributeWindow attributeWindow(Attribute.Function function, String alias) { return new Attribute.AttributeWindow(null, null, function, alias); }
	public static Attribute.AttributeWindow attributeWindow(Attribute.Function function) { return attributeWindow(function, null); }
	
	public static <U> Entity<U> entity(Class<U> entity) { return new Entity<>(entity); }
	public static <U> Entity<U> entity(Class<U> entity, String alias) { return new Entity<>(entity, alias); }

	public static <U> SubQueryEntity<U> subQueryEntity(SelectQuery<U> query) { return new SubQueryEntity<>(query); }
	public static <U> SubQueryEntity<U> subQueryEntity(SelectQuery<U> query, String alias) { return new SubQueryEntity<>(query, alias); }

	public static Aggregator or(EstivateNode... criterions) { return or(new ArrayList<>(Arrays.asList(criterions)));}
	public static Aggregator or(Collection<EstivateNode> criterions) { return new Aggregator(GroupType.OR, criterions); }
	public static Aggregator and(EstivateNode... criterions) { return and(new ArrayList<>(Arrays.asList(criterions))); }
	public static Aggregator and(Collection<EstivateNode> criterions) { return new Aggregator(GroupType.AND, criterions); }
	
	public static EstivateNode addIf(boolean condition, EstivateNode node) { return condition ? node : null; }
	
	public static Attribute.Function function(String prefix, String suffix) { return new Attribute.Function(prefix, suffix); }
	public static Attribute.Function function(Function... functions) { return Attribute.Function.compose(Arrays.asList(functions));}
	
	public static Order order(Entity<?> entity, String attribute, Order.Direction direction, Function function){  return Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build(); }

	public static Order order(Entity<?> entity, String attribute){ return order(entity, attribute, null, null); }
	public static Order order(Entity<?> entity, String attribute, Order.Direction direction){ return order(entity, attribute, direction, null); }
	public static Order order(Class<?> entity, String attribute){ return order(new Entity<>(entity), attribute, null, null); }
	public static Order order(Class<?> entity, String attribute, Order.Direction direction){ return order(new Entity<>(entity), attribute, direction, null); }
	public static Order order(Class<?> entity, String attribute, Order.Direction direction, Function function){ return order(new Entity<>(entity), attribute, direction, function); }

	public static Order order(Attribute attribute) { return order(attribute.entity, attribute.attribute, null, attribute.function); }
	public static Order order(Attribute attribute, Order.Direction direction) { return order(attribute.entity, attribute.attribute, direction, attribute.function); }
	

	/*
	 * Wrappers for Joins
	 */
	public static Join joinInner(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.INNER); }
	public static Join joinInner(Entity<?> leftEntity, 	Class<?> rightClass)	{ return joinInner(leftEntity, new Entity<>(rightClass)); }
	public static Join joinInner(Class<?> leftClass, 	Entity<?> rightEntity)	{ return joinInner(new Entity<>(leftClass), rightEntity); }
	public static Join joinInner(Class<?> leftClass, 	Class<?> rightClass)	{ return joinInner(new Entity<>(leftClass), new Entity<>(rightClass)); }

	public static Join joinOuter(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.OUTER); }
	public static Join joinOuter(Entity<?> leftEntity, 	Class<?> rightClass)	{ return joinOuter(leftEntity, new Entity<>(rightClass)); }
	public static Join joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)	{ return joinOuter(new Entity<>(leftClass), rightEntity); }
	public static Join joinOuter(Class<?> leftClass, 	Class<?> rightClass)	{ return joinOuter(new Entity<>(leftClass), new Entity<>(rightClass)); }

	public static Join joinLeft	(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.LEFT); }
	public static Join joinLeft	(Entity<?> leftEntity, 	Class<?> rightClass)	{ return joinLeft(leftEntity, new Entity<>(rightClass)); }
	public static Join joinLeft	(Class<?> leftClass, 	Entity<?> rightEntity)	{ return joinLeft(new Entity<>(leftClass), rightEntity); }
	public static Join joinLeft	(Class<?> leftClass, 	Class<?> rightClass)	{ return joinLeft(new Entity<>(leftClass), new Entity<>(rightClass)); }

	public static Join joinRight(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return Join.find(leftEntity, rightEntity, JoinType.RIGHT); }
	public static Join joinRight(Entity<?> leftEntity, 	Class<?> rightClass)	{ return joinRight(leftEntity, new Entity<>(rightClass)); }
	public static Join joinRight(Class<?> leftClass, 	Entity<?> rightEntity)	{ return joinRight(new Entity<>(leftClass), rightEntity); }
	public static Join joinRight(Class<?> leftClass, 	Class<?> rightClass)	{ return joinRight(new Entity<>(leftClass), new Entity<>(rightClass)); }


	public static Join joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Class<?> joinerEntity, Class<?> joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Entity<>(joinerEntity), new Entity<>(joinedEntity), joinerAttribute, joinedAttribute, JoinType.INNER); }
	public static Join joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightSubQuery, leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute, JoinType.INNER); }

	public static Join joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightSubQuery, leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute, JoinType.OUTER); }

	 
	public static Join joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightSubQuery, leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute, JoinType.LEFT); }

	public static Join joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Class<?> joinerEntity, Class<?> joinedEntity, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(joinerEntity), new Entity<>(joinedEntity), leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightSubQuery, leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return new Join(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute, JoinType.RIGHT); }
	
	// Eq methods
	public static Criterion eq   			(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.Eq, value); }
	public static Criterion eqIfNotNull		(Attribute attribute, Object value) { if(value != null) {return eq(attribute, value);} return null; }
	public static Criterion eqIfNotBlank	(Attribute attribute, String value) { if(StringUtils.isNotBlank(value)) { return eq(attribute, value); } return null; }
	public static Criterion eqNullable		(Attribute attribute, Object value) { if(value != null) {return eq(attribute, value);} return isNull(attribute); }
	public static Aggregator eqOrNull		(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).eq(attribute, value).isNull(attribute); }
	public static Criterion notEq			(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.NotEq, value); }
	public static Criterion notEqIfNotNull	(Attribute attribute, Object value) { if(value != null) {return notEq(attribute, value);} return null; }
	public static Criterion notEqIfNotBlank (Attribute attribute, String value) { if(StringUtils.isNotBlank(value)) { return notEq(attribute, value); } return null; }
	public static Criterion notEqNullable	(Attribute attribute, Object value) { if(value != null) {return notEq(attribute, value);} return isNotNull(attribute); }
	public static Aggregator notEqOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).notEq(attribute, value).isNull(attribute); }
	
	// Lt 
	public static Criterion lt   		(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.Lt, value); }
	public static Criterion ltIfNotNull (Attribute attribute, Object value) { if(value != null) {return lt(attribute, value);} return null; }
	public static Aggregator ltOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).lt(attribute, value).isNull(attribute); }
	
	// Lte
	public static Criterion lte  		(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.Lte, value); }
	public static Criterion lteIfNotNull(Attribute attribute, Object value) { if(value != null) {return lte(attribute, value);} return null; }
	public static Aggregator lteOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).lte(attribute, value).isNull(attribute); }
	
	// Gt 
	public static Criterion gt   		(Attribute attribute, Object value) { return new Operator(attribute, OperatorType.Gt, value); }
	public static Criterion gtIfNotNull (Attribute attribute, Object value) { if(value != null) {return gt(attribute, value);} return null; }
	public static Aggregator gtOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).gt(attribute, value).isNull(attribute); }
	
	// Gte
	public static Criterion gte  		(Attribute attribute, Object value)        		{ return new Operator(attribute, OperatorType.Gte, value); }
	public static Criterion gteIfNotNull(Attribute attribute, Object value) { if(value != null) {return gte(attribute, value);} return null; }
	public static Aggregator gteOrNull	(Attribute attribute, Object value) { return new Aggregator(GroupType.OR).gte(attribute, value).isNull(attribute); }
	
	
	// Between
	public static Criterion between (Attribute attribute, Object min, Object max) 	{ return new Between(attribute, min, max); }
	public static Criterion betweenIfNotNull(Attribute attribute, Object min, Object max) {if(min != null && max != null) { return between(attribute, min, max);} return null; }
	public static Aggregator betweenOrNull(Attribute attribute, Object min, Object max) { return new Aggregator(GroupType.OR).between(attribute, min, max).isNull(attribute); }
	
	// In
	public static Criterion in   					(Attribute attribute, Collection<?> values){ return new In(attribute, values); }
	public static Criterion inIfNotEmpty			(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(attribute, values);} return null; }
	public static Aggregator inIfNotEmptyNullable	(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return Estivate.or(inIfNotEmpty(attribute, values.stream().filter(x -> x != null).collect(Collectors.toList()))).addIf(values.stream().anyMatch(x -> x == null), Estivate.isNull(attribute)); } return null; }
	public static EstivateNode inOrFalseIfEmpty		(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(attribute, values);} return keywordFalse(); }
	public static Aggregator inOrNull				(Attribute attribute, Collection<?> values){ return or(in(attribute, values), isNull(attribute)); }
	public static Aggregator inIfNotEmptyOrNull		(Attribute attribute, Collection<?> values){ return or(inIfNotEmpty(attribute, values), isNull(attribute)); }
	
	public static Criterion notIn   				(Attribute attribute, Collection<?> values){ return new NotIn(attribute, values); }
	public static Criterion notInIfNotEmpty			(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(attribute, values);} return null; }
	public static Aggregator notInIfNotEmptyNullable(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return Estivate.or(notInIfNotEmpty(attribute, values.stream().filter(x -> x != null).collect(Collectors.toList()))).addIf(values.stream().anyMatch(x -> x == null), Estivate.isNull(attribute)); } return null; }
	public static EstivateNode notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(attribute, values);} return keywordTrue(); }
	public static Aggregator notInOrNull			(Attribute attribute, Collection<?> values){ return or(notIn(attribute, values), isNull(attribute)); }
	public static Aggregator notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values){ return or(notInIfNotEmpty(attribute, values), isNull(attribute)); }
	
	// Like
	public static Criterion like 			(Attribute attribute, String value) { return new Operator(attribute, OperatorType.Like, value); }
	public static Criterion notLike			(Attribute attribute, String value) { return new Operator(attribute, OperatorType.NotLike, value); }
	public static Criterion likeIfNotNull 	(Attribute attribute, String value) { if(value != null) {return like(attribute, value);} return null;  }
	public static Criterion notLikeIfNotNull(Attribute attribute, String value) { if(value != null) {return notLike(attribute, value);} return null;  }
	public static Criterion likeIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return like(attribute, value);} return null; }
	public static Criterion notLikeIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLike(attribute, value);} return null; }
	public static Criterion likeIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return like(attribute, value);} return null; }
	public static Criterion notLikeIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLike(attribute, value);} return null; }
	
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
	public static Criterion likeStartsWithIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return likeStartsWith(attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLikeStartsWith(attribute, value);} return null; }
	public static Criterion likeStartsWithIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return likeStartsWith(attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLikeStartsWith(attribute, value);} return null; }
	
	// Like starts in
	public static Aggregator likeStartsWithIn(Attribute attribute, Collection<String> values)	{ return or(values.stream().map(x -> likeStartsWith(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeStartsWithInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeStartsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeStartsWithIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeStartsWith(attribute, x)).collect(Collectors.toList()));}
	public static Aggregator notLikeStartsWithInIfNotEmpty(Attribute attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notLikeStartsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	
	
	// Like ends
	public static Criterion likeEndsWith(Attribute attribute, String value)			{ return new Operator(attribute, OperatorType.Like, "%"+value);		}
	public static Criterion notLikeEndsWith(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.NotLike, "%"+value);	}
	public static Criterion likeEndsWithIfNotNull 	(Attribute attribute, String value) { if(value != null) {return likeEndsWith(attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotNull(Attribute attribute, String value) { if(value != null) {return notLikeEndsWith(attribute, value);} return null; }
	public static Criterion likeEndsWithIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return likeEndsWith(attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLikeEndsWith(attribute, value);} return null; }
	public static Criterion likeEndsWithIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return likeEndsWith(attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLikeEndsWith(attribute, value);} return null; }

	// Like ends in
	public static Aggregator likeEndsWithIn(Attribute attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeEndsWith(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeEndsWithInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> likeEndsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	public static Aggregator notLikeEndsWithIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeEndsWith(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator notLikeEndsWithInIfNotEmpty(Attribute attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notLikeEndsWith(attribute, x)).collect(Collectors.toList())); } return null; }
	
	// Like contains
	public static Criterion likeContains(Attribute attribute, String value)			{ return new Operator(attribute, OperatorType.Like, "%"+value+"%");	}
	public static Criterion notLikeContains(Attribute attribute, String value)		{ return new Operator(attribute, OperatorType.NotLike, "%"+value+"%"); }
	public static Criterion likeContainsIfNotNull 	(Attribute attribute, String value) { if(value != null) {return likeContains(attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotNull(Attribute attribute, String value) { if(value != null) {return notLikeContains(attribute, value);} return null; }
	public static Criterion likeContainsIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return likeContains(attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotEmpty(Attribute attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLikeContains(attribute, value);} return null; }
	public static Criterion likeContainsIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return likeContains(attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotBlank(Attribute attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLikeContains(attribute, value);} return null; }


	// Like contains in
	public static Aggregator likeContainsIn(Attribute attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeContains(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeContainsInIfNotEmpty(Attribute attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return likeContainsIn(attribute, values); } return null; }
	public static Aggregator notLikeContainsIn(Attribute attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeContains(attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator notLikeContainsInIfNotEmpty(Attribute attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return notLikeContainsIn(attribute, values); } return null; }

	// isNull
	public static Criterion isNull		(Attribute attribute) 						{ return new NullCheck(attribute, true);}
	public static Criterion isNotNull	(Attribute attribute) 						{ return new NullCheck(attribute, false);}
	
	// natively
	public static Criterion nativeCriterion (Attribute attribute, String criterion) { return new NativeCriterion(attribute, criterion); }
	
	// subQuery
	public static Criterion in(Attribute attribute, SelectQuery<?> subQuery)	  	{ return new InSubQuery(attribute, subQuery, true); }
	public static Criterion notIn(Attribute attribute, SelectQuery<?> subQuery)		{ return new InSubQuery(attribute, subQuery, false); }
	
	// exists
	public static Criterion existsSubQuery(SelectQuery<?> subQuery)		{ return new ExistsSubQuery(subQuery, true); }
	public static Criterion notExistsSubQuery(SelectQuery<?> subQuery)	{ return new ExistsSubQuery(subQuery, false); }
	

	public static Keyword keywordTrue() { return new Keyword(KeywordValue.TRUE); }
	public static Keyword keywordFalse() { return new Keyword(KeywordValue.FALSE); }


	/* Wrapper for Entity */
	
	// Eq methods
	public static Criterion eq   			(Entity<?> entity, String attribute, Object value) { return eq(Estivate.attribute(entity, attribute), value); }
	public static Criterion eqIfNotNull		(Entity<?> entity, String attribute, Object value) { if(value != null) {return eq(entity, attribute, value);} return null; }
	public static Criterion eqIfNotBlank	(Entity<?> entity, String attribute, String value) { if(StringUtils.isNotBlank(value)) { return eq(entity, attribute, value); } return null; }
	public static Criterion eqNullable		(Entity<?> entity, String attribute, Object value) { if(value != null) {return eq(entity, attribute, value);} return isNull(entity, attribute); }
	public static Aggregator eqOrNull		(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).eq(entity, attribute, value).isNull(entity, attribute); }
	public static Criterion notEq			(Entity<?> entity, String attribute, Object value) { return notEq(Estivate.attribute(entity, attribute), value); }
	public static Criterion notEqIfNotNull	(Entity<?> entity, String attribute, Object value) { if(value != null) {return notEq(entity, attribute, value);} return null; }
	public static Criterion notEqIfNotBlank (Entity<?> entity, String attribute, String value) { if(StringUtils.isNotBlank(value)) { return notEq(entity, attribute, value); } return null; }
	public static Criterion notEqNullable	(Entity<?> entity, String attribute, Object value) { if(value != null) {return notEq(entity, attribute, value);} return isNotNull(entity, attribute); }
	public static Aggregator notEqOrNull	(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).notEq(entity, attribute, value).isNull(entity, attribute); }
	
	// Lt 
	public static Criterion lt   	(Entity<?> entity, String attribute, Object value)        		{ return lt(Estivate.attribute(entity, attribute), value); }
	public static Criterion ltIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lt(entity, attribute, value);} return null; }
	public static Aggregator ltOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).lt(entity, attribute, value).isNull(entity, attribute); }
	
	// Lte
	public static Criterion lte  	(Entity<?> entity, String attribute, Object value)        		{ return lte(Estivate.attribute(entity, attribute), value); }
	public static Criterion lteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return lte(entity, attribute, value);} return null; }
	public static Aggregator lteOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).lte(entity, attribute, value).isNull(entity, attribute); }
	
	
	// Gt 
	public static Criterion gt   	(Entity<?> entity, String attribute, Object value)        		{ return gt(Estivate.attribute(entity, attribute), value); }
	public static Criterion gtIfNotNull   	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gt(entity, attribute, value);} return null; }
	public static Aggregator gtOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).gt(entity, attribute, value).isNull(entity, attribute); }
	
	// Gte
	public static Criterion gte  	(Entity<?> entity, String attribute, Object value)        		{ return gte(Estivate.attribute(entity, attribute), value); }
	public static Criterion gteIfNotNull  	(Entity<?> entity, String attribute, Object value) { if(value != null) {return gte(entity, attribute, value);} return null; }
	public static Aggregator gteOrNull(Entity<?> entity, String attribute, Object value) { return new Aggregator(GroupType.OR).gte(entity, attribute, value).isNull(entity, attribute); }
	
	
	// Between
	public static Criterion between (Entity<?> entity, String attribute, Object min, Object max) 	{ return between(Estivate.attribute(entity, attribute), min, max); }
	public static Criterion betweenIfNotNull(Entity<?> entity, String attribute, Object min, Object max) {if(min != null && max != null) { return between(entity, attribute, min, max);} return null; }
	public static Aggregator betweenOrNull(Entity<?> entity, String attribute, Object min, Object max) { return new Aggregator(GroupType.OR).between(entity, attribute, min, max).isNull(entity, attribute); }
	
	// In
	public static Criterion in   					(Entity<?> entity, String attribute, Collection<?> values){ return in(Estivate.attribute(entity, attribute), values); }
	public static Criterion inIfNotEmpty			(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(entity, attribute, values);} return null; }
	public static Aggregator inOrNull				(Entity<?> entity, String attribute, Collection<?> values){ return or(in(entity, attribute, values), isNull(entity, attribute)); }
	public static Aggregator inIfNotEmptyOrNull		(Entity<?> entity, String attribute, Collection<?> values){ return or(inIfNotEmpty(entity, attribute, values), isNull(entity, attribute)); }
	public static Aggregator inIfNotEmptyNullable	(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return Estivate.or(inIfNotEmpty(entity, attribute, values.stream().filter(x -> x != null).collect(Collectors.toList()))).addIf(values.stream().anyMatch(x -> x == null), Estivate.isNull(entity, attribute)); } return null; }
	public static EstivateNode inOrFalseIfEmpty		(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return in(entity, attribute, values);} return keywordFalse(); }
	
	public static Criterion notIn   				(Entity<?> entity, String attribute, Collection<?> values){ return notIn(Estivate.attribute(entity, attribute), values); }
	public static Criterion notInIfNotEmpty			(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(entity, attribute, values);} return null; }
	public static Aggregator notInOrNull			(Entity<?> entity, String attribute, Collection<?> values){ return or(notIn(entity, attribute, values), isNull(entity, attribute)); }
	public static Aggregator notInIfNotEmptyOrNull	(Entity<?> entity, String attribute, Collection<?> values){ return or(notInIfNotEmpty(entity, attribute, values), isNull(entity, attribute)); }
	public static EstivateNode notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values){ if(values != null && !values.isEmpty()) {return notIn(entity, attribute, values);} return keywordTrue(); }

	// Like
	public static Criterion like 			(Entity<?> entity, String attribute, String value) { return like(Estivate.attribute(entity, attribute), value); }
	public static Criterion notLike			(Entity<?> entity, String attribute, String value) { return notLike(Estivate.attribute(entity, attribute), value); }
	public static Criterion likeIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return like(entity, attribute, value);} return null;  }
	public static Criterion notLikeIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLike(entity, attribute, value);} return null;  }
	public static Criterion likeIfNotEmpty(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return like(entity, attribute, value);} return null; }
	public static Criterion notLikeIfNotEmpty(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLike(entity, attribute, value);} return null; }
	public static Criterion likeIfNotBlank(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return like(entity, attribute, value);} return null; }
	public static Criterion notLikeIfNotBlank(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLike(entity, attribute, value);} return null; }
	
	// Match Against
	public static Criterion matchAgainst			(Entity<?> entity, String attribute, String value) { return matchAgainst(entity, attribute, value); }
	public static Criterion notMatchAgainst 		(Entity<?> entity, String attribute, String value) { return notMatchAgainst(entity, attribute, value); }
	public static Criterion matchAgainstIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return matchAgainst(entity, attribute, value);} return null;  }
	public static Criterion notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notMatchAgainst(entity, attribute, value);} return null;  }
	
	// Like In
	public static Aggregator likeIn 			(Entity<?> entity, String attribute, Collection<String> values) { return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList())); }
	public static Aggregator likeInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { if(values != null && !values.isEmpty()) { return or(values.stream().map(x -> like(entity, attribute, x)).collect(Collectors.toList()));} return null; }
	public static Aggregator notLikeIn			(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLike(entity, attribute, x)).collect(Collectors.toList()));  		}
	public static Aggregator notLikeInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return and(values.stream().map(x -> notLike(entity, attribute, x)).collect(Collectors.toList()));} return null; }

	// Match Against In
	public static Aggregator matchAgainstIn 			(Entity<?> entity, String attribute, Collection<String> values)	{ return matchAgainstIn(entity, attribute, values); }
	public static Aggregator matchAgainstInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { return matchAgainstInIfNotEmpty(entity, attribute, values); } 
	public static Aggregator notMatchAgainstIn			(Entity<?> entity, String attribute, Collection<String> values)	{ return notMatchAgainstIn(entity, attribute, values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values)	{ return notMatchAgainstInIfNotEmpty(entity, attribute, values); }

	// Like starts
	public static Criterion likeStartsWith(Entity<?> entity, String attribute, String value)		{ return likeStartsWith(Estivate.attribute(entity, attribute), value);	}
	public static Criterion notLikeStartsWith(Entity<?> entity, String attribute, String value)		{ return notLikeStartsWith(Estivate.attribute(entity, attribute), value);}
	public static Criterion likeStartsWithIfNotNull (Entity<?> entity, String attribute, String value) 	{ if(value != null) {return likeStartsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value){ if(value != null) {return notLikeStartsWith(entity, attribute, value);} return null; }
	public static Criterion likeStartsWithIfNotEmpty (Entity<?> entity, String attribute, String value) 	{ if(value != null && !StringUtils.isEmpty(value)) {return likeStartsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotEmpty(Entity<?> entity, String attribute, String value){ if(value != null && !StringUtils.isEmpty(value)) {return notLikeStartsWith(entity, attribute, value);} return null; }
	public static Criterion likeStartsWithIfNotBlank (Entity<?> entity, String attribute, String value) 	{ if(value != null && !StringUtils.isBlank(value)) {return likeStartsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeStartsWithIfNotBlank(Entity<?> entity, String attribute, String value){ if(value != null && !StringUtils.isBlank(value)) {return notLikeStartsWith(entity, attribute, value);} return null; }
	
	// Like starts in
	public static Aggregator likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return or(values.stream().map(x -> likeStartsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeStartsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return likeStartsWithIn(entity, attribute, values); } return null; }
	public static Aggregator notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeStartsWith(entity, attribute, x)).collect(Collectors.toList()));}
	public static Aggregator notLikeStartsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return notLikeStartsWithIn(entity, attribute, values); } return null; }
	
	
	// Like ends
	public static Criterion likeEndsWith(Entity<?> entity, String attribute, String value)			{ return likeEndsWith(Estivate.attribute(entity, attribute), value); }
	public static Criterion notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ return notLikeEndsWith(Estivate.attribute(entity, attribute), value);	}
	public static Criterion likeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeEndsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeEndsWith(entity, attribute, value);} return null; }
	public static Criterion likeEndsWithIfNotEmpty 	(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return likeEndsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotEmpty(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLikeEndsWith(entity, attribute, value);} return null; }
	public static Criterion likeEndsWithIfNotBlank 	(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return likeEndsWith(entity, attribute, value);} return null; }
	public static Criterion notLikeEndsWithIfNotBlank(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLikeEndsWith(entity, attribute, value);} return null; }
	
	// Like ends in
	public static Aggregator likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeEndsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeEndsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return likeEndsWithIn(entity, attribute, values); } return null; }
	public static Aggregator notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeEndsWith(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator notLikeEndsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values)	{ if(values != null && !values.isEmpty()) { return notLikeEndsWithIn(entity, attribute, values); } return null; }
	
	// Like contains
	public static Criterion likeContains(Entity<?> entity, String attribute, String value)			{ return likeContains(Estivate.attribute(entity, attribute), value);	}
	public static Criterion notLikeContains(Entity<?> entity, String attribute, String value)		{ return notLikeContains(Estivate.attribute(entity, attribute), value);	}
	public static Criterion likeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { if(value != null) {return likeContains(entity, attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value) { if(value != null) {return notLikeContains(entity, attribute, value);} return null; }
	public static Criterion likeContainsIfNotEmpty 	(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return likeContains(entity, attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotEmpty(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isEmpty(value)) {return notLikeContains(entity, attribute, value);} return null; }
	public static Criterion likeContainsIfNotBlank 	(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return likeContains(entity, attribute, value);} return null; }
	public static Criterion notLikeContainsIfNotBlank(Entity<?> entity, String attribute, String value) { if(value != null && !StringUtils.isBlank(value)) {return notLikeContains(entity, attribute, value);} return null; }


	// Like contains in
	public static Aggregator likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ return or(values.stream().map(x -> likeContains(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator likeContainsInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return likeContainsIn(entity, attribute, values); } return null; }
	public static Aggregator notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ return and(values.stream().map(x -> notLikeContains(entity, attribute, x)).collect(Collectors.toList()));	}
	public static Aggregator notLikeContainsInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) 	{ if(values != null && !values.isEmpty()) { return notLikeContainsIn(entity, attribute, values); } return null; }
	
	// isNull
	public static Criterion isNull		(Entity<?> entity, String attribute) 						{ return isNull(Estivate.attribute(entity, attribute));}
	public static Criterion isNotNull	(Entity<?> entity, String attribute) 						{ return isNotNull(Estivate.attribute(entity, attribute));}
	
	// natively
	public static Criterion nativeCriterion (Entity<?> entity, String attribute, String criterion) { return nativeCriterion(Estivate.attribute(entity, attribute), criterion); }
	
	// subQuery
	public static Criterion in(Entity<?> entity, String attribute, SelectQuery<?> subQuery)	  	{ return in(Estivate.attribute(entity, attribute), subQuery); }
	public static Criterion notIn(Entity<?> entity, String attribute, SelectQuery<?> subQuery)	{ return notIn(Estivate.attribute(entity, attribute), subQuery); }
	
	
	

	
	/* Wrappers for Class */
	
	public static Criterion eq    			(Class<?> entity, String attribute, Object value) { return eq(new Entity<>(entity), attribute, value); }
	public static Criterion eqIfNotNull		(Class<?> entity, String attribute, Object value) { return eqIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion eqIfNotBlank	(Class<?> entity, String attribute, String value) { return eqIfNotBlank(new Entity<>(entity), attribute, value); }
	public static Criterion eqNullable		(Class<?> entity, String attribute, Object value) { return eqNullable(new Entity<>(entity), attribute, value); }
	public static Aggregator eqOrNull		(Class<?> entity, String attribute, Object value) { return eqOrNull(new Entity<>(entity), attribute, value); }

	public static Criterion notEq 			(Class<?> entity, String attribute, Object value) { return notEq(new Entity<>(entity), attribute, value); }
	public static Criterion notEqIfNotNull	(Class<?> entity, String attribute, Object value) { return notEqIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notEqIfNotBlank (Class<?> entity, String attribute, String value) { return notEqIfNotBlank(new Entity<>(entity), attribute, value); }
	public static Criterion notEqNullable	(Class<?> entity, String attribute, Object value) { return notEqNullable(new Entity<>(entity), attribute, value); }
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
	public static Aggregator inOrNull				(Class<?> entity, String attribute, Collection<?> values){ return or(in(entity, attribute, values), isNull(entity, attribute)); }
	public static Aggregator inIfNotEmptyOrNull		(Class<?> entity, String attribute, Collection<?> values){ return or(inIfNotEmpty(entity, attribute, values), isNull(entity, attribute)); }

	

	public static Criterion notIn   (Class<?> entity, String attribute, Collection<?> values) { return notIn(new Entity<>(entity), attribute, values); }
	public static Criterion notInIfNotEmpty		(Class<?> entity, String attribute, Collection<?> values){ return notInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static EstivateNode notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values){ return notInOrTrueIfEmpty(new Entity<>(entity), attribute, values); }
	
	
	public static Criterion like			(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public static Criterion notLike			(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	public static Criterion likeIfNotNull	(Class<?> entity, String attribute, String value)	{ return likeIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeIfNotNull(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWith(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	public static Criterion likeStartsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion likeStartsWithIfNotEmpty(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWithIfNotEmpty(Class<?> entity, String attribute, String value)	{ return notLikeStartsWithIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Criterion likeStartsWithIfNotBlank(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotBlank(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeStartsWithIfNotBlank(Class<?> entity, String attribute, String value)	{ return notLikeStartsWithIfNotBlank(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeEndsWith	(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	public static Criterion likeEndsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWithIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion likeEndsWithIfNotEmpty(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWithIfNotEmpty(Class<?> entity, String attribute, String value)	{ return notLikeEndsWithIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Criterion likeEndsWithIfNotBlank(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotBlank(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeEndsWithIfNotBlank(Class<?> entity, String attribute, String value)	{ return notLikeEndsWithIfNotBlank(new Entity<>(entity), attribute, value); }
	
	public static Criterion likeContains	(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }
	public static Criterion likeContainsIfNotNull(Class<?> entity, String attribute, String value)	{ return likeContainsIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notLikeContainsIfNotNull(Class<?> entity, String attribute, String value)	{ return notLikeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	
	public static Criterion ltIfNotNull   	(Class<?> entity, String attribute, Object value) { return ltIfNotNull(new Entity<>(entity), attribute, value); }

	public static Criterion lteIfNotNull  	(Class<?> entity, String attribute, Object value) { return lteIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion gteIfNotNull  	(Class<?> entity, String attribute, Object value) { return gteIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion betweenIfNotNull(Class<?> entity, String attribute, Object min, Object max) { return betweenIfNotNull(new Entity<>(entity), attribute, min, max); }

	public static Aggregator likeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return likeStartsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeStartsWithInIfNotEmpty(new Entity<>(entity), attribute, values);}
	public static Aggregator notLikeStartsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeStartsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return notLikeStartsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	
	public static Aggregator likeEndsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeEndsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeEndsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeEndsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notLikeEndsWithIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeEndsWithIn(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeEndsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return notLikeEndsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	
	public static Aggregator likeContainsIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeContainsIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeContainsInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return likeContainsInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notLikeContainsIn(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeContainsIn(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeContainsInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return notLikeContainsInIfNotEmpty(new Entity<>(entity), attribute, values); }
	 
	public static Aggregator likeIn				(Class<?> entity, String attribute, Collection<String> value)	{ return likeIn(new Entity<>(entity), attribute, value); }
	public static Aggregator likeInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> value)  	{ return likeInIfNotEmpty(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeIn			(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeIn(new Entity<>(entity), attribute, value); }
	public static Aggregator notLikeInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 	{ return notLikeInIfNotEmpty(new Entity<>(entity), attribute, values); }
	

	public static Criterion isNull		(Class<?> entity, String attribute) 						{ return isNull(new Entity<>(entity), attribute);}
	public static Criterion isNotNull	(Class<?> entity, String attribute) 						{ return isNotNull(new Entity<>(entity), attribute);}
	

	public static Criterion matchAgainst(Class<?> entity, String attribute, String value) 			{ return matchAgainst(new Entity<>(entity), attribute, value); }	
	public static Criterion matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return matchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	public static Criterion notMatchAgainst(Class<?> entity, String attribute, String value) 			{ return notMatchAgainst(new Entity<>(entity), attribute, value); }
	public static Criterion notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return notMatchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	
	public static Aggregator matchAgainstIn(Class<?> entity, String attribute, Collection<String> values)			{ return matchAgainstIn(new Entity<>(entity), attribute, values); }
	public static Aggregator matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 		{ return matchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public static Aggregator notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values)			{ return notMatchAgainstIn(new Entity<>(entity), attribute, values); }
	public static Aggregator notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) 		{ return notMatchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }


	public static Criterion nativeCriterion (Class<?> entity, String attribute, String criterion)	{ return nativeCriterion(new Entity<>(entity), attribute, criterion); }

	
	public static Criterion in(Class<?> entity, String attribute, SelectQuery<?> subQuery)	  	{ return in(new Entity<>(entity), attribute, subQuery); }
	public static Criterion notIn(Class<?> entity, String attribute, SelectQuery<?> subQuery)	{ return notIn(new Entity<>(entity), attribute, subQuery); }
	
	
	
	/* Wrappers for Lambda */
	
	public static <E, P> Criterion eq    			(AttributeGetter<E, P> getter, P value) { return eq(Estivate.attribute(getter), value); }
	public static <E, P> Criterion eqIfNotNull		(AttributeGetter<E, P> getter, P value) { return eqIfNotNull(Estivate.attribute(getter), value); }
	public static <E> 	 Criterion eqIfNotBlank		(AttributeGetter<E, String> getter, String value) { return eqIfNotBlank(Estivate.attribute(getter), value); }
	public static <E, P> Criterion eqNullable		(AttributeGetter<E, P> getter, P value) { return eqNullable(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator eqOrNull		(AttributeGetter<E, P> getter, P value) { return eqOrNull(Estivate.attribute(getter), value); }

	public static <E, P> Criterion notEq 			(AttributeGetter<E, P> getter, P value) { return notEq(Estivate.attribute(getter), value); }
	public static <E, P> Criterion notEqIfNotNull	(AttributeGetter<E, P> getter, P value) { return notEqIfNotNull(Estivate.attribute(getter), value); }
	public static <E> 	 Criterion notEqIfNotBlank	(AttributeGetter<E, String> getter, String value) { return notEqIfNotBlank(Estivate.attribute(getter), value); }
	public static <E, P> Criterion notEqNullable	(AttributeGetter<E, P> getter, P value) { return notEqNullable(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator notEqOrNull	(AttributeGetter<E, P> getter, P value) { return notEqOrNull(Estivate.attribute(getter), value); }

	
	public static <E, P> Criterion lt    		(AttributeGetter<E, P> getter, P value)        { return lt(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator ltOrNull(AttributeGetter<E, P> getter, P value) { return ltOrNull(Estivate.attribute(getter), value); }
	public static <E, P> Criterion ltIfNotNull   	(AttributeGetter<E, P> getter, P value) { return ltIfNotNull(Estivate.attribute(getter), value); }
	
	
	public static <E, P> Criterion lte   	(AttributeGetter<E, P> getter, P value)        { return lte(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator lteOrNull(AttributeGetter<E, P> getter, P value) { return lteOrNull(Estivate.attribute(getter), value); }
	public static <E, P> Criterion lteIfNotNull  	(AttributeGetter<E, P> getter, P value) { return lteIfNotNull(Estivate.attribute(getter), value); }
	
	
	public static <E, P> Criterion gt    	(AttributeGetter<E, P> getter, P value)        { return gt(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator gtOrNull(AttributeGetter<E, P> getter, P value) { return gtOrNull(Estivate.attribute(getter), value); }
	public static <E, P> Criterion gtIfNotNull   	(AttributeGetter<E, P> getter, P value) { return gtIfNotNull(Estivate.attribute(getter), value); }
	
	public static <E, P> Criterion gte   	(AttributeGetter<E, P> getter, P value)        { return gte(Estivate.attribute(getter), value); }
	public static <E, P> Aggregator gteOrNull(AttributeGetter<E, P> getter, P value) { return gteOrNull(Estivate.attribute(getter), value); }
	public static <E, P> Criterion gteIfNotNull  	(AttributeGetter<E, P> getter, P value) { return gteIfNotNull(Estivate.attribute(getter), value); }
	
	
	public static <E, P> Criterion between	(AttributeGetter<E, P> getter, P left, P right) { return between(Estivate.attribute(getter), left, right); }
	public static <E, P> Aggregator betweenOrNull(AttributeGetter<E, P> getter, P left, P right) { return betweenOrNull(Estivate.attribute(getter), left, right); }
	public static <E, P> Criterion betweenIfNotNull(AttributeGetter<E, P> getter, P min, P max) { return betweenIfNotNull(Estivate.attribute(getter), min, max); }

	
	public static <E, P> Criterion in    				(AttributeGetter<E, P> getter, Collection<P> values) { return in(Estivate.attribute(getter), values); }
	public static <E, P> Criterion inIfNotEmpty		(AttributeGetter<E, P> getter, Collection<P> values) { return inIfNotEmpty(Estivate.attribute(getter), values); }
	public static <E, P> EstivateNode inIfNotEmptyNullable  (AttributeGetter<E, P> getter, Collection<P> values) { return inIfNotEmptyNullable(Estivate.attribute(getter), values); }
	public static <E, P> EstivateNode inOrFalseIfEmpty	(AttributeGetter<E, P> getter, Collection<P> values) { return inOrFalseIfEmpty(Estivate.attribute(getter), values); }
	public static <E, P> Aggregator inOrNull				(AttributeGetter<E, P> getter, Collection<P> values){ return inOrNull(Estivate.attribute(getter), values); }
	public static <E, P> Aggregator inIfNotEmptyOrNull		(AttributeGetter<E, P> getter, Collection<P> values){ return inIfNotEmptyOrNull(Estivate.attribute(getter), values); }

	public static <E, P> Criterion notIn   (AttributeGetter<E, P> getter, Collection<P> values) { return notIn(Estivate.attribute(getter), values); }
	public static <E, P> Criterion notInIfNotEmpty		(AttributeGetter<E, P> getter, Collection<P> values){ return notInIfNotEmpty(Estivate.attribute(getter), values); }
	public static <E, P> Aggregator notInIfNotEmptyNullable(AttributeGetter<E, P> getter, Collection<P> values){ return notInIfNotEmptyNullable(Estivate.attribute(getter), values); }
	public static <E, P> EstivateNode notInOrTrueIfEmpty	(AttributeGetter<E, P> getter, Collection<P> values){ return notInOrTrueIfEmpty(Estivate.attribute(getter), values); }
	public static <E, P> Aggregator notInOrNull			(AttributeGetter<E, P> getter, Collection<P> values){ return notInOrNull(Estivate.attribute(getter), values); }
	public static <E, P> Aggregator notInIfNotEmptyOrNull	(AttributeGetter<E, P> getter, Collection<P> values){ return notInIfNotEmptyOrNull(Estivate.attribute(getter), values); }
	
	
	public static <E> Criterion like			(AttributeGetter<E, String> getter, String value)	{ return like(Estivate.attribute(getter), value); }
	public static <E> Criterion notLike			(AttributeGetter<E, String> getter, String value)	{ return notLike(Estivate.attribute(getter), value); }
	public static <E> Criterion likeIfNotNull		(AttributeGetter<E, String> getter, String value)	{ return likeIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeIfNotNull	(AttributeGetter<E, String> getter, String value)	{ return notLikeIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion likeIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return likeIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return notLikeIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion likeIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return likeIfNotBlank(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return notLikeIfNotBlank(Estivate.attribute(getter), value); }

	public static <E> Criterion likeStartsWith	(AttributeGetter<E, String> getter, String value)	{ return likeStartsWith(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeStartsWith(AttributeGetter<E, String> getter, String value)	{ return notLikeStartsWith(Estivate.attribute(getter), value); }
	public static <E> Criterion likeStartsWithIfNotNull(AttributeGetter<E, String> getter, String value)	{ return likeStartsWithIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeStartsWithIfNotNull(AttributeGetter<E, String> getter, String value)	{ return notLikeStartsWithIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion likeStartsWithIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return likeStartsWithIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeStartsWithIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return notLikeStartsWithIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion likeStartsWithIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return likeStartsWithIfNotBlank(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeStartsWithIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return notLikeStartsWithIfNotBlank(Estivate.attribute(getter), value); }

	public static <E> Criterion likeEndsWith	(AttributeGetter<E, String> getter, String value)	{ return likeEndsWith(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeEndsWith	(AttributeGetter<E, String> getter, String value)	{ return notLikeEndsWith(Estivate.attribute(getter), value); }
	public static <E> Criterion likeEndsWithIfNotNull(AttributeGetter<E, String> getter, String value)	{ return likeEndsWithIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeEndsWithIfNotNull(AttributeGetter<E, String> getter, String value)	{ return notLikeEndsWithIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion likeEndsWithIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return likeEndsWithIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeEndsWithIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return notLikeEndsWithIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion likeEndsWithIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return likeEndsWithIfNotBlank(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeEndsWithIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return notLikeEndsWithIfNotBlank(Estivate.attribute(getter), value); }
	
	public static <E> Criterion likeContains	(AttributeGetter<E, String> getter, String value)	{ return likeContains(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeContains	(AttributeGetter<E, String> getter, String value)	{ return notLikeContains(Estivate.attribute(getter), value); }
	public static <E> Criterion likeContainsIfNotNull(AttributeGetter<E, String> getter, String value)	{ return likeContainsIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeContainsIfNotNull(AttributeGetter<E, String> getter, String value)	{ return notLikeContainsIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion likeContainsIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return likeContainsIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeContainsIfNotEmpty(AttributeGetter<E, String> getter, String value)	{ return notLikeContainsIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Criterion likeContainsIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return likeContainsIfNotBlank(Estivate.attribute(getter), value); }
	public static <E> Criterion notLikeContainsIfNotBlank(AttributeGetter<E, String> getter, String value)	{ return notLikeContainsIfNotBlank(Estivate.attribute(getter), value); }

	
	


	public static <E> Aggregator likeStartsWithIn(AttributeGetter<E, String> getter, Collection<String> value)	{ return likeStartsWithIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator likeStartsWithInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return likeStartsWithInIfNotEmpty(Estivate.attribute(getter), values);}
	public static <E> Aggregator notLikeStartsWithIn(AttributeGetter<E, String> getter, Collection<String> value)	{ return notLikeStartsWithIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator notLikeStartsWithInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return notLikeStartsWithInIfNotEmpty(Estivate.attribute(getter), values); }
	
	public static <E> Aggregator likeEndsWithIn	(AttributeGetter<E, String> getter, Collection<String> value)	{ return likeEndsWithIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator likeEndsWithInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return likeEndsWithInIfNotEmpty(Estivate.attribute(getter), values); }
	public static <E> Aggregator notLikeEndsWithIn(AttributeGetter<E, String> getter, Collection<String> value)	{ return notLikeEndsWithIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator notLikeEndsWithInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return notLikeEndsWithInIfNotEmpty(Estivate.attribute(getter), values); }
	
	public static <E> Aggregator likeContainsIn	(AttributeGetter<E, String> getter, Collection<String> value)	{ return likeContainsIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator likeContainsInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return likeContainsInIfNotEmpty(Estivate.attribute(getter), values); }
	public static <E> Aggregator notLikeContainsIn(AttributeGetter<E, String> getter, Collection<String> value)	{ return notLikeContainsIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator notLikeContainsInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 	{ return notLikeContainsInIfNotEmpty(Estivate.attribute(getter), values); }
	 
	public static <E> Aggregator likeIn				(AttributeGetter<E, String> getter, Collection<String> value)	{ return likeIn(Estivate.attribute(getter), value); }
	public static <E> Aggregator likeInIfNotEmpty	(AttributeGetter<E, String> getter, Collection<String> value)  	{ return likeInIfNotEmpty(Estivate.attribute(getter), value); }
	public static <E> Aggregator notLikeIn			(AttributeGetter<E, String> getter, Collection<String> value)	{ return notLikeIn(Estivate.attribute(getter), value); }
	

	public static <E, P> Criterion isNull		(AttributeGetter<E, P> getter)	{ return isNull(Estivate.attribute(getter));}
	public static <E, P> Criterion isNotNull	(AttributeGetter<E, P> getter) 	{ return isNotNull(Estivate.attribute(getter));}
	

	public static <E> Criterion matchAgainst(AttributeGetter<E, String> getter, String value) 			{ return matchAgainst(Estivate.attribute(getter), value); }	
	public static <E> Criterion matchAgainstIfNotNull(AttributeGetter<E, String> getter, String value) { return matchAgainstIfNotNull(Estivate.attribute(getter), value); }
	public static <E> Criterion notMatchAgainst(AttributeGetter<E, String> getter, String value) 			{ return notMatchAgainst(Estivate.attribute(getter), value); }
	public static <E> Criterion notMatchAgainstIfNotNull(AttributeGetter<E, String> getter, String value) { return notMatchAgainstIfNotNull(Estivate.attribute(getter), value); }
	
	public static <E> Aggregator matchAgainstIn(AttributeGetter<E, String> getter, Collection<String> values)			{ return matchAgainstIn(Estivate.attribute(getter), values); }
	public static <E> Aggregator matchAgainstInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 		{ return matchAgainstInIfNotEmpty(Estivate.attribute(getter), values); }
	public static <E> Aggregator notMatchAgainstIn(AttributeGetter<E, String> getter, Collection<String> values)			{ return notMatchAgainstIn(Estivate.attribute(getter), values); }
	public static <E> Aggregator notMatchAgainstInIfNotEmpty(AttributeGetter<E, String> getter, Collection<String> values) 		{ return notMatchAgainstInIfNotEmpty(Estivate.attribute(getter), values); }
	
	public static <E> Criterion nativeCriterion (AttributeGetter<E, String> getter, String criterion)	{ return nativeCriterion(Estivate.attribute(getter), criterion); }
	
	public static <E, P> Criterion in(AttributeGetter<E, P> getter, SelectQuery<?> subQuery)	  	{ return in(Estivate.attribute(getter), subQuery); }
	public static <E, P> Criterion notIn(AttributeGetter<E, P> getter, SelectQuery<?> subQuery)	{ return notIn(Estivate.attribute(getter), subQuery); }
	
	
	 
	 public static class Functions{
		
		/* Math Functions */
		public static Attribute.Function abs = new Attribute.Function("abs(", ")");
		public static Attribute.Function round = new Attribute.Function("round(", ")");
		public static Attribute.Function ceil = new Attribute.Function("ceil(", ")");
		public static Attribute.Function floor = new Attribute.Function("floor(", ")");
		public static Attribute.Function mod(int mod) {return new Attribute.Function("mod(", mod+")"); }
		public static Attribute.Function pow = new Attribute.Function("pow(", ")"); 
		public static Attribute.Function sqrt = new Attribute.Function("sqrt(", ")");
		public static Attribute.Function log = new Attribute.Function("log(", ")");
		public static Attribute.Function exp = new Attribute.Function("exp(", ")"); 
		public static Attribute.Function sin = new Attribute.Function("sin(", ")"); 
		public static Attribute.Function cos = new Attribute.Function("cos(", ")"); 
		public static Attribute.Function tan = new Attribute.Function("tan(", ")"); 
		
		/* Date Functions */
		public static Attribute.Function date = new Attribute.Function("date(", ")"); 
		public static Attribute.Function date_add(int value, String unit){ return new Attribute.Function("date_add(", ", INTERVAL "+value+" "+unit+")"); }		
		
		public static Attribute.Function time = new Attribute.Function("time(", ")"); 
		public static Attribute.Function timestamp = new Attribute.Function("timestamp(", ")");
		public static Attribute.Function now = new Attribute.Function("now(", ")"); 
		public static Attribute.Function month = new Attribute.Function("month(", ")");
		public static Attribute.Function year = new Attribute.Function("year(", ")");

		/* String Functions */
		public static Attribute.Function lower = new Attribute.Function("lower(", ")"); 
		public static Attribute.Function upper = new Attribute.Function("upper(", ")"); 
		public static Attribute.Function length = new Attribute.Function("length(", ")"); 
		public static Attribute.Function trim = new Attribute.Function("trim(", ")");
		public static Attribute.Function charLength = new Attribute.Function("char_length(", ")");
		public static Attribute.Function field(List<String> fields) { return new Attribute.Function("FIELD(", String.join(", ", fields)+")"); }
		public static Attribute.Function field(String... fields) { return field(Arrays.asList(fields)); }
		
		

		/* Aggregate Functions : cannot be used in where clause */
		public static Attribute.Function count = new Attribute.Function("count(", ")");
		public static Attribute.Function countDistinct = new Attribute.Function("count(distinct ", ")");
		public static Attribute.Function sum = new Attribute.Function("sum(", ")");
		public static Attribute.Function sumDistinct = new Attribute.Function("sum(distinct ", ")");
		public static Attribute.Function avg = new Attribute.Function("avg(", ")");
		public static Attribute.Function avgDistinct = new Attribute.Function("avg(distinct ", ")");
		public static Attribute.Function min = new Attribute.Function("min(", ")");
		public static Attribute.Function max = new Attribute.Function("max(", ")");
		public static Attribute.Function groupConcat = new Attribute.Function("group_concat(", ")");
		public static Attribute.Function groupConcatDistinct = new Attribute.Function("group_concat(distinct ", ")");

		/* Null Handling Functions */
		public static Attribute.Function isNull = new Attribute.Function("", " IS NULL");
		public static Attribute.Function isNotNull = new Attribute.Function("", " IS NOT NULL");
		public static Attribute.Function ifNull(String alternative) { return new Attribute.Function("ifnull(", ", "+alternative+")"); }

		/* JSON Functions */
		public static Attribute.Function json_extract(String path){ return new Attribute.Function("JSON_EXTRACT(", ", \""+path+"\")"); }
		public static Attribute.Function json_unquote = new Attribute.Function("JSON_UNQUOTE(", ")");
		public static Attribute.Function json_keys = new Attribute.Function("JSON_KEYS(", ")");
		public static Attribute.Function json_length = new Attribute.Function("JSON_LENGTH(", ")");

		/* Window Functions */
		public static Attribute.Function rowNumber = new Attribute.Function("row_number()", "", true);
		
	}

	
	 
}
