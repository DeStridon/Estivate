package com.estivate.test.query;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.DeleteQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.OrderEntity.OrderStatus;

public class DeleteQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	// ===== FOCUSED TESTS USING queryAsString() =====
	
	// === EQ Tests ===
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eq(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eq(idAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	// === NOT EQ Tests ===
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEq(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEq(idAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	// === LT Tests ===
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lt(idAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	// === LTE Tests ===
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lte(idAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	// === GT Tests ===
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gt(idAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	// === GTE Tests ===
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gte(idAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	// === BETWEEN Tests ===
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.between(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.between(customer, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.between(idAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	// === IN Tests ===
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.in(idAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	// === NOT IN Tests ===
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notIn(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notIn(idAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	// === IS NULL Tests ===
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNull(customer, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	// === IS NOT NULL Tests ===
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNotNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNotNull(customer, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.isNotNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	// === LIKE Tests ===
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.like(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.like(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE Tests ===
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLike(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === LIKE CONTAINS Tests ===
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContains(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === EQ IF NOT NULL Tests ===
	@Test
	public void eqIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	// === EQ NULLABLE Tests ===
	@Test
	public void eqNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");

		String actualSQL = context.queryAsString(query);
		
		Assert.assertTrue("Should generate = operator", actualSQL.contains("NAME = ?"));
	}
	
	@Test
	public void eqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqNullable(customer, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("NAME = ?"));
	}
	
	@Test
	public void eqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.eqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("NAME = ?"));
	}
	
	// === EXISTS Tests ===
	@Test
	public void existsQueryStringTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.exists(subQuery);
		
		Assert.assertTrue("Should generate EXISTS operator", context.queryAsString(query).contains("EXISTS"));
	}
	
	// === NOT EXISTS Tests ===
	@Test
	public void notExistsQueryStringTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notExists(subQuery);
		
		Assert.assertTrue("Should generate NOT EXISTS operator", context.queryAsString(query).contains("NOT EXISTS"));
	}
	
	// === IN SUB QUERY Tests ===
	@Test
	public void inSubQueryWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		Assert.assertTrue("Should generate IN subquery", context.queryAsString(query).contains("ID IN ("));
	}
	
	@Test
	public void inSubQueryWithAttributeQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.in(idAttribute, subQuery);
		
		Assert.assertTrue("Should generate IN subquery", context.queryAsString(query).contains("ID IN ("));
	}
	
	// === NOT IN SUB QUERY Tests ===
	@Test
	public void notInSubQueryWithClassQueryStringTest() throws SQLException {
		SelectQuery<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		Assert.assertTrue("Should generate NOT IN subquery", context.queryAsString(query).contains("ID NOT IN ("));
	}
	
	@Test
	public void notInSubQueryWithAttributeQueryStringTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.eq(OrderEntity.class, OrderEntity.Fields.status, OrderStatus.PENDING);
		
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notIn(idAttribute, subQuery);
		
		Assert.assertTrue("Should generate NOT IN subquery", context.queryAsString(query).contains("ID NOT IN ("));
	}
	
	// === IN IF NOT EMPTY Tests ===
	@Test
	public void inIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmpty(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmpty(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmpty(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	// === IN IF NOT EMPTY NULLABLE Tests ===
	@Test
	public void inIfNotEmptyNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyNullable(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyNullable(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyNullable(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	// === IN OR NULL Tests ===
	@Test
	public void inOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inOrNull(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	// === IN IF NOT EMPTY OR NULL Tests ===
	@Test
	public void inIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.inIfNotEmptyOrNull(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?)"));
	}
	
	// === NOT IN OR NULL Tests ===
	@Test
	public void notInOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInOrNull(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	// === NOT IN IF NOT EMPTY OR NULL Tests ===
	@Test
	public void notInIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInIfNotEmptyOrNull(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInIfNotEmptyOrNull(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notInIfNotEmptyOrNull(idAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?)"));
	}
	
	// === LIKE IN Tests ===
	@Test
	public void likeInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIn(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE IN Tests ===
	@Test
	public void notLikeInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeIn(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === LIKE STARTS WITH IN Tests ===
	@Test
	public void likeStartsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE STARTS WITH Tests ===
	@Test
	public void notLikeStartsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === LIKE ENDS WITH Tests ===
	@Test
	public void likeEndsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE ENDS WITH Tests ===
	@Test
	public void notLikeEndsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWith(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWith(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === NOT LIKE CONTAINS Tests ===
	@Test
	public void notLikeContainsWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContains(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === LIKE ENDS WITH IN Tests ===
	@Test
	public void likeEndsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE CONTAINS IN Tests ===
	@Test
	public void likeContainsInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE STARTS WITH IN Tests ===
	@Test
	public void notLikeStartsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === NOT LIKE ENDS WITH IN Tests ===
	@Test
	public void notLikeEndsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWithIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWithIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === NOT LIKE CONTAINS IN Tests ===
	@Test
	public void notLikeContainsInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContainsIn(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContainsIn(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notLikeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === NOT EQ IF NOT NULL Tests ===
	@Test
	public void notEqIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	// === NOT EQ NULLABLE Tests ===
	@Test
	public void notEqNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqNullable(CustomerEntity.class, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	@Test
	public void notEqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqNullable(customer, CustomerEntity.Fields.name, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	@Test
	public void notEqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.notEqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("NAME != ?"));
	}
	
	// === LT IF NOT NULL Tests ===
	@Test
	public void ltIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.ltIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.ltIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.ltIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	// === LTE IF NOT NULL Tests ===
	@Test
	public void lteIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lteIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.lteIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	// === GT IF NOT NULL Tests ===
	@Test
	public void gtIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gtIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gtIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gtIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	// === GTE IF NOT NULL Tests ===
	@Test
	public void gteIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gteIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gteIfNotNull(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.gteIfNotNull(idAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	// === BETWEEN IF NOT NULL Tests ===
	@Test
	public void betweenIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.betweenIfNotNull(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.betweenIfNotNull(customer, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute idAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.betweenIfNotNull(idAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	// === LIKE IF NOT NULL Tests ===
	@Test
	public void likeIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIfNotNull(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeIfNotNull(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE STARTS WITH IF NOT NULL Tests ===
	@Test
	public void likeStartsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE ENDS WITH IF NOT NULL Tests ===
	@Test
	public void likeEndsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE CONTAINS IF NOT NULL Tests ===
	@Test
	public void likeContainsIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIfNotNull(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIfNotNull(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE IN IF NOT EMPTY Tests ===
	@Test
	public void likeInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeInIfNotEmpty(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE STARTS WITH IN IF NOT EMPTY Tests ===
	@Test
	public void likeStartsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeStartsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE ENDS WITH IN IF NOT EMPTY Tests ===
	@Test
	public void likeEndsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeEndsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === LIKE CONTAINS IN IF NOT EMPTY Tests ===
	@Test
	public void likeContainsInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsInIfNotEmpty(CustomerEntity.class, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsInIfNotEmpty(customer, CustomerEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		DeleteQuery<CustomerEntity> query = new DeleteQuery<>(CustomerEntity.class)
			.likeContainsInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
}
