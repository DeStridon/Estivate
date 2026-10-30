package com.estivate.test.query.postgresql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.TestData;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

/**
 * Core Estivate reads and writes against PostgreSQL.
 * Skipped automatically when Docker is unavailable.
 */
@Testcontainers(disabledWithoutDocker = true)
public class PostgresqlTest {

	@Container
	static PostgreSQLContainer<?> postgres = PostgresqlTestDatabase.newContainer();

	static Context context;

	@BeforeAll
	static void openContext() {
		context = PostgresqlTestDatabase.createContext(postgres);
	}

	@BeforeEach
	void cleanTables() {
		context.truncateTable(CustomerEntity.class);
		context.truncateTable(ProductEntity.class);
	}

	@Test
	void schemaListsReconciledTables() {
		assertTrue(context.showTables().contains("CUSTOMERENTITY"));
		assertTrue(context.showTables().contains("PRODUCTENTITY"));
		assertTrue(context.listFields("CUSTOMERENTITY").stream().anyMatch(column -> "email".equalsIgnoreCase(column.getName())));
	}

	@Test
	void insertSelectUpdateDeleteCustomer() {
		CustomerEntity customer = TestData.createRandomCustomer();
		customer.setEmailVerified(true);
		customer.setCountry(CustomerEntity.Country.FRANCE);

		context.insert(customer);

		assertTrue(customer.getId() > 0);

		SelectQuery<CustomerEntity> byId = Estivate.selectQuery(CustomerEntity.class)
				.eq(AbstractEntity.Fields.id, customer.getId());

		CustomerEntity loaded = context.extractSingle(byId);
		assertEquals(customer.getEmail(), loaded.getEmail());
		assertEquals(CustomerEntity.Country.FRANCE, loaded.getCountry());
		assertTrue(loaded.isEmailVerified());

		loaded.setName("Updated Name");
		context.update(loaded);

		CustomerEntity updated = context.extractSingle(byId);
		assertEquals("Updated Name", updated.getName());
		assertEquals(customer.getId(), updated.getId());

		context.delete(updated);

		List<CustomerEntity> remaining = context.extractList(Estivate.selectQuery(CustomerEntity.class));
		assertTrue(remaining.isEmpty());
	}

	@Test
	void filterOrderAndLimit() {
		CustomerEntity first = TestData.createRandomCustomer();
		first.setName("Alpha");
		first.setEmail("alpha@example.com");
		CustomerEntity second = TestData.createRandomCustomer();
		second.setName("Zulu");
		second.setEmail("zulu@example.com");
		context.insert(first);
		context.insert(second);

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity.class, CustomerEntity.Fields.email, "alpha@example.com");

		String sql = context.queryAsString(query);
		assertTrue(sql.contains("\""), sql);

		List<CustomerEntity> matches = context.extractList(query);
		assertEquals(1, matches.size());
		assertEquals("Alpha", matches.get(0).getName());

		List<CustomerEntity> page = context.extractList(Estivate.selectQuery(CustomerEntity.class)
				.orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name)
				.limit(1));
		assertEquals(1, page.size());
		assertEquals("Alpha", page.get(0).getName());
	}

	@Test
	void roundTripsOrdinalEnumAndReal() {
		ProductEntity product = TestData.createRandomProduct();
		product.setName("Trail shoes");
		product.setCategory(ProductCategory.Shoes);
		product.setPrice(49.5f);
		product.setStock(3);

		context.insert(product);
		assertTrue(product.getId() > 0);

		ProductEntity loaded = context.extractSingle(Estivate.selectQuery(ProductEntity.class)
				.eq(AbstractEntity.Fields.id, product.getId()));

		assertEquals(ProductCategory.Shoes, loaded.getCategory());
		assertEquals(49.5f, loaded.getPrice());
		assertEquals(3, loaded.getStock());
	}

}
