package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

public class OrderLimitTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	void orderLimitTest(){
		
		context.truncateTable(TaskEntity.class);

		List<TaskEntity> list = Arrays.asList(
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask())
		);
		
		
		Query projectIdAscOrderedTaskQuery = new Query(TaskEntity.class).orderAsc(TaskEntity.class, TaskEntity.Fields.projectId).limit(2);
		List<TaskEntity> projectIdAscOrderedTasks = context.listAs(projectIdAscOrderedTaskQuery, TaskEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getProjectId()).min().orElse(0), projectIdAscOrderedTasks.get(0).getProjectId());
		Assert.assertEquals(2, projectIdAscOrderedTasks.size());
		
		Query idDescOrderedTaskQuery = new Query(TaskEntity.class).orderDesc(TaskEntity.class, AbstractEntity.Fields.id);
		List<TaskEntity> idDescOrderedTasks = context.listAs(idDescOrderedTaskQuery, TaskEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getId()).max().orElse(0), idDescOrderedTasks.get(0).getId());
		
	}
	
	
}
