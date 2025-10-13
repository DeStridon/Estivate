package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.estivate.context.Context;
import com.estivate.result.Result;
import com.estivate.result.ResultMapping;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class SelectQuery<T> extends Query<SelectQuery<T>, T> {
	

	public SelectQuery<T> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	
	
	
	@Getter
	boolean distinct = false;
	
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
	
	public SelectQuery<T> distinct(){
		return distinct(true);
	}
	
	public SelectQuery<T> distinct(boolean distinct){
		this.distinct = distinct;
		return this;
	}
	
	public SelectQuery<T> join(Join join) { 
		super.join(join);
		return this;
	}
	
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

	// Select count
	public SelectQuery<T> selectCountAs(String alias) { return selectFunctionAs(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count field 
	public SelectQuery<T> selectCountAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.count, alias); }
	
	// Select count distinct field
	public SelectQuery<T> selectCountDistinctAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	
	// Select min
	public SelectQuery<T> selectMinAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.min, alias); }
	
	// Select max
	public SelectQuery<T> selectMaxAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.max, alias); }

	// Select Sum
	public SelectQuery<T> selectSumAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.sum, alias); }
	
	// Select Avg
	public SelectQuery<T> selectAvgAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<T> selectAvgAs(Entity<?> c, String attribute, String alias) 	{ return selectFunctionAs(c, attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<T> selectAvgAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.avg, alias); }

	// Select Group Concat
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
		
		queryClone.distinct = this.distinct;
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


	// ==================== FETCH METHODS ====================
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
	
	public List<T> 			fetchList(Context context){ return context.fetchListAs(this, (Class<T>) entity.entity); }
	public <U> List<U> 		fetchListAs(Context context, Class<U> clazz) { return context.fetchListAs(this, clazz); }
	public List<Result> 	fetchListAsResults(Context context) { return context.fetchListAsResults(this); }
	public List<String> 	fetchListAsString(Context context) { return context.fetchListAsString(this); }
	public List<Short> 		fetchListAsShort(Context context) { return context.fetchListAsShort(this); }
	public List<Integer> 	fetchListAsInteger(Context context) { return context.fetchListAsInteger(this); }
	public List<Long> 		fetchListAsLong(Context context) { return context.fetchListAsLong(this); }
	public List<Float> 		fetchListAsFloat(Context context) { return context.fetchListAsFloat(this); }
	public List<Double> 	fetchListAsDouble(Context context) { return context.fetchListAsDouble(this); }
	public List<Date> 		fetchListAsDate(Context context) { return context.fetchListAsDate(this); }
	public List<Boolean> 	fetchListAsBoolean(Context context) { return context.fetchListAsBoolean(this); }
	
	// ==================== PROJECT METHODS ====================
	public <U> U project(Context context, Class<U> clazz) { return context.project(this, clazz); }
	public <U> Optional<U> projectOptional(Context context, Class<U> clazz) { return context.projectOptional(this, clazz); }
	public <U> List<U> projectList(Context context, Class<U> clazz) { return context.projectList(this, clazz); }
	
	public Object projectAttribute(Context context, Class<?> entity, String attributeName) { return context.projectAttribute(this, entity, attributeName); }
	public Optional<Object> projectAttributeOptional(Context context, Class<?> entity, String attributeName) { return context.projectAttributeOptional(this, entity, attributeName); }
	public List<?> projectAttributeList(Context context, Class<?> entity, String attributeName) { return context.projectAttributeList(this, entity, attributeName); }
	public Set<?> projectAttributeSet(Context context, Class<?> entity, String attributeName) { return context.projectAttributeSet(this, entity, attributeName); }

	public Long projectCount(Context context) { return context.projectCount(this); }
	public Optional<Long> projectCountOptional(Context context) { return context.projectCountOptional(this); }

	


	// ==================== AGGREGATION METHODS ====================
	
	public <U, V> Map<U, V> aggregateToMap(Context context, java.util.function.Function<Result,U> uType, java.util.function.Function<Result,V> vType){
		return context.aggregateToMap(this, uType, vType);
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(Context context, java.util.function.Function<Result,U> uType, java.util.function.Function<Result,V> vType){
		return context.aggregateToMapList(this, uType, vType);
	}


	// ==================== MISC METHODS ====================
	public SubQueryEntity<T> asSubQueryEntity(String alias){
		return Estivate.subQueryEntity(this, alias);
	}
	
	/*
	 * Imports an object with fields annotated with ReturnBuilder annotation to build select
	 */
	public SelectQuery<T> importSelectFromResultMapping(Class<?> objectClass){
		
		if (objectClass == null) {
			return self();
		}
		
		
		Field[] fields = objectClass.getDeclaredFields();
		
		for (Field field : fields) {
			field.setAccessible(true);
			
			if(field.getDeclaredAnnotation(ResultMapping.Attribute.class) != null) {
				ResultMapping.Attribute attribute = field.getDeclaredAnnotation(ResultMapping.Attribute.class);
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute());
			}
			else if(field.getDeclaredAnnotation(ResultMapping.Count.class) != null) {
				ResultMapping.Count attribute = field.getDeclaredAnnotation(ResultMapping.Count.class);
				selectCountAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(ResultMapping.Sum.class) != null) {
				ResultMapping.Sum attribute = field.getDeclaredAnnotation(ResultMapping.Sum.class);
				selectSumAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(ResultMapping.Min.class) != null) {
				ResultMapping.Min attribute = field.getDeclaredAnnotation(ResultMapping.Min.class);
				selectMinAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(ResultMapping.Max.class) != null) {
				ResultMapping.Max attribute = field.getDeclaredAnnotation(ResultMapping.Max.class);
				selectMaxAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(ResultMapping.Avg.class) != null) {
				ResultMapping.Avg attribute = field.getDeclaredAnnotation(ResultMapping.Avg.class);
				selectAvgAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}

			else if(field.getDeclaredAnnotation(ResultMapping.Function.class) != null) {
				ResultMapping.Function attribute = field.getDeclaredAnnotation(ResultMapping.Function.class);
				selectFunctionAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.function( attribute.functionPrefix(), attribute.functionSuffix()), attribute.alias());
			}
			else{
				log.warn("Field "+field.getName()+" has no mapping annotation");
			}
			
		}
		
		return self();
	
	}


	




}
