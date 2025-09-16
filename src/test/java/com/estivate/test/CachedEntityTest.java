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
		
		CustomerEntity parent = context.updateOrInsert(CustomerEntity.builder().name("intial name").email("initial email").build());
		
		CustomerEntity parentA = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, parent.getId()), CustomerEntity.class);
		CustomerEntity parentB = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, parent.getId()), CustomerEntity.class);
		
		parentA.setName("new name");
		parentB.setEmail("new email");
		
		assertTrue(parentA.isFieldUpdated(CustomerEntity.Fields.name));
		assertFalse(parentA.isFieldUpdated(CustomerEntity.Fields.name));
		
		
		context.updateOrInsert(parentA);
		context.updateOrInsert(parentB);
		
		
		
		CustomerEntity taskC = context.fetchSingleAs(Estivate.selectQuery(CustomerEntity.class).eq(CustomerEntity.class, AbstractEntity.Fields.id, parent.getId()), CustomerEntity.class);
		
		assertEquals(parentA.getName(), taskC.getName());
		assertEquals(parentB.getEmail(), taskC.getEmail());
		
	}
	
	
}
