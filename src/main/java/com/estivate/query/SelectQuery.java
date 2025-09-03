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
public class SelectQuery<T> extends Query<SelectQuery<T>, T> {
	


	



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
		
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinInner(leftEntity, rightEntity); return this; }
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinInner(leftEntity, rightClass); return this;}
//	public SelectQuery<T> joinInner(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinInner(leftClass, rightEntity); return this; }
//	public SelectQuery<T> joinInner(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinInner(leftClass, rightClass); return this; }
//
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinOuter(leftEntity, rightEntity); return this; }
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinOuter(leftEntity, rightClass); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinOuter(leftClass, rightEntity); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinOuter(leftClass, rightClass); return this; }
//
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)			{ super.joinLeft(leftEntity, rightEntity); return this; }
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)			{ super.joinLeft(leftEntity, rightClass); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinLeft(leftClass, rightEntity); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinLeft(leftClass, rightClass); return this; }
//
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity)			{ super.joinRight(leftEntity, rightEntity); return this; }
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, Class<?> rightClass)			{ super.joinRight(leftEntity, rightClass); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, 	Entity<?> rightEntity)			{ super.joinRight(leftClass, rightEntity); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, 	Class<?> rightClass)			{ super.joinRight(leftClass, rightClass); return this; }
//
//
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinInner(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinInner(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinOuter(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinOuter(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinLeft(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinLeft(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightEntity, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightClass, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightSubQuery, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinRight(leftClass, rightQuery, alias, leftAttribute, rightAttribute); return this; }
//	public SelectQuery<T> joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ super.joinRight(leftEntity, rightQuery, alias, leftAttribute, rightAttribute); return this; }

//	public SelectQuery<T> order(Order order) { super.order(order); return this; }
//	public SelectQuery<T> order(Entity<?> entity, String attribute, Order.Direction direction, Function function) {
//		super.order(entity, attribute, direction, function);
//		return this;
//	}
//	public SelectQuery<T> order(Class<?> entity, String attribute, Order.Direction direction, Function function) { super.order(entity, attribute, direction, function); return this; }
//	public SelectQuery<T> order(String attribute, Order.Direction direction, Function function) { super.order(this.entity, attribute, direction, function); return this; }
//	public SelectQuery<T> order(Entity<?> entity, String attribute, Order.Direction direction) { super.order(entity, attribute, direction, null); return this; }
//	public SelectQuery<T> order(Class<?> entity, String attribute, Order.Direction direction) { super.order(new Entity<>(entity), attribute, direction, null); return this; }
//	public SelectQuery<T> order(String attribute, Order.Direction direction) { super.order(this.entity, attribute, direction, null); return this; }
//	public SelectQuery<T> order(Attribute attribute, Order.Direction direction) { super.order(attribute.entity, attribute.attribute, direction, attribute.function); return this; }
//	public SelectQuery<T> orderAlias(String alias, Order.Direction direction) { super.orderAlias(alias, direction); return this; }
//
//	public SelectQuery<T> orderAsc(Entity<?> c, String attribute) { super.orderAsc(c, attribute); return this; }
//	public SelectQuery<T> orderAsc(Entity<?> c, String attribute, Function function) { super.orderAsc(c, attribute, function); return this; }
//	public SelectQuery<T> orderAsc(Class<?> c, String attribute) { super.orderAsc(c, attribute); return this; }
//	public SelectQuery<T> orderAsc(Class<?> c, String attribute, Function function) { super.orderAsc(c, attribute, function); return this; }
//	public SelectQuery<T> orderAsc(String attribute) { super.orderAsc(attribute); return this; }
//	public SelectQuery<T> orderAsc(String attribute, Function function) { super.orderAsc(attribute, function); return this; }
//	public SelectQuery<T> orderAsc(Attribute attribute) { super.orderAsc(attribute); return this; }
//	public SelectQuery<T> orderAscAlias(String alias){ super.orderAscAlias(alias); return this; }
//	
//	public SelectQuery<T> orderDesc(Entity<?> c, String attribute) { super.orderDesc(c, attribute); return this; }
//	public SelectQuery<T> orderDesc(Entity<?> c, String attribute, Function function){ super.orderDesc(c, attribute, function); return this; }
//	public SelectQuery<T> orderDesc(Class<?> c, String attribute) { super.orderDesc(c, attribute); return this; }
//	public SelectQuery<T> orderDesc(Class<?> c, String attribute, Function function) { super.orderDesc(c, attribute, function); return this; }
//	public SelectQuery<T> orderDesc(String attribute) { super.orderDesc(attribute); return this; }
//	public SelectQuery<T> orderDesc(String attribute, Function function) { super.orderDesc(attribute, function); return this; }
//	public SelectQuery<T> orderDesc(Attribute attribute) { super.orderDesc(attribute); return this; }
//	public SelectQuery<T> orderDescAlias(String alias) { super.orderDescAlias(alias); return this; }
	
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
	public SelectQuery<T> clearOrders(){ super.clearOrders(); return this; }
	
	
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
	
	public Long fetchCount(Context context) { return context.fetchCount(this); }


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
