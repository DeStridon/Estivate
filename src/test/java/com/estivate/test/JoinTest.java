package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

public class JoinTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void selectJoiningTest() {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("join test name 1").build());
		
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 1").build());
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 2").build());
		
		Query query = new Query(TaskEntity.class)
				.select(SegmentEntity.class)
				.eq(TaskEntity.class, TaskEntity.Fields.name, task.getName());
		
		List<Result> results = context.list(query);
		
		System.out.println(context.queryAsString(query));
		
		assertEquals(2, results.size());
	}
	

	@Test
	public void whereJoiningTest() {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("join test name 2").build());
		
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 1").build());
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 2").build());
		
		Query query = new Query(TaskEntity.class)
				.eq(SegmentEntity.class, SegmentEntity.Fields.sourceContent, "source content 1");
		
		String queryString = context.queryAsString(query);
		
		assertTrue(queryString.indexOf("JOIN") > 0);
	
	}

}
