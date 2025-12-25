package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * Tests to ensure EntityMapper works correctly with aliased Entity classes.
 * 
 * The concern is that when a query is created with an aliased Entity (e.g., new Entity<>(CustomerEntity.class, "c")),
 * the EntityMapper might not correctly map results because it creates a non-aliased Entity internally.
 */
@Slf4j
public class EntityMapperAliasTest {

	Context context = DatabaseGenerator.getContext();

	/**
	 * Test that fetching entities works when the query is created with an aliased Entity.
	 * This tests the scenario where:
	 * - Query is created with: new Entity<>(CustomerEntity.class, "c")
	 * - EntityMapper internally creates: new Entity<>(CustomerEntity.class) (no alias)
	 * - The mapping should still work correctly
	 */
	@Test
	public void testFetchWithAliasedEntity() {
		// Create test data
		CustomerEntity customer = CustomerEntity.builder()
				.name("Alias Test Customer")
				.email("alias@test.com")
				.build();
		context.updateOrInsert(customer);

		// Create an aliased entity
		Entity<CustomerEntity> aliasedEntity = new Entity<>(CustomerEntity.class, "cust");

		// Create query using the aliased entity
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(aliasedEntity)
				.eq(aliasedEntity, AbstractEntity.Fields.id, customer.getId());

		log.info("Query with alias: {}", context.queryAsString(query));

		// Fetch results - this uses EntityMapper internally
		List<CustomerEntity> results = context.fetchListAs(query, aliasedEntity);

		// Verify the mapping worked
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("Alias Test Customer", results.get(0).getName());
		assertEquals("alias@test.com", results.get(0).getEmail());
		assertEquals(customer.getId(), results.get(0).getId());
	}



}

