package com.estivate.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Mapper;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

public class MapperTest {
	
	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testPerf() {
		
		TaskEntity newTask = context.saveOrUpdate(TaskEntity.builder().name("task 1 name").build());	
		
		Query query = new Query(TaskEntity.class)
				.eq(TaskEntity.class, AbstractEntity.Fields.id, newTask.getId());
				
		List<TaskEntity> tasks = context.listAs(query, TaskEntity.class);
		
		Assert.assertEquals(1, tasks.size());
		
		System.out.println(tasks.get(0).getCreated());
		
		Mapper mapper = new Mapper(TaskEntity.class);
		
		//ResultSetMetaData rsmd
		
	}

}
