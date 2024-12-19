package com.estivate.query;

import com.estivate.query.Aggregator.GroupType;
import com.estivate.query.Criterion.NullCheck;
import com.estivate.query.Criterion.Operator;
import com.estivate.query.Criterion.Operator.OperatorType;
import com.estivate.query.Query.Entity;

public interface EstivateNode {
	
	

	
	
	public static Aggregator or(EstivateNode... criterions) {
		Aggregator aggregator = new Aggregator(GroupType.OR);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}
	
	public static Aggregator and(EstivateNode... criterions) {
		Aggregator aggregator = new Aggregator(GroupType.AND);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}

	public EstivateNode clone();

}
