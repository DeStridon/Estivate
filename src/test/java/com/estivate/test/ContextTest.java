package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

public class ContextTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void contextTest() {
		
		context.truncateTable(CustomerEntity.class);
		
		CustomerEntity parent1 = context.updateOrInsert(DatabaseGenerator.createRandomCustomer());
		CustomerEntity parent2 = context.updateOrInsert(DatabaseGenerator.createRandomCustomer());
		CustomerEntity parent3 = context.updateOrInsert(DatabaseGenerator.createRandomCustomer());

		parent1.setName("Updated Name 1");
		parent2.setName("Updated Name 2");
		parent3.setName("Updated Name 3");
		
		context.update(Arrays.asList(parent1, parent2, parent3));
		
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class).in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(parent1.getId(), parent2.getId(), parent3.getId()));
		List<CustomerEntity> resultQueries = context.fetchListAs(query, CustomerEntity.class);
		
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 1")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 2")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 3")));
		
		
	}
	
	@Test
	public void queryAliasTest() {
		
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class, "myTask");
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(parentEntity).in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1,2,3));
		List<CustomerEntity> resultQueries = context.fetchList(query);
		
	}


	@Test
	public void mergeTest() {
		
		CustomerEntity parent1 = CustomerEntity.builder().id(1).name("parent1").build();
		context.updateOrInsert(parent1);
		
		CustomerEntity parent2 = CustomerEntity.builder().id(1).name("parent1").build();
		context.merge(parent2);

		Assert.assertEquals(parent2.getId(), parent1.getId());
	
	}
	

	@Test
	public void mergeTest2() {
		CustomerEntity parent1 = CustomerEntity.builder().id(1).name("parent1").build();
		context.updateOrInsert(parent1);
		
		CustomerEntity parent2 = CustomerEntity.builder().id(1).name("parent2").build();
		parent2.setId(parent1.getId());
		context.merge(parent2);

		Assert.assertEquals(parent2.getName(), "parent1");

	}

	@Test
	public void rawSingleFetchTest() {
		CustomerEntity parent1 = CustomerEntity.builder().id(1).name("parent1-1").build();
		CustomerEntity parent2 = CustomerEntity.builder().id(1).name("parent2-1").build();
		context.updateOrInsert(parent1);
		context.updateOrInsert(parent2);
		

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.selectCountAs("count");

		Long count = context.fetchSingleAsLong(query);
		Assert.assertNotNull(count);
		Assert.assertTrue(count > 0);
	}

//	@Test
//	public void rawListFetchTest() {
//		Query query = Estivate.selectQuery(CustomerEntity.class)
//			.selectDistinct(CustomerEntity.Fields.name);
//
//		List<Long> count = context.fetchListAs(query, Long.class);
//		Assert.assertNotNull(count);
//		Assert.assertTrue(count.size() > 0);
//	}
}
