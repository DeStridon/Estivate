package com.estivate.test.query;

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
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Attribute;
import com.estivate.query.EstivateNode;
import com.estivate.query.SelectQuery;
import com.estivate.result.Result;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void insertTest() {
		
		CustomerEntity customer1 = context.updateOrInsert(CustomerEntity.builder().id(1).name("customer 1").build());
		
		assertEquals(new Date().getTime(), customer1.getCreated().getTime(), 100);
		assertNull(customer1.getUpdated());
		assertNotNull(customer1.getId());
	
		customer1.setName("customer 1");
		
		context.updateOrInsert(customer1);
		
		assertNotNull(customer1.getUpdated());
		
	}
	
	
	@Test 
	public void automatedJoinTest() {
		
		CustomerEntity customer2 = context.updateOrInsert(CustomerEntity.builder().id(1).name("customer 2").build());

		OrderEntity order21 = context.updateOrInsert(OrderEntity.builder().customerId(customer2.getId()).build());
		OrderEntity order22 = context.updateOrInsert(OrderEntity.builder().customerId(customer2.getId()).build());
		OrderEntity order23 = context.updateOrInsert(OrderEntity.builder().customerId(customer2.getId()).build());
		
		CustomerEntity customer3 = context.updateOrInsert(CustomerEntity.builder().id(1).name("customer 3").build());

		OrderEntity order31 = context.updateOrInsert(OrderEntity.builder().customerId(customer3.getId()).build());
		OrderEntity order32 = context.updateOrInsert(OrderEntity.builder().customerId(customer3.getId()).build());
		
		SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
				.joinInner(OrderEntity.class, CustomerEntity.class)
				.selectDistinct(CustomerEntity.class, AbstractEntity.Fields.id)
				.selectAll(CustomerEntity.class)
				
				.eq(CustomerEntity.class, CustomerEntity.Fields.name, "parent 2");
		
		List<Result> results = query.fetchListAsResults(context);
		
		assertEquals(1, results.size());
		
		for(Result result : results) {
			OrderEntity child = result.mapTo(OrderEntity.class);
			CustomerEntity parent = result.mapTo(CustomerEntity.class);
		}
		
	}
	
	@Test
	public void queryTest() {
		
		CustomerEntity testTask = CustomerEntity.builder()
				.id(4)
				.name("queryTest test task")
				.email("queryTest test task@test.com")
				.address("queryTest test task address")
				.country(CustomerEntity.Country.USA)
				.emailVerified(true)
				.archived(new Date())
				.created(new Date())
				.updated(new Date())
				.build();
		
		context.updateOrInsert(testTask);
		
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity.class, CustomerEntity.Fields.name, "queryTest test task")
				.lt(CustomerEntity.class, AbstractEntity.Fields.id, 5)
				.lte(CustomerEntity.class, AbstractEntity.Fields.id, 4)
				.gt(CustomerEntity.class, AbstractEntity.Fields.id, 1)
				.gte(CustomerEntity.class,  AbstractEntity.Fields.id, 4)
				.between(CustomerEntity.class, AbstractEntity.Fields.id, 3, 7)
				.notEq(CustomerEntity.class, CustomerEntity.Fields.name, "external Name 2")
				.in(CustomerEntity.class, CustomerEntity.Fields.country, Arrays.asList(CustomerEntity.Country.USA, CustomerEntity.Country.UK))
				
				.eqIfNotNull(CustomerEntity.class, CustomerEntity.Fields.created, null)
				.ltIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 5)
				.lteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 4)
				.gtIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1)
				.gteIfNotNull(CustomerEntity.class,  AbstractEntity.Fields.id, 4)
				.betweenIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 3, 7)
				.notEqIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "external Name 2");
		
		List<CustomerEntity> tasks = query.fetchListAs(context, CustomerEntity.class);
		
		Assert.assertEquals(1, tasks.size());
	
	}
	
	@Test
	public void taskEnumTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1, 2, 3, 4))
			.in(CustomerEntity.class, CustomerEntity.Fields.country, Arrays.asList(CustomerEntity.Country.USA, CustomerEntity.Country.UK));
		
		query.fetchList(context);
		
		System.out.println(context.queryAsString(query));
	
	}
	
	@Test
	public void inTest() {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(1234).name("task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(1235).name("task 2").build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1234, 1235));
		
		assertEquals(2, context.fetchList(query).size());
		
		SelectQuery<CustomerEntity> query2 = query.clone().in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query2).size());

		SelectQuery<CustomerEntity> query3 = query.clone().notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query3).size());

		SelectQuery<CustomerEntity> query4 = query.clone().notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1235));
		assertEquals(1, context.fetchList(query4).size());
		
	}
	
	@Test
	public void in2Test() {
		
		CustomerEntity parent1 = context.updateOrInsert(CustomerEntity.builder().id(2234).name("task 1").build());
		CustomerEntity parent2 = context.updateOrInsert(CustomerEntity.builder().id(2235).name("task 2").build());
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class);

		Entity<CustomerEntity> taskEntity = new Entity<>(CustomerEntity.class);
		
		query.in(taskEntity, AbstractEntity.Fields.id, Arrays.asList(2234, 2235));
		assertEquals(2, context.fetchList(query).size());
		
		query.in(taskEntity, AbstractEntity.Fields.id, Arrays.asList(2235));
		assertEquals(1, context.fetchList(query).size());
		
		query.notIn(taskEntity, AbstractEntity.Fields.id, Arrays.asList(2235));
		assertEquals(0, context.fetchList(query).size());
		
	}

	
	
	
	@Test
	public void inCollectionTest() throws SQLException {
		
		List<Long> taskIds = Arrays.asList(1L, 2L, 3L, 4L);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class).in(CustomerEntity.class, AbstractEntity.Fields.id, taskIds);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?, ?, ?, ?)"));
		
	}
	
	@Test
	public void isNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class).isNull(CustomerEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" is null"));
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class).isNull(parentEntity, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" is null"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class).isNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" is null"));
	}
	
	@Test
	public void isNotNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class).isNotNull(CustomerEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" is not null"));
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class).isNotNull(parentEntity, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" is not null"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class).isNotNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" is not null"));
	}
	
	@Test
	public void likeTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class).like(CustomerEntity.class, CustomerEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" like ?"));
		
	}
	
	@Test
	public void notLikeTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" not like ?"));
		
	}
	
	
	@Test
	public void eqOrNullTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1, 3, 5))
				.eqOrNull(CustomerEntity.class, CustomerEntity.Fields.created, new Date());
		
		System.out.println(context.queryAsString(query));
		
		
	}
	
	@Test
	public void orAggregatorTest() {
		Aggregator or = Estivate.or();
		or.add(Estivate.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1));
		or.add(Estivate.eq(CustomerEntity.class, AbstractEntity.Fields.id, 2));
	}
	
	@Test
	public void inIfNotEmptyNullableTest() {
		List<String> names = new ArrayList<>();
		names.add(null);
		EstivateNode node = Estivate.inIfNotEmptyNullable(CustomerEntity.class, CustomerEntity.Fields.name, names);
		System.out.println(node.toString());
	}
	

	@Test
	public void notInTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(3001).name("notIn task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(3002).name("notIn task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(3003).name("notIn task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(3001, 3003));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?, ?)"));
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 3002);
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 3001);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 3003);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(parentEntity, AbstractEntity.Fields.id, Arrays.asList(3001, 3003));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("not in (?, ?)"));
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 3002);
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 3001);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 3003);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(3001, 3003));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("not in (?, ?)"));
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 3002);
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 3001);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 3003);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void notInIfNotEmptyTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(4001).name("notInIfNotEmpty task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(4002).name("notInIfNotEmpty task 2").build());
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(4001));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?)"));
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 4002);
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 4001);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(parentEntity, AbstractEntity.Fields.id, Arrays.asList(4001));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("not in (?)"));
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 4002);
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 4001);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(homeIdAttribute, Arrays.asList(4001));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("not in (?)"));
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 4002);
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 4001);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryEmpty = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(4001, 4002))
			.notInIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		List<CustomerEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void notInOrTrueIfEmptyTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(5001).name("notInOrTrueIfEmpty task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(5002).name("notInOrTrueIfEmpty task 2").build());
		
		// Test with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notInOrTrueIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(5001));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("not in (?)"));
		
		boolean foundTask2 = results1.stream().anyMatch(t -> t.getId() == 5002);
		boolean foundTask1 = results1.stream().anyMatch(t -> t.getId() == 5001);
		
		Assert.assertTrue("Should find task2", foundTask2);
		Assert.assertFalse("Should not find task1", foundTask1);
		
		// Test with empty list - should return true (all results)
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(5001, 5002))
			.notInOrTrueIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("true"));
		assertEquals(2, results2.size());
	}
	
	@Test
	public void likeContainsTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(6001).name("likeContains search test").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(6002).name("different content").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "search");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 6001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 6002);
		Assert.assertTrue("Class-based: Should find task1 with 'search' in name", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'search' in name", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(parentEntity, CustomerEntity.Fields.name, "search");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 6001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 6002);
		Assert.assertTrue("Entity-based: Should find task1 with 'search' in name", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'search' in name", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(nameAttribute, "search");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 6001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 6002);
		Assert.assertTrue("Attribute-based: Should find task1 with 'search' in name", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'search' in name", foundTask2_3);
	}
	
	@Test
	public void notLikeContainsTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(7001).name("notLikeContains exclude test").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(7002).name("different content").build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(7001, 7002))
			.notLikeContains(CustomerEntity.class, CustomerEntity.Fields.name, "exclude");
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 7001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 7002);
		
		Assert.assertFalse("Should not find task1 with 'exclude' in name", foundTask1);
		Assert.assertTrue("Should find task2 without 'exclude' in name", foundTask2);
	}
	
	@Test
	public void eqWithClassTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 8001);

		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithEntityTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(parentEntity, AbstractEntity.Fields.id, 8001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithAttributeTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(homeIdAttribute, 8001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void notEqTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(9001).name("notEq test task").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(9002).name("different task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(9001, 9002))
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, 9001);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" != ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 9001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 9002);
		Assert.assertFalse("Class-based: Should not find task1 with homeId 9001", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2 with different homeId", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(9001, 9002))
			.notEq(parentEntity, AbstractEntity.Fields.id, 9001);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" != ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 9001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 9002);
		Assert.assertFalse("Entity-based: Should not find task1 with homeId 9001", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2 with different homeId", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(9001, 9002))
			.notEq(homeIdAttribute, 9001);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" != ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 9001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 9002);
		Assert.assertFalse("Attribute-based: Should not find task1 with homeId 9001", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2 with different homeId", foundTask2_3);
	}
	
	@Test
	public void eqIfNotNullTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(10001).name("eqIfNotNull test task").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(10002).name("different task").build());
		
		// Test 1: Class-based method signature with non-null value
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 10001);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 10001);
		Assert.assertTrue("Class-based: Should find task1 with homeId 10001", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-null value
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(parentEntity, AbstractEntity.Fields.id, 10001);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" = ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 10001);
		Assert.assertTrue("Entity-based: Should find task1 with homeId 10001", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-null value
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(homeIdAttribute, 10001);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" = ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 10001);
		Assert.assertTrue("Attribute-based: Should find task1 with homeId 10001", foundTask1_3);
		
		// Test with null value - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryNull = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(10001, 10002))
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, null);
		
		List<CustomerEntity> resultsNull = context.fetchList(queryNull);
		assertEquals(2, resultsNull.size());
	}
	
	@Test
	public void eqNullableTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(10101).name("eqNullable test task").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(10102).name("different task").email("external").build());
		
		// Test with non-null value
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(10101, 10102))
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundTask2 = results1.stream().anyMatch(t -> t.getId() == 10102);
		Assert.assertTrue("Should find task2 with external name", foundTask2);
		
		// Test with null value - should find entities with null external name
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(10101, 10102))
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, null);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" is null"));
		boolean foundTask1 = results2.stream().anyMatch(t -> t.getId() == 10101);
		Assert.assertTrue("Should find task1 with null external name", foundTask1);
	}
	
	@Test
	public void ltTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(11001).name("lt test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(11002).name("lt test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(11003).name("lt test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(11001, 11002, 11003))
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, 11003);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" < ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 11001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 11002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 11003);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(11001, 11002, 11003))
			.lt(parentEntity, AbstractEntity.Fields.id, 11003);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" < ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 11001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 11002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 11003);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(11001, 11002, 11003))
			.lt(homeIdAttribute, 11003);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" < ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 11001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 11002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 11003);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void lteTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(12001).name("lte test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(12002).name("lte test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(12003).name("lte test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(12001, 12002, 12003))
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, 12002);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" <= ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 12001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 12002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 12003);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(12001, 12002, 12003))
			.lte(parentEntity, AbstractEntity.Fields.id, 12002);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" <= ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 12001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 12002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 12003);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(12001, 12002, 12003))
			.lte(homeIdAttribute, 12002);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" <= ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 12001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 12002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 12003);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundTask3_3);
	}
	
	@Test
	public void gtTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(13001).name("gt test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(13002).name("gt test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(13003).name("gt test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(13001, 13002, 13003))
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 13001);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" > ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 13001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 13002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 13003);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(13001, 13002, 13003))
			.gt(parentEntity, AbstractEntity.Fields.id, 13001);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" > ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 13001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 13002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 13003);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(13001, 13002, 13003))
			.gt(homeIdAttribute, 13001);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" > ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 13001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 13002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 13003);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
	}
	
	@Test
	public void gteTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(14001).name("gte test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(14002).name("gte test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(14003).name("gte test task 3").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(14001, 14002, 14003))
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, 14002);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" >= ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 14001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 14002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 14003);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(14001, 14002, 14003))
			.gte(parentEntity, AbstractEntity.Fields.id, 14002);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" >= ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 14001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 14002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 14003);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(14001, 14002, 14003))
			.gte(homeIdAttribute, 14002);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" >= ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 14001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 14002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 14003);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
	}
	
	@Test
	public void betweenTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(15001).name("between test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(15002).name("between test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(15003).name("between test task 3").build());
		CustomerEntity task4 = context.updateOrInsert(CustomerEntity.builder().id(15004).name("between test task 4").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(15001, 15002, 15003, 15004))
			.between(CustomerEntity.class, AbstractEntity.Fields.id, 15002, 15003);
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" between ? and ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 15001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 15002);
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == 15003);
		boolean foundTask4_1 = results1.stream().anyMatch(t -> t.getId() == 15004);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertTrue("Class-based: Should find task3", foundTask3_1);
		Assert.assertFalse("Class-based: Should not find task4", foundTask4_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(15001, 15002, 15003, 15004))
			.between(parentEntity, AbstractEntity.Fields.id, 15002, 15003);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" between ? and ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 15001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 15002);
		boolean foundTask3_2 = results2.stream().anyMatch(t -> t.getId() == 15003);
		boolean foundTask4_2 = results2.stream().anyMatch(t -> t.getId() == 15004);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		Assert.assertTrue("Entity-based: Should find task3", foundTask3_2);
		Assert.assertFalse("Entity-based: Should not find task4", foundTask4_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(15001, 15002, 15003, 15004))
			.between(homeIdAttribute, 15002, 15003);
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" between ? and ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 15001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 15002);
		boolean foundTask3_3 = results3.stream().anyMatch(t -> t.getId() == 15003);
		boolean foundTask4_3 = results3.stream().anyMatch(t -> t.getId() == 15004);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
		Assert.assertTrue("Attribute-based: Should find task3", foundTask3_3);
		Assert.assertFalse("Attribute-based: Should not find task4", foundTask4_3);
	}
	
	@Test
	public void inIfNotEmptyTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(16001).name("inIfNotEmpty test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(16002).name("inIfNotEmpty test task 2").build());
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(16001));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" in (?)"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 16001);
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(parentEntity, AbstractEntity.Fields.id, Arrays.asList(16001));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" in (?)"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 16001);
		Assert.assertTrue("Entity-based: Should find task1", foundTask1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(16001));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" in (?)"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 16001);
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryEmpty = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(16001, 16002))
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		List<CustomerEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void inOrNullTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(17001).name("inOrNull test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(17002).name("inOrNull test task 2").build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(17001, 17002))
			.inOrNull(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("external"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (?)"));
		Assert.assertTrue(queryString.contains(" is null"));
		
		// Should find both tasks - task2 because external name is in list, task1 because external name is null
		assertEquals(2, results.size());
	}
	
	@Test
	public void likeStartsWithTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(18001).name("prefix_test_task").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(18002).name("different_task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(18001, 18002))
			.likeStartsWith(CustomerEntity.class, CustomerEntity.Fields.name, "prefix");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 18001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 18002);
		Assert.assertTrue("Class-based: Should find task1 with name starting with 'prefix'", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'prefix' at start", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(18001, 18002))
			.likeStartsWith(parentEntity, CustomerEntity.Fields.name, "prefix");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 18001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 18002);
		Assert.assertTrue("Entity-based: Should find task1 with name starting with 'prefix'", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'prefix' at start", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(18001, 18002))
			.likeStartsWith(nameAttribute, "prefix");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 18001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 18002);
		Assert.assertTrue("Attribute-based: Should find task1 with name starting with 'prefix'", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'prefix' at start", foundTask2_3);
	}
	
	@Test
	public void likeEndsWithTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(19001).name("test_task_suffix").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(19002).name("different_task").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(19001, 19002))
			.likeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "suffix");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" like ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 19001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 19002);
		Assert.assertTrue("Class-based: Should find task1 with name ending with 'suffix'", foundTask1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'suffix' at end", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(19001, 19002))
			.likeEndsWith(parentEntity, CustomerEntity.Fields.name, "suffix");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" like ?"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 19001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 19002);
		Assert.assertTrue("Entity-based: Should find task1 with name ending with 'suffix'", foundTask1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'suffix' at end", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(19001, 19002))
			.likeEndsWith(nameAttribute, "suffix");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" like ?"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 19001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 19002);
		Assert.assertTrue("Attribute-based: Should find task1 with name ending with 'suffix'", foundTask1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'suffix' at end", foundTask2_3);
	}
	
	@Test
	public void likeInTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(20001).name("pattern1_test").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(20002).name("pattern2_test").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(20003).name("different_test").build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(20001, 20002, 20003))
			.likeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 20001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 20002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getId() == 20003);
		
		Assert.assertTrue("Should find task1 matching pattern1", foundTask1);
		Assert.assertTrue("Should find task2 matching pattern2", foundTask2);
		Assert.assertFalse("Should not find task3 not matching patterns", foundTask3);
	}
	
	@Test
	public void notLikeInTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(21001).name("pattern1_test").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(21002).name("pattern2_test").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(21003).name("different_test").build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(21001, 21002, 21003))
			.notLikeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not like ?"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 21001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 21002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getId() == 21003);
		
		Assert.assertFalse("Should not find task1 matching pattern1", foundTask1);
		Assert.assertFalse("Should not find task2 matching pattern2", foundTask2);
		Assert.assertTrue("Should find task3 not matching patterns", foundTask3);
	}
	
	@Test
	public void inOrFalseIfEmptyTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(22001).name("inOrFalseIfEmpty test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(22002).name("inOrFalseIfEmpty test task 2").build());
		
		// Test with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.inOrFalseIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(22001));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" in (?)"));
		boolean foundTask1 = results1.stream().anyMatch(t -> t.getId() == 22001);
		Assert.assertTrue("Should find task1", foundTask1);
		
		// Test with empty list - should return false (no results)
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(22001, 22002))
			.inOrFalseIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("false"));
		assertEquals(0, results2.size());
	}
	
	@Test
	public void nativeCriterionTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(23001).name("native test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(23002).name("native test task 2").build());
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(23001, 23002))
			.nativeCriterion(CustomerEntity.class, AbstractEntity.Fields.id, "> 23001");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("> 23001"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == 23001);
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == 23002);
		Assert.assertFalse("Class-based: Should not find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(23001, 23002))
			.nativeCriterion(parentEntity, AbstractEntity.Fields.id, "> 23001");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("> 23001"));
		boolean foundTask1_2 = results2.stream().anyMatch(t -> t.getId() == 23001);
		boolean foundTask2_2 = results2.stream().anyMatch(t -> t.getId() == 23002);
		Assert.assertFalse("Entity-based: Should not find task1", foundTask1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundTask2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(23001, 23002))
			.nativeCriterion(homeIdAttribute, "> 23001");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("> 23001"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == 23001);
		boolean foundTask2_3 = results3.stream().anyMatch(t -> t.getId() == 23002);
		Assert.assertFalse("Attribute-based: Should not find task1", foundTask1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundTask2_3);
	}
	
	@Test
	public void inSubQueryTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(24001).name("inSubQuery test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(24002).name("inSubQuery test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(24003).name("inSubQuery test task 3").build());
		
		// Create subquery that selects homeIds > 24001
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.select(CustomerEntity.class, AbstractEntity.Fields.id)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 24001);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(24001, 24002, 24003))
			.inSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" in (SELECT"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 24001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 24002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getId() == 24003);
		
		Assert.assertFalse("Should not find task1", foundTask1);
		Assert.assertTrue("Should find task2", foundTask2);
		Assert.assertTrue("Should find task3", foundTask3);
	}
	
	@Test
	public void notInSubQueryTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(25001).name("notInSubQuery test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(25002).name("notInSubQuery test task 2").build());
		CustomerEntity task3 = context.updateOrInsert(CustomerEntity.builder().id(25003).name("notInSubQuery test task 3").build());
		
		// Create subquery that selects homeIds > 25001
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.select(CustomerEntity.class, AbstractEntity.Fields.id)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 25001);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(25001, 25002, 25003))
			.notInSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" not in (SELECT"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 25001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 25002);
		boolean foundTask3 = results.stream().anyMatch(t -> t.getId() == 25003);
		
		Assert.assertTrue("Should find task1", foundTask1);
		Assert.assertFalse("Should not find task2", foundTask2);
		Assert.assertFalse("Should not find task3", foundTask3);
	}
	
	@Test
	public void existsTest() throws SQLException {
		
		CustomerEntity task1 = context.updateOrInsert(CustomerEntity.builder().id(26001).name("exists test task 1").build());
		CustomerEntity task2 = context.updateOrInsert(CustomerEntity.builder().id(26002).name("exists test task 2").build());
		
		OrderEntity child1 = context.updateOrInsert(OrderEntity.builder().customerId(task1.getId()).build());
		
		// Create subquery that checks for children
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.select(OrderEntity.class, AbstractEntity.Fields.id)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(26001, 26002))
			.exists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("EXISTS"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 26001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 26002);
		
		Assert.assertTrue("Should find task1 which has children", foundTask1);
		Assert.assertFalse("Should not find task2 which has no children", foundTask2);
	}
	
	@Test
	public void notExistsTest() throws SQLException {
		
		CustomerEntity customer1 = context.updateOrInsert(CustomerEntity.builder().id(27001).name("notExists test task 1").build());
		CustomerEntity customer2 = context.updateOrInsert(CustomerEntity.builder().id(27002).name("notExists test task 2").build());
		
		OrderEntity child1 = context.updateOrInsert(OrderEntity.builder().customerId(customer1.getId()).build());
		
		// Create subquery that checks for children
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.select(OrderEntity.class, AbstractEntity.Fields.id)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(27001, 27002))
			.notExists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("NOT EXISTS"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == 27001);
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == 27002);
		
		Assert.assertFalse("Should not find task1 which has children", foundTask1);
		Assert.assertTrue("Should find task2 which has no children", foundTask2);
	}
	
	// ========================================
	// FOCUSED TESTS - One method per SelectQuery signature
	// Using queryAsString for efficiency
	// ========================================
	
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(parentEntity, AbstractEntity.Fields.id, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(homeIdAttribute, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("HOMEID_D"));
	}
	
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(parentEntity, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(parentEntity, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME_D  is null"));
	}
	
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(parentEntity, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME_D  is not null"));
	}
	
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(parentEntity, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(parentEntity, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void eqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(parentEntity, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void inIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(parentEntity, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void likeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void likeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notEqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(parentEntity, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void ltIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void lteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void gtIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void betweenIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(parentEntity, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void likeIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(parentEntity, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(parentEntity, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> parentEntity = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(parentEntity, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
}
