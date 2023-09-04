package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.CriterionType;
import com.estivate.query.Query.Entity;

import lombok.Getter;

@Getter
public class Aggregator implements EstivateNode {
	
	final GroupType groupType;
	
	List<EstivateNode> criterions = new ArrayList<>();
	
	public static enum GroupType{AND, OR}
	
	public Aggregator(GroupType groupType) {
		this.groupType = groupType;
	}
	
	
	public Aggregator eq    (Class entity, String attribute, Object value)        { return eq(new Entity(entity), attribute, value); }
	public Aggregator notEq (Class entity, String attribute, Object value)        { return notEq(new Entity(entity), attribute, value); }
	public Aggregator lt    (Class entity, String attribute, Object value)        { return lt(new Entity(entity), attribute, value); }
	public Aggregator gt    (Class entity, String attribute, Object value)        { return gt(new Entity(entity), attribute, value); }
	public Aggregator lte   (Class entity, String attribute, Object value)        { return lte(new Entity(entity), attribute, value); }
	public Aggregator gte   (Class entity, String attribute, Object value)        { return gte(new Entity(entity), attribute, value); }
	public Aggregator in    (Class entity, String attribute, Object... values)    { return in(new Entity(entity), attribute, values); }
	public Aggregator in    (Class entity, String attribute, Collection<Object> values) { return in(new Entity(entity), attribute, values); }
	public Aggregator like	(Class entity, String attribute, String value)        { return like(new Entity(entity), attribute, value); }
	
	public Aggregator eqIfNotNull    (Class entity, String attribute, Object value)        { return eqIfNotNull(new Entity(entity), attribute, value); }
	public Aggregator ltIfNotNull    (Class entity, String attribute, Object value)        { return ltIfNotNull(new Entity(entity), attribute, value); }
	public Aggregator gtIfNotNull    (Class entity, String attribute, Object value)        { return gtIfNotNull(new Entity(entity), attribute, value); }
	public Aggregator lteIfNotNull   (Class entity, String attribute, Object value)        { return lteIfNotNull(new Entity(entity), attribute, value); }
	public Aggregator gteIfNotNull   (Class entity, String attribute, Object value)        { return gteIfNotNull(new Entity(entity), attribute, value); }
	public Aggregator inIfNotNull    (Class entity, String attribute, Object... values)    { return inIfNotNull(new Entity(entity), attribute, values); }
	public Aggregator inIfNotNull    (Class entity, String attribute, List<Object> values) { return inIfNotNull(new Entity(entity), attribute, values); }
	public Aggregator likeIfNotNull	(Class entity, String attribute, String value)        { return likeIfNotNull(new Entity(entity), attribute, value); }
	
	
	public Aggregator isNotNull(Class entity, String attribute) { return isNotNull(new Entity(entity), attribute); }
	public Aggregator isNull(Class entity, String attribute) { return isNull(new Entity(entity), attribute); }

	public Aggregator eqOrNull(Class entity, String attribute, Object value) { return eqOrNull(new Entity(entity), attribute, value); }
	public Aggregator lteOrNull(Class entity, String attribute, Object value) { return lteOrNull(new Entity(entity), attribute, value); }

	
	public Aggregator eq    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Eq,     value)); return this; }
	public Aggregator notEq (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.NotEq,  value)); return this; }
	public Aggregator lt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lt,     value)); return this; }
	public Aggregator gt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gt,     value)); return this; }
	public Aggregator lte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lte,    value)); return this; }
	public Aggregator gte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gte,    value)); return this; }
	public Aggregator in    (Entity entity, String attribute, Object... values) { criterions.add(new Criterion.In(entity, attribute, List.of(values))); return this; }
	public Aggregator in    (Entity entity, String attribute, Collection<Object> values) { criterions.add(new Criterion.In(entity, attribute, values)); return this; }
	public Aggregator like	(Entity entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, CriterionType.Like,   value)); return this; }
	
	
	public Aggregator eqIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Eq,  value));} return this; }
	public Aggregator ltIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lt,  value));} return this; }
	public Aggregator gtIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gt,  value));} return this; }
	public Aggregator lteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lte, value));} return this; }
	public Aggregator gteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gte, value));} return this; }
	public Aggregator inIfNotNull   (Entity entity, String attribute, Object... values) {	if(values != null) {criterions.add(new Criterion.In(entity, attribute, List.of(values)));} return this; }
	public Aggregator inIfNotNull   (Entity entity, String attribute, List<Object> values) {	if(values != null) {criterions.add(new Criterion.In(entity, attribute, values));} return this; }
	public Aggregator likeIfNotNull (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Like, value));} return this; }
	
	public Aggregator isNotNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, false)); return this; }
	public Aggregator isNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, true)); return this; }

	public Aggregator eqOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new Aggregator(GroupType.OR)
				.eq(entity, attribute, value)
				.isNull(entity, attribute));
		return this;
	}
	
	public Aggregator lteOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new Aggregator(GroupType.OR)
					.lte(entity, attribute, value)
					.isNull(entity, attribute));
		return this;
	}
	
	public Aggregator add(EstivateNode joinNode) {
		criterions.add(joinNode);
		return this;
	}
	
//	public String compile() {
//		List<String> criterionsCompiled = criterions.stream().map(x -> x.compile()).collect(Collectors.toList());
//		if(criterions.size() == 1) {
//			return criterions.get(0).compile();
//		}
//		return "("+criterionsCompiled.stream().collect(Collectors.joining(" "+groupType+" "))+")";
//	}

	public Aggregator clone() {
		Aggregator joinAggregator = new Aggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).toList();
		return joinAggregator;
	}

}
