package com.estivate.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.Getter;

public class UpdateQuery<E> extends Query<UpdateQuery<E>, E> {

    @Getter
    LinkedHashMap<Attribute, Object> updates = new LinkedHashMap<>();

    public UpdateQuery(Class<E> baseClass) {
    	super(baseClass);
    }

    public UpdateQuery(Entity<E> entity) {
        super(entity);
    }

    public UpdateQuery<E> set(Attribute attribute, Object value) {
        updates.put(attribute, value);
        return this;
    }

    public UpdateQuery<E> set(String attribute, Object value) {
        return set(Estivate.attribute(entity, attribute), value);
    }

    public <T, P> UpdateQuery<E> set(AttributeGetter<T, P> getter, P value) {
        return set(Estivate.attribute(getter), value);
    }

	@SuppressWarnings("unchecked")
	public UpdateQuery<E> clone() {
		UpdateQuery<E> queryClone = new UpdateQuery<E>(entity);

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
