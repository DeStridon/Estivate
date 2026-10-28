package com.estivate.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public abstract class CachedEntity {
	
	@javax.persistence.Transient
	@jakarta.persistence.Transient
	final Map<Field, Integer> __cache = new IdentityHashMap<>();
	
	@SneakyThrows
	@javax.persistence.PostLoad
	@jakarta.persistence.PostLoad
	public void saveState() {
		__cache.clear();
		for(Field field : ClassMeta.of(this.getClass()).getFields()) {
			__cache.put(field, hashCode(field.get(this)));
		}
	}
	
	@SneakyThrows
	public boolean isUpdated(Field field) {
		if(!__cache.containsKey(field)) {
			return true;
		}
		return !equals(__cache.get(field), hashCode(field.get(this)));
	}

	@SneakyThrows
	public Set<Field> updatedFields(){
		Set<Field> fields = new HashSet<>();
		for(Field field : ClassMeta.of(this.getClass()).getFields()) {
			if(isUpdated(field)) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	public Boolean isFieldUpdated(String fieldName) {
		Field field = ClassMeta.of(this.getClass()).findField(fieldName);
		if(field == null) {
			return null;
		}
		return isUpdated(field);
	}
	
	public Boolean isAnyFieldUpdated() {
		for(Field field : ClassMeta.of(this.getClass()).getFields()) {
			if(isUpdated(field)) {
				return true;
			}
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
