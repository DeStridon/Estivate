package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

public class ContextTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void contextTest() {
		
		context.truncateTable(ParentEntity.class);
		
		ParentEntity parent1 = context.updateOrInsert(DatabaseGenerator.createRandomParent());
		ParentEntity parent2 = context.updateOrInsert(DatabaseGenerator.createRandomParent());
		ParentEntity parent3 = context.updateOrInsert(DatabaseGenerator.createRandomParent());

		parent1.setName("Updated Name 1");
		parent2.setName("Updated Name 2");
		parent3.setName("Updated Name 3");
		
		context.updateAll(Arrays.asList(parent1, parent2, parent3));
		
		
		Query query = new Query(ParentEntity.class).in(ParentEntity.class, AbstractEntity.Fields.id, Arrays.asList(parent1.getId(), parent2.getId(), parent3.getId()));
		List<ParentEntity> resultQueries = context.fetchListAs(query, ParentEntity.class);
		
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 1")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 2")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 3")));
		
		
	}
	
	@Test
	public void queryAliasTest() {
		
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class, "myTask");
		
		Query query = new Query(parentEntity).in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1,2,3));
		List<ParentEntity> resultQueries = context.fetchListAs(query, ParentEntity.class);
		
	}


	@Test
	public void mergeTest() {
		
		ParentEntity parent1 = ParentEntity.builder().homeId(1).name("parent1").build();
		context.updateOrInsert(parent1);
		
		ParentEntity parent2 = ParentEntity.builder().homeId(1).name("parent1").build();
		context.merge(parent2);

		Assert.assertEquals(parent2.getId(), parent1.getId());
	
	}
	

	@Test
	public void mergeTest2() {
		ParentEntity parent1 = ParentEntity.builder().homeId(1).name("parent1").build();
		context.updateOrInsert(parent1);
		
		ParentEntity parent2 = ParentEntity.builder().homeId(1).name("parent2").build();
		parent2.setId(parent1.getId());
		context.merge(parent2);

		Assert.assertEquals(parent2.getName(), "parent1");

	}

	@Test
	public void rawSingleFetchTest() {
		ParentEntity parent1 = ParentEntity.builder().homeId(1).name("parent1-1").build();
		ParentEntity parent2 = ParentEntity.builder().homeId(1).name("parent2-1").build();
		context.updateOrInsert(parent1);
		context.updateOrInsert(parent2);
		

		Query query = Estivate.query(ParentEntity.class)
			.selectCount();

		Long count = context.fetchSingleAs(query, Long.class);
		Assert.assertNotNull(count);
		Assert.assertTrue(count > 0);
	}

//	@Test
//	public void rawListFetchTest() {
//		Query query = Estivate.query(ParentEntity.class)
//			.selectDistinct(ParentEntity.Fields.name);
//
//		List<Long> count = context.fetchListAs(query, Long.class);
//		Assert.assertNotNull(count);
//		Assert.assertTrue(count.size() > 0);
//	}
}
