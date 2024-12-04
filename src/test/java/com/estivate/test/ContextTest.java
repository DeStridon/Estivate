package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

public class ContextTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void contextTest() {
		
		context.truncateTable(TaskEntity.class);
		
		TaskEntity task1 = context.saveOrUpdate(DatabaseGenerator.createRandomTask());
		TaskEntity task2 = context.saveOrUpdate(DatabaseGenerator.createRandomTask());
		TaskEntity task3 = context.saveOrUpdate(DatabaseGenerator.createRandomTask());

		task1.setName("Updated Name 1");
		task2.setName("Updated Name 2");
		task3.setName("Updated Name 3");
		
		context.updateAll(Arrays.asList(task1, task2, task3));
		
		
		Query query = new Query(TaskEntity.class).in(TaskEntity.class, AbstractEntity.Fields.id, Arrays.asList(task1.getId(), task2.getId(), task3.getId()));
		List<TaskEntity> resultQueries = context.listAs(query, TaskEntity.class);
		
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 1")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 2")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 3")));
		
		
	}
	
	@Test
	public void queryAliasTest() {
		
		Entity<TaskEntity> taskEntity = new Entity(TaskEntity.class, "myTask");
		
		Query query = new Query(taskEntity).in(taskEntity, AbstractEntity.Fields.id, Arrays.asList(1,2,3));
		List<TaskEntity> resultQueries = context.listAs(query, TaskEntity.class);
		
	}
	
	
}
