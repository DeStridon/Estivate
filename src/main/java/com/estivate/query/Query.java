package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.estivate.NameMapper;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.query.Select.SelectMethod;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Query extends Aggregator{
	
	// Method wrappers
	
	public Query eq   	(Class entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public Query notEq	(Class entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public Query lt   	(Class entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public Query gt   	(Class entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public Query lte  	(Class entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public Query gte  	(Class entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public Query in   	(Class entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public Query in   	(Class entity, String attribute, Collection<Object> values) { super.in    (entity, attribute, values); return this; }
	public Query notIn  (Class entity, String attribute, Object... values)    { super.notIn    (entity, attribute, values); return this; }
	public Query notIn  (Class entity, String attribute, Collection<Object> values) { super.notIn    (entity, attribute, values); return this; }
	public Query like 	(Class entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	public Query notLike(Class entity, String attribute, String value)	    { super.notLike  (entity, attribute, value);  return this; }
	
	public Query eqIfNotNull   		(Class entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query notEqIfNotNull		(Class entity, String attribute, Object value)        { super.notEqIfNotNull(entity, attribute, value);  return this; }
	public Query ltIfNotNull   		(Class entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public Query gtIfNotNull   		(Class entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query lteIfNotNull  		(Class entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public Query gteIfNotNull  		(Class entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public Query inIfNotNull   		(Class entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public Query inIfNotNull   		(Class entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public Query notInIfNotNull   	(Class entity, String attribute, Object... values)    { super.notInIfNotNull   (entity, attribute, values); return this; }
	public Query notInIfNotNull   	(Class entity, String attribute, List<Object> values) { super.notInIfNotNull   (entity, attribute, values); return this; }
	public Query likeIfNotNull 		(Class entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeIfNotNull 	(Class entity, String attribute, String value)        { super.notLikeIfNotNull (entity, attribute, value);  return this; }
	
	
	public Query isNotNull(Class entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public Query isNull(Class entity, String attribute) {super.isNull(entity, attribute); return this;}
	public Query eqOrNull(Class entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	public Query eq   	(Entity entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public Query notEq	(Entity entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public Query lt   	(Entity entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public Query gt   	(Entity entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public Query lte  	(Entity entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public Query gte  	(Entity entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public Query in   	(Entity entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public Query in   	(Entity entity, String attribute, List<Object> values) { super.in    (entity, attribute, values); return this; }
	public Query notIn  (Entity entity, String attribute, Object... values)    { super.notIn    (entity, attribute, values); return this; }
	public Query notIn  (Entity entity, String attribute, List<Object> values) { super.notIn    (entity, attribute, values); return this; }
	public Query like 	(Entity entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	public Query notLike(Entity entity, String attribute, String value)	    { super.notLike  (entity, attribute, value);  return this; }
	
	
	public Query eqIfNotNull   		(Entity entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public Query ltIfNotNull   		(Entity entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public Query gtIfNotNull   		(Entity entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public Query lteIfNotNull  		(Entity entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public Query gteIfNotNull  		(Entity entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public Query inIfNotNull   		(Entity entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public Query inIfNotNull   		(Entity entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public Query notInIfNotNull   	(Entity entity, String attribute, Object... values)    { super.notInIfNotNull   (entity, attribute, values); return this; }
	public Query notInIfNotNull   	(Entity entity, String attribute, List<Object> values) { super.notInIfNotNull   (entity, attribute, values); return this; }
	public Query likeIfNotNull 		(Entity entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	public Query notLikeIfNotNull 	(Entity entity, String attribute, String value)        { super.notLikeIfNotNull (entity, attribute, value);  return this; }
	
	public Query isNotNull			(Entity entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public Query isNull				(Entity entity, String attribute) {super.isNull(entity, attribute); return this;}
	public Query eqOrNull			(Entity entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	
	@Getter
	final Class baseClass;

	public static NameMapper nameMapper = new DefaultNameMapper();
	
	
	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	Set<Join> joins = new LinkedHashSet<>();
	
	@Getter
	Set<Select> selects = new TreeSet<>();
	
	@Getter
	Set<String> orders = new TreeSet<>();
	
	@Getter
	Set<String> groupBys = new LinkedHashSet<>();
	
	@Getter
	Integer offset;

	@Getter
	Integer limit;

	@Getter
	IndexHint indexHint;

	@Getter
	Set<String> indexNames = new LinkedHashSet<>();
	
	public Query(Class baseClass) {
		super(GroupType.AND);
		this.baseClass = baseClass;
	}
	

	// TODO : how to implement count ?
	// should not be implemented on query but on executor: query is made for filtering, executor for specific results fetching
	// add sum, min and other potential projections ?
	// set new field method ? yes, similar to projection


	public Query and(EstivateNode... nodes) {
		criterions.add(EstivateNode.add(nodes));
		return this;
	}
	
	
	// nested search of the different classes used in criterions and to be added in joins
	public Set<Entity> digClasses(Aggregator aggregator){
		
		Set<Entity> classes = new HashSet<>();
		for(EstivateNode node : aggregator.criterions) {
			
			if(node instanceof Criterion) {
				classes.add(((Criterion) node).entity);
				
				if(node instanceof Criterion.Operator) {
					Criterion.Operator operator = (Criterion.Operator) node;
					if(operator.value instanceof PropertyValue) {
						PropertyValue estivateField = (PropertyValue) operator.value;
						classes.add(estivateField.entity);
					}
				}
				
			}
			else if(node instanceof Aggregator) {
				classes.addAll(digClasses((Aggregator) node));
			}
			
		}
		
		return classes;
		
	}
	
	
	// purpose : build join tree out of entities nodes and join branches
	public List<Join> buildJoins() {

		// 0. initiate
		Set<Entity> joinedEntities = new HashSet<>(Arrays.asList(new Entity(baseClass)));
		List<Join> classJoins = new ArrayList<>();
		
		// 1. list all classes needed for query
		Set<Entity> targetEntities = new HashSet<>(digClasses(this));
		targetEntities.addAll(selects.stream().filter(x -> x.entity != null).map(x -> x.entity).collect(Collectors.toSet()));
		for(Join join : joins) {
			targetEntities.add(join.joinerEntity);
			targetEntities.add(join.joinedEntity);
		}
		
		while(true) { 
			Join cj = tryAddingJoinedClass(joinedEntities, targetEntities);
			if(cj != null) {
				joinedEntities.add(cj.joinedEntity);
				classJoins.add(cj);
			}
			else {
				break;
			}
		}

		// check no missing class from queryClasses in joinedQueryClasses
		if(!joinedEntities.containsAll(targetEntities)) {
			throw new RuntimeException(
					"No junction found for classes "
					+ targetEntities.stream().filter(x -> !joinedEntities.contains(x)).map(x -> x.entity.getSimpleName()).collect(Collectors.joining(", ", "{", "}")) 
					+ " with classes "
					+joinedEntities.stream().map(x -> x.entity.getSimpleName()).collect(Collectors.joining(", ", "{", "}")));
		}
		
		return classJoins;
		
		
	}

	// links first suitable class of candidates to one of already joined classes
	// return null if every class already joined or if all remaining classes cannot be joined
	private Join tryAddingJoinedClass(Set<Entity> joined, Set<Entity> candidates) {
		for(Entity candidate : candidates) {
			
			// if already joined, skip
			if(joined.contains(candidate)) {
				continue;
			}
			
			// joining strategy #1 : manual joins
			for(Entity joinedClass : joined) {
				Join manualJoin = joins.stream()
						.filter(x -> x.joinerEntity.equals(joinedClass) && x.joinedEntity.equals(candidate))
						.findFirst().orElse(null);
				if(manualJoin != null) {
					return manualJoin;
				}
			}
			
			// joining strategy #2 : VirtualKey
			for(Entity joinedClass : joined) {
				
				Join cj = Join.find(joinedClass, candidate);
				if(cj != null) {
					return cj;
				}
			}
		}
		
		return null;
	}
	
	
	
	//public EstivateQuery join(Entity c) { joinedClasses.add(c); return this; }
	//public EstivateQuery join(Class c) { return join(new Entity(c)); }

	public Query join(Join classJoin) { joins.add(classJoin); return this; }
	
	public Query orderAsc(Entity c, String attribute) { orders.add(nameMapper.mapDatabase(c, attribute) + " ASC"); return this; }
	public Query orderAsc(Class c, String attribute) { return orderAsc(new Entity(c), attribute); }
	
	
	public Query orderDesc(Entity c, String attribute) { orders.add(nameMapper.mapDatabase(c, attribute) + " DESC"); return this; }
	public Query orderDesc(Class c, String attribute) { return orderDesc(new Entity(c), attribute); }
	
	public Query limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public Query offset(Integer offset) {
		this.offset = offset;
		return this;
	}
	
	
	public Query select(Class c, String...fields) { return select(new Entity(c), fields); }
	
	public Query select(Entity c, String...args) {
		
		Class<?> currentClazz = c.entity;
		while(currentClazz != Object.class) {
			
			String[] fields = args.length == 0 ? FieldUtils.getEntityFields(currentClazz).stream().map( x -> x.getName() ).toArray(String[]::new) : args;
			
			for(String field : fields){
				if(selects.stream().noneMatch(x -> x.entity.equals(c) && x.attribute.equals(field))) {
					select(c, field);
				}
			}
			currentClazz = currentClazz.getSuperclass();
		}
		return this;
	}
	
	public Query select(Class c, String attribute) { return select(new Entity(c), attribute); }
	
	public Query select(Entity c, String attribute) {
		Select select = selects.stream().filter(x -> x.entity.equals(c) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}
		selects.add(Select.builder().entity(c).attribute(attribute).build());
		return this;
	}
	
	public Query selectCount() {
		selects.add(Select.builder().method(SelectMethod.Count).build());
		return this;
	}
	
	public Query selectCount(Class c, String attribute) {
		selects.add(Select.builder().method(SelectMethod.Count).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	public Query selectDistinct(Class c, String attribute) {
		
		Select select = selects.stream().filter(x -> x.entity.equals(new Entity(c)) && x.attribute.equals(attribute)).findAny().orElse(null);
		if(select != null) {
			selects.remove(select);
		}
		
		
		selects.add(Select.builder().method(SelectMethod.Distinct).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	public Query selectMin(Class c, String attribute) {
		selects.add(Select.builder().method(SelectMethod.Min).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	public Query selectMax(Class c, String attribute) {
		selects.add(Select.builder().method(SelectMethod.Max).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	public Query selectSum(Class c, String attribute) {
		selects.add(Select.builder().method(SelectMethod.Sum).entity(new Entity(c)).attribute(attribute).build());
		return this;
	}
	
	
	
	public Query clone() {
		Query joinQuery = new Query(baseClass);
		
		joinQuery.selects = new LinkedHashSet<>(this.selects);
		joinQuery.joins = new LinkedHashSet<>(this.joins);

		joinQuery.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		joinQuery.indexHint = this.indexHint;
		joinQuery.indexNames = new LinkedHashSet<>(this.indexNames);

		joinQuery.orders = new LinkedHashSet<>(this.orders);
		joinQuery.groupBys = new LinkedHashSet<>(this.groupBys);

		joinQuery.limit = this.limit;
		joinQuery.offset = this.offset;
		
		return joinQuery;
	}

	public Query groupBy(Class c, String field) {
		
		// Create entity object
		Entity entity = new Entity(c);
		
		// Add to select
		select(entity, field);
		groupBys.add(nameMapper.mapDatabase(c, field));
		
		return this;
	}
	
	public Query setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		this.indexNames = new LinkedHashSet<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}
	
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class Entity{
		public final Class<? extends Object> entity;
		public final String alias;
		
		public Entity(Class entity) {
			this(entity, null);
		}
		
//		public String getName() {
//			if(alias != null) {
//				return alias;
//			}
//			return Query.nameMapper.mapEntityClass(entity);
//		}
	}


	
}
