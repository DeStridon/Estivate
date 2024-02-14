package com.estivate.query;

import com.estivate.query.Query.Entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyValue {
	public Entity entity;
	public String attributeName;
	
	public PropertyValue(Class entityClass, String attributeName) {
		this(new Entity(entityClass), attributeName);
	}
	
	public String toString() {
		return Query.nameMapper.mapEntity(entity, attributeName);
	}
}
