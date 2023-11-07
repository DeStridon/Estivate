package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

public class CachedEntityTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void cachedEntityTest() {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("intial name").externalName("initial external name").build());
		
		TaskEntity taskA = context.uniqueResult(new Query(TaskEntity.class).eq(TaskEntity.class, AbstractEntity.Fields.id, task.getId()), TaskEntity.class);
		TaskEntity taskB = context.uniqueResult(new Query(TaskEntity.class).eq(TaskEntity.class, AbstractEntity.Fields.id, task.getId()), TaskEntity.class);
		
		taskA.setName("new name");
		taskB.setExternalName("new external name");
		
		context.saveOrUpdate(taskA);
		context.saveOrUpdate(taskB);
		
		
		TaskEntity taskC = context.uniqueResult(new Query(TaskEntity.class).eq(TaskEntity.class, AbstractEntity.Fields.id, task.getId()), TaskEntity.class);
		
		assertEquals(taskA.getName(), taskC.getName());
		assertEquals(taskB.getExternalName(), taskC.getExternalName());
		
		
		
	}
	
}
