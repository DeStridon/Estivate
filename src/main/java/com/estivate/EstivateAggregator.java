package com.estivate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.EstivateCriterion.NullCheck;
import com.estivate.EstivateCriterion.Operator;
import com.estivate.EstivateCriterion.Operator.CriterionType;
import com.estivate.EstivateQuery.Entity;

public class EstivateAggregator implements EstivateNode {
	
	final GroupType groupType;
	
	List<EstivateNode> criterions = new ArrayList<>();
	
	public static enum GroupType{AND, OR}
	
	public EstivateAggregator(GroupType groupType) {
		this.groupType = groupType;
	}
	
	
	public EstivateAggregator eq    (Class entity, String attribute, Object value)        { return eq(new Entity(entity), attribute, value); }
	public EstivateAggregator notEq (Class entity, String attribute, Object value)        { return notEq(new Entity(entity), attribute, value); }
	public EstivateAggregator lt    (Class entity, String attribute, Object value)        { return lt(new Entity(entity), attribute, value); }
	public EstivateAggregator gt    (Class entity, String attribute, Object value)        { return gt(new Entity(entity), attribute, value); }
	public EstivateAggregator lte   (Class entity, String attribute, Object value)        { return lte(new Entity(entity), attribute, value); }
	public EstivateAggregator gte   (Class entity, String attribute, Object value)        { return gte(new Entity(entity), attribute, value); }
	public EstivateAggregator in    (Class entity, String attribute, Object... values)    { return in(new Entity(entity), attribute, values); }
	public EstivateAggregator in    (Class entity, String attribute, Collection<Object> values) { return in(new Entity(entity), attribute, values); }
	public EstivateAggregator like	(Class entity, String attribute, String value)        { return like(new Entity(entity), attribute, value); }
	
	public EstivateAggregator eqIfNotNull    (Class entity, String attribute, Object value)        { return eqIfNotNull(new Entity(entity), attribute, value); }
	public EstivateAggregator ltIfNotNull    (Class entity, String attribute, Object value)        { return ltIfNotNull(new Entity(entity), attribute, value); }
	public EstivateAggregator gtIfNotNull    (Class entity, String attribute, Object value)        { return gtIfNotNull(new Entity(entity), attribute, value); }
	public EstivateAggregator lteIfNotNull   (Class entity, String attribute, Object value)        { return lteIfNotNull(new Entity(entity), attribute, value); }
	public EstivateAggregator gteIfNotNull   (Class entity, String attribute, Object value)        { return gteIfNotNull(new Entity(entity), attribute, value); }
	public EstivateAggregator inIfNotNull    (Class entity, String attribute, Object... values)    { return inIfNotNull(new Entity(entity), attribute, values); }
	public EstivateAggregator inIfNotNull    (Class entity, String attribute, List<Object> values) { return inIfNotNull(new Entity(entity), attribute, values); }
	public EstivateAggregator likeIfNotNull	(Class entity, String attribute, String value)        { return likeIfNotNull(new Entity(entity), attribute, value); }
	
	
	public EstivateAggregator isNotNull(Class entity, String attribute) { return isNotNull(new Entity(entity), attribute); }
	public EstivateAggregator isNull(Class entity, String attribute) { return isNull(new Entity(entity), attribute); }

	public EstivateAggregator eqOrNull(Class entity, String attribute, Object value) { return eqOrNull(new Entity(entity), attribute, value); }
	public EstivateAggregator lteOrNull(Class entity, String attribute, Object value) { return lteOrNull(new Entity(entity), attribute, value); }

	
	public EstivateAggregator eq    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Eq,     value)); return this; }
	public EstivateAggregator notEq (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.NotEq,  value)); return this; }
	public EstivateAggregator lt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lt,     value)); return this; }
	public EstivateAggregator gt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gt,     value)); return this; }
	public EstivateAggregator lte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lte,    value)); return this; }
	public EstivateAggregator gte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gte,    value)); return this; }
	public EstivateAggregator in    (Entity entity, String attribute, Object... values) { criterions.add(new EstivateCriterion.In(entity, attribute, List.of(values))); return this; }
	public EstivateAggregator in    (Entity entity, String attribute, Collection<Object> values) { criterions.add(new EstivateCriterion.In(entity, attribute, values)); return this; }
	public EstivateAggregator like	(Entity entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, CriterionType.Like,   value)); return this; }
	
	
	public EstivateAggregator eqIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Eq,  value));} return this; }
	public EstivateAggregator ltIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lt,  value));} return this; }
	public EstivateAggregator gtIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gt,  value));} return this; }
	public EstivateAggregator lteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lte, value));} return this; }
	public EstivateAggregator gteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gte, value));} return this; }
	public EstivateAggregator inIfNotNull   (Entity entity, String attribute, Object... values) {	if(values != null) {criterions.add(new EstivateCriterion.In(entity, attribute, List.of(values)));} return this; }
	public EstivateAggregator inIfNotNull   (Entity entity, String attribute, List<Object> values) {	if(values != null) {criterions.add(new EstivateCriterion.In(entity, attribute, values));} return this; }
	public EstivateAggregator likeIfNotNull (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Like, value));} return this; }
	
	public EstivateAggregator isNotNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, false)); return this; }
	public EstivateAggregator isNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, true)); return this; }

	public EstivateAggregator eqOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new EstivateAggregator(GroupType.OR)
				.eq(entity, attribute, value)
				.isNull(entity, attribute));
		return this;
	}
	
	public EstivateAggregator lteOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new EstivateAggregator(GroupType.OR)
					.lte(entity, attribute, value)
					.isNull(entity, attribute));
		return this;
	}
	
	public EstivateAggregator add(EstivateNode joinNode) {
		criterions.add(joinNode);
		return this;
	}
	
	public String compile() {
		
		List<String> criterionsCompiled = criterions.stream().map(x -> x.compile()).collect(Collectors.toList());

		if(criterions.size() == 1) {
			return criterions.get(0).compile();
		}
		
		return "("+criterionsCompiled.stream().collect(Collectors.joining(" "+groupType+" "))+")";
		
	}




	public EstivateAggregator clone() {
		EstivateAggregator joinAggregator = new EstivateAggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).toList();
		return joinAggregator;
	}


//	public EstivateStatement_old preparedStatement() {
//		List<EstivateStatement_old> criterionsStatements = criterions.stream().map(x -> x.preparedStatement()).collect(Collectors.toList());
//		
//		if(criterions.size() == 1) {
//			return criterionsStatements.get(0);
//		}
//		
//		return EstivateStatement_old.mergeInOne(criterionsStatements, "(", ")", " "+groupType+" ");
//	}

	


	





	
	

}
