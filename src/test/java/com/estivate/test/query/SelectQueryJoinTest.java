package com.estivate.test.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.Result;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.CustomerEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class SelectQueryJoinTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		
		CustomerEntity customer = context.updateOrInsert(CustomerEntity.builder().name("join test customer").build());
		context.updateOrInsert(OrderEntity.builder().status(OrderEntity.OrderStatus.PENDING).customerId(customer.getId()).build());
		context.updateOrInsert(OrderEntity.builder().status(OrderEntity.OrderStatus.PENDING).customerId(customer.getId()).build());
		
		SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
				.comment("Query Join Test")
				.eq(OrderEntity.class, OrderEntity.Fields.customerId, customer.getId());
		
		List<OrderEntity> results = context.fetchListAs(query, OrderEntity.class);
		
		assertEquals(2, results.size());
		
	}
	
	@Test
	public void selectJoiningTest2() throws SQLException {
		
		CustomerEntity parent = context.updateOrInsert(CustomerEntity.builder().name("join test name 1").build());
		
		context.updateOrInsert(OrderEntity.builder().customerId(parent.getId()).build());
		context.updateOrInsert(OrderEntity.builder().customerId(parent.getId()).build());
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class)
				.joinInner(CustomerEntity.class, OrderEntity.class)
				.selectAll(OrderEntity.class)
				.eq(CustomerEntity.class, CustomerEntity.Fields.name, parent.getName());
		
		List<Result> results = context.fetchListAsResults(query);
		
		log.debug(context.queryAsString(query));
		
		assertEquals(2, results.size());
	}
	

	@Test
	public void whereJoiningTest() throws SQLException {
		
		CustomerEntity parent = context.updateOrInsert(CustomerEntity.builder().name("join test name 2").build());
		
		context.updateOrInsert(OrderEntity.builder().customerId(parent.getId()).build());
		context.updateOrInsert(OrderEntity.builder().customerId(parent.getId()).build());
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(OrderEntity.class, OrderEntity.Fields.status, "source content 1");
		
		String queryString = context.queryAsString(query);
	
	}
	
	@Test
	public void nameMappingTest() throws SQLException {
		
		
		Entity<OrderEntity> firstOrder = new Entity<>(OrderEntity.class, "firstOrder");
		Entity<OrderEntity> secondOrder = new Entity<>(OrderEntity.class, "secondOrder");
		
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.select(firstOrder, AbstractEntity.Fields.id)
			.select(secondOrder, AbstractEntity.Fields.id)
			.joinInner(CustomerEntity.class, firstOrder, AbstractEntity.Fields.id, OrderEntity.Fields.customerId)
			.joinInner(firstOrder, secondOrder, OrderEntity.Fields.status, OrderEntity.Fields.status)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, 35)
			.lt(firstOrder, AbstractEntity.Fields.id, Estivate.attribute(secondOrder, AbstractEntity.Fields.id));
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		assertTrue(queryString.contains("INNER JOIN ORDERENTITY firstOrder"));
		assertTrue(queryString.contains("firstOrder.ID < secondOrder.ID"));

	}

	@Test
	public void squareJoinTest() throws SQLException {
		
		
		Entity<OrderEntity> orderA = new Entity<>(OrderEntity.class, "OrderA");
		Entity<OrderEntity> orderB = new Entity<>(OrderEntity.class, "OrderB");
		Entity<OrderLineEntity> orderLineA = new Entity<>(OrderLineEntity.class, "OrderLineA");
		Entity<OrderLineEntity> orderLineB = new Entity<>(OrderLineEntity.class, "OrderLineB");
		
		SelectQuery<OrderEntity> query = new SelectQuery<>(orderA)
				.joinInner(orderA, orderLineA)
				.joinInner(orderLineA, ProductEntity.class)
				.joinInner(ProductEntity.class, orderLineB)
				.joinLeft(orderLineB, orderB)
				.select(ProductEntity.class, ProductEntity.Fields.name);
		
		String queryString = context.queryAsString(query);
		System.out.println(queryString);
		
		Assert.assertTrue(queryString.contains("INNER JOIN ORDERLINEENTITY OrderLineA ON OrderA.ID = OrderLineA.ORDERID"));
		Assert.assertTrue(queryString.contains("INNER JOIN PRODUCTENTITY ON OrderLineA.PRODUCTID = PRODUCTENTITY.ID"));
		Assert.assertTrue(queryString.contains("INNER JOIN ORDERLINEENTITY OrderLineB ON PRODUCTENTITY.ID = OrderLineB.PRODUCTID"));
		Assert.assertTrue(queryString.contains("LEFT JOIN ORDERENTITY OrderB ON OrderLineB.ORDERID = OrderB.ID"));
		


	}

	@Test
	public void joinWithSubQueryTest() throws SQLException {

		SubQueryEntity<CustomerEntity> subQuery = Estivate.selectQuery(CustomerEntity.class)
			.in(AbstractEntity.Fields.id, Arrays.asList(1, 2, 3))
			.asSubQueryEntity("subQuery");

		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class)
			.joinInner(CustomerEntity.class, subQuery, AbstractEntity.Fields.id, AbstractEntity.Fields.id)
			.select(CustomerEntity.class, CustomerEntity.Fields.name);

		String queryString = context.queryAsString(query);
		System.out.println(queryString);

	
	}
	
	
}
