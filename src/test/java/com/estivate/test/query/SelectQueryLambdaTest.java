package com.estivate.test.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;

/**
 * Test class for select queries using lambda methods for criteria.
 * Tests the new lambda-based API for building query criteria.
 */
public class SelectQueryLambdaTest {

	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testEqLambda() {
		// Create test data
		CustomerEntity customer = CustomerEntity.builder()
				.name("Lambda Test Customer")
				.email("lambda@test.com")
				.build();
		context.updateOrInsert(customer);
		
		// Test eq with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity::getName, "Lambda Test Customer");

		List<CustomerEntity> results = query.fetchAsList(context);

		assertNotNull(results);
		assertTrue(results.size() > 0);
		assertEquals("Lambda Test Customer", results.get(0).getName());
	}
	
	@Test
	public void testNotEqLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer 1")
				.email("customer1@test.com")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer 2")
				.email("customer2@test.com")
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test notEq with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.notEq(CustomerEntity::getName, "Customer 1");
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		// Should not contain customer1
		assertTrue(results.stream().noneMatch(c -> "Customer 1".equals(c.getName())));
	}
	
	@Test
	public void testLtLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer A")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer B")
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test lt with lambda - comparing IDs
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.lt(CustomerEntity::getId, customer2.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() < customer2.getId()));
	}
	
	@Test
	public void testLteLambda() {
		CustomerEntity customer = CustomerEntity.builder()
				.name("Customer LTE")
				.build();
		context.updateOrInsert(customer);
		
		// Test lte with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.lte(CustomerEntity::getId, customer.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() <= customer.getId()));
	}
	
	@Test
	public void testGtLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer GT 1")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer GT 2")
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test gt with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.gt(CustomerEntity::getId, customer1.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() > customer1.getId()));
	}
	
	@Test
	public void testGteLambda() {
		CustomerEntity customer = CustomerEntity.builder()
				.name("Customer GTE")
				.build();
		context.updateOrInsert(customer);
		
		// Test gte with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.gte(CustomerEntity::getId, customer.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() >= customer.getId()));
	}
	
	@Test
	public void testBetweenLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer Between 1")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer Between 2")
				.build();
		CustomerEntity customer3 = CustomerEntity.builder()
				.name("Customer Between 3")
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test between with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.between(CustomerEntity::getId, customer1.getId(), customer3.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().allMatch(c -> 
			c.getId() >= customer1.getId() && c.getId() <= customer3.getId()));
	}
	
	@Test
	public void testInLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer In 1")
				.country(CustomerEntity.Country.USA)
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer In 2")
				.country(CustomerEntity.Country.UK)
				.build();
		CustomerEntity customer3 = CustomerEntity.builder()
				.name("Customer In 3")
				.country(CustomerEntity.Country.FRANCE)
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test in with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.in(CustomerEntity::getCountry, 
					Arrays.asList(CustomerEntity.Country.USA, CustomerEntity.Country.UK));
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().allMatch(c -> 
			c.getCountry() == CustomerEntity.Country.USA || 
			c.getCountry() == CustomerEntity.Country.UK));
	}
	
	@Test
	public void testNotInLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer NotIn 1")
				.country(CustomerEntity.Country.USA)
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer NotIn 2")
				.country(CustomerEntity.Country.UK)
				.build();
		CustomerEntity customer3 = CustomerEntity.builder()
				.name("Customer NotIn 3")
				.country(CustomerEntity.Country.FRANCE)
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		context.updateOrInsert(customer3);
		
		// Test notIn with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.notIn(CustomerEntity::getCountry, 
					Arrays.asList(CustomerEntity.Country.USA, CustomerEntity.Country.UK));
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().noneMatch(c -> 
			c.getCountry() == CustomerEntity.Country.USA || 
			c.getCountry() == CustomerEntity.Country.UK));
	}
	
	@Test
	public void testEqIfNotNullLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer IfNotNull 1")
				.email("email1@test.com")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer IfNotNull 2")
				.email(null) // null email
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test eqIfNotNull with lambda - null value should be ignored
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eqIfNotNull(CustomerEntity::getEmail, null);
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		// Should return all customers since null criterion is ignored
		assertTrue(results.size() >= 2);
	}
	
	@Test
	public void testLtIfNotNullLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer LtIfNotNull")
				.build();
		context.updateOrInsert(customer1);
		
		// Test ltIfNotNull with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.ltIfNotNull(CustomerEntity::getId, customer1.getId() + 10);
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() < customer1.getId() + 10));
	}
	
	@Test
	public void testGteIfNotNullLambda() {
		CustomerEntity customer = CustomerEntity.builder()
				.name("Customer GteIfNotNull")
				.build();
		context.updateOrInsert(customer);
		
		// Test gteIfNotNull with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.gteIfNotNull(CustomerEntity::getId, customer.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getId() >= customer.getId()));
	}
	
	@Test
	public void testBetweenIfNotNullLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer BetweenIfNotNull 1")
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer BetweenIfNotNull 2")
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test betweenIfNotNull with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.betweenIfNotNull(CustomerEntity::getId, customer1.getId(), customer2.getId());
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> 
			c.getId() >= customer1.getId() && c.getId() <= customer2.getId()));
	}
	
	@Test
	public void testInIfNotEmptyLambda() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Customer InIfNotEmpty 1")
				.country(CustomerEntity.Country.USA)
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Customer InIfNotEmpty 2")
				.country(CustomerEntity.Country.UK)
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test inIfNotEmpty with lambda
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.inIfNotEmpty(CustomerEntity::getCountry, 
					Arrays.asList(CustomerEntity.Country.USA));
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> c.getCountry() == CustomerEntity.Country.USA));
	}
	
	@Test
	public void testMultipleLambdaCriteria() {
		CustomerEntity customer = CustomerEntity.builder()
				.name("Multiple Criteria")
				.email("multiple@test.com")
				.country(CustomerEntity.Country.USA)
				.emailVerified(true)
				.build();
		context.updateOrInsert(customer);
		
		// Test multiple lambda criteria combined
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity::getName, "Multiple Criteria")
				.eq(CustomerEntity::getEmail, "multiple@test.com")
				.eq(CustomerEntity::getCountry, CustomerEntity.Country.USA)
				.eq(CustomerEntity::isEmailVerified, true);
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("Multiple Criteria", results.get(0).getName());
		assertEquals("multiple@test.com", results.get(0).getEmail());
		assertEquals(CustomerEntity.Country.USA, results.get(0).getCountry());
		assertTrue(results.get(0).isEmailVerified());
	}
	
	@Test
	public void testLambdaWithJoin() {
		CustomerEntity customer = CustomerEntity.builder()
				.name("Lambda Join Customer")
				.email("join@test.com")
				.build();
		context.updateOrInsert(customer);
		
		OrderEntity order = OrderEntity.builder()
				.customerId(customer.getId())
				.status(OrderEntity.OrderStatus.PENDING)
				.build();
		context.updateOrInsert(order);
		
		// Test lambda criteria with join
		// For joined entities, we can now use lambda methods by specifying the entity class
		SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
				.joinInner(OrderEntity.class, CustomerEntity.class)
				.eq(CustomerEntity::getName, "Lambda Join Customer");
		
		List<OrderEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.size() > 0);
		assertEquals(customer.getId(), results.get(0).getCustomerId());
	}
	
	@Test
	public void testLambdaWithDateComparison() {
		Date testDate = new Date();
		CustomerEntity customer = CustomerEntity.builder()
				.name("Date Lambda Customer")
				.created(testDate)
				.build();
		context.updateOrInsert(customer);
		
		// Test lambda with date comparison
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.gte(CustomerEntity::getCreated, testDate);
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().anyMatch(c -> 
			c.getCreated() != null && c.getCreated().compareTo(testDate) >= 0));
	}
	
	@Test
	public void testLambdaWithBoolean() {
		CustomerEntity customer1 = CustomerEntity.builder()
				.name("Boolean True")
				.emailVerified(true)
				.build();
		CustomerEntity customer2 = CustomerEntity.builder()
				.name("Boolean False")
				.emailVerified(false)
				.build();
		context.updateOrInsert(customer1);
		context.updateOrInsert(customer2);
		
		// Test lambda with boolean
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity::isEmailVerified, true);
		
		List<CustomerEntity> results = query.fetchAsList(context);
		
		assertNotNull(results);
		assertTrue(results.stream().allMatch(CustomerEntity::isEmailVerified));
	}
}

