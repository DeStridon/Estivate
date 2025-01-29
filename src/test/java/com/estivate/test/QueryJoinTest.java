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
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class QueryJoinTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		
		
		context.saveOrUpdate(ChildEntity.builder().homeId(1).parentId(2).description("source content 1").age(3).build());
		context.saveOrUpdate(ChildEntity.builder().homeId(1).parentId(2).description("source content 2").age(3).build());
		
		Query query = new Query(ChildEntity.class)
				.name("Query Join Test")
				.eq(ChildEntity.class, ChildEntity.Fields.parentId, 2);
		
		List<ChildEntity> results = context.fetchListAs(query, ChildEntity.class);
		
		assertEquals(2, results.size());
		
	}
	
	@Test
	public void selectJoiningTest2() throws SQLException {
		
		ParentEntity task = context.saveOrUpdate(ParentEntity.builder().name("join test name 1").build());
		
		context.saveOrUpdate(ChildEntity.builder().parentId(task.getId()).description("source content 1").build());
		context.saveOrUpdate(ChildEntity.builder().parentId(task.getId()).description("source content 2").build());
		
		Query query = new Query(ParentEntity.class)
				.join(Join.Inner(ParentEntity.class, ChildEntity.class))
				.selectAll(ChildEntity.class)
				.eq(ParentEntity.class, ParentEntity.Fields.name, task.getName());
		
		List<Result> results = context.fetchList(query);
		
		log.debug(context.queryAsString(query));
		
		assertEquals(2, results.size());
	}
	

	@Test
	public void whereJoiningTest() throws SQLException {
		
		ParentEntity parent = context.saveOrUpdate(ParentEntity.builder().name("join test name 2").build());
		
		context.saveOrUpdate(ChildEntity.builder().parentId(parent.getId()).description("source content 1").build());
		context.saveOrUpdate(ChildEntity.builder().parentId(parent.getId()).description("source content 2").build());
		
		Query query = new Query(ParentEntity.class)
				.eq(ChildEntity.class, ChildEntity.Fields.description, "source content 1");
		
		String queryString = context.queryAsString(query);
	
	}
	
	@Test
	public void nameMappingTest() throws SQLException {
		
		
		Entity<ChildEntity> sourceSegment = new Entity<>(ChildEntity.class, "sourceSegment");
		Entity<ChildEntity> targetSegment = new Entity<>(ChildEntity.class, "targetSegment");
		
		
		Query query = new Query(ParentEntity.class)
			.select(sourceSegment, AbstractEntity.Fields.id)
			.select(targetSegment, AbstractEntity.Fields.id)
			.join(Join.Inner(ParentEntity.class, sourceSegment, AbstractEntity.Fields.id, ChildEntity.Fields.parentId))
			.join(Join.Inner(sourceSegment, targetSegment, ChildEntity.Fields.description, ChildEntity.Fields.description))
			.eq(ParentEntity.class, AbstractEntity.Fields.id, 35)
			.notEq(sourceSegment, AbstractEntity.Fields.id, new PropertyValue(targetSegment, AbstractEntity.Fields.id));
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		assertTrue(queryString.contains("INNER JOIN SEGMENTENTITY_D sourceSegment"));
		assertTrue(queryString.contains("sourceSegment.SOURCELANGUAGE_D = ?"));
		
		
	}

	@Test
	public void squareJoinTest() throws SQLException {
		
		
		Entity<?> taskA = new Entity<>(ParentEntity.class, "TaskA");
		Entity<?> segmentA = new Entity<>(ChildEntity.class, "SegmentA");
		Entity<?> segmentB = new Entity<>(ChildEntity.class, "SegmentB");
		Entity<?> taskB = new Entity<>(ParentEntity.class, "TaskB");
		
		Query query = new Query(taskA)
				.joinInner(taskA, segmentA)
				.joinInner(segmentA, segmentB, ChildEntity.Fields.description, ChildEntity.Fields.description)
				.joinInner(segmentB, taskB)
				.joinInner(segmentB, taskB)
				.joinLeft(segmentB, taskB)
				.select(taskB, ParentEntity.Fields.name);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("INNER JOIN SEGMENTENTITY_D SegmentB ON SegmentA.SOURCECONTENT_D = SegmentB.SOURCECONTENT_D"));
		Assert.assertTrue(queryString.contains("INNER JOIN TASKENTITY_D TaskB ON SegmentB.TASKID_D = TaskB.ID_D"));
		
	}
	
	
}
