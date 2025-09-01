package com.estivate.query.test;

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

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Attribute;
import com.estivate.query.EstivateNode;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;
import com.estivate.test.entities.ParentEntity.JobEnum;
import com.estivate.test.entities.misc.Language;

public class SelectQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void insertTest() {
		
		ParentEntity parent1 = context.updateOrInsert(ParentEntity.builder().homeId(1).name("task 1").build());
		
		assertEquals(new Date().getTime(), parent1.getCreated().getTime(), 100);
		assertNull(parent1.getUpdated());
		assertNotNull(parent1.getId());
	
		parent1.setExternalName("external name 1");
		
		context.updateOrInsert(parent1);
		
		assertNotNull(parent1.getUpdated());
		
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
		
		SelectQuery<ChildEntity> query = Estivate.selectQuery(ChildEntity.class)
				.joinInner(ChildEntity.class, ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id)
				.selectAll(ParentEntity.class)
				
				.eq(ParentEntity.class, ParentEntity.Fields.name, "parent 2");
		
		List<Result> results = query.fetchListAsResults(context);
		
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
		
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
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
		
		List<ParentEntity> tasks = query.fetchListAs(context, ParentEntity.class);
		
		Assert.assertEquals(1, tasks.size());
	
	}
	
	@Test
	public void taskEnumTest() throws SQLException {
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1, 2, 3, 4))
			.in(ParentEntity.class, ParentEntity.Fields.sourceLanguage, Arrays.asList(Language.en_GB, Language.fr_FR))
			.in(ParentEntity.class, ParentEntity.Fields.status, Arrays.asList(JobEnum.Analysis, JobEnum.Translation));
		
		query.fetchList(context);
		
		System.out.println(context.queryAsString(query));
	
	}
	
	@Test
	public void inTest() {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(1234).name("task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(1235).name("task 2").build());
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1234, 1235));
		
		assertEquals(2, context.fetchList(query).size());
		
		SelectQuery<ParentEntity> query2 = query.clone().in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query2).size());

		SelectQuery<ParentEntity> query3 = query.clone().notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query3).size());

		SelectQuery<ParentEntity> query4 = query.clone().notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query4).size());
		
	}
	
	@Test
	public void in2Test() {
		
		ParentEntity parent1 = context.updateOrInsert(ParentEntity.builder().homeId(2234).name("task 1").build());
		ParentEntity parent2 = context.updateOrInsert(ParentEntity.builder().homeId(2235).name("task 2").build());
		
		SelectQuery<ParentEntity> query = new SelectQuery<>(ParentEntity.class);

		Entity<ParentEntity> taskEntity = new Entity<>(ParentEntity.class);
		
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
		
		SelectQuery query = new SelectQuery(ParentEntity.class).in(ParentEntity.class, AbstractEntity.Fields.id, taskIds);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?, ?, ?, ?)"));
		
	}
	
	@Test
	public void isNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = new SelectQuery(ParentEntity.class).isNull(ParentEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" is null"));
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = new SelectQuery(ParentEntity.class).isNull(parentEntity, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" is null"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id);
		SelectQuery<ParentEntity> query3 = new SelectQuery(ParentEntity.class).isNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" is null"));
	}
	
	@Test
	public void isNotNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = new SelectQuery(ParentEntity.class).isNotNull(ParentEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" is not null"));
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = new SelectQuery(ParentEntity.class).isNotNull(parentEntity, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" is not null"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id);
		SelectQuery<ParentEntity> query3 = new SelectQuery(ParentEntity.class).isNotNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" is not null"));
	}
	
	@Test
	public void likeTest() throws SQLException {
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class).like(ParentEntity.class, ParentEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" like ?"));
		
	}
	
	@Test
	public void notLikeTest() throws SQLException {
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
				.notLike(ParentEntity.class, ParentEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" not like ?"));
		
	}
	
	
	@Test
	public void eqOrNullTest() throws SQLException {
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
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
	

	@Test
	public void notInTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(3001).name("notIn task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(3002).name("notIn task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(3003).name("notIn task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(3001, 3003));
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?, ?)"));
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 3002);
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 3001);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 3003);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.notIn(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(3001, 3003));
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("not in (?, ?)"));
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 3002);
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 3001);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 3003);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(3001, 3003));
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("not in (?, ?)"));
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 3002);
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 3001);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 3003);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void notInIfNotEmptyTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(4001).name("notInIfNotEmpty task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(4002).name("notInIfNotEmpty task 2").build());
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(4001));
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?)"));
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 4002);
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 4001);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmpty(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(4001));
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("not in (?)"));
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 4002);
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 4001);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmpty(homeIdAttribute, Arrays.asList(4001));
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("not in (?)"));
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 4002);
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 4001);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<ParentEntity> queryEmpty = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(4001, 4002))
			.notInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, new ArrayList<>());
		
		List<ParentEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void notInOrTrueIfEmptyTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(5001).name("notInOrTrueIfEmpty task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(5002).name("notInOrTrueIfEmpty task 2").build());
		
		// Test with non-empty list
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.notInOrTrueIfEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(5001));
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?)"));
		
		boolean foundTask2 = results1.stream().anyMatch(t -> t.getHomeId() == 5002);
		boolean foundTask1 = results1.stream().anyMatch(t -> t.getHomeId() == 5001);
		
		Assert.assertTrue("Should find task2", foundTask2);
		Assert.assertFalse("Should not find task1", foundTask1);
		
		// Test with empty list - should return true (all results)
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(5001, 5002))
			.notInOrTrueIfEmpty(ParentEntity.class, ParentEntity.Fields.homeId, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("true"));
		assertEquals(2, results2.size());
	}
	
	@Test
	public void likeContainsTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(6001).name("likeContains search test").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(6002).name("different content").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.likeContains(ParentEntity.class, ParentEntity.Fields.name, "search");
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 6001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 6002);
		Assert.assertTrue("Class-based: Should find task1 with 'search' in name", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'search' in name", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.likeContains(parentEntity, ParentEntity.Fields.name, "search");
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 6001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 6002);
		Assert.assertTrue("Entity-based: Should find task1 with 'search' in name", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'search' in name", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.likeContains(nameAttribute, "search");
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 6001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 6002);
		Assert.assertTrue("Attribute-based: Should find task1 with 'search' in name", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'search' in name", foundTask2_3);
	}
	
	@Test
	public void notLikeContainsTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(7001).name("notLikeContains exclude test").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(7002).name("different content").build());
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(7001, 7002))
			.notLikeContains(ParentEntity.class, ParentEntity.Fields.name, "exclude");
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 7001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 7002);
		
		Assert.assertFalse("Should not find task1 with 'exclude' in name", foundTask1);
		Assert.assertTrue("Should find task2 without 'exclude' in name", foundTask2);
	}
	
	@Test
	public void eqWithClassTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 8001);

		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithEntityTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(parentEntity, ParentEntity.Fields.homeId, 8001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithAttributeTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(homeIdAttribute, 8001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void notEqTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(9001).name("notEq test task").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(9002).name("different task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(9001, 9002))
			.notEq(ParentEntity.class, ParentEntity.Fields.homeId, 9001);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" != ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 9001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 9002);
		Assert.assertFalse("Class-based: Should not find task1 with homeId 9001", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2 with different homeId", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(9001, 9002))
			.notEq(parentEntity, ParentEntity.Fields.homeId, 9001);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" != ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 9001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 9002);
		Assert.assertFalse("Entity-based: Should not find task1 with homeId 9001", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2 with different homeId", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(9001, 9002))
			.notEq(homeIdAttribute, 9001);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" != ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 9001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 9002);
		Assert.assertFalse("Attribute-based: Should not find task1 with homeId 9001", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2 with different homeId", foundTask2_3);
	}
	
	@Test
	public void eqIfNotNullTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(10001).name("eqIfNotNull test task").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(10002).name("different task").build());
		
		// Test 1: Class-based method signature with non-null value
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 10001);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 10001);
		Assert.assertTrue("Class-based: Should find task1 with homeId 10001", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-null value
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(parentEntity, ParentEntity.Fields.homeId, 10001);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" = ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 10001);
		Assert.assertTrue("Entity-based: Should find task1 with homeId 10001", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-null value
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(homeIdAttribute, 10001);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" = ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 10001);
		Assert.assertTrue("Attribute-based: Should find task1 with homeId 10001", foundTask1_3);
		
		// Test with null value - should return all results (Class-based example)
		SelectQuery<ParentEntity> queryNull = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(10001, 10002))
			.eqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, null);
		
		List<ParentEntity> resultsNull = context.fetchList(queryNull);
		assertEquals(2, resultsNull.size());
	}
	
	@Test
	public void eqNullableTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(10101).name("eqNullable test task").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(10102).name("different task").externalName("external").build());
		
		// Test with non-null value
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(10101, 10102))
			.eqNullable(ParentEntity.class, ParentEntity.Fields.externalName, "external");
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundTask2 = results1.stream().anyMatch(t -> t.getHomeId() == 10102);
		Assert.assertTrue("Should find task2 with external name", foundTask2);
		
		// Test with null value - should find entities with null external name
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(10101, 10102))
			.eqNullable(ParentEntity.class, ParentEntity.Fields.externalName, null);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" is null"));
		boolean foundTask1 = results2.stream().anyMatch(t -> t.getHomeId() == 10101);
		Assert.assertTrue("Should find task1 with null external name", foundTask1);
	}
	
	@Test
	public void ltTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(11001).name("lt test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(11002).name("lt test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(11003).name("lt test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(11001, 11002, 11003))
			.lt(ParentEntity.class, ParentEntity.Fields.homeId, 11003);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" < ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 11001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 11002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 11003);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(11001, 11002, 11003))
			.lt(parentEntity, ParentEntity.Fields.homeId, 11003);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" < ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 11001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 11002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 11003);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(11001, 11002, 11003))
			.lt(homeIdAttribute, 11003);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" < ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 11001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 11002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 11003);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void lteTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(12001).name("lte test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(12002).name("lte test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(12003).name("lte test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(12001, 12002, 12003))
			.lte(ParentEntity.class, ParentEntity.Fields.homeId, 12002);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" <= ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 12001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 12002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 12003);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(12001, 12002, 12003))
			.lte(parentEntity, ParentEntity.Fields.homeId, 12002);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" <= ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 12001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 12002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 12003);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(12001, 12002, 12003))
			.lte(homeIdAttribute, 12002);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" <= ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 12001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 12002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 12003);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void gtTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(13001).name("gt test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(13002).name("gt test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(13003).name("gt test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(13001, 13002, 13003))
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 13001);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" > ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 13001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 13002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 13003);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(13001, 13002, 13003))
			.gt(parentEntity, ParentEntity.Fields.homeId, 13001);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" > ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 13001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 13002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 13003);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(13001, 13002, 13003))
			.gt(homeIdAttribute, 13001);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" > ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 13001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 13002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 13003);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
	}
	
	@Test
	public void gteTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(14001).name("gte test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(14002).name("gte test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(14003).name("gte test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(14001, 14002, 14003))
			.gte(ParentEntity.class, ParentEntity.Fields.homeId, 14002);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" >= ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 14001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 14002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 14003);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(14001, 14002, 14003))
			.gte(parentEntity, ParentEntity.Fields.homeId, 14002);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" >= ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 14001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 14002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 14003);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(14001, 14002, 14003))
			.gte(homeIdAttribute, 14002);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" >= ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 14001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 14002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 14003);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
	}
	
	@Test
	public void betweenTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(15001).name("between test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(15002).name("between test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(15003).name("between test task 3").build());
		ParentEntity task4 = context.updateOrInsert(ParentEntity.builder().homeId(15004).name("between test task 4").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(15001, 15002, 15003, 15004))
			.between(ParentEntity.class, ParentEntity.Fields.homeId, 15002, 15003);
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" between ? and ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 15001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 15002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getHomeId() == 15003);
		boolean foundTask4_1 = results1.stream().anyMatch(t -> t.getHomeId() == 15004);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		Assert.assertFalse("Class-based: Should not find task4", foundTask4_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(15001, 15002, 15003, 15004))
			.between(parentEntity, ParentEntity.Fields.homeId, 15002, 15003);
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" between ? and ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 15001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 15002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getHomeId() == 15003);
		boolean foundTask4_2 = results2.stream().anyMatch(t -> t.getHomeId() == 15004);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		Assert.assertFalse("Entity-based: Should not find task4", foundTask4_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(15001, 15002, 15003, 15004))
			.between(homeIdAttribute, 15002, 15003);
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" between ? and ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 15001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 15002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getHomeId() == 15003);
		boolean foundTask4_3 = results3.stream().anyMatch(t -> t.getHomeId() == 15004);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
		Assert.assertFalse("Attribute-based: Should not find task4", foundTask4_3);
	}
	
	@Test
	public void inIfNotEmptyTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(16001).name("inIfNotEmpty test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(16002).name("inIfNotEmpty test task 2").build());
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(16001));
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" in (?)"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 16001);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(16001));
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" in (?)"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 16001);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(16001));
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" in (?)"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 16001);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<ParentEntity> queryEmpty = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(16001, 16002))
			.inIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, new ArrayList<>());
		
		List<ParentEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void inOrNullTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(17001).name("inOrNull test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(17002).name("inOrNull test task 2").externalName("external").build());
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(17001, 17002))
			.inOrNull(ParentEntity.class, ParentEntity.Fields.externalName, Arrays.asList("external"));
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?)"));
		Assert.assertTrue(queryString.contains(" is null"));
		
		// Should find both tasks - task2 because external name is in list, task1 because external name is null
		assertEquals(2, results.size());
	}
	
	@Test
	public void likeStartsWithTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(18001).name("prefix_test_task").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(18002).name("different_task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(18001, 18002))
			.likeStartsWith(ParentEntity.class, ParentEntity.Fields.name, "prefix");
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 18001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 18002);
		Assert.assertTrue("Class-based: Should find task1 with name starting with 'prefix'", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'prefix' at start", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(18001, 18002))
			.likeStartsWith(parentEntity, ParentEntity.Fields.name, "prefix");
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 18001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 18002);
		Assert.assertTrue("Entity-based: Should find task1 with name starting with 'prefix'", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'prefix' at start", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(18001, 18002))
			.likeStartsWith(nameAttribute, "prefix");
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 18001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 18002);
		Assert.assertTrue("Attribute-based: Should find task1 with name starting with 'prefix'", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'prefix' at start", foundTask2_3);
	}
	
	@Test
	public void likeEndsWithTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(19001).name("test_task_suffix").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(19002).name("different_task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(19001, 19002))
			.likeEndsWith(ParentEntity.class, ParentEntity.Fields.name, "suffix");
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 19001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 19002);
		Assert.assertTrue("Class-based: Should find task1 with name ending with 'suffix'", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'suffix' at end", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(19001, 19002))
			.likeEndsWith(parentEntity, ParentEntity.Fields.name, "suffix");
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 19001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 19002);
		Assert.assertTrue("Entity-based: Should find task1 with name ending with 'suffix'", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'suffix' at end", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(19001, 19002))
			.likeEndsWith(nameAttribute, "suffix");
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 19001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 19002);
		Assert.assertTrue("Attribute-based: Should find task1 with name ending with 'suffix'", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'suffix' at end", foundTask2_3);
	}
	
	@Test
	public void likeInTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(20001).name("pattern1_test").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(20002).name("pattern2_test").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(20003).name("different_test").build());
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(20001, 20002, 20003))
			.likeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 20001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 20002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getHomeId() == 20003);
		
		Assert.assertTrue("Should find task1 matching pattern1", foundTask1);
		Assert.assertTrue("Should find task2 matching pattern2", foundTask2);
		Assert.assertFalse("Should not find task3 not matching patterns", foundTask3);
	}
	
	@Test
	public void notLikeInTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(21001).name("pattern1_test").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(21002).name("pattern2_test").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(21003).name("different_test").build());
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(21001, 21002, 21003))
			.notLikeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 21001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 21002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getHomeId() == 21003);
		
		Assert.assertFalse("Should not find task1 matching pattern1", foundTask1);
		Assert.assertFalse("Should not find task2 matching pattern2", foundTask2);
		Assert.assertTrue("Should find task3 not matching patterns", foundTask3);
	}
	
	@Test
	public void inOrFalseIfEmptyTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(22001).name("inOrFalseIfEmpty test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(22002).name("inOrFalseIfEmpty test task 2").build());
		
		// Test with non-empty list
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.inOrFalseIfEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(22001));
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" in (?)"));
		boolean foundTask1 = results1.stream().anyMatch(t -> t.getHomeId() == 22001);
		Assert.assertTrue("Should find task1", foundTask1);
		
		// Test with empty list - should return false (no results)
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(22001, 22002))
			.inOrFalseIfEmpty(ParentEntity.class, ParentEntity.Fields.homeId, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("false"));
		assertEquals(0, results2.size());
	}
	
	@Test
	public void nativeCriterionTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(23001).name("native test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(23002).name("native test task 2").build());
		
		// Test 1: Class-based method signature
		SelectQuery<ParentEntity> query1 = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(23001, 23002))
			.nativeCriterion(ParentEntity.class, ParentEntity.Fields.homeId, "> 23001");
		
		String queryString1 = context.queryAsString(query1);
		List<ParentEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("> 23001"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getHomeId() == 23001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getHomeId() == 23002);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query2 = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(23001, 23002))
			.nativeCriterion(parentEntity, ParentEntity.Fields.homeId, "> 23001");
		
		String queryString2 = context.queryAsString(query2);
		List<ParentEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("> 23001"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getHomeId() == 23001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getHomeId() == 23002);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query3 = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(23001, 23002))
			.nativeCriterion(homeIdAttribute, "> 23001");
		
		String queryString3 = context.queryAsString(query3);
		List<ParentEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("> 23001"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getHomeId() == 23001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getHomeId() == 23002);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
	}
	
	@Test
	public void inSubQueryTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(24001).name("inSubQuery test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(24002).name("inSubQuery test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(24003).name("inSubQuery test task 3").build());
		
		// Create subquery that selects homeIds > 24001
		SelectQuery<ParentEntity> subQuery = Estivate.selectQuery(ParentEntity.class)
			.select(ParentEntity.class, ParentEntity.Fields.homeId)
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 24001);
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(24001, 24002, 24003))
			.inSubQuery(ParentEntity.class, ParentEntity.Fields.homeId, subQuery);
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (SELECT"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 24001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 24002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getHomeId() == 24003);
		
		Assert.assertFalse("Should not find task1", foundTask1);
		Assert.assertTrue("Should find task2", foundTask2);
		Assert.assertTrue("Should find task3", foundTask3);
	}
	
	@Test
	public void notInSubQueryTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(25001).name("notInSubQuery test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(25002).name("notInSubQuery test task 2").build());
		ParentEntity task3 = context.updateOrInsert(ParentEntity.builder().homeId(25003).name("notInSubQuery test task 3").build());
		
		// Create subquery that selects homeIds > 25001
		SelectQuery<ParentEntity> subQuery = Estivate.selectQuery(ParentEntity.class)
			.select(ParentEntity.class, ParentEntity.Fields.homeId)
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 25001);
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(25001, 25002, 25003))
			.notInSubQuery(ParentEntity.class, ParentEntity.Fields.homeId, subQuery);
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not in (SELECT"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 25001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 25002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getHomeId() == 25003);
		
		Assert.assertTrue("Should find task1", foundTask1);
		Assert.assertFalse("Should not find task2", foundTask2);
		Assert.assertFalse("Should not find task3", foundTask3);
	}
	
	@Test
	public void existsTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(26001).name("exists test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(26002).name("exists test task 2").build());
		
		ChildEntity child1 = context.updateOrInsert(ChildEntity.builder().parentId(task1.getId()).description("child for task1").build());
		
		// Create subquery that checks for children
		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
			.select(ChildEntity.class, AbstractEntity.Fields.id)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(26001, 26002))
			.exists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("EXISTS"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 26001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 26002);
		
		Assert.assertTrue("Should find task1 which has children", foundTask1);
		Assert.assertFalse("Should not find task2 which has no children", foundTask2);
	}
	
	@Test
	public void notExistsTest() throws SQLException {
		
		ParentEntity task1 = context.updateOrInsert(ParentEntity.builder().homeId(27001).name("notExists test task 1").build());
		ParentEntity task2 = context.updateOrInsert(ParentEntity.builder().homeId(27002).name("notExists test task 2").build());
		
		ChildEntity child1 = context.updateOrInsert(ChildEntity.builder().parentId(task1.getId()).description("child for task1").build());
		
		// Create subquery that checks for children
		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
			.select(ChildEntity.class, AbstractEntity.Fields.id)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(27001, 27002))
			.notExists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<ParentEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("NOT EXISTS"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getHomeId() == 27001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getHomeId() == 27002);
		
		Assert.assertFalse("Should not find task1 which has children", foundTask1);
		Assert.assertTrue("Should find task2 which has no children", foundTask2);
	}
	
	// ========================================
	// FOCUSED TESTS - One method per SelectQuery signature
	// Using queryAsString for efficiency
	// ========================================
	
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eq(homeIdAttribute, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.between(ParentEntity.class, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.between(parentEntity, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notIn(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNull(ParentEntity.class, ParentEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNull(parentEntity, ParentEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNotNull(ParentEntity.class, ParentEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNotNull(parentEntity, ParentEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.isNotNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.like(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.like(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLike(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLike(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContains(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContains(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void eqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqNullable(ParentEntity.class, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqNullable(parentEntity, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.externalName);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.eqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void inIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyNullable(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyNullable(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyNullable(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.inIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmptyOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmptyOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notInIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void likeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContains(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContains(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContainsIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContainsIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notLikeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notEqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqNullable(ParentEntity.class, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqNullable(parentEntity, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.externalName);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.notEqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void ltIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.ltIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.ltIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.ltIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void lteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lteIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lteIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.lteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void gtIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gtIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gtIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gtIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gteIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gteIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.gteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void betweenIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.betweenIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.betweenIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.betweenIfNotNull(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void likeIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIfNotNull(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeIfNotNull(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeInIfNotEmpty(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.likeContainsInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
}
