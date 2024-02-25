package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.NameMapper.TestNameMapper;
import com.estivate.Result;
import com.estivate.query.Join;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		
		log.debug(context.queryAsString(query));
		
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
		

	
		
		
		
	
	}
	
	@Test
	public void nameMappingTest() {
		
		Query.nameMapper = new TestNameMapper();
		
		Entity sourceSegment = new Entity(SegmentEntity.class, "sourceSegment");
		Entity targetSegment = new Entity(SegmentEntity.class, "targetSegment");
		
		
		Query query = new Query(TaskEntity.class)
			.select(sourceSegment, AbstractEntity.Fields.id)
			.select(targetSegment, AbstractEntity.Fields.id)
			.join(new Join(TaskEntity.class, sourceSegment, AbstractEntity.Fields.id, SegmentEntity.Fields.taskId))
			.join(new Join(sourceSegment, targetSegment, SegmentEntity.Fields.sourceContent, SegmentEntity.Fields.targetContent))
			.eq(sourceSegment, SegmentEntity.Fields.sourceLanguage, "en-FR")
			.eq(TaskEntity.class, AbstractEntity.Fields.id, 35)
			.notEq(sourceSegment, AbstractEntity.Fields.id, new PropertyValue(targetSegment, AbstractEntity.Fields.id));
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		assertTrue(queryString.contains("INNER JOIN SegmentEntity_d sourceSegment"));
		assertTrue(queryString.contains("sourceSegment.sourceLanguage_d = ?"));
		assertTrue(queryString.contains("INNER JOIN SegmentEntity_d targetSegment ON sourceSegment.sourceContent_d = targetSegment.targetContent_d"));
		assertTrue(queryString.contains("sourceSegment.sourceLanguage_d = ?"));
		
	}

}
