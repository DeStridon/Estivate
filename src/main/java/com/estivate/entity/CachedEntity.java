package com.estivate.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import com.estivate.util.FieldUtils;

import lombok.SneakyThrows;


public abstract class CachedEntity {
	
	@Transient
	Map<String, Integer> __cache = new HashMap<>();
	
	@SneakyThrows
	public void saveState() {
		
		for(Field field : FieldUtils.getEntityFields(this.getClass())) {
			field.setAccessible(true);
			Object object = field.get(this);
			__cache.put(field.getName(), hashCode(object));
		}
		
	}
	
	@SneakyThrows
	public Set<Field> updatedFields(){
		
		Set<Field> fields = new HashSet<>();
		
		for(Field field : FieldUtils.getEntityFields(this.getClass())) {
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