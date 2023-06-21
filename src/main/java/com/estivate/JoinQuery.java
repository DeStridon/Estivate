package com.wezen.framework.orm.joinQuery;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wezen.framework.orm.joinQuery.JoinNameMapper.DefaultNameMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JoinQuery extends JoinAggregator{
	
	@Autowired SessionFactory sessionFactory;
	
	// Method wrappers
	
	public JoinQuery eq   (Class entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public JoinQuery notEq(Class entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public JoinQuery lt   (Class entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public JoinQuery gt   (Class entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public JoinQuery lte  (Class entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public JoinQuery gte  (Class entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public JoinQuery in   (Class entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public JoinQuery in   (Class entity, String attribute, Collection<Object> values) { super.in    (entity, attribute, values); return this; }
	public JoinQuery like (Class entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	
	public JoinQuery eqIfNotNull   (Class entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery ltIfNotNull   (Class entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery gtIfNotNull   (Class entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery lteIfNotNull  (Class entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public JoinQuery gteIfNotNull  (Class entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public JoinQuery inIfNotNull   (Class entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public JoinQuery inIfNotNull   (Class entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public JoinQuery likeIfNotNull (Class entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	
	public JoinQuery isNotNull(Class entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public JoinQuery isNull(Class entity, String attribute) {super.isNull(entity, attribute); return this;}
	public JoinQuery eqOrNull(Class entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	public JoinQuery eq   (Entity entity, String attribute, Object value)        { super.eq    (entity, attribute, value);  return this; }
	public JoinQuery notEq(Entity entity, String attribute, Object value)        { super.notEq (entity, attribute, value);  return this; }
	public JoinQuery lt   (Entity entity, String attribute, Object value)        { super.lt    (entity, attribute, value);  return this; }
	public JoinQuery gt   (Entity entity, String attribute, Object value)        { super.gt    (entity, attribute, value);  return this; }
	public JoinQuery lte  (Entity entity, String attribute, Object value)        { super.lte   (entity, attribute, value);  return this; }
	public JoinQuery gte  (Entity entity, String attribute, Object value)        { super.gte   (entity, attribute, value);  return this; }
	public JoinQuery in   (Entity entity, String attribute, Object... values)    { super.in    (entity, attribute, values); return this; }
	public JoinQuery in   (Entity entity, String attribute, List<Object> values) { super.in    (entity, attribute, values); return this; }
	public JoinQuery like (Entity entity, String attribute, String value)	    { super.like  (entity, attribute, value);  return this; }
	
	public JoinQuery eqIfNotNull   (Entity entity, String attribute, Object value)        { super.eqIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery ltIfNotNull   (Entity entity, String attribute, Object value)        { super.ltIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery gtIfNotNull   (Entity entity, String attribute, Object value)        { super.gtIfNotNull   (entity, attribute, value);  return this; }
	public JoinQuery lteIfNotNull  (Entity entity, String attribute, Object value)        { super.lteIfNotNull  (entity, attribute, value);  return this; }
	public JoinQuery gteIfNotNull  (Entity entity, String attribute, Object value)        { super.gteIfNotNull  (entity, attribute, value);  return this; }
	public JoinQuery inIfNotNull   (Entity entity, String attribute, Object... values)    { super.inIfNotNull   (entity, attribute, values); return this; }
	public JoinQuery inIfNotNull   (Entity entity, String attribute, List<Object> values) { super.inIfNotNull   (entity, attribute, values); return this; }
	public JoinQuery likeIfNotNull (Entity entity, String attribute, String value)        { super.likeIfNotNull (entity, attribute, value);  return this; }
	
	public JoinQuery isNotNull(Entity entity, String attribute) {super.isNotNull(entity, attribute); return this;}
	public JoinQuery isNull(Entity entity, String attribute) {super.isNull(entity, attribute); return this;}
	public JoinQuery eqOrNull(Entity entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return this;	}
	
	
	final Class baseClass;
	public static JoinNameMapper nameMapper = new DefaultNameMapper();
	
	
	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	Set<Entity> manuallyJoinedClasses = new LinkedHashSet<>();
	Set<JoinQueryClassJoin> manualJoins = new LinkedHashSet<>();
	
	@Getter
	Set<String> selects = new TreeSet<>();
	
	@Getter
	Set<String> orders = new TreeSet<>();
	
	@Getter
	Set<String> groupBys = new LinkedHashSet<>();
	
	Integer offset;
	Integer limit;
	
	// TODO : delete nameMapper here to load it dynamically from hibernate bean ?
	public JoinQuery(Class baseClass) {
		super(GroupType.AND);
		this.baseClass = baseClass;
	}
	

	// TODO : how to implement count ?
	// should not be implemented on query but on executor: query is made for filtering, executor for specific results fetching
	// add sum, min and other potential projections ?
	// set new field method ? yes, similar to projection


	public JoinQuery and(JoinNode... nodes) {
		criterions.add(JoinNode.add(nodes));
		return this;
	}
	
	
	
	public String compile() {
		StringBuilder sb = new StringBuilder();
		
		// Append selects 
		sb.append("SELECT");
		
		// If no select specified by developer, fill with baseclass attributes
		if(selects.isEmpty()) {
			select(baseClass);
		}
		
		// "distinct" keyword is used to avoid having duplicate results when joining from multiple tables, only shown when not having anything else than columns and no grouping
		if(selects.stream().allMatch(x -> x.contains(".")) && groupBys.isEmpty()) {
			sb.append(" distinct");
		}
		
		sb.append("\n  "+String.join(",\n  ", selects)+"\n");
		sb.append("FROM "+nameMapper.mapEntity(baseClass)+"\n");

		// Append joins
		for(JoinQueryClassJoin c : buildJoins()) {
			sb.append(c.toString()+'\n');
		}
		
		// Append where
		sb.append("WHERE ");
		sb.append(super.compile()+"\n");
		
		// Append group bys (if any)
		if(!groupBys.isEmpty()) {
			sb.append(groupBys.stream().collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// Append order
		if(!orders.isEmpty()) {
			sb.append(orders.stream().collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// Append limit & offset
		if(limit != null) {
			sb.append("LIMIT "+limit+"\n");
		}
		if(offset != null) {
			sb.append("OFFSET "+ offset +"\n");
		}

		return sb.toString();
	}
	
	
	public JoinQueryPreparedStatement preparedStatement() {
		
		JoinQueryPreparedStatement jqps = super.preparedStatement();
		
		StringBuilder sb = new StringBuilder();
		
		// Append selects 
		sb.append("SELECT");
		
		// If no select specified by developer, fill with baseclass attributes
		if(selects.isEmpty()) {
			select(baseClass);
		}
		
		// "distinct" keyword is used to avoid having duplicate results when joining from multiple tables, only shown when not having anything else than columns and no grouping
		if(selects.stream().allMatch(x -> x.contains(".")) && groupBys.isEmpty()) {
			sb.append(" distinct");
		}
		
		sb.append("\n  "+String.join(",\n  ", selects)+"\n");
		sb.append("FROM "+nameMapper.mapEntity(baseClass)+"\n");

		// Append joins
		for(JoinQueryClassJoin c : buildJoins()) {
			sb.append(c.toString()+'\n');
		}
		
		// Append where
		sb.append("WHERE ");
		
		sb.append(jqps.query);
		
		sb.append("\n");
		
		// Append group bys (if any)
		if(!groupBys.isEmpty()) {
			sb.append(groupBys.stream().collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// Append order
		if(!orders.isEmpty()) {
			sb.append(orders.stream().collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// Append limit & offset
		if(limit != null) {
			sb.append("LIMIT "+limit+"\n");
		}
		if(offset != null) {
			sb.append("OFFSET "+ offset +"\n");
		}
		
		jqps.query = sb.toString();
		
		return jqps;
	}
	
	
	// nested search of the different classes used in criterions and to be added in joins
	public Set<Entity> digClasses(JoinAggregator aggregator){
		
		Set<Entity> classes = new HashSet<>();
		for(JoinNode node : aggregator.criterions) {
			if(node instanceof JoinCriterion) {
				classes.add(((JoinCriterion) node).entity);
			}
			else if(node instanceof JoinAggregator) {
				classes.addAll(digClasses((JoinAggregator) node));
			}
			
		}
		
		return classes;
		
	}
	
	
	// purpose : build join tree out of entities nodes and join branches
	public List<JoinQueryClassJoin> buildJoins() {

		// list all classes needed for query
		Set<Entity> targetEntities = new HashSet<>(digClasses(this));
		targetEntities.addAll(manuallyJoinedClasses);
		targetEntities.addAll(manualJoins.stream().map(x -> x.internalClass).collect(Collectors.toSet()));
		

		// initiate joinedQueryClasses list
		Set<Entity> joinedEntities = new HashSet<>(List.of(new Entity(baseClass)));
		
		// now build classJoins
		List<JoinQueryClassJoin> classJoins = new ArrayList<>();
		
		while(true) { 
			JoinQueryClassJoin cj = tryAddingJoinedClass(joinedEntities, targetEntities);
			if(cj != null) {
				joinedEntities.add(cj.externalClass);
				classJoins.add(cj);
			}
			else {
				break;
			}
		}

		// check no missing class from queryClasses in joinedQueryClasses
		if(targetEntities.size() != joinedEntities.size()) {
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
	private JoinQueryClassJoin tryAddingJoinedClass(Set<Entity> joined, Set<Entity> candidates) {
		for(Entity candidate : candidates) {
			// if already joined, skip
			if(joined.contains(candidate)) {
				continue;
			}
			
			// try to join, if not successful throw exc
			for(Entity joinedClass : joined) {
				
				// try joining through manual joins
				JoinQueryClassJoin manualJoin = manualJoins.stream()
						.filter(x -> x.internalClass.equals(joinedClass) && x.externalClass.equals(candidate))
						.findFirst().orElse(null);
				
				if(manualJoin != null) {
					return manualJoin;
				}
				
				JoinQueryClassJoin cj = JoinQueryClassJoin.find(joinedClass, candidate);
				if(cj != null) {
					return cj;
				}
			}
		}
		
		return null;
	}
	
	
	
	public JoinQuery join(Entity c) { manuallyJoinedClasses.add(c); return this; }
	public JoinQuery join(Class c) { return join(new Entity(c)); }

	public JoinQuery join(JoinQueryClassJoin classJoin) { manualJoins.add(classJoin); return this; }
	
	public JoinQuery orderAsc(Entity c, String attribute) { orders.add(c.getName()+"."+ nameMapper.mapAttribute(attribute) + " ASC"); return this; }
	public JoinQuery orderAsc(Class c, String attribute) { return orderAsc(new Entity(c), attribute); }
	
	
	public JoinQuery orderDesc(Entity c, String attribute) { orders.add(c.getName()+"."+ nameMapper.mapAttribute(attribute) + " DESC"); return this; }
	public JoinQuery orderDesc(Class c, String attribute) { return orderDesc(new Entity(c), attribute); }
	
	public JoinQuery limit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public JoinQuery offset(Integer offset) {
		this.offset = offset;
		return this;
	}
	
	
	public JoinQuery select(Class c, String...fields) { return select(new Entity(c), fields); }
	
	public JoinQuery select(Entity c, String...args) {
		
		Class<?> currentClazz = c.entity;
		while(currentClazz != Object.class) {
			
			String[] fields = args.length == 0 ?
					Arrays.stream(currentClazz.getDeclaredFields()).map( x -> x.getName() ).toArray(String[]::new) : args;
			
			for(String field : fields){
				select(c, field);
			}
			currentClazz = currentClazz.getSuperclass();
		}
		return this;
	}
	
	public JoinQuery select(Class c, String attribute) { return select(new Entity(c), attribute); }
	
	public JoinQuery select(Entity c, String attribute) {
		selects.add(c.getName() + "." + nameMapper.mapAttribute(attribute)+" as `"+c.getName()+"."+attribute+"`");
		return this;
	}
	
	public JoinQuery selectCount() {
		selects.add("COUNT(*)");
		return this;
	}
	
	public JoinQuery selectCount(Class c, String attribute) {
		selects.add("COUNT(distinct "+nameMapper.mapEntityAttribute(c, attribute)+")");
		return this;
	}
	
	public JoinQuery clone() {
		JoinQuery joinQuery = new JoinQuery(baseClass);
		joinQuery.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		joinQuery.selects = new LinkedHashSet<>(this.selects);
		
		joinQuery.limit = this.limit;
		joinQuery.offset = this.offset;
		
		return joinQuery;
	}

	public JoinQuery groupBy(Class c, String attribute) {
		
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
			return JoinQuery.nameMapper.mapEntity(entity);
		}
	}


}
