package com.estivate.query;

import com.estivate.query.EstivateQuery.Entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EstivateField {
	public Entity entity;
	public String attributeName;
	
	public EstivateField(Class entityClass, String attributeName) {
		this(new Entity(entityClass), attributeName);
	}
	
	public String toString() {
		return entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(attributeName);
	}
}
