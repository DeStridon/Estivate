package com.estivate.query;

import java.util.List;

import com.estivate.query.Aggregator.GroupType;

public interface EstivateNode {
	
	

	
	
	public static Aggregator or(EstivateNode... criterions) {
		Aggregator aggregator = new Aggregator(GroupType.OR);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}
	
	public static Aggregator or(List<EstivateNode> criterions) {
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

	public static Aggregator and(List<EstivateNode> criterions) {
		Aggregator aggregator = new Aggregator(GroupType.AND);
		for(EstivateNode criterion : criterions) {
			aggregator.criterions.add(criterion);
		}
		return aggregator;
	}

	public EstivateNode clone();

}
