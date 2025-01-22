package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Join;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.misc.Language;

public class QueryCriterionTest {

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
				.join(Join.Inner(SegmentEntity.class, TaskEntity.class))
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.selectAll(TaskEntity.class)
				
				.eq(TaskEntity.class, TaskEntity.Fields.name, "task 2");
		
		List<Result> results = context.fetchList(query);
		
		assertEquals(1, results.size());
		
		for(Result result : results) {
			SegmentEntity segment = result.mapTo(SegmentEntity.class);
			TaskEntity task = result.mapTo(TaskEntity.class);
		}
		
	}
	
	@Test
	public void queryTest() {
		
		TaskEntity testTask = TaskEntity.builder()
				.projectId(4)
				.name("queryTest test task")
				.externalName("external Name")
				.sourceLanguage(Language.ar_KW)
				.targetLanguage(Language.en_SG)
				.build();
		
		context.saveOrUpdate(testTask);
		
		
		Query query = new Query(TaskEntity.class)
				.eq(TaskEntity.class, TaskEntity.Fields.name, "queryTest test task")
				.lt(TaskEntity.class, TaskEntity.Fields.projectId, 5)
				.lte(TaskEntity.class, TaskEntity.Fields.projectId, 4)
				.gt(TaskEntity.class, TaskEntity.Fields.projectId, 1)
				.gte(TaskEntity.class,  TaskEntity.Fields.projectId, 4)
				.between(TaskEntity.class, TaskEntity.Fields.projectId, 3, 7)
				.notEq(TaskEntity.class, TaskEntity.Fields.externalName, "external Name 2")
				.in(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Arrays.asList(Language.ar_KW, Language.ar_BH, Language.ar_QA))
				.notIn(TaskEntity.class, TaskEntity.Fields.targetLanguage, Arrays.asList(Language.ar_AE, Language.ar_BH, Language.ar_EG))
				
				
				.eqIfNotNull(TaskEntity.class, TaskEntity.Fields.created, null)
				.ltIfNotNull(TaskEntity.class, TaskEntity.Fields.projectId, 5)
				.lteIfNotNull(TaskEntity.class, TaskEntity.Fields.projectId, 4)
				.gtIfNotNull(TaskEntity.class, TaskEntity.Fields.projectId, 1)
				.gteIfNotNull(TaskEntity.class,  TaskEntity.Fields.projectId, 4)
				.betweenIfNotNull(TaskEntity.class, TaskEntity.Fields.projectId, 3, 7)
				.notEqIfNotNull(TaskEntity.class, TaskEntity.Fields.externalName, "external Name 2")
				.inIfNotEmpty(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Arrays.asList(Language.ar_KW, Language.ar_BH, Language.ar_QA))
				.notInIfNotEmpty(TaskEntity.class, TaskEntity.Fields.targetLanguage, Arrays.asList(Language.ar_AE, Language.ar_BH, Language.ar_EG))
				;
		
		List<TaskEntity> tasks = context.fetchListAs(query, TaskEntity.class);
		
		Assert.assertEquals(1, tasks.size());		
	
		
	
	}
	
	@Test
	public void taskEnumTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class);
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1, 2, 3, 4));
		
		query.in(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Arrays.asList(Language.en_GB, Language.fr_FR));
		
		query.in(TaskEntity.class, TaskEntity.Fields.status, Arrays.asList(MacroState.Analysis, MacroState.Translation));
		
		context.fetchList(query);
		
		System.out.println(context.queryAsString(query));
	
	}
	
	@Test
	public void inTest() {
		
		TaskEntity task1 = context.saveOrUpdate(TaskEntity.builder().projectId(1234).name("task 1").build());
		TaskEntity task2 = context.saveOrUpdate(TaskEntity.builder().projectId(1235).name("task 2").build());
		
		Query query = new Query(TaskEntity.class);

		Entity taskEntity = new Query.Entity(TaskEntity.class);
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1234, 1235));
		assertEquals(2, context.fetchList(query).size());
		
		Query query2 = query.clone().in(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query2).size());

		Query query3 = query.clone().notIn(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query3).size());

		Query query4 = query.clone().notIn(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query4).size());
		
	}
	
	@Test
	public void in2Test() {
		
		TaskEntity task1 = context.saveOrUpdate(TaskEntity.builder().projectId(2234).name("task 1").build());
		TaskEntity task2 = context.saveOrUpdate(TaskEntity.builder().projectId(2235).name("task 2").build());
		
		Query query = new Query(TaskEntity.class);

		Entity taskEntity = new Query.Entity(TaskEntity.class);
		
		query.in(taskEntity, TaskEntity.Fields.projectId, Arrays.asList(2234, 2235));
		assertEquals(2, context.fetchList(query).size());
		
		query.in(taskEntity, TaskEntity.Fields.projectId, Arrays.asList(2235));
		assertEquals(1, context.fetchList(query).size());
		
		query.notIn(taskEntity, TaskEntity.Fields.projectId, Arrays.asList(2235));
		assertEquals(0, context.fetchList(query).size());
		
	}

	
	
	
	@Test
	public void inCollectionTest() throws SQLException {
		
		List<Long> taskIds = Arrays.asList(1L, 2L, 3L, 4L);
		
		Query query = new Query(TaskEntity.class).in(TaskEntity.class, AbstractEntity.Fields.id, taskIds);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?, ?, ?, ?)"));
		
	}
	
	@Test
	public void isNullTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class).isNull(TaskEntity.class, AbstractEntity.Fields.id);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" is null"));
		
	}
	
	@Test
	public void isNotNullTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class).isNotNull(TaskEntity.class, AbstractEntity.Fields.id);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" is not null"));
		
	}
	
	@Test
	public void likeTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class).like(TaskEntity.class, TaskEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		context.fetchList(query);

		Assert.assertTrue(queryString.contains(" like ?"));
		
	}
	
	@Test
	public void notLikeTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class).notLike(TaskEntity.class, TaskEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		context.fetchList(query);

		Assert.assertTrue(queryString.contains(" not like ?"));
		
	}
	
	
	@Test
	public void eqOrNullTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class)
				.in(TaskEntity.class, TaskEntity.Fields.projectId, Arrays.asList(1, 3, 5))
				.eqOrNull(TaskEntity.class, TaskEntity.Fields.created, new Date());
		
		System.out.println(context.queryAsString(query));
		
		
	}
	
	@Test
	public void orAggregatorTest() {
		Aggregator or = Estivate.or();
		or.add(Estivate.eq(TaskEntity.class, AbstractEntity.Fields.id, 1));
		or.add(Estivate.eq(TaskEntity.class, TaskEntity.Fields.projectId, 2));
	}
	
	
}
