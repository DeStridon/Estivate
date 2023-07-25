package com.estivate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;


public abstract class CachedEntity {
	
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
			Object object = field.get(this);
			if(__cache.containsKey(field.getName()) && __cache.get(field.getName()).equals(hashCode(object))) {
				continue;
			}
			fields.add(field);
		}
		
		return fields;
		
	}
	
	private Integer hashCode(Object object) {
		if(object == null) {
			return null;
		}
		return object.hashCode();
	}
	
}