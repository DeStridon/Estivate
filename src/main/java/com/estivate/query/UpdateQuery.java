package com.estivate.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.context.Context;

import lombok.Getter;

public class UpdateQuery<T> extends Query<UpdateQuery<T>, T> {

    @Getter
    LinkedHashMap<Attribute, Object> updates = new LinkedHashMap<>();

    public UpdateQuery(Class<T> baseClass) {
    	super(baseClass);
    }

    public UpdateQuery(Entity<T> entity) {
        super(entity);
    }

    public UpdateQuery<T> set(Attribute attribute, Object value) {
        updates.put(attribute, value);
        return this;
    }

    public UpdateQuery<T> set(String attribute, Object value) {
        return set(new Attribute(entity, attribute, null), value);
    }

	@SuppressWarnings("unchecked")
	public UpdateQuery<T> clone() {
		UpdateQuery<T> queryClone = new UpdateQuery<T>(entity);

		queryClone.comments = new ArrayList<>(this.comments);
		
        queryClone.updates = new LinkedHashMap<>(this.updates);
		
		queryClone.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		queryClone.joins = this.joins.stream().map(x -> x.clone()).collect(Collectors.toSet());
		queryClone.limit = this.limit;
		queryClone.offset = this.offset;
		
		return queryClone;
	}
	
	public Boolean execute(Context context) {
		return context.execute(this);
	}


}
