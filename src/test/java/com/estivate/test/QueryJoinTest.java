package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;


import com.estivate.NameMapper;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class QueryJoinTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		
		
		context.saveOrUpdate(SegmentEntity.builder().projectId(1).taskId(2).sourceContent("source content 1").wordcount(3).sourceFragmentId(5).build());
		context.saveOrUpdate(SegmentEntity.builder().projectId(1).taskId(2).sourceContent("source content 2").wordcount(3).sourceFragmentId(6).build());
		
		Query query = new Query(SegmentEntity.class)
				.name("Query Join Test")
				.eq(SegmentEntity.class, SegmentEntity.Fields.taskId, 2);
		
		List<SegmentEntity> results = context.fetchListAs(query, SegmentEntity.class);
		
		assertEquals(2, results.size());
		
	}
	
	@Test
	public void selectJoiningTest2() throws SQLException {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("join test name 1").build());
		
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 1").build());
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 2").build());
		
		Query query = new Query(TaskEntity.class)
				.join(Join.Inner(TaskEntity.class, SegmentEntity.class))
				.selectAll(SegmentEntity.class)
				.eq(TaskEntity.class, TaskEntity.Fields.name, task.getName());
		
		List<Result> results = context.fetchList(query);
		
		log.debug(context.queryAsString(query));
		
		assertEquals(2, results.size());
	}
	

	@Test
	public void whereJoiningTest() throws SQLException {
		
		TaskEntity task = context.saveOrUpdate(TaskEntity.builder().name("join test name 2").build());
		
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 1").build());
		context.saveOrUpdate(SegmentEntity.builder().taskId(task.getId()).sourceContent("source content 2").build());
		
		Query query = new Query(TaskEntity.class)
				.eq(SegmentEntity.class, SegmentEntity.Fields.sourceContent, "source content 1");
		
		String queryString = context.queryAsString(query);
	
	}
	
	@Test
	public void nameMappingTest() throws SQLException {
		
		
		Entity<SegmentEntity> sourceSegment = new Entity<>(SegmentEntity.class, "sourceSegment");
		Entity<SegmentEntity> targetSegment = new Entity<>(SegmentEntity.class, "targetSegment");
		
		
		Query query = new Query(TaskEntity.class)
			.select(sourceSegment, AbstractEntity.Fields.id)
			.select(targetSegment, AbstractEntity.Fields.id)
			.join(Join.Inner(TaskEntity.class, sourceSegment, AbstractEntity.Fields.id, SegmentEntity.Fields.taskId))
			.join(Join.Inner(sourceSegment, targetSegment, SegmentEntity.Fields.sourceContent, SegmentEntity.Fields.targetContent))
			.eq(sourceSegment, SegmentEntity.Fields.sourceLanguage, "en-FR")
			.eq(TaskEntity.class, AbstractEntity.Fields.id, 35)
			.notEq(sourceSegment, AbstractEntity.Fields.id, new PropertyValue(targetSegment, AbstractEntity.Fields.id));
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		assertTrue(queryString.contains("INNER JOIN SEGMENTENTITY_D sourceSegment"));
		assertTrue(queryString.contains("sourceSegment.SOURCELANGUAGE_D = ?"));
		
		
	}

	@Test
	public void squareJoinTest() throws SQLException {
		
		
		Entity<?> taskA = new Entity<>(TaskEntity.class, "TaskA");
		Entity<?> segmentA = new Entity<>(SegmentEntity.class, "SegmentA");
		Entity<?> segmentB = new Entity<>(SegmentEntity.class, "SegmentB");
		Entity<?> taskB = new Entity<>(TaskEntity.class, "TaskB");
		
		Query query = new Query(taskA)
				.joinInner(taskA, segmentA)
				.joinInner(segmentA, segmentB, SegmentEntity.Fields.sourceContent, SegmentEntity.Fields.sourceContent)
				.joinInner(segmentB, taskB)
				.joinInner(segmentB, taskB)
				.joinLeft(segmentB, taskB)
				.select(taskB, TaskEntity.Fields.name);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("INNER JOIN SEGMENTENTITY_D SegmentB ON SegmentA.SOURCECONTENT_D = SegmentB.SOURCECONTENT_D"));
		Assert.assertTrue(queryString.contains("INNER JOIN TASKENTITY_D TaskB ON SegmentB.TASKID_D = TaskB.ID_D"));
		
	}
	
	
}
