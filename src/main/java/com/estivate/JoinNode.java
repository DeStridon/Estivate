package com.estivate;

import com.wezen.framework.orm.joinQuery.JoinAggregator.GroupType;
import com.wezen.framework.orm.joinQuery.JoinCriterion.Operator;
import com.wezen.framework.orm.joinQuery.JoinCriterion.Operator.CriterionType;
import com.wezen.framework.orm.joinQuery.JoinQuery.Entity;

public interface JoinNode {
	
	public String compile();
	
	public static Operator eq (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Eq,  value); }
	public static Operator lt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lt,  value); }
	public static Operator gt (Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gt,  value); }
	public static Operator lte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Lte, value); }
	public static Operator gte(Entity entity, String attribute, Object value) { return new Operator(entity, attribute, CriterionType.Gte, value); }

	public static JoinAggregator or(JoinNode... criterions) {
		JoinAggregator aggregator = new JoinAggregator(GroupType.OR);
		for(JoinNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}
	
	public static JoinAggregator add(JoinNode... criterions) {
		JoinAggregator aggregator = new JoinAggregator(GroupType.AND);
		for(JoinNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}

	public JoinNode clone();

	public JoinQueryPreparedStatement preparedStatement();

}
