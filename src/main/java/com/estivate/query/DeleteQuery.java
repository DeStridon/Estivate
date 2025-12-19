package com.estivate.query;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.context.Context;

public class DeleteQuery<E> extends Query<DeleteQuery<E>, E> {


    public DeleteQuery(Class<E> baseClass) {
    	super(baseClass);
    }

    public DeleteQuery(Entity<E> entity) {
    	super(entity);
    }




	@SuppressWarnings("unchecked")
	public DeleteQuery<E> clone() {
		DeleteQuery<E> queryClone = new DeleteQuery<E>(entity);
		
		queryClone.comments = new ArrayList<>(this.comments);
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
