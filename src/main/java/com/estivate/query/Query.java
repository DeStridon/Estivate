package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Estivate;
import com.estivate.query.Attribute.Function;
import com.estivate.query.Query.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public abstract class Query<Q extends Query<Q, T>, T> extends Aggregator {
	
	@Getter
	final Entity<T> entity;
	
	public Query(Class<T> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<T>(baseClass);
	}
	
	public Query(Entity<T> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}
	
	
	@Getter
	List<String> comments = new ArrayList<>();

	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	@Getter
	Set<Join> joins = new LinkedHashSet<>();

	
	@Getter
	List<Order> orders = new ArrayList<>();

	
	@Getter
	Integer offset;

	@Getter
	Integer limit;
	
	
	public abstract Query<Q, T> clone();
	
	@SuppressWarnings("unchecked")
	protected Q self() {
        return (Q) this;
    }

	public Q comment(String comment) {
		comments.add(comment);
		return self();
	}

	public Q join(Join join) { 
		if(joins.stream().noneMatch(x -> x.leftEntity.equals(join.leftEntity) && x.rightEntity.equals(join.rightEntity) && x.joinType == join.joinType)) {
			joins.add(join);
		}
		return self();
	}

	public Q joinInner(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinInner(leftEntity, rightEntity)); }
	public Q joinInner(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass)));}
	public Q joinInner(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity));}
	public Q joinInner(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinOuter(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinOuter(leftEntity, rightEntity)); }
	public Q joinOuter(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass))); }
	public Q joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity)); }
	public Q joinOuter(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinLeft(leftEntity, rightEntity)); }
	public Q joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)	{ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass))); }
	public Q joinLeft(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity)); }
	public Q joinLeft(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinRight(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinRight(leftEntity, rightEntity)); }
	public Q joinRight(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass))); }
	public Q joinRight(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity)); }
	public Q joinRight(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	
	public Q order(Order order) { orders.add(order); return self(); }
	public Q order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
		orders.add(Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build()); 
		return self(); 
	}
	public Q order(Class<?> entity, String attribute, Order.Direction direction, Function function) { return order(new Entity<>(entity), attribute, direction, function); }
	public Q order(String attribute, Order.Direction direction, Function function) 					{ return order(this.entity, attribute, direction, function); }
	public Q order(Entity<?> entity, String attribute, Order.Direction direction) 					{ return order(entity, attribute, direction, null); }
	public Q order(Class<?> entity, String attribute, Order.Direction direction) 					{ return order(new Entity<>(entity), attribute, direction, null); }
	public Q order(String attribute, Order.Direction direction) 									{ return order(this.entity, attribute, direction, null); }
	public Q order(Attribute attribute, Order.Direction direction) 									{ return order(attribute.entity, attribute.attribute, direction, attribute.function); }
	public Q orderAlias(String alias, Order.Direction direction) 									{ orders.add(Order.builder().attribute(alias).direction(direction).build()); return self(); }

	public Q orderAsc(Entity<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Asc); }
	public Q orderAsc(Entity<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Q orderAsc(Class<?> c, String attribute) 						{ return order(c, attribute, Order.Direction.Asc); }
	public Q orderAsc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Q orderAsc(String attribute) 									{ return order(this.entity, attribute, Order.Direction.Asc); }
	public Q orderAsc(String attribute, Function function) 				{ return order(this.entity, attribute, Order.Direction.Asc, function); }
	public Q orderAsc(Attribute attribute) 								{ return order(attribute.entity, attribute.attribute, Order.Direction.Asc, attribute.function); }
	public Q orderAscAlias(String alias)									{ return orderAlias(alias, Order.Direction.Asc); }
	
	public Q orderDesc(Entity<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Desc); }
	public Q orderDesc(Entity<?> c, String attribute, Function function)	{ return order(c, attribute, Order.Direction.Desc, function); }
	public Q orderDesc(Class<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Desc); }
	public Q orderDesc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Desc, function); }
	public Q orderDesc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Desc); }
	public Q orderDesc(String attribute, Function function) 				{ return order(this.entity, attribute, Order.Direction.Desc, function); }
	public Q orderDesc(Attribute attribute) 								{ return order(attribute.entity, attribute.attribute, Order.Direction.Desc, attribute.function); }
	public Q orderDescAlias(String alias)								{ return orderAlias(alias, Order.Direction.Desc); }
	
	
	public Q clearOrders(){
		orders.clear();
		return self();
	}


	public Q limit(Integer limit) { this.limit = limit; return self(); }
	public Q limitIfNotNull(Integer limit) { if(limit != null) { this.limit = limit; } return self(); }
	public Q limitIfNotNullOr(Integer limit, Integer fallbackLimit) { if(limit != null) { this.limit = limit; } else { this.limit = fallbackLimit; } return self(); }
	public Q offset(Integer offset) { this.offset = offset; return self();}
	public Q offsetIfNotNull(Integer offset) { if(offset != null) { this.offset = offset; } return self(); }
	public Q offsetIfNotNullOr(Integer offset, Integer fallbackOffset) { if(offset != null) { this.offset = offset; } else { this.offset = fallbackOffset; } return self(); }

	
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
	
	
	public Q eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return self(); }
	public Q eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return self(); }
	public Q eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return self(); }
	public Q notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return self(); }
	public Q notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return self(); }
	public Q notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return self(); }
	
	public Q lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return self(); }
	public Q ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return self(); }
	
	public Q lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return self(); }
	public Q lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return self(); }
	
	
	public Q gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return self(); }
	public Q gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return self(); }
	public Q gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return self(); }
	public Q gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return self(); }
	
	public Q between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return self(); }
	public Q betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return self(); }

	public Q in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return self(); }
	public Q inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return self(); }
	public Q inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return self(); }
	public Q inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return self(); }
	public Q inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return self(); }
	public Q notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return self(); }
	public Q notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return self(); }


	public Q like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return self(); }
	public Q likeIn 		(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return self(); }
	public Q notLike		(Attribute attribute, String value)				{ super.notLike(attribute, value);  return self(); }
	public Q notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return self(); }
	
	
	public Q likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return self(); }
	public Q likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return self(); }
	public Q notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return self(); }
	
	public Q likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return self(); }
	public Q notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return self(); }
	
	public Q likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return self(); }
	public Q notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return self(); }

	public Q likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return self(); }
	public Q likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return self(); }
	public Q notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return self(); }
	public Q notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return self(); }

	public Q nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return self(); }
	
	public Q inSubQuery			(Attribute attribute, SelectQuery<?> subQuery){ super.inSubQuery(attribute, subQuery); return self(); }
	public Q notInSubQuery		(Attribute attribute, SelectQuery<?> subQuery){ super.notInSubQuery(attribute, subQuery); return self(); }
	public Q exists		(SelectQuery<?> subQuery){ super.exists(subQuery); return self(); }
	public Q notExists	(SelectQuery<?> subQuery){ super.notExists(subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return self(); }
	
	
	public Q likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return self(); }
	public Q likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty  (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return self(); }

	public Q matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return self(); }

	public Q matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return self(); }

	public Q notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return self(); }

	public Q notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return self(); }


	public Q matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return self(); }
	public Q notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return self(); }
	
	public Q isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return self();}
	public Q isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return self();}

	public Q eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return self();	}
	public Q ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return self();	}
	public Q gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return self();	}
	public Q lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return self();	}
	public Q gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return self();	}


	// Attribute only wrappers

	public Q eq   			(String attribute, Object value)	{ super.eq(this.entity, attribute, value);  return self(); }
	public Q eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return self(); }
	public Q eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return self(); }
	public Q notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return self(); }
	public Q notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return self(); }
	public Q notEqNullable	(String attribute, Object value)	{ super.notEqNullable(this.entity, attribute, value); return self(); }
	
	public Q lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return self(); }
	public Q ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return self(); }
	
	public Q lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return self(); }
	public Q lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return self(); }
	
	
	public Q gt   	(String attribute, Object value)        		{ super.gt(this.entity, attribute, value);  return self(); }
	public Q gtIfNotNull   		(String attribute, Object value)   { super.gtIfNotNull(this.entity, attribute, value);  return self(); }
	public Q gte  	(String attribute, Object value)        		{ super.gte(this.entity, attribute, value);  return self(); }
	public Q gteIfNotNull  		(String attribute, Object value)   { super.gteIfNotNull(this.entity, attribute, value);  return self(); }
	
	public Q between(String attribute, Object min, Object max) 	{ super.between(this.entity, attribute, min, max); return self(); }
	public Q betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return self(); }

	public Q in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return self(); }
	public Q inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return self(); }
	public Q inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return self(); }
	public Q notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return self(); }
	public Q notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return self(); }


	public Q like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return self(); }
	public Q likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return self(); }
	public Q notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return self(); }
	public Q notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return self(); }
	
	
	public Q likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return self(); }
	public Q likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return self(); }
	
	public Q likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return self(); }
	
	public Q likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return self(); }
	public Q notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return self(); }

	public Q likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return self(); }
	public Q likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return self(); }

	public Q nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return self(); }
	
	public Q inSubQuery			(String attribute, SelectQuery<?> subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return self(); }
	public Q notInSubQuery		(String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return self(); }
	
	
	public Q likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return self(); }

	public Q matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return self(); }

	public Q matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return self(); }

	public Q notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return self(); }

	public Q notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return self(); }


	public Q matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return self(); }
	
	public Q isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return self();}
	public Q isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return self();}

	public Q eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return self();	}
	public Q ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return self();	}
	public Q gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return self();	}
	public Q lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return self();	}
	public Q gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return self();	}


	// Class wrappers
	public Q eq   			(Class<?> entity, String attribute, Object value)	{ super.eq(entity, attribute, value);  return self(); }
	public Q eqIfNotNull   (Class<?> entity, String attribute, Object value)   { super.eqIfNotNull(entity, attribute, value);  return self(); }
	public Q eqNullable	(Class<?> entity, String attribute, Object value)   { super.eqNullable(entity, attribute, value); return self(); }
	public Q notEq			(Class<?> entity, String attribute, Object value)   { super.notEq(entity, attribute, value);  return self(); }
	public Q notEqIfNotNull(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return self(); }
	public Q notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return self(); }
	
	public Q lt   			(Class<?> entity, String attribute, Object value)   { super.lt(entity, attribute, value);  return self(); }
	public Q ltIfNotNull	(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull(entity, attribute, value);  return self(); }
	
	public Q lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return self(); }
	public Q lteIfNotNull  (Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return self(); }
	
	
	public Q gt   			(Class<?> entity, String attribute, Object value)   { super.gt    (entity, attribute, value);  return self(); }
	public Q gtIfNotNull 	(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return self(); }
	public Q gte  			(Class<?> entity, String attribute, Object value)   { super.gte   (entity, attribute, value);  return self(); }
	public Q gteIfNotNull	(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return self(); }
	
	public Q between			(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return self(); }
	public Q betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return self(); }

	public Q in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return self(); }
	public Q inIfNotEmpty  		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable  (Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return self(); }
	public Q inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return self(); }
	public Q notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return self(); }
	public Q notInOrNull		  	(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return self(); }
	


	public Q like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return self(); }
	public Q likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return self(); }
	public Q notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return self(); }
	public Q notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return self(); }
	
	
	public Q likeStartsWith 		(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return self(); }
	public Q likeStartsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return self(); }
	
	public Q likeEndsWith 			(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return self(); }
	public Q notLikeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return self(); }
	
	public Q likeContains 			(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return self(); }
	public Q notLikeContains 		(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return self(); }

	public Q likeEndsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return self(); }
	public Q likeContainsIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return self(); }

	public Q nativeCriterion 		(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return self(); }
	
	public Q inSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.inSubQuery(entity, attribute, subQuery); return self(); }
	public Q notInSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(entity, attribute, subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty		(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return self(); }
	
	
	public Q likeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q likeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return self(); }

	public Q matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return self(); }
	public Q matchAgainst(Class<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return self(); }
	public Q matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q matchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return self(); }
	public Q notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return self(); }
	public Q notMatchAgainst(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return self(); }
	public Q notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q notMatchAgainstIfNotNull(Class<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return self(); }

	public Q matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return self(); }
	public Q matchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return self(); }
	public Q notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Class<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return self(); }
	
	public Q isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return self();}
	public Q isNull	(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return self();}

	public Q eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return self();	}
	public Q ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return self();	}
	public Q gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return self();	}
	public Q lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return self();	}
	public Q gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return self();	}


	public Q eq   			(Entity<?> entity, String attribute, Object value)      { super.eq    	(entity, attribute, value);  		return self(); }
	public Q eqNullable	(Entity<?> entity, String attribute, Object value)		{ super.eqNullable(entity, attribute, value); 		return self(); }
	public Q notEq			(Entity<?> entity, String attribute, Object value)      { super.notEq 	(entity, attribute, value);  		return self(); }
	public Q notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return self(); }
	public Q notEqNullable	(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return self(); }
	

	public Q lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return self(); }
	public Q lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return self(); }
	
	public Q gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return self(); }
	public Q gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return self(); }
	public Q between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return self(); }

	public Q in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return self(); }
	public Q inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return self(); }
	public Q inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return self(); }
	public Q notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return self(); }
	public Q notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return self(); }
	
	public Q like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return self(); }
	public Q likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return self(); }
	public Q likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return self(); }
	public Q likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return self(); }

	public Q notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return self(); }
	public Q notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return self(); }
	public Q notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return self(); }
	public Q notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return self(); }

	public Q likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return self(); }
	public Q likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return self(); }
	public Q likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return self(); }
	public Q likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return self(); }

	public Q notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return self(); }
	public Q notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return self(); }
	public Q notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return self(); }
	public Q notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return self(); }

	public Q eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return self(); }
	public Q ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return self(); }
	public Q gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return self(); }
	public Q lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return self(); }
	public Q gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return self(); }
	public Q betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return self(); }
	
	public Q notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return self(); }
	
	public Q likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return self(); }
	public Q likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return self(); }
	public Q likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return self(); }

	public Q likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return self(); }

	
	public Q notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return self(); }
	public Q notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return self(); }
	public Q notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return self(); }
	
	public Q isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return self();}
	public Q isNull				(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return self();}
	public Q eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return self();	}
	public Q ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return self();	}
	public Q gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return self();	}
	public Q lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return self();}
	public Q gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return self();}
	
	public Q matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return self(); }	
	public Q matchAgainst(Entity<?> entity, List<String> attributes, String value) { super.matchAgainst(entity, attributes, value); return self(); }
	public Q matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q matchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.matchAgainstIfNotNull(entity, attributes, value); return self(); }
	public Q notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return self(); }
	public Q notMatchAgainst(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainst(entity, attributes, value); return self(); }
	public Q notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q notMatchAgainstIfNotNull(Entity<?> entity, List<String> attributes, String value) { super.notMatchAgainstIfNotNull(entity, attributes, value); return self(); }
	
	public Q matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return self(); }	
	public Q matchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstIn(entity, attributes, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attributes, values); return self(); }
	public Q notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstIn(entity, attributes, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Entity<?> entity, List<String> attributes, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attributes, values); return self(); }


}
