package com.estivate.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.estivate.util.FieldUtils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public abstract class CachedEntity {
	
	@Transient
	final Map<String, Integer> __cache = new HashMap<>();
	
	@SneakyThrows
	@PostLoad
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
	
	public Boolean isFieldUpdated(String fieldName) {
		
		Field field = FieldUtils.getEntityFields(this.getClass()).stream().filter(x -> fieldName.equals(x.getName())).findFirst().orElse(null);
		
		if(field == null) {
			return null;
		}
		
		try {
			field.setAccessible(true);
			Object object = field.get(this);
			return !(__cache.containsKey(field.getName()) && equals(__cache.get(field.getName()), hashCode(object)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	}
	
	@SneakyThrows
	public Boolean isAnyFieldUpdated() {
		for(Field field : FieldUtils.getEntityFields(this.getClass())) {
			field.setAccessible(true);
			Object object = field.get(this);
			if(__cache.containsKey(field.getName()) && equals(__cache.get(field.getName()), hashCode(object))) {
				continue;
			}
			return true;
		}
		return false;
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