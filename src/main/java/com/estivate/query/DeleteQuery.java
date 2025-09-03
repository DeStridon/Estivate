package com.estivate.query;

import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.context.Context;

public class DeleteQuery<T> extends Query<DeleteQuery<T>, T> {


    public DeleteQuery(Class<T> baseClass) {
    	super(baseClass);
    }

    public DeleteQuery(Entity<T> entity) {
    	super(entity);
    }




	@SuppressWarnings("unchecked")
	public DeleteQuery<T> clone() {
		DeleteQuery<T> queryClone = new DeleteQuery<T>(entity);
		
		queryClone.criterions = this.criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		
		return queryClone;
	}
	
	public Boolean execute(Context context) {
		return context.execute(this);
	}

}
