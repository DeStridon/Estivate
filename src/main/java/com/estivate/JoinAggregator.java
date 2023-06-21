package com.estivate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.JoinCriterion.NullCheck;
import com.estivate.JoinCriterion.Operator;
import com.estivate.JoinCriterion.Operator.CriterionType;

public class JoinAggregator implements JoinNode {
	
	final GroupType groupType;
	
	List<JoinNode> criterions = new ArrayList<>();
	
	public static enum GroupType{AND, OR}
	
	public JoinAggregator(GroupType groupType) {
		this.groupType = groupType;
	}
	
	
	public JoinAggregator eq    (Class entity, String attribute, Object value)        { return eq(new Entity(entity), attribute, value); }
	public JoinAggregator notEq (Class entity, String attribute, Object value)        { return notEq(new Entity(entity), attribute, value); }
	public JoinAggregator lt    (Class entity, String attribute, Object value)        { return lt(new Entity(entity), attribute, value); }
	public JoinAggregator gt    (Class entity, String attribute, Object value)        { return gt(new Entity(entity), attribute, value); }
	public JoinAggregator lte   (Class entity, String attribute, Object value)        { return lte(new Entity(entity), attribute, value); }
	public JoinAggregator gte   (Class entity, String attribute, Object value)        { return gte(new Entity(entity), attribute, value); }
	public JoinAggregator in    (Class entity, String attribute, Object... values)    { return in(new Entity(entity), attribute, values); }
	public JoinAggregator in    (Class entity, String attribute, Collection<Object> values) { return in(new Entity(entity), attribute, values); }
	public JoinAggregator like	(Class entity, String attribute, String value)        { return like(new Entity(entity), attribute, value); }
	
	public JoinAggregator eqIfNotNull    (Class entity, String attribute, Object value)        { return eqIfNotNull(new Entity(entity), attribute, value); }
	public JoinAggregator ltIfNotNull    (Class entity, String attribute, Object value)        { return ltIfNotNull(new Entity(entity), attribute, value); }
	public JoinAggregator gtIfNotNull    (Class entity, String attribute, Object value)        { return gtIfNotNull(new Entity(entity), attribute, value); }
	public JoinAggregator lteIfNotNull   (Class entity, String attribute, Object value)        { return lteIfNotNull(new Entity(entity), attribute, value); }
	public JoinAggregator gteIfNotNull   (Class entity, String attribute, Object value)        { return gteIfNotNull(new Entity(entity), attribute, value); }
	public JoinAggregator inIfNotNull    (Class entity, String attribute, Object... values)    { return inIfNotNull(new Entity(entity), attribute, values); }
	public JoinAggregator inIfNotNull    (Class entity, String attribute, List<Object> values) { return inIfNotNull(new Entity(entity), attribute, values); }
	public JoinAggregator likeIfNotNull	(Class entity, String attribute, String value)        { return likeIfNotNull(new Entity(entity), attribute, value); }
	
	
	public JoinAggregator isNotNull(Class entity, String attribute) { return isNotNull(new Entity(entity), attribute); }
	public JoinAggregator isNull(Class entity, String attribute) { return isNull(new Entity(entity), attribute); }

	public JoinAggregator eqOrNull(Class entity, String attribute, Object value) { return eqOrNull(new Entity(entity), attribute, value); }
	public JoinAggregator lteOrNull(Class entity, String attribute, Object value) { return lteOrNull(new Entity(entity), attribute, value); }

	
	public JoinAggregator eq    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Eq,     value)); return this; }
	public JoinAggregator notEq (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.NotEq,  value)); return this; }
	public JoinAggregator lt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lt,     value)); return this; }
	public JoinAggregator gt    (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gt,     value)); return this; }
	public JoinAggregator lte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Lte,    value)); return this; }
	public JoinAggregator gte   (Entity entity, String attribute, Object value)     { criterions.add(new Operator(entity, attribute, CriterionType.Gte,    value)); return this; }
	public JoinAggregator in    (Entity entity, String attribute, Object... values) { criterions.add(new JoinCriterion.In(entity, attribute, List.of(values))); return this; }
	public JoinAggregator in    (Entity entity, String attribute, Collection<Object> values) { criterions.add(new JoinCriterion.In(entity, attribute, values)); return this; }
	public JoinAggregator like	(Entity entity, String attribute, String value)     { criterions.add(new Operator(entity, attribute, CriterionType.Like,   value)); return this; }
	
	
	public JoinAggregator eqIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Eq,  value));} return this; }
	public JoinAggregator ltIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lt,  value));} return this; }
	public JoinAggregator gtIfNotNull   (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gt,  value));} return this; }
	public JoinAggregator lteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Lte, value));} return this; }
	public JoinAggregator gteIfNotNull  (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Gte, value));} return this; }
	public JoinAggregator inIfNotNull   (Entity entity, String attribute, Object... values) {	if(values != null) {criterions.add(new JoinCriterion.In(entity, attribute, List.of(values)));} return this; }
	public JoinAggregator inIfNotNull   (Entity entity, String attribute, List<Object> values) {	if(values != null) {criterions.add(new JoinCriterion.In(entity, attribute, values));} return this; }
	public JoinAggregator likeIfNotNull (Entity entity, String attribute, Object value) { if(value != null) {criterions.add(new Operator(entity, attribute, CriterionType.Like, value));} return this; }
	
	public JoinAggregator isNotNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, false)); return this; }
	public JoinAggregator isNull(Entity entity, String attribute) { criterions.add(new NullCheck(entity, attribute, true)); return this; }

	public JoinAggregator eqOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new JoinAggregator(GroupType.OR)
				.eq(entity, attribute, value)
				.isNull(entity, attribute));
		return this;
	}
	
	public JoinAggregator lteOrNull(Entity entity, String attribute, Object value) {
		criterions.add(new JoinAggregator(GroupType.OR)
					.lte(entity, attribute, value)
					.isNull(entity, attribute));
		return this;
	}
	
	public JoinAggregator add(JoinNode joinNode) {
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




	public JoinAggregator clone() {
		JoinAggregator joinAggregator = new JoinAggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).toList();
		return joinAggregator;
	}


	public JoinQueryPreparedStatement preparedStatement() {
		List<JoinQueryPreparedStatement> criterionsStatements = criterions.stream().map(x -> x.preparedStatement()).collect(Collectors.toList());
		
		if(criterions.size() == 1) {
			return criterionsStatements.get(0);
		}
		
		return JoinQueryPreparedStatement.mergeInOne(criterionsStatements, "(", ")", " "+groupType+" ");
	}

	


	





	
	

}
