package com.estivate.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.repository.RepositoryQuery;
import com.estivate.test.entities.CustomerEntity;

/**
 * Ensures repository method references are recognized and transformed into SelectQuery.
 */
class RepositoryMethodTransformTest {

	@Test
	void findById() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::findById, 42L);

		assertNotNull(query);
		assertEquals(CustomerEntity.class, query.getEntity().entity);
		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findByNameAndEmail() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(
			CustomerManager::findByNameAndEmail,
			"Alice",
			"alice@example.com");

		assertEquals(2, query.getCriterions().size());
	}

	@Test
	void findByIdBetween() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::findByIdBetween, 1L, 10L);

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findByCountryOrderByCreatedDesc() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(
			CustomerManager::findByCountryOrderByCreatedDesc,
			CustomerEntity.Country.FRANCE);

		assertEquals(1, query.getOrders().size());
		assertEquals(Order.Direction.Desc, query.getOrders().get(0).getDirection());
	}

	@Test
	void findByEmailVerifiedIsTrue() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::findByEmailVerifiedIsTrue);

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void existsById() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::existsById, 1L);

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findDistinctNameByCountry() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(
			CustomerManager::findDistinctNameByCountry,
			CustomerEntity.Country.USA);

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findNameById() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(CustomerManager::findNameById, 7L);

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findByIdInIfNotEmpty() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(
			CustomerManager::findByIdInIfNotEmpty, Arrays.asList(1L));

		assertEquals(1, query.getCriterions().size());
	}

	@Test
	void findByNameAndEmailAndAddress() {
		SelectQuery<?> query = RepositoryQuery.getQueryFromMethod(
			CustomerManager::findByNameAndEmailAndAddress,
			"Alice",
			"alice@example.com",
			"Paris");

		assertEquals(3, query.getCriterions().size());
	}
}
