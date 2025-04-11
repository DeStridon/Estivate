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
		
		
		
		context.updateOrInsert(ChildEntity.builder().homeId(1).parentId(2).description("source content 1").age(3).build());
		context.updateOrInsert(ChildEntity.builder().homeId(1).parentId(2).description("source content 2").age(3).build());
		
		Query query = new Query(ChildEntity.class)
				.name("Query Join Test")
				.eq(ChildEntity.class, ChildEntity.Fields.parentId, 2);
		
		List<ChildEntity> results = context.fetchListAs(query, ChildEntity.class);
		
		assertEquals(2, results.size());
		
	}
	
	@Test
	public void selectJoiningTest2() throws SQLException {
		
		ParentEntity task = context.updateOrInsert(ParentEntity.builder().name("join test name 1").build());
		
		context.updateOrInsert(ChildEntity.builder().parentId(task.getId()).description("source content 1").build());
		context.updateOrInsert(ChildEntity.builder().parentId(task.getId()).description("source content 2").build());
		
		Query query = new Query(ParentEntity.class)
				.joinInner(ParentEntity.class, ChildEntity.class)
				.selectAll(ChildEntity.class)
				.eq(ParentEntity.class, ParentEntity.Fields.name, task.getName());
		
		List<Result> results = context.fetchList(query);
		
		log.debug(context.queryAsString(query));
		
		assertEquals(2, results.size());
	}
	

	@Test
	public void whereJoiningTest() throws SQLException {
		
		ParentEntity parent = context.updateOrInsert(ParentEntity.builder().name("join test name 2").build());
		
		context.updateOrInsert(ChildEntity.builder().parentId(parent.getId()).description("source content 1").build());
		context.updateOrInsert(ChildEntity.builder().parentId(parent.getId()).description("source content 2").build());
		
		Query query = new Query(ParentEntity.class)
				.eq(ChildEntity.class, ChildEntity.Fields.description, "source content 1");
		
		String queryString = context.queryAsString(query);
	
	}
	
	@Test
	public void nameMappingTest() throws SQLException {
		
		
		Entity<ChildEntity> firstChild = new Entity<>(ChildEntity.class, "firstChild");
		Entity<ChildEntity> secondChild = new Entity<>(ChildEntity.class, "secondChild");
		
		
		Query query = new Query(ParentEntity.class)
			.select(firstChild, AbstractEntity.Fields.id)
			.select(secondChild, AbstractEntity.Fields.id)
			.joinInner(ParentEntity.class, firstChild, AbstractEntity.Fields.id, ChildEntity.Fields.parentId)
			.joinInner(firstChild, secondChild, ChildEntity.Fields.description, ChildEntity.Fields.description)
			.eq(ParentEntity.class, AbstractEntity.Fields.id, 35)
			.notEq(firstChild, AbstractEntity.Fields.id, new PropertyValue(secondChild, AbstractEntity.Fields.id));
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		assertTrue(queryString.contains("INNER JOIN CHILDENTITY_D firstChild"));
		assertTrue(queryString.contains("firstChild.ID_D != secondChild.ID_D"));
		
		
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
