package com.estivate.query;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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
import com.estivate.result.ResultTable;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class SelectQuery<E> extends Query<SelectQuery<E>, E> {
	

	public SelectQuery<E> nativeCriterion  	(Entity<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return this; }


	
	
	
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
	
	public SelectQuery(Class<E> baseClass) {
		super(baseClass);
	}
	
	public SelectQuery(Entity<E> entity) {
		super(entity);
	}

	
	public SelectQuery<E> comment(String comment) {
		super.comment(comment);
		return this;
	}
	
	public SelectQuery<E> distinct(){
		return distinct(true);
	}
	
	public SelectQuery<E> distinct(boolean distinct){
		this.distinct = distinct;
		return this;
	}
	
	public SelectQuery<E> join(Join join) { 
		super.join(join);
		return this;
	}
	


	public SelectQuery<E> select(Attribute attribute) {	selects.add(attribute); return this; }
	
	/* Wrappers */

	public SelectQuery<E> select(String attribute) { return select(Estivate.attribute(this.entity, attribute)); }
	public SelectQuery<E> select(String attribute, String alias) { return select(Estivate.attribute(this.entity, attribute, null, alias)); }
	public SelectQuery<E> select(String attribute, Attribute.Function function) { return select(Estivate.attribute(this.entity, attribute, function)); }
	public SelectQuery<E> select(String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(this.entity, attribute, function, alias)); }

	public SelectQuery<E> select(Class<?> c, String attribute) { return select(Estivate.attribute(new Entity<>(c), attribute)); }
	public SelectQuery<E> select(Entity<?> c, String attribute) { return select(Estivate.attribute(c, attribute)); }
	public <T, P> SelectQuery<E> select(com.estivate.util.FieldUtils.AttributeGetter<T, P> getter) { return select(Estivate.attribute(getter)); }

	public SelectQuery<E> select(Class<?> c, String attribute, String alias) { return select(Estivate.attribute(new Entity<>(c), attribute, null, alias)); }
	public SelectQuery<E> select(Entity<?> c, String attribute, String alias) { return select(Estivate.attribute(c, attribute, null, alias)); }
	public <T, P> SelectQuery<E> select(com.estivate.util.FieldUtils.AttributeGetter<T, P> getter, String alias) { return select(Estivate.attribute(getter, alias)); }
	
	
	public SelectQuery<E> select(Class<?> c, String attribute, Attribute.Function function) { return select(Estivate.attribute(new Entity<>(c), attribute, function)); }
	public SelectQuery<E> select(Entity<?> c, String attribute, Attribute.Function function) { return select(Estivate.attribute(c, attribute, function)); }
	public <T, P> SelectQuery<E> select(com.estivate.util.FieldUtils.AttributeGetter<T, P> getter, Attribute.Function function) { return select(Estivate.attribute(getter, function)); }

	public SelectQuery<E> select(Class<?> c, String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(new Entity<>(c), attribute, function, alias)); }
	public SelectQuery<E> select(Entity<?> c, String attribute, Attribute.Function function, String alias) { return select(Estivate.attribute(c, attribute, function, alias)); }
	public <T, P> SelectQuery<E> select(com.estivate.util.FieldUtils.AttributeGetter<T, P> getter, Attribute.Function function, String alias) { return select(Estivate.attribute(getter, function, alias)); }
	
	public SelectQuery<E> select(Attribute.AttributeWindow attributeWindow) { select(attributeWindow.entity, attributeWindow.attribute, attributeWindow.function, attributeWindow.alias); return this; }

	public SelectQuery<E> selectAll(Class<?> entity, String...fieldNames) { 
		
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
			else if(field.getDeclaredAnnotation(Projection.CountDistinct.class) != null) {
				Projection.CountDistinct attribute = field.getDeclaredAnnotation(Projection.CountDistinct.class);
				selectCountDistinct(Estivate.attribute(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), null, attribute.alias()));
			}
			else if(field.getDeclaredAnnotation(Projection.Sum.class) != null) {
				Projection.Sum attribute = field.getDeclaredAnnotation(Projection.Sum.class);
				selectSum(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Min.class) != null) {
				Projection.Min attribute = field.getDeclaredAnnotation(Projection.Min.class);
				selectMin(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Max.class) != null) {
				Projection.Max attribute = field.getDeclaredAnnotation(Projection.Max.class);
				selectMax(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Avg.class) != null) {
				Projection.Avg attribute = field.getDeclaredAnnotation(Projection.Avg.class);
				selectAvg(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), attribute.alias());
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
	public SelectQuery<E> selectAll(Entity<?> c, String... fieldNames) {
		
		Set<Field> fields = FieldUtils.getEntityFields(c.entity);
		
		for(Field field : fields){
			if(fieldNames.length != 0 && ArrayUtils.indexOf(fieldNames, field.getName()) == -1){
				continue;
			}
			
			select(c, field.getName());
			
		}
		
		return this;
		
	}

	// Select count all with alias
	public SelectQuery<E> selectCountAll() 				{ return select(new Entity<>(null), null, Estivate.Functions.count); }
	public SelectQuery<E> selectCountAll(String alias) 	{ return select(new Entity<>(null), null, Estivate.Functions.count, alias); }

	// Select count
	public SelectQuery<E> selectCount(Attribute attribute) { return select(attribute.entity, attribute.attribute, Estivate.Functions.count, attribute.alias); }
	public SelectQuery<E> selectCount(Entity<?> entity, String attribute) { return select(entity, attribute, Estivate.Functions.count); }
	public SelectQuery<E> selectCount(Class<?> c, String attribute) { return select(new Entity<>(c), attribute, Estivate.Functions.count); }
	public SelectQuery<E> selectCount(String attribute) { return select(this.entity, attribute, Estivate.Functions.count); }
	public <T, P> SelectQuery<E> selectCount(AttributeGetter<T, P> getter) { return select(getter, Estivate.Functions.count); }

	// Select count with alias@
	public SelectQuery<E> selectCount(Attribute attribute, String alias) 				{ return select(attribute.entity, attribute.attribute, Estivate.Functions.count, alias); }
	public SelectQuery<E> selectCount(Entity<?> c, String attribute, String alias) 	{ return select(c, attribute, Estivate.Functions.count, alias); }
	public SelectQuery<E> selectCount(Class<?> c, String attribute, String alias) 	{ return select(new Entity<>(c), attribute, Estivate.Functions.count, alias); }
	public SelectQuery<E> selectCount(String attribute, String alias) 				{ return select(this.entity, attribute, Estivate.Functions.count, alias); }
	public <T, P> SelectQuery<E> selectCount(AttributeGetter<T, P> getter, String alias) 	{ return select(getter, Estivate.Functions.count, alias); }

	// Select count distinct field
	public SelectQuery<E> selectCountDistinct(Attribute attribute) 								{ return select(attribute.entity, attribute.attribute, Estivate.Functions.countDistinct, attribute.alias); }
	public SelectQuery<E> selectCountDistinct(Entity<?> entity, String attribute) 				{ return select(entity, attribute, Estivate.Functions.countDistinct); }
	public SelectQuery<E> selectCountDistinct(Class<?> c, String attribute) 					{ return select(new Entity<>(c), attribute, Estivate.Functions.countDistinct); }
	public SelectQuery<E> selectCountDistinct(String attribute) 								{ return select(this.entity, attribute, Estivate.Functions.countDistinct); }
	public <T, P> SelectQuery<E> selectCountDistinct(AttributeGetter<T, P> getter) 				{ return select(getter, Estivate.Functions.countDistinct); }

	// Select count distinct with alias
	public SelectQuery<E> selectCountDistinct(Attribute attribute, String alias) 				{ return select(attribute.entity, attribute.attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<E> selectCountDistinct(Entity<?> c, String attribute, String alias) 		{ return select(c, attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<E> selectCountDistinct(Class<?> c, String attribute, String alias) 		{ return select(new Entity<>(c), attribute, Estivate.Functions.countDistinct, alias); }
	public SelectQuery<E> selectCountDistinct(String attribute, String alias) 					{ return select(this.entity, attribute, Estivate.Functions.countDistinct, alias); }
	public <T, P> SelectQuery<E> selectCountDistinct(AttributeGetter<T, P> getter, String alias){ return select(getter, Estivate.Functions.countDistinct, alias); }
	

	// Select min
	public SelectQuery<E> selectMin(Attribute attribute) 										{ return select(attribute.entity, attribute.attribute, Estivate.Functions.min, attribute.alias); }
	public SelectQuery<E> selectMin(Entity<?> entity, String attribute) 						{ return select(entity, attribute, Estivate.Functions.min); }
	public SelectQuery<E> selectMin(Class<?> c, String attribute) 								{ return select(new Entity<>(c), attribute, Estivate.Functions.min); }
	public SelectQuery<E> selectMin(String attribute) 											{ return select(this.entity, attribute, Estivate.Functions.min); }
	public <T, P> SelectQuery<E> selectMin(AttributeGetter<T, P> getter) 						{ return select(getter, Estivate.Functions.min); }


	// Select min with alias
	public SelectQuery<E> selectMin(Attribute attribute, String alias) 							{ return select(attribute.entity, attribute.attribute, Estivate.Functions.min, alias); }
	public SelectQuery<E> selectMin(Entity<?> c, String attribute, String alias) 				{ return select(c, attribute, Estivate.Functions.min, alias); }
	public SelectQuery<E> selectMin(Class<?> c, String attribute, String alias) 				{ return select(new Entity<>(c), attribute, Estivate.Functions.min, alias); }
	public SelectQuery<E> selectMin(String attribute, String alias) 							{ return select(this.entity, attribute, Estivate.Functions.min, alias); }
	public <T, P> SelectQuery<E> selectMin(AttributeGetter<T, P> getter, String alias) 			{ return select(getter, Estivate.Functions.min, alias); }

	// Select max
	public SelectQuery<E> selectMax(Attribute attribute) 										{ return select(attribute.entity, attribute.attribute, Estivate.Functions.max, attribute.alias); }
	public SelectQuery<E> selectMax(Entity<?> entity, String attribute) 						{ return select(entity, attribute, Estivate.Functions.max); }
	public SelectQuery<E> selectMax(Class<?> c, String attribute) 								{ return select(new Entity<>(c), attribute, Estivate.Functions.max); }
	public SelectQuery<E> selectMax(String attribute) 											{ return select(this.entity, attribute, Estivate.Functions.max); }
	public <T, P> SelectQuery<E> selectMax(AttributeGetter<T, P> getter) 						{ return select(getter, Estivate.Functions.max); }
	
	// Select max with alias
	public SelectQuery<E> selectMax(Attribute attribute, String alias) 							{ return select(attribute.entity, attribute.attribute, Estivate.Functions.max, alias); }
	public SelectQuery<E> selectMax(Entity<?> c, String attribute, String alias) 				{ return select(c, attribute, Estivate.Functions.max, alias); }
	public SelectQuery<E> selectMax(Class<?> c, String attribute, String alias) 				{ return select(new Entity<>(c), attribute, Estivate.Functions.max, alias); }
	public SelectQuery<E> selectMax(String attribute, String alias) 							{ return select(this.entity, attribute, Estivate.Functions.max, alias); }
	public <T, P> SelectQuery<E> selectMax(AttributeGetter<T, P> getter, String alias) 			{ return select(getter, Estivate.Functions.max, alias); }

	// Select sum
	public SelectQuery<E> selectSum(Attribute attribute) 										{ return select(attribute.entity, attribute.attribute, Estivate.Functions.sum, attribute.alias); }
	public SelectQuery<E> selectSum(Entity<?> entity, String attribute) 						{ return select(entity, attribute, Estivate.Functions.sum); }
	public SelectQuery<E> selectSum(Class<?> c, String attribute) 								{ return select(new Entity<>(c), attribute, Estivate.Functions.sum); }
	public SelectQuery<E> selectSum(String attribute) 											{ return select(this.entity, attribute, Estivate.Functions.sum); }
	public <T, P> SelectQuery<E> selectSum(AttributeGetter<T, P> getter) 						{ return select(getter, Estivate.Functions.sum); }

	// Select sum with alias
	public SelectQuery<E> selectSum(Attribute attribute, String alias) 							{ return select(attribute.entity, attribute.attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<E> selectSum(Entity<?> c, String attribute, String alias) 				{ return select(c, attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<E> selectSum(Class<?> c, String attribute, String alias) 				{ return select(new Entity<>(c), attribute, Estivate.Functions.sum, alias); }
	public SelectQuery<E> selectSum(String attribute, String alias) 							{ return select(this.entity, attribute, Estivate.Functions.sum, alias); }
	public <T, P> SelectQuery<E> selectSum(AttributeGetter<T, P> getter, String alias) 			{ return select(getter, Estivate.Functions.sum, alias); }
	
	// Select avg
	public SelectQuery<E> selectAvg(Attribute attribute) 										{ return select(attribute.entity, attribute.attribute, Estivate.Functions.avg, attribute.alias); }
	public SelectQuery<E> selectAvg(Entity<?> entity, String attribute) 						{ return select(entity, attribute, Estivate.Functions.avg); }
	public SelectQuery<E> selectAvg(Class<?> c, String attribute) 								{ return select(new Entity<>(c), attribute, Estivate.Functions.avg); }
	public SelectQuery<E> selectAvg(String attribute) 											{ return select(this.entity, attribute, Estivate.Functions.avg); }
	public <T, P> SelectQuery<E> selectAvg(AttributeGetter<T, P> getter) 						{ return select(getter, Estivate.Functions.avg); }

	// Select Avg with alias
	public SelectQuery<E> selectAvg(Class<?> c, String attribute, String alias) 				{ return select(new Entity<>(c), attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<E> selectAvg(Entity<?> c, String attribute, String alias) 				{ return select(c, attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<E> selectAvg(String attribute, String alias) 							{ return select(this.entity, attribute, Estivate.Functions.avg, alias); }
	public SelectQuery<E> selectAvg(Attribute attribute, String alias) 							{ return select(attribute.entity, attribute.attribute, Estivate.Functions.avg, alias); }
	public <T, P> SelectQuery<E> selectAvg(AttributeGetter<T, P> getter, String alias) 			{ return select(getter, Estivate.Functions.avg, alias); }

	// Select group concat
	public SelectQuery<E> selectGroupConcat(Attribute attribute) 								{ return select(attribute.entity, attribute.attribute, Estivate.Functions.groupConcat, attribute.alias); }
	public SelectQuery<E> selectGroupConcat(Entity<?> entity, String attribute) 				{ return select(entity, attribute, Estivate.Functions.groupConcat); }
	public SelectQuery<E> selectGroupConcat(Class<?> c, String attribute) 						{ return select(new Entity<>(c), attribute, Estivate.Functions.groupConcat); }
	public SelectQuery<E> selectGroupConcat(String attribute) 									{ return select(this.entity, attribute, Estivate.Functions.groupConcat); }
	public <T, P> SelectQuery<E> selectGroupConcat(AttributeGetter<T, P> getter) 				{ return select(getter, Estivate.Functions.groupConcat); }

	// Select Group Concat with alias
	public SelectQuery<E> selectGroupConcat(Attribute attribute, String alias) 					{ return select(attribute.entity, attribute.attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<E> selectGroupConcat(Entity<?> c, String attribute, String alias)		{ return select(c, attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<E> selectGroupConcat(Class<?> c, String attribute, String alias) 		{ return select(new Entity<>(c), attribute, Estivate.Functions.groupConcat, alias); }
	public SelectQuery<E> selectGroupConcat(String attribute, String alias) 					{ return select(this.entity, attribute, Estivate.Functions.groupConcat, alias); }
	public <T, P> SelectQuery<E> selectGroupConcat(AttributeGetter<T, P> getter, String alias) 	{ return select(getter, Estivate.Functions.groupConcat, alias); }

	public SelectQuery<E> clearSelects(){ selects.clear(); return this; }
	public SelectQuery<E> clearOrderBys(){ super.clearOrderBys(); return this; }
	
	
	// Having
	public SelectQuery<E> having(EstivateNode node) { this.having = node; return this; }
	
	@SuppressWarnings("unchecked")
	public SelectQuery<E> clone() {
		SelectQuery<E> queryClone = new SelectQuery<E>(entity);
		
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

	public SelectQuery<E> groupBy(Entity<?> entity, String field) { groupBys.add(Estivate.attribute(entity, field)); return this; }
	public SelectQuery<E> groupBy(Class<?> c, String field) {
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
	public SelectQuery<E> groupBy(String attribute) { return groupBy(this.entity, attribute); }
	public SelectQuery<E> groupByAlias(String alias) { groupBys.add(Estivate.attributeOfAlias(alias, null)); return this; }
	public SelectQuery<E> clearGroupBys(){ groupBys.clear(); return this; }
	
	public SelectQuery<E> setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}


	// ==================== FETCH METHODS ====================

	public ResultTable fetch(Context context) { return context.fetch(this); }

	// Base entity shortcuts (uses the query's type parameter E)
	@SuppressWarnings("unchecked")
	public 		E 	fetchSingle(Context context) { return context.fetchSingle(this); }
	public <T> 	T 	fetchAsSingle(Context context, Class<T> clazz) { return context.fetchAsSingle(this, clazz); }
	public <T> 	T 	fetchAsSingle(Context context, Entity<T> entity) { return context.fetchAsSingle(this, entity); }
	public 	Object 	fetchAsSingle(Context context, Attribute attribute) { return context.fetchAsSingle(this, attribute); }
	public 	Object 	fetchAsSingle(Context context, Class<?> entity, String attributeName) { return context.fetchAsSingle(this, entity, attributeName); }
	public 	Object 	fetchAsSingle(Context context, Entity<?> entity, String attributeName) { return context.fetchAsSingle(this, entity.entity, attributeName); }
	public <T, P> P fetchAsSingle(Context context, AttributeGetter<T, P> getter) { return (P) fetchAsSingle(context, Estivate.attribute(getter));}
	public Object 	fetchAsSingle(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsSingle(this, entity, attributeName, function); }
	public Object 	fetchAsSingle(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsSingle(this, entity.entity, attributeName, function); }
	public <T, P> P fetchAsSingle(Context context, AttributeGetter<T, P> getter, Attribute.Function function) { return (P) fetchAsSingle(context, Estivate.attribute(getter, function));}
	
	
	@SuppressWarnings("unchecked")
	public 			Optional<E> fetchOptional(Context context) { return context.fetchAsOptional(this, (Class<E>) entity.entity); }
	public <T> 		Optional<T> fetchAsOptional(Context context, Class<T> clazz) { return context.fetchAsOptional(this, clazz); }
	public <T> 		Optional<T> fetchAsOptional(Context context, Entity<T> entity) { return context.fetchAsOptional(this, entity); }
	public 			Optional<?> fetchAsOptional(Context context, Attribute attribute) { return context.fetchAsOptional(this, attribute); }
	public 			Optional<?> fetchAsOptional(Context context, Entity<?> entity, String attributeName) { return context.fetchAsOptional(this, entity.entity, attributeName); }
	public 			Optional<?> fetchAsOptional(Context context, Class<?> entity, String attributeName) { return context.fetchAsOptional(this, entity, attributeName); }
	public <T, P> 	Optional<P> fetchAsOptional(Context context, AttributeGetter<T, P> attributeGetter) { return (Optional<P>) context.fetchAsOptional(this, Estivate.attribute(attributeGetter)); }
	public 			Optional<?> fetchAsOptional(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsOptional(this, entity.entity, attributeName, function); }
	public 			Optional<?> fetchAsOptional(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsOptional(this, entity, attributeName, function); }
	public <T, P> 	Optional<P> fetchAsOptional(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (Optional<P>) context.fetchAsOptional(this, Estivate.attribute(attributeGetter, function)); }
	
	
	public 			List<E> fetchList(Context context){ return context.fetchAsList(this, entity); }
	public <T> 		List<T> fetchAsList(Context context, Class<T> entity) { return context.fetchAsList(this, entity); }
	public <T> 		List<T> fetchAsList(Context context, Entity<T> entity) { return context.fetchAsList(this, entity); }
	public 			List<?> fetchAsList(Context context, Attribute attribute) { return context.fetchAsList(this, attribute); }
	public 			List<?> fetchAsList(Context context, Class<?> entity, String attributeName) { return context.fetchAsList(this, entity, attributeName); }
	public 			List<?> fetchAsList(Context context, Entity<?> entity, String attributeName) { return context.fetchAsList(this, entity, attributeName); }
	public <T, P> 	List<P> fetchAsList(Context context, AttributeGetter<T, P> attributeGetter) { return context.fetchAsList(this, attributeGetter); }
	public 			List<?> fetchAsList(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsList(this, entity, attributeName, function); }
	public 			List<?> fetchAsList(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsList(this, entity, attributeName, function); }
	public <T, P> 	List<P> fetchAsList(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.fetchAsList(this, attributeGetter, function); }

	public 			List<?> fetchAsListDistinct(Context context, Attribute attribute) { return context.fetchAsListDistinct(this, attribute); }
	public 			List<?> fetchAsListDistinct(Context context, Class<?> entity, String attributeName) { return context.fetchAsListDistinct(this, entity, attributeName); }
	public 			List<?> fetchAsListDistinct(Context context, Entity<?> entity, String attributeName) { return context.fetchAsListDistinct(this, entity, attributeName); }
	public <T, P> 	List<P> fetchAsListDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return context.fetchAsListDistinct(this, attributeGetter); }
	public 			List<?> fetchAsListDistinct(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsListDistinct(this, entity, attributeName, function); }
	public 			List<?> fetchAsListDistinct(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsListDistinct(this, entity, attributeName, function); }
	public <T, P> 	List<P> fetchAsListDistinct(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.fetchAsListDistinct(this, attributeGetter, function); }

	public 			Set<?> fetchAsSet(Context context, Attribute attribute) { return context.fetchAsSet(this, attribute); }
	public 			Set<?> fetchAsSet(Context context, Class<?> entity, String attributeName) { return context.fetchAsSet(this, entity, attributeName); }
	public 			Set<?> fetchAsSet(Context context, Entity<?> entity, String attributeName) { return context.fetchAsSet(this, entity, attributeName); }
	public <T, P> 	Set<P> fetchAsSet(Context context, AttributeGetter<T, P> attributeGetter) { return context.fetchAsSet(this, attributeGetter); }
	public 			Set<?> fetchAsSet(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsSet(this, entity, attributeName, function); }
	public 			Set<?> fetchAsSet(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.fetchAsSet(this, entity, attributeName, function); }
	public <T, P> 	Set<P> fetchAsSet(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.fetchAsSet(this, attributeGetter, function); }

	public Long fetchCountAll(Context context) { return context.fetchCountAll(this); }
	public Optional<Long> fetchOptionalCountAll(Context context) { return context.fetchOptionalCountAll(this); }

	
	

	// ==================== AGGREGATION METHODS ====================
	
	public <U, V> Map<U, V> aggregateToMap(Context context, java.util.function.Function<ResultRow,U> uType, java.util.function.Function<ResultRow,V> vType){
		return context.aggregateToMap(this, uType, vType);
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(Context context, java.util.function.Function<ResultRow,U> uType, java.util.function.Function<ResultRow,V> vType){
		return context.aggregateToMapList(this, uType, vType);
	}


	// ==================== MISC METHODS ====================
	public SubQueryEntity<E> asSubQueryEntity(String alias){
		return Estivate.subQueryEntity(this, alias);
	}

	public SelectQuery<E> pruneUnknownColumns(){
		
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
