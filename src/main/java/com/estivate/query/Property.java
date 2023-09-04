package com.estivate.query;

import com.estivate.query.Query.Entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Property {
	public Entity entity;
	public String attributeName;
	
	public Property(Class entityClass, String attributeName) {
		this(new Entity(entityClass), attributeName);
	}
	
	public String toString() {
		return entity.getName() + "." + Query.nameMapper.mapAttribute(attributeName);
	}
}
