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

import org.apache.commons.lang3.ArrayUtils;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.result.Result;
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
//		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
//		if(select != null) {
//			selects.remove(select);
//		}
		
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
	
	public SelectQuery<T> selectAll(Class<?> entity, String...fieldNames) { 
		
		Set<Field> fields = FieldUtils.getEntityFields(entity);

		for(Field field : fields){

			if(fieldNames.length != 0 && ArrayUtils.indexOf(fieldNames, field.getName()) == -1){
				continue;
			}

			field.setAccessible(true);
			
			if(field.getDeclaredAnnotation(Projection.Attribute.class) != null) {
				Projection.Attribute attribute = field.getDeclaredAnnotation(Projection.Attribute.class);
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute());
			}
			else if(field.getDeclaredAnnotation(Projection.Count.class) != null) {
				Projection.Count attribute = field.getDeclaredAnnotation(Projection.Count.class);
				selectCountAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Sum.class) != null) {
				Projection.Sum attribute = field.getDeclaredAnnotation(Projection.Sum.class);
				selectSumAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Min.class) != null) {
				Projection.Min attribute = field.getDeclaredAnnotation(Projection.Min.class);
				selectMinAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Max.class) != null) {
				Projection.Max attribute = field.getDeclaredAnnotation(Projection.Max.class);
				selectMaxAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Avg.class) != null) {
				Projection.Avg attribute = field.getDeclaredAnnotation(Projection.Avg.class);
				selectAvgAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Function.class) != null) {
				Projection.Function attribute = field.getDeclaredAnnotation(Projection.Function.class);
				selectFunctionAs(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.function( attribute.functionPrefix(), attribute.functionSuffix()), attribute.alias());
			}
			else{
				select(entity, field.getName()); 
			}
		
		}

		return this;
		
	}	
	public SelectQuery<T> selectAll(Entity<?> c, String... fieldNames) {
		
		Set<Field> fields = FieldUtils.getEntityFields(c.entity);
		
		for(Field field : fields){
			if(fieldNames.length != 0 && ArrayUtils.indexOf(fieldNames, field.getName()) == -1){
				continue;
			}
			
			select(c, field.getName());
			
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
	public SelectQuery<T> selectGroupConcatAs(Class<?> c, String attribute, String alias) 	{ return selectFunctionAs(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(Entity<?> c, String attribute, String alias)	{ return selectFunctionAs(c, attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(String attribute, String alias) 				{ return selectFunctionAs(this.entity, attribute, Estivate.Functions.groupConcat, alias); }

	public SelectQuery<T> clearSelects(){ selects.clear(); return this; }
	public SelectQuery<T> clearOrderBys(){ super.clearOrderBys(); return this; }
	
	
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
	public SelectQuery<T> groupBy(Class<?> c, String field) {
		Field groupByfield = FieldUtils.findField(c, field);
		if(groupByfield == null) {
			throw new IllegalArgumentException("Field " + field + " not found in entity " + c);
		}
		Projection.Attribute attribute = groupByfield.getDeclaredAnnotation(Projection.Attribute.class);
		if(attribute != null) {		
			return groupBy(new Entity<>(attribute.entity()), attribute.attribute());
		}		
		return groupBy(new Entity<>(c), field); 
	}
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
	public <U> U projectTo(Context context, Class<U> clazz) { return context.projectTo(this, clazz); }
	public <U> Optional<U> projectToOptional(Context context, Class<U> clazz) { return context.projectToOptional(this, clazz); }
	public <U> List<U> projectToList(Context context, Class<U> clazz) { return context.projectToList(this, clazz); }
	
	public Object projectToAttribute(Context context, Class<?> entity, String attributeName) { return context.projectToAttribute(this, entity, attributeName); }
	public Optional<Object> projectToAttributeOptional(Context context, Class<?> entity, String attributeName) { return context.projectToAttributeOptional(this, entity, attributeName); }
	public List<?> projectToAttributeList(Context context, Class<?> entity, String attributeName) { return context.projectToAttributeList(this, entity, attributeName); }
	public Set<?> projectToAttributeSet(Context context, Class<?> entity, String attributeName) { return context.projectToAttributeSet(this, entity, attributeName); }

	public Long projectToCount(Context context) { return context.projectToCount(this); }
	public Optional<Long> projectToCountOptional(Context context) { return context.projectToCountOptional(this); }

	public Long projectToCountDistinct(Context context, Class<?> entity, String attributeName) { return context.projectToCountDistinct(this, entity, attributeName); }
	public Optional<Long> projectToCountDistinctOptional(Context context, Class<?> entity, String attributeName) { return context.projectToCountDistinctOptional(this, entity, attributeName); }
	
	


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
	
	


	




}
