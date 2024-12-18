package com.estivate.query;

import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.Query.Entity;

public interface EstivateNode {
	
	public static Operator eq (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Eq,  value); }
	public static Operator lt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Lt,  value); }
	public static Operator gt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Gt,  value); }
	public static Operator lte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Lte, value); }
	public static Operator gte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, OperatorType.Gte, value); }

	public static Aggregator or(EstivateNode... criterions) {
		Aggregator aggregator = new Aggregator(GroupType.OR);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}
	
	public static Aggregator add(EstivateNode... criterions) {
		Aggregator aggregator = new Aggregator(GroupType.AND);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}

	public EstivateNode clone();

}
