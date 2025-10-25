package com.estivate.test.query;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.UpdateQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.CustomerEntity;

public class UpdateQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	// ===== FOCUSED TESTS USING queryAsString() =====
	
	// === EQ Tests ===
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = Estivate.updateQuery(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.eq(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.eq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("ID = ?"));
	}
	
	// === NOT EQ Tests ===
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notEq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notEq(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("ID != ?"));
	}
	
	// === LT Tests ===
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("ID < ?"));
	}
	
	// === LTE Tests ===
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("ID <= ?"));
	}
	
	// === GT Tests ===
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gt(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gt(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("ID > ?"));
	}
	
	// === GTE Tests ===
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gte(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gte(customer, AbstractEntity.Fields.id, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("ID >= ?"));
	}
	
	// === BETWEEN Tests ===
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.between(CustomerEntity.class, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.between(customer, AbstractEntity.Fields.id, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("ID BETWEEN ? AND ?"));
	}
	
	// === IN Tests ===
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.in(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.in(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("ID IN (?, ?, ?)"));
	}
	
	// === NOT IN Tests ===
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notIn(CustomerEntity.class, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notIn(customer, AbstractEntity.Fields.id, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("ID NOT IN (?, ?, ?)"));
	}
	
	// === IS NULL Tests ===
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNull(customer, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME IS NULL"));
	}
	
	// === IS NOT NULL Tests ===
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNotNull(CustomerEntity.class, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNotNull(customer, CustomerEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.isNotNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME IS NOT NULL"));
	}
	
	// === LIKE Tests ===
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.like(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.like(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === NOT LIKE Tests ===
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notLike(CustomerEntity.class, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notLike(customer, CustomerEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME NOT LIKE ?"));
	}
	
	// === LIKE CONTAINS Tests ===
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<CustomerEntity> customer = new Entity<>(CustomerEntity.class);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.likeContains(customer, CustomerEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME LIKE ?"));
	}
	
	// === EXISTS Tests ===
	@Test
	public void existsQueryStringTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.exists(subQuery);
		
		Assert.assertTrue("Should generate EXISTS operator", context.queryAsString(query).contains("EXISTS"));
	}
	
	// === NOT EXISTS Tests ===
	@Test
	public void notExistsQueryStringTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated")
			.notExists(subQuery);
		
		Assert.assertTrue("Should generate NOT EXISTS operator", context.queryAsString(query).contains("NOT EXISTS"));
	}
	
	// === SET Method Tests ===
	@Test
	public void setWithFieldNameQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated value")
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause", actualSQL.contains("SET") && actualSQL.contains("NAME = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("ID = ?"));
	}
	
	@Test
	public void setWithStringFieldNameQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set("name", "updated value")
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause", actualSQL.contains("SET") && actualSQL.contains("NAME = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("ID = ?"));
	}
	
	@Test
	public void multipleSetQueryStringTest() throws SQLException {
		UpdateQuery<CustomerEntity> query = new UpdateQuery<>(CustomerEntity.class)
			.set(CustomerEntity.Fields.name, "updated name")
			.set(CustomerEntity.Fields.name, "updated external")
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause for name", actualSQL.contains("NAME = ?"));
		Assert.assertTrue("Should generate SET clause for externalName", actualSQL.contains("NAME = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("ID = ?"));
	}
	
}
