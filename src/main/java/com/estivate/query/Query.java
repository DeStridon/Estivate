package com.estivate.query;

import java.util.ArrayList;
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

public abstract class Query<T> extends Aggregator {
	
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
	
	
	public abstract Query<T> clone();
	
	

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

	public Query<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity)			{ return join(Estivate.joinInner(leftEntity, rightEntity)); }
	public Query<T> joinInner(Entity<?> leftEntity, Class<?> rightClass)			{ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass)));}
	public Query<T> joinInner(Class<?> leftClass, 	Entity<?> rightEntity)			{ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity));}
	public Query<T> joinInner(Class<?> leftClass, 	Class<?> rightClass)			{ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Query<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity)			{ return join(Estivate.joinOuter(leftEntity, rightEntity)); }
	public Query<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass)			{ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)			{ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinOuter(Class<?> leftClass, 	Class<?> rightClass)			{ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Query<T> joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)			{ return join(Estivate.joinLeft(leftEntity, rightEntity)); }
	public Query<T> joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)			{ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinLeft(Class<?> leftClass, 	Entity<?> rightEntity)			{ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinLeft(Class<?> leftClass, 	Class<?> rightClass)			{ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Query<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity)			{ return join(Estivate.joinRight(leftEntity, rightEntity)); }
	public Query<T> joinRight(Entity<?> leftEntity, Class<?> rightClass)			{ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass))); }
	public Query<T> joinRight(Class<?> leftClass, 	Entity<?> rightEntity)			{ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity)); }
	public Query<T> joinRight(Class<?> leftClass, 	Class<?> rightClass)			{ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass))); }


	public Query<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Query<T> joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Query<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Query<T> joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Query<T> joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Query<T> joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Query<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Query<T> joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	
	public Query<T> order(Order order) { orders.add(order); return this; }
	public Query<T> order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
		orders.add(Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build()); 
		return this; 
	}
	public Query<T> order(Class<?> entity, String attribute, Order.Direction direction, Function function) 	{ return order(new Entity<>(entity), attribute, direction, function); }
	public Query<T> order(String attribute, Order.Direction direction, Function function) 					{ return order(this.entity, attribute, direction, function); }
	public Query<T> order(Entity<?> entity, String attribute, Order.Direction direction) 					{ return order(entity, attribute, direction, null); }
	public Query<T> order(Class<?> entity, String attribute, Order.Direction direction) 					{ return order(new Entity<>(entity), attribute, direction, null); }
	public Query<T> order(String attribute, Order.Direction direction) 										{ return order(this.entity, attribute, direction, null); }
	public Query<T> order(Attribute attribute, Order.Direction direction) 									{ return order(attribute.entity, attribute.attribute, direction, attribute.function); }
	public Query<T> orderAlias(String alias, Order.Direction direction) 									{ orders.add(Order.builder().attribute(alias).direction(direction).build()); return this; }

	public Query<T> orderAsc(Entity<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(Entity<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(Class<?> c, String attribute) 						{ return order(c, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(String attribute) 									{ return order(this.entity, attribute, Order.Direction.Asc); }
	public Query<T> orderAsc(String attribute, Function function) 				{ return order(this.entity, attribute, Order.Direction.Asc, function); }
	public Query<T> orderAsc(Attribute attribute) 								{ return order(attribute.entity, attribute.attribute, Order.Direction.Asc, attribute.function); }
	public Query<T> orderAscAlias(String alias)									{ return orderAlias(alias, Order.Direction.Asc); }
	
	public Query<T> orderDesc(Entity<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(Entity<?> c, String attribute, Function function)	{ return order(c, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(Class<?> c, String attribute) 					{ return order(c, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(Class<?> c, String attribute, Function function) 	{ return order(c, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(String attribute) 								{ return order(this.entity, attribute, Order.Direction.Desc); }
	public Query<T> orderDesc(String attribute, Function function) 				{ return order(this.entity, attribute, Order.Direction.Desc, function); }
	public Query<T> orderDesc(Attribute attribute) 								{ return order(attribute.entity, attribute.attribute, Order.Direction.Desc, attribute.function); }
	public Query<T> orderDescAlias(String alias)								{ return orderAlias(alias, Order.Direction.Desc); }
	
	


	public Query<T> limit(Integer limit) { this.limit = limit; return this; }
	public Query<T> limitIfNotNull(Integer limit) { if(limit != null) { this.limit = limit; } return this; }
	public Query<T> limitIfNotNullOr(Integer limit, Integer fallbackLimit) { if(limit != null) { this.limit = limit; } else { this.limit = fallbackLimit; } return this; }
	public Query<T> offset(Integer offset) { this.offset = offset; return this;}
	public Query<T> offsetIfNotNull(Integer offset) { if(offset != null) { this.offset = offset; } return this; }
	public Query<T> offsetIfNotNullOr(Integer offset, Integer fallbackOffset) { if(offset != null) { this.offset = offset; } else { this.offset = fallbackOffset; } return this; }

	
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


}
