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
import com.estivate.result.ResultRow;
import com.estivate.util.FieldUtils;


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
	Set<Attribute> selects = new LinkedHashSet<>();
	
	
	
	@Getter
	List<Attribute> groupBys = new ArrayList<>();

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
	


	public SelectQuery<T> select(Attribute attribute) {	selects.add(attribute); return this; }
	
	/* Wrappers */

	public SelectQuery<T> select(String attribute) { return select(Estivate.attribute(this.entity, attribute)); }
	public SelectQuery<T> select(String attribute, String alias) { return select(Estivate.attribute(this.entity, attribute, null, alias)); }
	public SelectQuery<T> select(String attribute, Attribute.Function function) { return select(Estivate.attribute(this.entity, attribute, function)); }
	public SelectQuery<T> select(String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(this.entity, attribute, function, alias)); }

	public SelectQuery<T> select(Class<?> c, String attribute) { return select(Estivate.attribute(new Entity<>(c), attribute)); }
	public SelectQuery<T> select(Entity<?> c, String attribute) { return select(Estivate.attribute(c, attribute)); }
	public <E, P> SelectQuery<T> select(com.estivate.util.FieldUtils.Getter<E, P> getter) { return select(Estivate.attribute(getter)); }

	public SelectQuery<T> select(Class<?> c, String attribute, String alias) { return select(Estivate.attribute(new Entity<>(c), attribute, null, alias)); }
	public SelectQuery<T> select(Entity<?> c, String attribute, String alias) { return select(Estivate.attribute(c, attribute, null, alias)); }
	public <E, P> SelectQuery<T> select(com.estivate.util.FieldUtils.Getter<E, P> getter, String alias) { return select(Estivate.attribute(getter, alias)); }
	
	
	public SelectQuery<T> select(Class<?> c, String attribute, Attribute.Function function) { return select(Estivate.attribute(new Entity<>(c), attribute, function)); }
	public SelectQuery<T> select(Entity<?> c, String attribute, Attribute.Function function) { return select(Estivate.attribute(c, attribute, function)); }
	public <E, P> SelectQuery<T> select(com.estivate.util.FieldUtils.Getter<E, P> getter, Attribute.Function function) { return select(Estivate.attribute(getter, function)); }

	public SelectQuery<T> select(Class<?> c, String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(new Entity<>(c), attribute, function, alias)); }
	public SelectQuery<T> select(Entity<?> c, String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(c, attribute, function, alias)); }
	public <E, P> SelectQuery<T> select(com.estivate.util.FieldUtils.Getter<E, P> getter, Attribute.Function function, String alias) { return select(Estivate.attribute(getter, function, alias)); }
	
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
				selectCount(Estivate.attribute(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), null, attribute.alias()));
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
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.function( attribute.functionPrefix(), attribute.functionSuffix()), attribute.alias());
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


	public SelectQuery<T> selectCount(Attribute attribute) { return select(attribute.entity, attribute.attribute, Estivate.Functions.count, attribute.alias); }
	
	public SelectQuery<T> selectCount(Entity<?> entity, String attribute) { return select(entity, attribute, Estivate.Functions.count); }
	public SelectQuery<T> selectCount(Class<?> c, String attribute) { return select(new Entity<>(c), attribute, Estivate.Functions.count); }
	public SelectQuery<T> selectCount(String attribute) { return select(this.entity, attribute, Estivate.Functions.count); }
	
	// Select count
	public SelectQuery<T> selectCountAs(String alias) { return select(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count with alias
	public SelectQuery<T> selectCountAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.count, alias); }
	public SelectQuery<T> selectCountAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.count, alias); }
	
	
	// Select count distinct field
	public SelectQuery<T> selectCountDistinctAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<T> selectCountDistinctAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	
	// Select min
	public SelectQuery<T> selectMinAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.min, alias); }
	public SelectQuery<T> selectMinAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.min, alias); }
	
	// Select max
	public SelectQuery<T> selectMaxAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.max, alias); }
	public SelectQuery<T> selectMaxAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.max, alias); }

	// Select Sum
	public SelectQuery<T> selectSumAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<T> selectSumAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.sum, alias); }
	
	// Select Avg
	public SelectQuery<T> selectAvgAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<T> selectAvgAs(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<T> selectAvgAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.avg, alias); }

	// Select Group Concat
	public SelectQuery<T> selectGroupConcatAs(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(Entity<?> c, String attribute, String alias)	{ return select(c, attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<T> selectGroupConcatAs(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.groupConcat, alias); }

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

	public SelectQuery<T> groupBy(Entity<?> entity, String field) { groupBys.add(Estivate.attribute(entity, field)); return this; }
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
	public SelectQuery<T> groupByAlias(String alias) { groupBys.add(Estivate.attributeOfAlias(alias, null)); return this; }
	public SelectQuery<T> clearGroupBys(){ groupBys.clear(); return this; }
	
	public SelectQuery<T> setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}


	// ==================== FETCH METHODS ====================
	public T 		fetchSingle(Context context) 					{ return context.fetchSingle(this); }
	public ResultRow 	fetchSingleAsResult(Context context)			{ return context.fetchSingleAsResult(this); }
	public <U> U 	fetchSingleAs(Context context, Class<U> clazz) 	{ return context.fetchSingleAs(this, clazz); }
	public String 	fetchSingleAsString(Context context) 			{ return context.fetchSingleAsString(this); }
	public Short 	fetchSingleAsShort(Context context) 			{ return context.fetchSingleAsShort(this); }
	public Integer 	fetchSingleAsInteger(Context context) 			{ return context.fetchSingleAsInteger(this); }
	public Long 	fetchSingleAsLong(Context context) 				{ return context.fetchSingleAsLong(this); }
	public Float 	fetchSingleAsFloat(Context context) 			{ return context.fetchSingleAsFloat(this); }
	public Double 	fetchSingleAsDouble(Context context) 			{ return context.fetchSingleAsDouble(this); }
	public Date 	fetchSingleAsDate(Context context) 				{ return context.fetchSingleAsDate(this); }
	public Boolean 	fetchSingleAsBoolean(Context context) 			{ return context.fetchSingleAsBoolean(this); }
	
	public Optional<T> 			fetchOptional(Context context) 			{ return context.fetchOptional(this); }
	public Optional<ResultRow> 	fetchOptionalAsResult(Context context) 	{ return context.fetchOptionalAsResult(this); }
	public Optional<T> 			fetchOptionalAs(Context context, Class<T> clazz) 			{ return context.fetchOptionalAs(this, clazz); }
	public Optional<String>		fetchOptionalAsString(Context context) 	{ return context.fetchOptionalAsString(this); }
	public Optional<Short>		fetchOptionalAsShort(Context context) 	{ return context.fetchOptionalAsShort(this); }
	public Optional<Integer>	fetchOptionalAsInteger(Context context) 	{ return context.fetchOptionalAsInteger(this); }
	public Optional<Long>		fetchOptionalAsLong(Context context) 		{ return context.fetchOptionalAsLong(this); }
	public Optional<Float>		fetchOptionalAsFloat(Context context) 	{ return context.fetchOptionalAsFloat(this); }
	public Optional<Double>		fetchOptionalAsDouble(Context context) 	{ return context.fetchOptionalAsDouble(this); }
	public Optional<Date>		fetchOptionalAsDate(Context context) 		{ return context.fetchOptionalAsDate(this); }
	public Optional<Boolean>	fetchOptionalAsBoolean(Context context) 	{ return context.fetchOptionalAsBoolean(this); }
	public <U extends Enum<U>> Optional<U> 		fetchOptionalAsStringEnum(Context context, Class<U> enumClass) { return context.fetchOptionalAsStringEnum(this, enumClass); }
	public <U extends Enum<U>> Optional<U> 		fetchOptionalAsOrdinalEnum(Context context, Class<U> enumClass) { return context.fetchOptionalAsOrdinalEnum(this, enumClass); }
	
	public List<T> 			fetchList(Context context){ return context.fetchListAs(this, (Class<T>) entity.entity); }
	public <U> List<U> 		fetchListAs(Context context, Class<U> clazz) { return context.fetchListAs(this, clazz); }
	public List<ResultRow<T>> 	fetchListAsResults(Context context) { return context.fetchListAsResults(this); }
	public List<String> 	fetchListAsString(Context context) { return context.fetchListAsString(this); }
	public List<Short> 		fetchListAsShort(Context context) { return context.fetchListAsShort(this); }
	public List<Integer> 	fetchListAsInteger(Context context) { return context.fetchListAsInteger(this); }
	public List<Long> 		fetchListAsLong(Context context) { return context.fetchListAsLong(this); }
	public List<Float> 		fetchListAsFloat(Context context) { return context.fetchListAsFloat(this); }
	public List<Double> 	fetchListAsDouble(Context context) { return context.fetchListAsDouble(this); }
	public List<Date> 		fetchListAsDate(Context context) { return context.fetchListAsDate(this); }
	public List<Boolean> 	fetchListAsBoolean(Context context) { return context.fetchListAsBoolean(this); }
	public <U extends Enum<U>> List<U> 		fetchListAsStringEnum(Context context, Class<U> enumClass) { return context.fetchListAsStringEnum(this, enumClass); }
	public <U extends Enum<U>> List<U> 		fetchListAsOrdinalEnum(Context context, Class<U> enumClass) { return context.fetchListAsOrdinalEnum(this, enumClass); }
	
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
	
	public <U, V> Map<U, V> aggregateToMap(Context context, java.util.function.Function<ResultRow<T>,U> uType, java.util.function.Function<ResultRow<T>,V> vType){
		return context.aggregateToMap(this, uType, vType);
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(Context context, java.util.function.Function<ResultRow<T>,U> uType, java.util.function.Function<ResultRow<T>,V> vType){
		return context.aggregateToMapList(this, uType, vType);
	}


	// ==================== MISC METHODS ====================
	public SubQueryEntity<T> asSubQueryEntity(String alias){
		return Estivate.subQueryEntity(this, alias);
	}

	public SelectQuery<T> pruneUnknownColumns(){
		
		Set<Entity<?>> entities = new LinkedHashSet<>();
		entities.add(this.entity);

		for(Join join : joins){
			entities.add(join.leftEntity);
			entities.add(join.rightEntity);
		}
		
		for(Attribute select : new ArrayList<>(selects)){
			if(select.getEntity() != null && !entities.contains(select.getEntity())){
				selects.remove(select);
			}
		}

		return this;
	}
}
