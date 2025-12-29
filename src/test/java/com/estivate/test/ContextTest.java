package com.estivate.test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		
		CustomerEntity customer1 = DatabaseGenerator.createRandomCustomer();
		context.updateOrInsert(customer1);
		CustomerEntity customer2 = DatabaseGenerator.createRandomCustomer();
		context.updateOrInsert(customer2);
		CustomerEntity customer3 = DatabaseGenerator.createRandomCustomer();
		context.updateOrInsert(customer3);

		customer1.setName("Updated Name 1");
		customer2.setName("Updated Name 2");
		customer3.setName("Updated Name 3");
		
		context.update(Arrays.asList(customer1, customer2, customer3));
		
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class).in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()));
		List<CustomerEntity> resultQueries = context.fetchAsList(query, CustomerEntity.class);
		
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 1")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 2")));
		Assert.assertTrue(resultQueries.stream().anyMatch(x -> x.getName().equals("Updated Name 3")));
		
		
	}
	
	@Test
	public void queryAliasTest() {
		
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class, "myTask");
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(customer).in(customer, AbstractEntity.Fields.id, Arrays.asList(1,2,3));
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
		CustomerEntity parent1 = CustomerEntity.builder().name("parent1").build();
		context.updateOrInsert(parent1);
		
		CustomerEntity parent2 = CustomerEntity.builder().id(parent1.getId()).name("parent2").build();
		parent2.setId(parent1.getId());
		context.merge(parent2);

		Assert.assertEquals(parent2.getName(), "parent1");

	}

	@Test
	public void rawSingleFetchTest() {
		CustomerEntity parent1 = CustomerEntity.builder().name("parent1-1").build();
		CustomerEntity parent2 = CustomerEntity.builder().name("parent2-1").build();
		context.updateOrInsert(parent1);
		context.updateOrInsert(parent2);
		

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.selectCountAll();

		Long count = context.fetch(query).asSingleLong();
		Assert.assertNotNull(count);
		Assert.assertTrue(count > 0);
	}

	@Test
	public void batchUpdateTest() {
		// Create initial entities
		CustomerEntity customer1 = CustomerEntity.builder().name("customer1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("customer2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("customer3").build();
		
		List<CustomerEntity> customers = Arrays.asList(customer1, customer2, customer3);
		
		// Insert all customers
		for (CustomerEntity customer : customers) {
			context.updateOrInsert(customer);
		}
		
		// Modify the entities
		customer1.setName("customer1-updated");
		customer2.setName("customer2-updated");
		customer3.setName("customer3-updated");
		
		// Update all modified customers at once
		context.update(customers);
		
		// Verify changes were saved
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, customers.stream().map(c -> c.getId()).collect(Collectors.toList()));
		
		List<CustomerEntity> updatedCustomers = context.fetchList(query);
		
		Assert.assertEquals(3, updatedCustomers.size());
		Assert.assertTrue(updatedCustomers.stream().anyMatch(c -> c.getName().equals("customer1-updated")));
		Assert.assertTrue(updatedCustomers.stream().anyMatch(c -> c.getName().equals("customer2-updated")));
		Assert.assertTrue(updatedCustomers.stream().anyMatch(c -> c.getName().equals("customer3-updated")));
	}
	
	
}
