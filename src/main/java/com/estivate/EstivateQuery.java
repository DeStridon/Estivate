package com.estivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import com.estivate.EstivateNameMapper.DefaultNameMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstivateQuery extends EstivateAggregator{
	
	// Method wrappers
	
	public EstivateQuery eq   (Class entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public EstivateQuery notEq(Class entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public EstivateQuery lt   (Class entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public EstivateQuery gt   (Class entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public EstivateQuery lte  (Class entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public EstivateQuery gte  (Class entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public EstivateQuery in   (Class entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public EstivateQuery in   (Class entity, String attribute, Collection<Object> values) { super.in    (entity, attribute, values); return this; }
	public EstivateQuery like (Class entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	
	public EstivateQuery eqIfNotNull   (Class entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery ltIfNotNull   (Class entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery gtIfNotNull   (Class entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery lteIfNotNull  (Class entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public EstivateQuery gteIfNotNull  (Class entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public EstivateQuery inIfNotNull   (Class entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public EstivateQuery inIfNotNull   (Class entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public EstivateQuery likeIfNotNull (Class entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	
	public EstivateQuery isNotNull(Class entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public EstivateQuery isNull(Class entity, String attribute) {super.isNull(entity, attribute); return this;}
	public EstivateQuery eqOrNull(Class entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	public EstivateQuery eq   (Entity entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public EstivateQuery notEq(Entity entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public EstivateQuery lt   (Entity entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public EstivateQuery gt   (Entity entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public EstivateQuery lte  (Entity entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public EstivateQuery gte  (Entity entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public EstivateQuery in   (Entity entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public EstivateQuery in   (Entity entity, String attribute, List<Object> values) { super.in    (entity, attribute, values); return this; }
	public EstivateQuery like (Entity entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	
	public EstivateQuery eqIfNotNull   (Entity entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery ltIfNotNull   (Entity entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery gtIfNotNull   (Entity entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public EstivateQuery lteIfNotNull  (Entity entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public EstivateQuery gteIfNotNull  (Entity entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public EstivateQuery inIfNotNull   (Entity entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public EstivateQuery inIfNotNull   (Entity entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public EstivateQuery likeIfNotNull (Entity entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	
	public EstivateQuery isNotNull(Entity entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public EstivateQuery isNull(Entity entity, String attribute) {super.isNull(entity, attribute); return this;}
	public EstivateQuery eqOrNull(Entity entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	
	final Class baseClass;
	public static EstivateNameMapper nameMapper = new DefaultNameMapper();
	
	
	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	Set<Entity> joinedClasses = new LinkedHashSet<>();
	Set<EstivateJoin> joins = new LinkedHashSet<>();
	
	@Getter
	Set<String> selects = new TreeSet<>();
	
	@Getter
	Set<String> orders = new TreeSet<>();
	
	@Getter
	Set<String> groupBys = new LinkedHashSet<>();
	
	Integer offset;
	Integer limit;
	
	// TODO : delete nameMapper here to load it dynamically from hibernate bean ?
	public EstivateQuery(Class baseClass) {
		super(GroupType.AND);
		this.baseClass = baseClass;
	}
	

	// TODO : how to implement count ?
	// should not be implemented on query but on executor: query is made for filtering, executor for specific results fetching
	// add sum, min and other potential projections ?
	// set new field method ? yes, similar to projection


	public EstivateQuery and(EstivateNode... nodes) {
		criterions.add(EstivateNode.add(nodes));
		return this;
	}
	
	
	// nested search of the different classes used in criterions and to be added in joins
	public Set<Entity> digClasses(EstivateAggregator aggregator){
		
		Set<Entity> classes = new HashSet<>();
		for(EstivateNode node : aggregator.criterions) {
			if(node instanceof EstivateCriterion) {
				classes.add(((EstivateCriterion) node).entity);
			}
			else if(node instanceof EstivateAggregator) {
				classes.addAll(digClasses((EstivateAggregator) node));
			}
			
		}
		
		return classes;
		
	}
	
	
//	// purpose : build join tree out of entities nodes and join branches
//	public List<EstivateJoin> buildJoins() {
//
//		// 0. initiate
//		Set<Entity> joinedEntities = new HashSet<>(List.of(new Entity(baseClass)));
//		List<EstivateJoin> classJoins = new ArrayList<>();
//		
//		// 1. list all classes needed for query
//		Set<Entity> targetEntities = new HashSet<>(digClasses(this));
////		targetEntities.addAll(manuallyJoinedClasses);
////		targetEntities.addAll(manualJoins.stream().map(x -> x.joinerEntity).collect(Collectors.toSet()));
////		targetEntities.addAll(manualJoins.stream().map(x -> x.joinedEntity).collect(Collectors.toSet()));
//		
//		// 2. use manual joins
//		
//
//		// initiate joinedQueryClasses list
//		
//		
//		while(true) { 
//			EstivateJoin cj = tryAddingJoinedClass(joinedEntities, targetEntities);
//			if(cj != null) {
//				joinedEntities.add(cj.joinedEntity);
//				classJoins.add(cj);
//			}
//			else {
//				break;
//			}
//		}
//
//		// check no missing class from queryClasses in joinedQueryClasses
//		if(!joinedEntities.containsAll(targetEntities)) {
//			throw new RuntimeException(
//					"No junction found for classes "
//					+ targetEntities.stream().filter(x -> !joinedEntities.contains(x)).map(x -> x.entity.getSimpleName()).collect(Collectors.joining(", ", "{", "}")) 
//					+ " with classes "
//					+joinedEntities.stream().map(x -> x.entity.getSimpleName()).collect(Collectors.joining(", ", "{", "}")));
//		}
//		
//		return classJoins;
//		
//		
//	}

	// links first suitable class of candidates to one of already joined classes
	// return null if every class already joined or if all remaining classes cannot be joined
	private EstivateJoin tryAddingJoinedClass(Set<Entity> joined, Set<Entity> candidates) {
		for(Entity candidate : candidates) {
			// if already joined, skip
			if(joined.contains(candidate)) {
				continue;
			}
			
			// try to join, if not successful throw exc
			for(Entity joinedClass : joined) {
				
				// try joining through manual joins
				EstivateJoin manualJoin = joins.stream()
						.filter(x -> x.joinerEntity.equals(joinedClass) && x.joinedEntity.equals(candidate))
						.findFirst().orElse(null);
				
				if(manualJoin != null) {
					return manualJoin;
				}
				
				EstivateJoin cj = EstivateJoin.find(joinedClass, candidate);
				if(cj != null) {
					return cj;
				}
			}
		}
		
		return null;
	}
	
	
	
	public EstivateQuery join(Entity c) { joinedClasses.add(c); return this; }
	public EstivateQuery join(Class c) { return join(new Entity(c)); }

	public EstivateQuery join(EstivateJoin classJoin) { joins.add(classJoin); return this; }
	
	public EstivateQuery orderAsc(Entity c, String attribute) { orders.add(c.getName()+"."+ nameMapper.mapAttribute(attribute) + " ASC"); return this; }
	public EstivateQuery orderAsc(Class c, String attribute) { return orderAsc(new Entity(c), attribute); }
	
	
	public EstivateQuery orderDesc(Entity c, String attribute) { orders.add(c.getName()+"."+ nameMapper.mapAttribute(attribute) + " DESC"); return this; }
	public EstivateQuery orderDesc(Class c, String attribute) { return orderDesc(new Entity(c), attribute); }
	
	public EstivateQuery limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public EstivateQuery offset(Integer offset) {
		this.offset = offset;
		return this;
	}
	
	
	public EstivateQuery select(Class c, String...fields) { return select(new Entity(c), fields); }
	
	public EstivateQuery select(Entity c, String...args) {
		
		Class<?> currentClazz = c.entity;
		while(currentClazz != Object.class) {
			
			String[] fields = args.length == 0 ? Arrays.stream(currentClazz.getDeclaredFields()).filter(x -> !x.isAnnotationPresent(Transient.class)).map( x -> x.getName() ).toArray(String[]::new) : args;
			
			for(String field : fields){
				select(c, field);
			}
			currentClazz = currentClazz.getSuperclass();
		}
		return this;
	}
	
	public EstivateQuery select(Class c, String attribute) { return select(new Entity(c), attribute); }
	
	public EstivateQuery select(Entity c, String attribute) {
		selects.add(c.getName() + "." + nameMapper.mapAttribute(attribute)+" as `"+c.getName()+"."+attribute+"`");
		return this;
	}
	
	public EstivateQuery selectCount() {
		selects.add("COUNT(*)");
		return this;
	}
	
	public EstivateQuery selectCount(Class c, String attribute) {
		selects.add("COUNT(distinct "+nameMapper.mapEntityAttribute(c, attribute)+")");
		return this;
	}
	
	public EstivateQuery clone() {
		EstivateQuery joinQuery = new EstivateQuery(baseClass);
		joinQuery.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		joinQuery.selects = new LinkedHashSet<>(this.selects);
		
		joinQuery.limit = this.limit;
		joinQuery.offset = this.offset;
		
		return joinQuery;
	}

	public EstivateQuery groupBy(Class c, String attribute) {
		
		// Create entity object
		Entity entity = new Entity(c);
		
		// Add to select
		select(entity, attribute);
		groupBys.add("`"+entity.getName()+"."+attribute+"`");
		
		return this;
	}
	
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	public static class Entity{
		Class entity;
		String alias;
		
		public Entity(Class entity) {
			this(entity, null);
		}
		
		public String getName() {
			if(alias != null) {
				return alias;
			}
			return EstivateQuery.nameMapper.mapEntity(entity);
		}
	}
	
	


}
