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
import com.estivate.result.ResultRow;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void insertTest() {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("customer 1").build();
		context.updateOrInsert(customer1);
		
		assertEquals(new Date().getTime(), customer1.getCreated().getTime(), 100);
		assertNull(customer1.getUpdated());
		assertNotNull(customer1.getId());
		
	}
	
	
	@Test 
	public void automatedJoinTest() {
		
		CustomerEntity customer2 = CustomerEntity.builder().name("customer 2").build();
		context.updateOrInsert(customer2);

		OrderEntity order21 = OrderEntity.builder().customerId(customer2.getId()).build();
		OrderEntity order22 = OrderEntity.builder().customerId(customer2.getId()).build();
		OrderEntity order23 = OrderEntity.builder().customerId(customer2.getId()).build();
		context.updateOrInsert(order21);
		context.updateOrInsert(order22);
		context.updateOrInsert(order23);
		
		CustomerEntity customer3 = CustomerEntity.builder().name("customer 3").build();
		context.updateOrInsert(customer3);

		OrderEntity order31 = OrderEntity.builder().customerId(customer3.getId()).build();
		OrderEntity order32 = OrderEntity.builder().customerId(customer3.getId()).build();
		context.updateOrInsert(order31);
		context.updateOrInsert(order32);
		SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
				.joinInner(OrderEntity.class, CustomerEntity.class)
				.distinct()
				.selectAll(CustomerEntity.class)
				.eq(CustomerEntity.class, CustomerEntity.Fields.name, "customer 2");
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue(queryString.contains("DISTINCT"));
		
		List<ResultRow> results = query.fetch(context).getRows();
		
		assertEquals(1, results.size());
		
		for(ResultRow result : results) {
			OrderEntity child = result.mapTo(OrderEntity.class);
			CustomerEntity parent = result.mapTo(CustomerEntity.class);
		}
		
	}
	
	@Test
	public void queryTest() {
		
		CustomerEntity testTask = CustomerEntity.builder()
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
				.lt(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()+1)
				.lte(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId())
				.gt(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()-1)
				.gte(CustomerEntity.class,  AbstractEntity.Fields.id, testTask.getId())
				.between(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()-2, testTask.getId()+2)
				.notEq(CustomerEntity.class, CustomerEntity.Fields.name, "external Name 2")
				.in(CustomerEntity.class, CustomerEntity.Fields.country, Arrays.asList(CustomerEntity.Country.USA, CustomerEntity.Country.UK))
				
				.eqIfNotNull(CustomerEntity.class, CustomerEntity.Fields.created, null)
				.ltIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()+1)
				.lteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId())
				.gtIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()-1)
				.gteIfNotNull(CustomerEntity.class,  AbstractEntity.Fields.id, testTask.getId())
				.betweenIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, testTask.getId()-2, testTask.getId()+2)
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
		
		CustomerEntity customer1 = CustomerEntity.builder().name("task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()));
		
		assertEquals(2, context.fetchList(query).size());

		query.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer2.getId()));
		assertEquals(1, context.fetchList(query).size());
		
	}
	
	@Test
	public void in2Test() {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class);

		Entity<CustomerEntity> taskEntity = new Entity<>(CustomerEntity.class);
		
		query.in(taskEntity, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()));
		assertEquals(2, context.fetchList(query).size());
		
		query.in(taskEntity, AbstractEntity.Fields.id, Arrays.asList(customer2.getId()));
		assertEquals(1, context.fetchList(query).size());
		
		query.notIn(taskEntity, AbstractEntity.Fields.id, Arrays.asList(customer2.getId()));
		assertEquals(0, context.fetchList(query).size());
		
	}

	
	
	
	@Test
	public void inCollectionTest() throws SQLException {
		
		List<Long> taskIds = Arrays.asList(1L, 2L, 3L, 4L);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class).in(CustomerEntity.class, AbstractEntity.Fields.id, taskIds);

		String queryString = context.queryAsString(query);
		context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" IN (?, ?, ?, ?)"));
		
	}
	
	@Test
	public void isNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class).isNull(CustomerEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" IS NULL"));
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class).isNull(customer, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" IS NULL"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class).isNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" IS NULL"));
	}
	
	@Test
	public void isNotNullTest() throws SQLException {
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class).isNotNull(CustomerEntity.class, AbstractEntity.Fields.id);
		String queryString1 = context.queryAsString(query1);
		query1.fetchList(context);
		Assert.assertTrue(queryString1.contains(" IS NOT NULL"));
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class).isNotNull(customer, AbstractEntity.Fields.id);
		String queryString2 = context.queryAsString(query2);
		query2.fetchList(context);
		Assert.assertTrue(queryString2.contains(" IS NOT NULL"));
		
		// Test 3: Attribute-based method signature
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class).isNotNull(idAttribute);
		String queryString3 = context.queryAsString(query3);
		query3.fetchList(context);
		Assert.assertTrue(queryString3.contains(" IS NOT NULL"));
	}
	
	@Test
	public void likeTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class).like(CustomerEntity.class, CustomerEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" LIKE ?"));
		
	}
	
	@Test
	public void notLikeTest() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "task%");
		
		String queryString = context.queryAsString(query);
		query.fetchList(context);

		Assert.assertTrue(queryString.contains(" NOT LIKE ?"));
		
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
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notIn customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("notIn customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("notIn customer 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer3.getId()));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("NOT IN (?, ?)"));
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Class-based: Should find customer2", foundCustomer2_1);
		Assert.assertFalse("Class-based: Should not find customer1", foundCustomer1_1);
		Assert.assertFalse("Class-based: Should not find customer3", foundCustomer3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer3.getId()));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("NOT IN (?, ?)"));
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		Assert.assertFalse("Entity-based: Should not find customer3", foundCustomer3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(customer1.getId(), customer3.getId()));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("NOT IN (?, ?)"));
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		Assert.assertFalse("Attribute-based: Should not find customer3", foundCustomer3_3);
	}
	
	@Test
	public void notInIfNotEmptyTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notInIfNotEmpty task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("notInIfNotEmpty task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("NOT IN (?)"));
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Class-based: Should find customer2", foundCustomer2_1);
		Assert.assertFalse("Class-based: Should not find customer1", foundCustomer1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("NOT IN (?)"));
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmpty(homeIdAttribute, Arrays.asList(customer1.getId()));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("NOT IN (?)"));
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryEmpty = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notInIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		List<CustomerEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void notInOrTrueIfEmptyTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notInOrTrueIfEmpty customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("notInOrTrueIfEmpty customer 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.notInOrTrueIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("NOT IN (?)"));
		
		boolean foundCustomer2 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		
		Assert.assertTrue("Should find customer2", foundCustomer2);
		Assert.assertFalse("Should not find customer1", foundCustomer1);
		
		// Test with empty list - should return true (all results)
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notInOrTrueIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("true"));
		assertEquals(2, results2.size());
	}
	
	@Test
	public void likeContainsTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("likeContains search test").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different content").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "search");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" LIKE ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Class-based: Should find customer1 with 'search' in name", foundCustomer1_1);
		Assert.assertFalse("Class-based: Should not find customer2 without 'search' in name", foundCustomer2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(customer, CustomerEntity.Fields.name, "search");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" LIKE ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Entity-based: Should find customer1 with 'search' in name", foundCustomer1_2);
		Assert.assertFalse("Entity-based: Should not find customer2 without 'search' in name", foundCustomer2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(nameAttribute, "search");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
			
		Assert.assertTrue(queryString3.contains(" LIKE ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Attribute-based: Should find customer1 with 'search' in name", foundCustomer1_3);
		Assert.assertFalse("Attribute-based: Should not find customer2 without 'search' in name", foundCustomer2_3);
	}
	
	@Test
	public void notLikeContainsTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notLikeContains exclude test").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different content").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notLikeContains(CustomerEntity.class, CustomerEntity.Fields.name, "exclude");
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" NOT LIKE ?"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		
		Assert.assertFalse("Should not find customer1 with 'exclude' in name", foundCustomer1);
		Assert.assertTrue("Should find customer2 without 'exclude' in name", foundCustomer2);
	}
	
	@Test
	public void eqWithClassTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1);

		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithEntityTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(customer, AbstractEntity.Fields.id, 1);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithAttributeTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(homeIdAttribute, 1);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void notEqTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notEq test task").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different task").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" != ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Class-based: Should not find customer1 with homeId " + customer1.getId(), foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find customer2 with different id", foundCustomer2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notEq(customer, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" != ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Entity-based: Should not find customer1 with id " + customer1.getId(), foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2 with different id", foundCustomer2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId()))
			.notEq(homeIdAttribute, customer1.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" != ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Attribute-based: Should not find customer1 with id 9001", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2 with different id", foundCustomer2_3);
	}
	
	@Test
	public void eqIfNotNullTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("eqIfNotNull test task").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different task").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature with non-null value
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Class-based: Should find customer1 with id " + customer1.getId(), foundCustomer1_1);
		
		// Test 2: Entity-based method signature with non-null value
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(customer, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" = ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Entity-based: Should find customer1 with id " + customer1.getId(), foundCustomer1_2);
		
		// Test 3: Attribute-based method signature with non-null value
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(homeIdAttribute, customer1.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" = ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Attribute-based: Should find customer1 with id " + customer1.getId(), foundCustomer1_3);
		
		// Test with null value - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryNull = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, null);
		
		List<CustomerEntity> resultsNull = context.fetchList(queryNull);
		assertEquals(2, resultsNull.size());
	}
	
	@Test
	public void eqNullableTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different task").email("external").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test with non-null value
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.email, "external");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" = ?"));
		boolean foundCustomer2 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Should find customer2 with external name", foundCustomer2);
		
		// Test with null value - should find entities with null external name
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, null);
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" IS NULL"));
		boolean foundTask1 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Should find task1 with null external name", foundTask1);
	}
	
	@Test
	public void ltTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("lt test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("lt test customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("lt test customer 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, customer3.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" < ?"));
		boolean foundTask1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundTask2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundTask3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Class-based: Should find task1", foundTask1_1);
		Assert.assertTrue("Class-based: Should find task2", foundTask2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundTask3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lt(customer, AbstractEntity.Fields.id, customer3.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" < ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Entity-based: Should find task1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find task2", foundCustomer2_2);
		Assert.assertFalse("Entity-based: Should not find task3", foundCustomer3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lt(homeIdAttribute, customer3.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" < ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Attribute-based: Should find task1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find task2", foundCustomer2_3);
		Assert.assertFalse("Attribute-based: Should not find task3", foundCustomer3_3);
	}
	
	@Test
	public void lteTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("lte test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("lte test customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("lte test customer 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, customer2.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" <= ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Class-based: Should find task1", foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find task2", foundCustomer2_1);
		Assert.assertFalse("Class-based: Should not find task3", foundCustomer3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lte(customer, AbstractEntity.Fields.id, customer2.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" <= ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Entity-based: Should find customer1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertFalse("Entity-based: Should not find customer3", foundCustomer3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.lte(homeIdAttribute, customer2.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" <= ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertTrue("Attribute-based: Should find customer1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertFalse("Attribute-based: Should not find customer3", foundCustomer3_3);
	}
	
	@Test
	public void gtTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("gt test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("gt test customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("gt test customer 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" > ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Class-based: Should not find customer1", foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find customer2", foundCustomer2_1);
		Assert.assertTrue("Class-based: Should find customer3", foundCustomer3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gt(customer, AbstractEntity.Fields.id, customer1.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" > ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertTrue("Entity-based: Should find customer3", foundCustomer3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gt(homeIdAttribute, customer1.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" > ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertTrue("Attribute-based: Should find customer3", foundCustomer3_3);
	}
	
	@Test
	public void gteTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("gte test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("gte test customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("gte test customer 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, customer2.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" >= ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Class-based: Should not find customer1", foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find customer2", foundCustomer2_1);
		Assert.assertTrue("Class-based: Should find customer3", foundCustomer3_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gte(customer, AbstractEntity.Fields.id, customer2.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" >= ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertTrue("Entity-based: Should find customer3", foundCustomer3_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.gte(homeIdAttribute, customer2.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" >= ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertTrue("Attribute-based: Should find customer3", foundCustomer3_3);
	}
	
	@Test
	public void betweenTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("between test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("between test customer 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("between test customer 3").build();
		CustomerEntity customer4 = CustomerEntity.builder().name("between test customer 4").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		context.updateOrInsert(customer4);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId(), customer4.getId()))
			.between(CustomerEntity.class, AbstractEntity.Fields.id, customer2.getId(), customer3.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" BETWEEN ? AND ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_1 = results1.stream().anyMatch(t -> t.getId() == customer3.getId());
		boolean foundCustomer4_1 = results1.stream().anyMatch(t -> t.getId() == customer4.getId());
		Assert.assertFalse("Class-based: Should not find customer1", foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find customer2", foundCustomer2_1);
		Assert.assertTrue("Class-based: Should find customer3", foundCustomer3_1);
		Assert.assertFalse("Class-based: Should not find customer4", foundCustomer4_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId(), customer4.getId()))
			.between(customer, AbstractEntity.Fields.id, customer2.getId(), customer3.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" BETWEEN ? AND ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_2 = results2.stream().anyMatch(t -> t.getId() == customer3.getId());
		boolean foundCustomer4_2 = results2.stream().anyMatch(t -> t.getId() == customer4.getId());
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		Assert.assertTrue("Entity-based: Should find customer3", foundCustomer3_2);
		Assert.assertFalse("Entity-based: Should not find customer4", foundCustomer4_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId(), customer4.getId()))
			.between(homeIdAttribute, customer2.getId(), customer3.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" BETWEEN ? AND ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3_3 = results3.stream().anyMatch(t -> t.getId() == customer3.getId());
		boolean foundCustomer4_3 = results3.stream().anyMatch(t -> t.getId() == customer4.getId());
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
		Assert.assertTrue("Attribute-based: Should find customer3", foundCustomer3_3);
		Assert.assertFalse("Attribute-based: Should not find customer4", foundCustomer4_3);
	}
	
	@Test
	public void inIfNotEmptyTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("inIfNotEmpty test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("inIfNotEmpty test customer 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" IN (?)"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Class-based: Should find customer1", foundCustomer1_1);
		
		// Test 2: Entity-based method signature with non-empty list
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" IN (?)"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Entity-based: Should find customer1", foundCustomer1_2);
		
		// Test 3: Attribute-based method signature with non-empty list
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(customer1.getId()));
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" IN (?)"));
		boolean foundTask1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Attribute-based: Should find task1", foundTask1_3);
		
		// Test with empty list - should return all results (Class-based example)
		SelectQuery<CustomerEntity> queryEmpty = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		List<CustomerEntity> resultsEmpty = context.fetchList(queryEmpty);
		assertEquals(2, resultsEmpty.size());
	}
	
	@Test
	public void inOrNullTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("inOrNull test customer 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name(null).build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.inOrNull(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("inOrNull test customer 1"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" IN (?)"));
		Assert.assertTrue(queryString.contains(" IS NULL"));
		
		// Should find both tasks - task2 because external name is in list, task1 because external name is null
		assertEquals(2, results.size());
	}
	
	@Test
	public void likeStartsWithTest() throws SQLException {
		

		CustomerEntity customer1 = CustomerEntity.builder().name("prefix_test_task").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different_task").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeStartsWith(CustomerEntity.class, CustomerEntity.Fields.name, "prefix");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" LIKE ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Class-based: Should find customer1 with name starting with 'prefix'", foundCustomer1_1);
		Assert.assertFalse("Class-based: Should not find customer2 without 'prefix' at start", foundCustomer2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeStartsWith(customer, CustomerEntity.Fields.name, "prefix");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" LIKE ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Entity-based: Should find customer1 with name starting with 'prefix'", foundCustomer1_2);
		Assert.assertFalse("Entity-based: Should not find customer2 without 'prefix' at start", foundCustomer2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeStartsWith(nameAttribute, "prefix");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" LIKE ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Attribute-based: Should find customer1 with name starting with 'prefix'", foundCustomer1_3);
		Assert.assertFalse("Attribute-based: Should not find customer2 without 'prefix' at start", foundCustomer2_3);
	}
	
	@Test
	public void likeEndsWithTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("test_task_suffix").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("different_task").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "suffix");
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" LIKE ?"));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Class-based: Should find task1 with name ending with 'suffix'", foundCustomer1_1);
		Assert.assertFalse("Class-based: Should not find task2 without 'suffix' at end", foundCustomer2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeEndsWith(customer, CustomerEntity.Fields.name, "suffix");
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains(" LIKE ?"));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Entity-based: Should find task1 with name ending with 'suffix'", foundCustomer1_2);
		Assert.assertFalse("Entity-based: Should not find task2 without 'suffix' at end", foundCustomer2_2);
		
		// Test 3: Attribute-based method signature
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId()))
			.likeEndsWith(nameAttribute, "suffix");
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains(" LIKE ?"));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertTrue("Attribute-based: Should find task1 with name ending with 'suffix'", foundCustomer1_3);
		Assert.assertFalse("Attribute-based: Should not find task2 without 'suffix' at end", foundCustomer2_3);
	}
	
	@Test
	public void likeInTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("pattern1_test").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("pattern2_test").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("different_test").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.likeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" LIKE ?"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3 = results.stream().anyMatch(t -> t.getId() == customer3.getId());
		
		Assert.assertTrue("Should find customer1 matching pattern1", foundCustomer1);
		Assert.assertTrue("Should find customer2 matching pattern2", foundCustomer2);
		Assert.assertFalse("Should not find customer3 not matching patterns", foundCustomer3);
	}
	
	@Test
	public void notLikeInTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("pattern1_test").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("pattern2_test").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("different_test").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.notLikeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("pattern1%", "pattern2%"));
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" NOT LIKE ?"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3 = results.stream().anyMatch(t -> t.getId() == customer3.getId());
		
		Assert.assertFalse("Should not find task1 matching pattern1", foundCustomer1);
		Assert.assertFalse("Should not find task2 matching pattern2", foundCustomer2);
		Assert.assertTrue("Should find task3 not matching patterns", foundCustomer3);
	}
	
	@Test
	public void inOrFalseIfEmptyTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("inOrFalseIfEmpty test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("inOrFalseIfEmpty test task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test with non-empty list
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.inOrFalseIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId()));
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains(" IN (?)"));
		boolean foundCustomer1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		Assert.assertTrue("Should find task1", foundCustomer1);
		
		// Test with empty list - should return false (no results)
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.inOrFalseIfEmpty(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("false"));
		assertEquals(0, results2.size());
	}
	
	@Test
	public void nativeCriterionTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("native test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("native test task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test 1: Class-based method signature
		SelectQuery<CustomerEntity> query1 = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.nativeCriterion(CustomerEntity.class, AbstractEntity.Fields.id, "> "+customer1.getId());
		
		String queryString1 = context.queryAsString(query1);
		List<CustomerEntity> results1 = context.fetchList(query1);
		
		Assert.assertTrue(queryString1.contains("> "+customer1.getId()));
		boolean foundCustomer1_1 = results1.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_1 = results1.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Class-based: Should not find task1", foundCustomer1_1);
		Assert.assertTrue("Class-based: Should find task2", foundCustomer2_1);
		
		// Test 2: Entity-based method signature
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query2 = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.nativeCriterion(customer, AbstractEntity.Fields.id, "> "+customer1.getId());
		
		String queryString2 = context.queryAsString(query2);
		List<CustomerEntity> results2 = context.fetchList(query2);
		
		Assert.assertTrue(queryString2.contains("> "+customer1.getId()));
		boolean foundCustomer1_2 = results2.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_2 = results2.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Entity-based: Should not find customer1", foundCustomer1_2);
		Assert.assertTrue("Entity-based: Should find customer2", foundCustomer2_2);
		
		// Test 3: Attribute-based method signature
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query3 = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(customer1.getId(), customer2.getId()))
			.nativeCriterion(homeIdAttribute, "> "+customer1.getId());
		
		String queryString3 = context.queryAsString(query3);
		List<CustomerEntity> results3 = context.fetchList(query3);
		
		Assert.assertTrue(queryString3.contains("> "+customer1.getId()));
		boolean foundCustomer1_3 = results3.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2_3 = results3.stream().anyMatch(t -> t.getId() == customer2.getId());
		Assert.assertFalse("Attribute-based: Should not find customer1", foundCustomer1_3);
		Assert.assertTrue("Attribute-based: Should find customer2", foundCustomer2_3);
	}
	
	@Test
	public void inSubQueryTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("inSubQuery test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("inSubQuery test task 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("inSubQuery test task 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Create subquery that selects homeIds > 24001
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.select(CustomerEntity.class, AbstractEntity.Fields.id)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, customer1.getId());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.inSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" IN (SELECT"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3 = results.stream().anyMatch(t -> t.getId() == customer3.getId());
		
		Assert.assertFalse("Should not find customer1", foundCustomer1);
		Assert.assertTrue("Should find customer2", foundCustomer2);
		Assert.assertTrue("Should find customer3", foundCustomer3);
	}
	
	@Test
	public void notInSubQueryTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notInSubQuery test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("notInSubQuery test task 2").build();
		CustomerEntity customer3 = CustomerEntity.builder().name("notInSubQuery test task 3").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Create subquery that selects homeIds > 25001
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.select(CustomerEntity.class, AbstractEntity.Fields.id)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, customer1.getId());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId(), customer3.getId()))
			.notInSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains(" NOT IN (SELECT"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		boolean foundCustomer3 = results.stream().anyMatch(t -> t.getId() == customer3.getId());
		
		Assert.assertTrue("Should find customer1", foundCustomer1);
		Assert.assertFalse("Should not find customer2", foundCustomer2);
		Assert.assertFalse("Should not find customer3", foundCustomer3);
	}
	
	@Test
	public void existsTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("exists test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("exists test task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);

		OrderEntity child1 = OrderEntity.builder().customerId(customer1.getId()).build();
		context.updateOrInsert(child1);
		
		// Create subquery that checks for children
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.select(OrderEntity.class, AbstractEntity.Fields.id)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.exists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("EXISTS"));
		
		boolean foundCustomer1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundCustomer2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		
		Assert.assertTrue("Should find customer1 which has children", foundCustomer1);
		Assert.assertFalse("Should not find customer2 which has no children", foundCustomer2);
	}
	
	@Test
	public void notExistsTest() throws SQLException {
		
		CustomerEntity customer1 = CustomerEntity.builder().name("notExists test task 1").build();
		CustomerEntity customer2 = CustomerEntity.builder().name("notExists test task 2").build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		OrderEntity child1 = OrderEntity.builder().customerId(customer1.getId()).build();
		context.updateOrInsert(child1);
		
		// Create subquery that checks for children
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.select(OrderEntity.class, AbstractEntity.Fields.id)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(customer1.getId(), customer2.getId()))
			.notExists(subQuery);
		
		String queryString = context.queryAsString(query);
		List<CustomerEntity> results = context.fetchList(query);
		
		Assert.assertTrue(queryString.contains("NOT EXISTS"));
		
		boolean foundTask1 = results.stream().anyMatch(t -> t.getId() == customer1.getId());
		boolean foundTask2 = results.stream().anyMatch(t -> t.getId() == customer2.getId());
		
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
		Assert.assertTrue("Should reference homeId field", queryString.contains("ID"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(customer, AbstractEntity.Fields.id, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("ID"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(homeIdAttribute, 1001);
		
		String queryString = context.queryAsString(query);
		Assert.assertTrue("Should generate = operator", queryString.contains(" = ?"));
		Assert.assertTrue("Should reference homeId field", queryString.contains("ID"));
	}
	
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(customer, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(customer, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NULL operator", context.queryAsString(query).contains("NAME IS NULL"));
	}
	
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(customer, CustomerEntity.Fields.name);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.isNotNull(nameAttribute);
		
		Assert.assertTrue("Should generate IS NOT NULL operator", context.queryAsString(query).contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void eqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("NAME = ?"));
	}
	
	@Test
	public void eqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(customer, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("NAME = ?"));
	}
	
	@Test
	public void eqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("NAME = ?"));
	}
	
	@Test
	public void inIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyNullable(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.inIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notInIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void likeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void notLikeInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void likeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsInWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notLikeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notEqIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqNullableWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	@Test
	public void notEqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(customer, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	@Test
	public void notEqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.notEqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	@Test
	public void ltIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.ltIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void lteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void gtIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gtIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gteIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.gteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void betweenIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(customer, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.betweenIfNotNull(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void likeIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeIfNotNull(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeInIfNotEmpty(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.likeContainsInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
}
