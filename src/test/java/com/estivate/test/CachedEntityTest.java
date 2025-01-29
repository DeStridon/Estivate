package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

public class CachedEntityTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void cachedEntityTest() {
		
		ParentEntity task = context.saveOrUpdate(ParentEntity.builder().name("intial name").externalName("initial external name").build());
		
		ParentEntity taskA = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, task.getId()), ParentEntity.class);
		ParentEntity taskB = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, task.getId()), ParentEntity.class);
		
		taskA.setName("new name");
		taskB.setExternalName("new external name");
		
		assertTrue(taskA.isFieldUpdated(ParentEntity.Fields.name));
		assertFalse(taskA.isFieldUpdated(ParentEntity.Fields.externalName));
		
		
		context.saveOrUpdate(taskA);
		context.saveOrUpdate(taskB);
		
		
		
		ParentEntity taskC = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, task.getId()), ParentEntity.class);
		
		assertEquals(taskA.getName(), taskC.getName());
		assertEquals(taskB.getExternalName(), taskC.getExternalName());
		
	}
	
	
}
