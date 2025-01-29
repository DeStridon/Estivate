package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

public class QueryTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	void orderLimitTest(){
		
		context.truncateTable(ParentEntity.class);

		List<ParentEntity> list = Arrays.asList(
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask()),
			context.saveOrUpdate(DatabaseGenerator.createRandomTask())
		);
		
		
		Query projectIdAscOrderedTaskQuery = new Query(ParentEntity.class).orderAsc(ParentEntity.class, ParentEntity.Fields.homeId).limit(2);
		List<ParentEntity> projectIdAscOrderedTasks = context.fetchListAs(projectIdAscOrderedTaskQuery, ParentEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getHomeId()).min().orElse(0), projectIdAscOrderedTasks.get(0).getHomeId());
		Assert.assertEquals(2, projectIdAscOrderedTasks.size());
		
		Query idDescOrderedTaskQuery = new Query(ParentEntity.class).orderDesc(ParentEntity.class, AbstractEntity.Fields.id);
		List<ParentEntity> idDescOrderedTasks = context.fetchListAs(idDescOrderedTaskQuery, ParentEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getId()).max().orElse(0), idDescOrderedTasks.get(0).getId());
		
	}
	
	
}
