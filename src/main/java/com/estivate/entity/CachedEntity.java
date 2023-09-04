package com.estivate.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import lombok.SneakyThrows;


public abstract class CachedEntity {
	
	@Transient
	Map<String, Integer> __cache = new HashMap<>();
	
	@SneakyThrows
	public void saveState() {
		
		for(Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object object = field.get(this);
			__cache.put(field.getName(), hashCode(object));
		}
		
	}
	
	@SneakyThrows
	public List<Field> updatedFields(){
		
		List<Field> fields = new ArrayList<>();
		
		for(Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object object = field.get(this);
			if(__cache.containsKey(field.getName()) && equals(__cache.get(field.getName()), hashCode(object))) {
				continue;
			}
			fields.add(field);
		}
		
		return fields;
		
	}
	
	public boolean equals(Integer hashLeft, Integer hashRight) {
		if(hashLeft == null && hashRight == null) {
			return true;
		}
		
		if(hashLeft == null || hashRight == null) {
			return false;
		}
		
		return hashLeft.equals(hashRight);
		
	}
	
	private Integer hashCode(Object object) {
		if(object == null) {
			return null;
		}
		return object.hashCode();
	}
	
}