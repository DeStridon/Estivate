package com.estivate.query;

import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.CriterionType;
import com.estivate.query.Query.Entity;

public interface EstivateNode {
	
	//public String compile();
	
	public static Operator eq (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Eq,  value); }
	public static Operator lt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lt,  value); }
	public static Operator gt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gt,  value); }
	public static Operator lte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lte, value); }
	public static Operator gte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gte, value); }

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

//	public EstivateStatement_old preparedStatement();

}
