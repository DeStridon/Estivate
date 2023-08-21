package com.estivate;

import com.estivate.EstivateAggregator.GroupType;
import com.estivate.EstivateCriterion.Operator;
import com.estivate.EstivateCriterion.Operator.CriterionType;
import com.estivate.EstivateQuery.Entity;

public interface EstivateNode {
	
	public String compile();
	
	public static Operator eq (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Eq,  value); }
	public static Operator lt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lt,  value); }
	public static Operator gt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gt,  value); }
	public static Operator lte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lte, value); }
	public static Operator gte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gte, value); }

	public static EstivateAggregator or(EstivateNode... criterions) {
		EstivateAggregator aggregator = new EstivateAggregator(GroupType.OR);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}
	
	public static EstivateAggregator add(EstivateNode... criterions) {
		EstivateAggregator aggregator = new EstivateAggregator(GroupType.AND);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}

	public EstivateNode clone();

//	public EstivateStatement_old preparedStatement();

}
