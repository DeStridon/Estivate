package com.estivate.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Mapper;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

public class MapperTest {
	
	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testPerf() {
		
		ParentEntity newTask = context.updateOrInsert(ParentEntity.builder().name("task 1 name").build());	
		
		Query query = new Query(ParentEntity.class)
				.eq(ParentEntity.class, AbstractEntity.Fields.id, newTask.getId());
				
		List<ParentEntity> tasks = context.fetchListAs(query, ParentEntity.class);
		
		Assert.assertEquals(1, tasks.size());
		
		System.out.println(tasks.get(0).getCreated());
		
		Mapper mapper = new Mapper<>(ParentEntity.class, context);
		
		//ResultSetMetaData rsmd
		
	}

}
