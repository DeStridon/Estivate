package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

public class SimpleMappingTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("join test name 1").build());
		
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 1").build());
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 2").build());
		
		Query query = new Query(TaskEntity.class)
				.select(SegmentEntity.class)
				.select(TaskEntity.class)
				.eq(TaskEntity.class, TaskEntity.Fields.name, task.getName());
		
		List<TaskEntity> results = context.listAsNew(query, TaskEntity.class);
		
		assertEquals(2, results.size());
		
	}
}
