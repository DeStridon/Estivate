package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.misc.Language;

public class QueryTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void insertTest() {
		
		TaskEntity task1 = context.saveOrUpdate(TaskEntity.builder().projectId(1).name("task 1").build());
		
		assertEquals(new Date().getTime(), task1.getCreated().getTime(), 100);
		assertNull(task1.getUpdated());
		assertNotNull(task1.getId());
	
		task1.setExternalName("external name 1");
		
		context.saveOrUpdate(task1);
		
		assertNotNull(task1.getUpdated());
		
	}
	
	
	@Test 
	public void automatedJoinTest() {
		
		TaskEntity task2 = context.saveOrUpdate(TaskEntity.builder().projectId(1).name("task 2").build());

		SegmentEntity segment21 = context.saveOrUpdate(SegmentEntity.builder().taskId(task2.getId()).sourceContent("source content 2.1").build());
		SegmentEntity segment22 = context.saveOrUpdate(SegmentEntity.builder().taskId(task2.getId()).sourceContent("source content 2.2").build());
		SegmentEntity segment23 = context.saveOrUpdate(SegmentEntity.builder().taskId(task2.getId()).sourceContent("source content 2.3").build());
		
		TaskEntity task3 = context.saveOrUpdate(TaskEntity.builder().projectId(1).name("task 3").build());

		SegmentEntity segment31 = context.saveOrUpdate(SegmentEntity.builder().taskId(task3.getId()).sourceContent("source content 3.1").build());
		SegmentEntity segment32 = context.saveOrUpdate(SegmentEntity.builder().taskId(task3.getId()).sourceContent("source content 3.2").build());
		
		Query query = new Query(SegmentEntity.class)
				.select(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.eq(TaskEntity.class, TaskEntity.Fields.name, "task 2");
		
		List<Result> results = context.list(query);
		
		assertEquals(3, results.size());
		
		for(Result result : results) {
			SegmentEntity segment = result.mapAs(SegmentEntity.class);
			TaskEntity task = result.mapAs(TaskEntity.class);
		}
		
	}
	
	@Test
	public void queryTest() {
		
		Query query = new Query(TaskEntity.class)
				.eq(TaskEntity.class, TaskEntity.Fields.name, "name")
				.eqIfNotNull(TaskEntity.class, TaskEntity.Fields.created, null)
				
				.gt(TaskEntity.class, AbstractEntity.Fields.id, 3)
				.gte(TaskEntity.class, AbstractEntity.Fields.id, 4)
				.lt(TaskEntity.class, TaskEntity.Fields.projectId, 5)
				.lte(TaskEntity.class, TaskEntity.Fields.projectId, 6);
				
		
		
		
	}
	
	@Test
	public void taskEnumTest() {
		
		Query query = new Query(TaskEntity.class);
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, 1, 2, 3, 4);
		
		query.in(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Language.en_GB, Language.fr_FR);
		
		query.in(TaskEntity.class, TaskEntity.Fields.status, MacroState.Analysis, MacroState.Translation);
		
		System.out.println(context.queryAsString(query));
	
	}
	
	@Test
	public void inTest() {
		
		TaskEntity task1 = context.saveOrUpdate(TaskEntity.builder().projectId(1234).name("task 1").build());
		
		
		Query query = new Query(TaskEntity.class);
		
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, 1234, 234, 34, 4);
		
		System.out.println(context.queryAsString(query));
		
		List<Result> results = context.list(query);
		
		assertEquals(1, results.size());
		
	}
	
	
	@Test
	public void countTest() {
		Query query = new Query(TaskEntity.class);
		
		query.selectCount();
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest() {
		Query query = new Query(TaskEntity.class)
				.select(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id);
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest2() {
		Query query = new Query(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.select(TaskEntity.class);
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest3() {
		Query query = new Query(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.select(TaskEntity.class, AbstractEntity.Fields.id);

		System.out.println(context.queryAsString(query));
		
	}
	
//	@Test
//	public void inCollectionTest() {
//		
//		List<Long> taskIds = Arrays.asList(1L, 2L, 3L, 4L);
//		
//		Query query = new Query(TaskEntity.class)
//				.in(TaskEntity.class, AbstractEntity.Fields.id, taskIds);
//
//		System.out.println(context.queryAsString(query));
//		
//	}
	
	
}
