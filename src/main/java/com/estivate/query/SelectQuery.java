package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.estivate.util.ReflectionUtils;

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
			else if(field.getDeclaredAnnotation(Projection.IsNull.class) != null) {
				Projection.IsNull attribute = field.getDeclaredAnnotation(Projection.IsNull.class);
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.Functions.isNull, attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.IsNotNull.class) != null) {
				Projection.IsNotNull attribute = field.getDeclaredAnnotation(Projection.IsNotNull.class);
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.Functions.isNotNull, attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Function.class) != null) {
				Projection.Function attribute = field.getDeclaredAnnotation(Projection.Function.class);
				select(attribute.entity() == null ? this.entity : new Entity<>(attribute.entity()), attribute.attribute(), Estivate.function( attribute.functionPrefix(), attribute.functionSuffix()), attribute.alias());
			}
			else if(field.getDeclaredAnnotation(Projection.Nested.class) != null) {
				if(field.getType().isAssignableFrom(List.class)) {
					Class<?> listType = ReflectionUtils.getListType(field);
					if(listType == null) {
						log.error("List type is not found for field " + field.getName());
						continue;
					}
					selectAll(listType);
				}
				else {
					selectAll(field.getType());
				}
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

	public SelectQuery<E> groupBy(Attribute attribute) { groupBys.add(attribute); return this; }
	public SelectQuery<E> groupBy(Entity<?> entity, String field) { return groupBy(Estivate.attribute(entity, field)); }
	public SelectQuery<E> groupBy(Class<?> c, String field) { return groupBy(new Entity<>(c), field); }
	public <T, P> SelectQuery<E> groupBy(AttributeGetter<T, P> getter) { return groupBy(Estivate.attribute(getter)); }
	

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
	public 		E 	extractSingle(Context context) { return context.extractSingle(this); }
	public <T> 	T 	extractSingle(Context context, Class<T> clazz) { return context.extractSingle(this, clazz); }
	public <T> 	T 	extractSingle(Context context, Entity<T> entity) { return context.extractSingle(this, entity); }
	public 	Object 	extractSingle(Context context, Attribute attribute) { return context.extractSingle(this, attribute); }
	public 	Object 	extractSingle(Context context, Class<?> entity, String attributeName) { return context.extractSingle(this, entity, attributeName); }
	public 	Object 	extractSingle(Context context, Entity<?> entity, String attributeName) { return context.extractSingle(this, entity.entity, attributeName); }
	public <T, P> P extractSingle(Context context, AttributeGetter<T, P> getter) { return (P) extractSingle(context, Estivate.attribute(getter));}
	public Object 	extractSingle(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.extractSingle(this, entity, attributeName, function); }
	public Object 	extractSingle(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.extractSingle(this, entity.entity, attributeName, function); }
	public <T, P> P extractSingle(Context context, AttributeGetter<T, P> getter, Attribute.Function function) { return (P) extractSingle(context, Estivate.attribute(getter, function));}
	
	
	@SuppressWarnings("unchecked")
	public 			Optional<E> extractOptional(Context context) { return context.extractOptional(this, (Class<E>) entity.entity); }
	public <T> 		Optional<T> extractOptional(Context context, Class<T> clazz) { return context.extractOptional(this, clazz); }
	public <T> 		Optional<T> extractOptional(Context context, Entity<T> entity) { return context.extractOptional(this, entity); }
	public 			Optional<?> extractOptional(Context context, Attribute attribute) { return context.extractOptional(this, attribute); }
	public 			Optional<?> extractOptional(Context context, Entity<?> entity, String attributeName) { return context.extractOptional(this, entity.entity, attributeName); }
	public 			Optional<?> extractOptional(Context context, Class<?> entity, String attributeName) { return context.extractOptional(this, entity, attributeName); }
	public <T, P> 	Optional<P> extractOptional(Context context, AttributeGetter<T, P> attributeGetter) { return (Optional<P>) context.extractOptional(this, Estivate.attribute(attributeGetter)); }
	public 			Optional<?> extractOptional(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.extractOptional(this, entity.entity, attributeName, function); }
	public 			Optional<?> extractOptional(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.extractOptional(this, entity, attributeName, function); }
	public <T, P> 	Optional<P> extractOptional(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (Optional<P>) context.extractOptional(this, Estivate.attribute(attributeGetter, function)); }
	
	
	public 			List<E> extractList(Context context){ return context.extractList(this, entity); }
	public <T> 		List<T> extractList(Context context, Class<T> entity) { return context.extractList(this, entity); }
	public <T> 		List<T> extractList(Context context, Entity<T> entity) { return context.extractList(this, entity); }
	public 			List<?> extractList(Context context, Attribute attribute) { return context.extractList(this, attribute); }
	public 			List<?> extractList(Context context, Class<?> entity, String attributeName) { return context.extractList(this, entity, attributeName); }
	public 			List<?> extractList(Context context, Entity<?> entity, String attributeName) { return context.extractList(this, entity, attributeName); }
	public <T, P> 	List<P> extractList(Context context, AttributeGetter<T, P> attributeGetter) { return context.extractList(this, attributeGetter); }
	public 			List<?> extractList(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.extractList(this, entity, attributeName, function); }
	public 			List<?> extractList(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.extractList(this, entity, attributeName, function); }
	public <T, P> 	List<P> extractList(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.extractList(this, attributeGetter, function); }

	public 			List<?> extractListDistinct(Context context, Attribute attribute) { return context.extractListDistinct(this, attribute); }
	public 			List<?> extractListDistinct(Context context, Class<?> entity, String attributeName) { return context.extractListDistinct(this, entity, attributeName); }
	public 			List<?> extractListDistinct(Context context, Entity<?> entity, String attributeName) { return context.extractListDistinct(this, entity, attributeName); }
	public <T, P> 	List<P> extractListDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return context.extractListDistinct(this, attributeGetter); }
	public 			List<?> extractListDistinct(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.extractListDistinct(this, entity, attributeName, function); }
	public 			List<?> extractListDistinct(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.extractListDistinct(this, entity, attributeName, function); }
	public <T, P> 	List<P> extractListDistinct(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.extractListDistinct(this, attributeGetter, function); }

	public 			Set<?> extractSet(Context context, Attribute attribute) { return context.extractSet(this, attribute); }
	public 			Set<?> extractSet(Context context, Class<?> entity, String attributeName) { return context.extractSet(this, entity, attributeName); }
	public 			Set<?> extractSet(Context context, Entity<?> entity, String attributeName) { return context.extractSet(this, entity, attributeName); }
	public <T, P> 	Set<P> extractSet(Context context, AttributeGetter<T, P> attributeGetter) { return context.extractSet(this, attributeGetter); }
	public 			Set<?> extractSet(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return context.extractSet(this, entity, attributeName, function); }
	public 			Set<?> extractSet(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return context.extractSet(this, entity, attributeName, function); }
	public <T, P> 	Set<P> extractSet(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return context.extractSet(this, attributeGetter, function); }

	public Long extractCountAll(Context context) { return context.extractCountAll(this); }
	public Optional<Long> extractOptionalCountAll(Context context) { return context.extractOptionalCountAll(this); }

	public Long extractCountDistinct(Context context, Attribute attribute) { return context.extractCountDistinct(this, attribute); }
	public Long extractCountDistinct(Context context, Class<?> entity, String attributeName) { return context.extractCountDistinct(this, entity, attributeName); }
	public Long extractCountDistinct(Context context, Entity<?> entity, String attributeName) { return context.extractCountDistinct(this, entity, attributeName); }
	public <T, P> Long extractCountDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return context.extractCountDistinct(this, attributeGetter); }

	public Optional<Long> extractOptionalCountDistinct(Context context, Attribute attribute) { return context.extractOptionalCountDistinct(this, attribute); }
	public Optional<Long> extractOptionalCountDistinct(Context context, Class<?> entity, String attributeName) { return context.extractOptionalCountDistinct(this, entity, attributeName); }
	public Optional<Long> extractOptionalCountDistinct(Context context, Entity<?> entity, String attributeName) { return context.extractOptionalCountDistinct(this, entity, attributeName); }
	public <T, P> Optional<Long> extractOptionalCountDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return context.extractOptionalCountDistinct(this, attributeGetter); }


	// ==================== WRAPPERS ====================

	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public 		E 	fetchSingle(Context context) { return extractSingle(context); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public <T> 	T 	fetchAsSingle(Context context, Class<T> clazz) { return extractSingle(context, clazz); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public <T> 	T 	fetchAsSingle(Context context, Entity<T> entity) { return extractSingle(context, entity); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public 	Object 	fetchAsSingle(Context context, Attribute attribute) { return extractSingle(context, attribute); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public 	Object 	fetchAsSingle(Context context, Class<?> entity, String attributeName) { return extractSingle(context, entity, attributeName); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public 	Object 	fetchAsSingle(Context context, Entity<?> entity, String attributeName) { return extractSingle(context, entity, attributeName); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public <T, P> P fetchAsSingle(Context context, AttributeGetter<T, P> getter) { return extractSingle(context, getter); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public Object 	fetchAsSingle(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return extractSingle(context, entity, attributeName, function); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public Object 	fetchAsSingle(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return extractSingle(context, entity, attributeName, function); }
	/** @deprecated Use extractSingle with the same arguments. */
	@Deprecated
	public <T, P> P fetchAsSingle(Context context, AttributeGetter<T, P> getter, Attribute.Function function) { return extractSingle(context, getter, function); }

	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<E> fetchOptional(Context context) { return extractOptional(context); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public <T> 		Optional<T> fetchAsOptional(Context context, Class<T> clazz) { return extractOptional(context, clazz); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public <T> 		Optional<T> fetchAsOptional(Context context, Entity<T> entity) { return extractOptional(context, entity); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<?> fetchAsOptional(Context context, Attribute attribute) { return extractOptional(context, attribute); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<?> fetchAsOptional(Context context, Entity<?> entity, String attributeName) { return extractOptional(context, entity, attributeName); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<?> fetchAsOptional(Context context, Class<?> entity, String attributeName) { return extractOptional(context, entity, attributeName); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public <T, P> 	Optional<P> fetchAsOptional(Context context, AttributeGetter<T, P> attributeGetter) { return extractOptional(context, attributeGetter); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<?> fetchAsOptional(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return extractOptional(context, entity, attributeName, function); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public 			Optional<?> fetchAsOptional(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return extractOptional(context, entity, attributeName, function); }
	/** @deprecated Use extractOptional with the same arguments. */
	@Deprecated
	public <T, P> 	Optional<P> fetchAsOptional(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return extractOptional(context, attributeGetter, function); }

	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<E> fetchList(Context context) { return extractList(context); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public <T> 		List<T> fetchAsList(Context context, Class<T> entity) { return extractList(context, entity); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public <T> 		List<T> fetchAsList(Context context, Entity<T> entity) { return extractList(context, entity); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsList(Context context, Attribute attribute) { return extractList(context, attribute); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsList(Context context, Class<?> entity, String attributeName) { return extractList(context, entity, attributeName); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsList(Context context, Entity<?> entity, String attributeName) { return extractList(context, entity, attributeName); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public <T, P> 	List<P> fetchAsList(Context context, AttributeGetter<T, P> attributeGetter) { return extractList(context, attributeGetter); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsList(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return extractList(context, entity, attributeName, function); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsList(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return extractList(context, entity, attributeName, function); }
	/** @deprecated Use extractList with the same arguments. */
	@Deprecated
	public <T, P> 	List<P> fetchAsList(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return extractList(context, attributeGetter, function); }

	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsListDistinct(Context context, Attribute attribute) { return extractListDistinct(context, attribute); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsListDistinct(Context context, Class<?> entity, String attributeName) { return extractListDistinct(context, entity, attributeName); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsListDistinct(Context context, Entity<?> entity, String attributeName) { return extractListDistinct(context, entity, attributeName); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public <T, P> 	List<P> fetchAsListDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return extractListDistinct(context, attributeGetter); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsListDistinct(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return extractListDistinct(context, entity, attributeName, function); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public 			List<?> fetchAsListDistinct(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return extractListDistinct(context, entity, attributeName, function); }
	/** @deprecated Use extractListDistinct with the same arguments. */
	@Deprecated
	public <T, P> 	List<P> fetchAsListDistinct(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return extractListDistinct(context, attributeGetter, function); }

	/** @deprecated Use extractCountAll with the same arguments. */
	@Deprecated
	public Long fetchCountAll(Context context) { return extractCountAll(context); }

	/** @deprecated Use extractCountDistinct with the same arguments. */
	@Deprecated
	public Long fetchCountDistinct(Context context, Attribute attribute) { return extractCountDistinct(context, attribute); }
	/** @deprecated Use extractCountDistinct with the same arguments. */
	@Deprecated
	public Long fetchCountDistinct(Context context, Class<?> entity, String attributeName) { return extractCountDistinct(context, entity, attributeName); }
	/** @deprecated Use extractCountDistinct with the same arguments. */
	@Deprecated
	public Long fetchCountDistinct(Context context, Entity<?> entity, String attributeName) { return extractCountDistinct(context, entity, attributeName); }
	/** @deprecated Use extractCountDistinct with the same arguments. */
	@Deprecated
	public <T, P> Long fetchCountDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return extractCountDistinct(context, attributeGetter); }

	/** @deprecated Use extractOptionalCountAll with the same arguments. */
	@Deprecated
	public Optional<Long> fetchOptionalCountAll(Context context) { return extractOptionalCountAll(context); }

	/** @deprecated Use extractOptionalCountDistinct with the same arguments. */
	@Deprecated
	public Optional<Long> fetchOptionalCountDistinct(Context context, Attribute attribute) { return extractOptionalCountDistinct(context, attribute); }
	/** @deprecated Use extractOptionalCountDistinct with the same arguments. */
	@Deprecated
	public Optional<Long> fetchOptionalCountDistinct(Context context, Class<?> entity, String attributeName) { return extractOptionalCountDistinct(context, entity, attributeName); }
	/** @deprecated Use extractOptionalCountDistinct with the same arguments. */
	@Deprecated
	public Optional<Long> fetchOptionalCountDistinct(Context context, Entity<?> entity, String attributeName) { return extractOptionalCountDistinct(context, entity, attributeName); }
	/** @deprecated Use extractOptionalCountDistinct with the same arguments. */
	@Deprecated
	public <T, P> Optional<Long> fetchOptionalCountDistinct(Context context, AttributeGetter<T, P> attributeGetter) { return extractOptionalCountDistinct(context, attributeGetter); }

	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public 			Set<?> fetchAsSet(Context context, Attribute attribute) { return extractSet(context, attribute); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public 			Set<?> fetchAsSet(Context context, Class<?> entity, String attributeName) { return extractSet(context, entity, attributeName); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public 			Set<?> fetchAsSet(Context context, Entity<?> entity, String attributeName) { return extractSet(context, entity, attributeName); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public <T, P> 	Set<P> fetchAsSet(Context context, AttributeGetter<T, P> attributeGetter) { return extractSet(context, attributeGetter); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public 			Set<?> fetchAsSet(Context context, Class<?> entity, String attributeName, Attribute.Function function) { return extractSet(context, entity, attributeName, function); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public 			Set<?> fetchAsSet(Context context, Entity<?> entity, String attributeName, Attribute.Function function) { return extractSet(context, entity, attributeName, function); }
	/** @deprecated Use extractSet with the same arguments. */
	@Deprecated
	public <T, P> 	Set<P> fetchAsSet(Context context, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return extractSet(context, attributeGetter, function); }

	/** @deprecated Use extractMap with the same arguments. */
	@Deprecated
	public <A1E, A1T, A2E, A2T> Map<A1T, A2T> fetchAsMap(Context context, AttributeGetter<A1E, A1T> keyAttributeGetter, AttributeGetter<A2E, A2T> valueAttributeGetter) { return extractMap(context, keyAttributeGetter, valueAttributeGetter); }
	/** @deprecated Use extractMap with the same arguments. */
	@Deprecated
	public <AE, AT, C> Map<AT, C> fetchAsMap(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass) { return extractMap(context, attributeGetter, vClass); }
	/** @deprecated Use extractMap with the same arguments. */
	@Deprecated
	public <C1, C2> Map<C1, C2> fetchAsMap(Context context, Class<C1> uClass, Class<C2> vClass) { return extractMap(context, uClass, vClass); }
	/** @deprecated Use extractMap with the same arguments. */
	@Deprecated
	public <C, AE, AT> Map<C, AT> fetchAsMap(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter) { return extractMap(context, uClass, valueGetter); }
	/** @deprecated Use extractMap with the same arguments. */
	@Deprecated
	public Map<Object, Object> fetchAsMap(Context context, Attribute keyAttribute, Attribute valueAttribute) { return extractMap(context, keyAttribute, valueAttribute); }

	/** @deprecated Use extractMapList with the same arguments. */
	@Deprecated
	public <A1E, A1T, A2E, A2T> Map<A1T, List<A2T>> fetchAsMapList(Context context, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter) { return extractMapList(context, attributeGetter, valueGetter); }
	/** @deprecated Use extractMapList with the same arguments. */
	@Deprecated
	public <AE, AT, C> Map<AT, List<C>> fetchAsMapList(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass) { return extractMapList(context, attributeGetter, vClass); }
	/** @deprecated Use extractMapList with the same arguments. */
	@Deprecated
	public <C1, C2> Map<C1, List<C2>> fetchAsMapList(Context context, Class<C1> uClass, Class<C2> vClass) { return extractMapList(context, uClass, vClass); }
	/** @deprecated Use extractMapList with the same arguments. */
	@Deprecated
	public <C, AE, AT> Map<C, List<AT>> fetchAsMapList(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter) { return extractMapList(context, uClass, valueGetter); }

	/** @deprecated Use extractMapSet with the same arguments. */
	@Deprecated
	public <A1E, A1T, A2E, A2T> Map<A1T, Set<A2T>> fetchAsMapSet(Context context, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter) { return extractMapSet(context, attributeGetter, valueGetter); }
	/** @deprecated Use extractMapSet with the same arguments. */
	@Deprecated
	public <AE, AT, C> Map<AT, Set<C>> fetchAsMapSet(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass) { return extractMapSet(context, attributeGetter, vClass); }
	/** @deprecated Use extractMapSet with the same arguments. */
	@Deprecated
	public <C1, C2> Map<C1, Set<C2>> fetchAsMapSet(Context context, Class<C1> uClass, Class<C2> vClass) { return extractMapSet(context, uClass, vClass); }
	/** @deprecated Use extractMapSet with the same arguments. */
	@Deprecated
	public <C, AE, AT> Map<C, Set<AT>> fetchAsMapSet(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter) { return extractMapSet(context, uClass, valueGetter); }


	// ==================== AGGREGATION METHODS ====================
	
	public <A1E, A1T, A2E, A2T> Map<A1T, A2T> extractMap(Context context, AttributeGetter<A1E, A1T> keyAttributeGetter, AttributeGetter<A2E, A2T> valueAttributeGetter){ return context.extractMap(this, keyAttributeGetter, valueAttributeGetter); }
	public <A1E, A1T, A2E, A2T> Map<A1T, A2T> extractMap(Context context, AttributeGetter<A1E, A1T> keyAttributeGetter, AttributeGetter<A2E, A2T> valueAttributeGetter, Map<A1T, A2T> map){ return context.extractMap(this, keyAttributeGetter, valueAttributeGetter, map); }
	public <AE, AT, C> Map<AT, C> extractMap(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){ return context.extractMap(this, attributeGetter, vClass); }
	public <AE, AT, C> Map<AT, C> extractMap(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass, Map<AT, C> map){ return context.extractMap(this, attributeGetter, vClass, map); }
	public <C1, C2> Map<C1, C2> extractMap(Context context, Class<C1> uClass, Class<C2> vClass){ return context.extractMap(this, uClass, vClass); }
	public <C1, C2> Map<C1, C2> extractMap(Context context, Class<C1> uClass, Class<C2> vClass, Map<C1, C2> map){ return context.extractMap(this, uClass, vClass, map); }
	public <C, AE, AT> Map<C, AT> extractMap(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){ return context.extractMap(this, uClass, valueGetter); }
	public <C, AE, AT> Map<C, AT> extractMap(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter, Map<C, AT> map){ return context.extractMap(this, uClass, valueGetter, map); }

	public Map<Object, Object> extractMap(Context context, Attribute keyAttribute, Attribute valueAttribute){ return context.extractMap(this, keyAttribute, valueAttribute); }
	public Map<Object, Object> extractMap(Context context, Attribute keyAttribute, Attribute valueAttribute, Map<Object, Object> map){ return context.extractMap(this, keyAttribute, valueAttribute, map); }

	public <A1E, A1T, A2E, A2T> Map<A1T, List<A2T>> extractMapList(Context context, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter){ return context.extractMapList(this, attributeGetter, valueGetter); }
	public <A1E, A1T, A2E, A2T> Map<A1T, List<A2T>> extractMapList(Context context, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter, Map<A1T, List<A2T>> map){ return context.extractMapList(this, attributeGetter, valueGetter, map); }
	public <AE, AT, C> Map<AT, List<C>> extractMapList(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){ return context.extractMapList(this, attributeGetter, vClass); }
	public <AE, AT, C> Map<AT, List<C>> extractMapList(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass, Map<AT, List<C>> map){ return context.extractMapList(this, attributeGetter, vClass, map); }
	public <C1, C2> Map<C1, List<C2>> extractMapList(Context context, Class<C1> uClass, Class<C2> vClass){ return context.extractMapList(this, uClass, vClass); }
	public <C1, C2> Map<C1, List<C2>> extractMapList(Context context, Class<C1> uClass, Class<C2> vClass, Map<C1, List<C2>> map){ return context.extractMapList(this, uClass, vClass, map); }
	public <C, AE, AT> Map<C, List<AT>> extractMapList(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){ return context.extractMapList(this, uClass, valueGetter); }
	public <C, AE, AT> Map<C, List<AT>> extractMapList(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter, Map<C, List<AT>> map){ return context.extractMapList(this, uClass, valueGetter, map); }

	public <A1E, A1T, A2E, A2T> Map<A1T, Set<A2T>> extractMapSet(Context context, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter){ return context.extractMapSet(this, attributeGetter, valueGetter); }
	public <AE, AT, C> Map<AT, Set<C>> extractMapSet(Context context, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){ return context.extractMapSet(this, attributeGetter, vClass); }
	public <C1, C2> Map<C1, Set<C2>> extractMapSet(Context context, Class<C1> uClass, Class<C2> vClass){ return context.extractMapSet(this, uClass, vClass); }
	public <C, AE, AT> Map<C, Set<AT>> extractMapSet(Context context, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){ return context.extractMapSet(this, uClass, valueGetter); }


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
