package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

public class CachedEntityTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void cachedEntityTest() {
		
		CustomerEntity customer = context.updateOrInsert(CustomerEntity.builder().name("initial name").email("initial email").build());
		
		CustomerEntity customerA = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId()), CustomerEntity.class);
		CustomerEntity customerB = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId()), CustomerEntity.class);
		
		customerA.setName("new name");
		customerB.setEmail("new email");
		
		assertTrue(customerA.isFieldUpdated(CustomerEntity.Fields.name));
		assertFalse(customerA.isFieldUpdated(CustomerEntity.Fields.email));
		
		
		context.updateOrInsert(customerA);
		context.updateOrInsert(customerB);
		
		
		CustomerEntity customerC = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId()), CustomerEntity.class);
		
		assertEquals(customerA.getName(), customerC.getName());
		assertEquals(customerB.getEmail(), customerC.getEmail());
		
	}
	
	
}
