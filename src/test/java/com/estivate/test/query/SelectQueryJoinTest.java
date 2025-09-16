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
import com.estivate.test.entities.CustomerEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class SelectQueryJoinTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		
		
		context.updateOrInsert(OrderEntity.builder().id(1).status(OrderEntity.OrderStatus.PENDING).build());
		context.updateOrInsert(OrderEntity.builder().id(1).status(OrderEntity.OrderStatus.PENDING).build());
		
		SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
				.comment("Query Join Test")
				.eq(OrderEntity.class, OrderEntity.Fields.customerId, 2);
		
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
		
		assertTrue(queryString.contains("INNER JOIN CHILDENTITY_D firstChild"));
		assertTrue(queryString.contains("firstChild.ID_D != secondChild.ID_D"));

	}

	@Test
	public void squareJoinTest() throws SQLException {
		
		
		Entity<CustomerEntity> parentA = new Entity<>(CustomerEntity.class, "ParentA");
		Entity<CustomerEntity> parentB = new Entity<>(CustomerEntity.class, "ParentB");
		Entity<OrderEntity> childA = new Entity<>(OrderEntity.class, "ChildA");
		Entity<OrderEntity> childB = new Entity<>(OrderEntity.class, "ChildB");
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(parentA)
				.joinInner(parentA, childA)
				.joinInner(childB, parentB)
				.joinInner(childB, parentB)
				.joinLeft(childB, parentB)
				.select(parentB, CustomerEntity.Fields.name);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("INNER JOIN CHILDENTITY_D ChildB ON ChildA.DESCRIPTION_D = ChildB.DESCRIPTION_D"));
		Assert.assertTrue(queryString.contains("INNER JOIN PARENTENTITY_D ParentB ON ChildB.PARENTID_D = ParentB.ID_D"));

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
