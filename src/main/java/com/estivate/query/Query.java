package com.estivate.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.estivate.Entity;
import com.estivate.query.SelectQuery.Order;

import lombok.Getter;

public abstract class Query<T> extends Aggregator {
	
	@Getter
	final Entity<T> entity;
	
	public Query(Class<T> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<T>(baseClass);
	}
	
	public Query(Entity<T> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}
	
	
	@Getter
	List<String> comments = new ArrayList<>();

	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	@Getter
	Set<Join> joins = new LinkedHashSet<>();

	
	@Getter
	List<Order> orders = new ArrayList<>();

	
	@Getter
	Integer offset;

	@Getter
	Integer limit;
	
	
	public abstract Query<T> clone();


}
