package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;
import com.estivate.test.entities.ParentEntity.JobEnum;
import com.estivate.test.entities.misc.Language;

public class QueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void insertTest() {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(1).name("task 1").build());
		
		assertEquals(new Date().getTime(), task1.getCreated().getTime(), 100);
		assertNull(task1.getUpdated());
		assertNotNull(task1.getId());
	
		task1.setExternalName("external name 1");
		
		context.updateOrInsert(task1);
		
		assertNotNull(task1.getUpdated());
		
	}
	
	
	@Test 
	public void automatedJoinTest() {
		
		ParentEntity parent2 = context.updateOrInsert(ParentEntity.builder().homeId(1).name("parent 2").build());

		ChildEntity child21 = context.updateOrInsert(ChildEntity.builder().parentId(parent2.getId()).description("source content 2.1").build());
		ChildEntity child22 = context.updateOrInsert(ChildEntity.builder().parentId(parent2.getId()).description("source content 2.2").build());
		ChildEntity child23 = context.updateOrInsert(ChildEntity.builder().parentId(parent2.getId()).description("source content 2.3").build());
		
		ParentEntity parent3 = context.updateOrInsert(ParentEntity.builder().homeId(1).name("parent 3").build());

		ChildEntity child31 = context.updateOrInsert(ChildEntity.builder().parentId(parent3.getId()).description("source content 3.1").build());
		ChildEntity child32 = context.updateOrInsert(ChildEntity.builder().parentId(parent3.getId()).description("source content 3.2").build());
		
		Query query = new Query(ChildEntity.class)
				.joinInner(ChildEntity.class, ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id)
				.selectAll(ParentEntity.class)
				
				.eq(ParentEntity.class, ParentEntity.Fields.name, "parent 2");
		
		List<Result> results = context.fetchList(query);
		
		assertEquals(1, results.size());
		
		for(Result result : results) {
			ChildEntity child = result.mapTo(ChildEntity.class);
			ParentEntity parent = result.mapTo(ParentEntity.class);
		}
		
	}
	
	@Test
	public void queryTest() {
		
		ParentEntity testTask = ParentEntity.builder()
				.homeId(4)
				.name("queryTest test task")
				.externalName("external Name")
				.sourceLanguage(Language.ar_KW)
				.targetLanguage(Language.de_AT)
				.build();
		
		context.updateOrInsert(testTask);
		
		
		Query query = new Query(ParentEntity.class)
				.eq(ParentEntity.class, ParentEntity.Fields.name, "queryTest test task")
				.lt(ParentEntity.class, ParentEntity.Fields.homeId, 5)
				.lte(ParentEntity.class, ParentEntity.Fields.homeId, 4)
				.gt(ParentEntity.class, ParentEntity.Fields.homeId, 1)
				.gte(ParentEntity.class,  ParentEntity.Fields.homeId, 4)
				.between(ParentEntity.class, ParentEntity.Fields.homeId, 3, 7)
				.notEq(ParentEntity.class, ParentEntity.Fields.externalName, "external Name 2")
				.in(ParentEntity.class, ParentEntity.Fields.sourceLanguage, Arrays.asList(Language.ar_KW, Language.ar_BH, Language.ar_QA))
				.notIn(ParentEntity.class, ParentEntity.Fields.targetLanguage, Arrays.asList(Language.ar_AE, Language.ar_BH, Language.ar_EG))
				
				
				.eqIfNotNull(ParentEntity.class, ParentEntity.Fields.created, null)
				.ltIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 5)
				.lteIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 4)
				.gtIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1)
				.gteIfNotNull(ParentEntity.class,  ParentEntity.Fields.homeId, 4)
				.betweenIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 3, 7)
				.notEqIfNotNull(ParentEntity.class, ParentEntity.Fields.externalName, "external Name 2")
				.inIfNotEmpty(ParentEntity.class, ParentEntity.Fields.sourceLanguage, Arrays.asList(Language.ar_KW, Language.ar_BH, Language.ar_QA))
				.notInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.targetLanguage, Arrays.asList(Language.ar_AE, Language.ar_BH, Language.ar_EG))
				;
		
		List<ParentEntity> tasks = context.fetchListAs(query, ParentEntity.class);
		
		Assert.assertEquals(1, tasks.size());
	
		
	
	}
	
	@Test
	public void taskEnumTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class);
		
		query.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1, 2, 3, 4));
		
		query.in(ParentEntity.class, ParentEntity.Fields.sourceLanguage, Arrays.asList(Language.en_GB, Language.fr_FR));
		
		query.in(ParentEntity.class, ParentEntity.Fields.status, Arrays.asList(JobEnum.Analysis, JobEnum.Translation));
		
		context.fetchList(query);
		
		System.out.println(context.queryAsString(query));
	
	}
	
	@Test
	public void inTest() {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(1234).name("task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(1235).name("task 2").build());
		
		Query query = new Query(ParentEntity.class);

		Entity taskEntity = new Query.Entity(ParentEntity.class);
		
		query.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1234, 1235));
		assertEquals(2, context.fetchList(query).size());
		
		Query query2 = query.clone().in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query2).size());

		Query query3 = query.clone().notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query3).size());

		Query query4 = query.clone().notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query4).size());
		
	}
	
	@Test
	public void in2Test() {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(2234).name("task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(2235).name("task 2").build());
		
		Query query = new Query(ParentEntity.class);

		Entity taskEntity = new Query.Entity(ParentEntity.class);
		
		query.in(taskEntity, ParentEntity.Fields.homeId, Arrays.asList(2234, 2235));
		assertEquals(2, context.fetchList(query).size());
		
		query.in(taskEntity, ParentEntity.Fields.homeId, Arrays.asList(2235));
		assertEquals(1, context.fetchList(query).size());
		
		query.notIn(taskEntity, ParentEntity.Fields.homeId, Arrays.asList(2235));
		assertEquals(0, context.fetchList(query).size());
		
	}

	
	
	
	@Test
	public void inCollectionTest() throws SQLException {
		
		List<Long> taskIds = Arrays.asList(1L, 2L, 3L, 4L);
		
		Query query = new Query(ParentEntity.class).in(ParentEntity.class, AbstractEntity.Fields.id, taskIds);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?, ?, ?, ?)"));
		
	}
	
	@Test
	public void isNullTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class).isNull(ParentEntity.class, AbstractEntity.Fields.id);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" is null"));
		
	}
	
	@Test
	public void isNotNullTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class).isNotNull(ParentEntity.class, AbstractEntity.Fields.id);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" is not null"));
		
	}
	
	@Test
	public void likeTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class).like(ParentEntity.class, ParentEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		context.fetchList(query);

		Assert.assertTrue(queryString.contains(" like ?"));
		
	}
	
	@Test
	public void notLikeTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class).notLike(ParentEntity.class, ParentEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		context.fetchList(query);

		Assert.assertTrue(queryString.contains(" not like ?"));
		
	}
	
	
	@Test
	public void eqOrNullTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class)
				.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1, 3, 5))
				.eqOrNull(ParentEntity.class, ParentEntity.Fields.created, new Date());
		
		System.out.println(context.queryAsString(query));
		
		
	}
	
	@Test
	public void orAggregatorTest() {
		Aggregator or = Estivate.or();
		or.add(Estivate.eq(ParentEntity.class, AbstractEntity.Fields.id, 1));
		or.add(Estivate.eq(ParentEntity.class, ParentEntity.Fields.homeId, 2));
	}
	
	@Test
	public void inIfNotEmptyNullableTest() {
		List<String> names = new ArrayList<>();
		names.add(null);
		EstivateNode node = Estivate.inIfNotEmptyNullable(ParentEntity.class, ParentEntity.Fields.name, names);
		System.out.println(node.toString());
	}
	
	
	
}
