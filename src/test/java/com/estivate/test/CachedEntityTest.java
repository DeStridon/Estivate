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
		
		ParentEntity parent = context.updateOrInsert(ParentEntity.builder().name("intial name").externalName("initial external name").build());
		
		ParentEntity parentA = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, parent.getId()), ParentEntity.class);
		ParentEntity parentB = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, parent.getId()), ParentEntity.class);
		
		parentA.setName("new name");
		parentB.setExternalName("new external name");
		
		assertTrue(parentA.isFieldUpdated(ParentEntity.Fields.name));
		assertFalse(parentA.isFieldUpdated(ParentEntity.Fields.externalName));
		
		
		context.updateOrInsert(parentA);
		context.updateOrInsert(parentB);
		
		
		
		ParentEntity taskC = context.fetchSingleAs(new Query(ParentEntity.class).eq(ParentEntity.class, AbstractEntity.Fields.id, parent.getId()), ParentEntity.class);
		
		assertEquals(parentA.getName(), taskC.getName());
		assertEquals(parentB.getExternalName(), taskC.getExternalName());
		
	}
	
	
}
