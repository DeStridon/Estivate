package com.estivate.test.query.h2;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.InsertQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class InsertFilterTest {

	Context context = DatabaseGenerator.getContext();

	@BeforeEach
	public void setUp() {
		Estivate.Tools.createTableFullQuery(CustomerEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(CustomerEntity.class);
		clearFilters();
	}

	@AfterEach
	public void tearDown() {
		clearFilters();
	}

	private void clearFilters() {
		context.insertFilters.clear();
		context.insertInterceptor = null;
	}

	@Test
	public void testInsertFilterMutatesValues() {
		context.addInsertFilter((query, proceed) -> {
			if(query.getEntity() == CustomerEntity.class) {
				for(Object value : query.getValues()) {
					CustomerEntity customer = (CustomerEntity) value;
					customer.setName("FILTERED_" + customer.getName());
				}
			}
			return proceed.apply(query);
		});

		CustomerEntity customer = new CustomerEntity();
		customer.setName("Alice");
		customer.setEmail("filter_insert_" + System.currentTimeMillis() + "@example.com");

		context.insert(customer);

		Assert.assertEquals("FILTERED_Alice", customer.getName());
		Assert.assertNotNull(customer.getId());
	}

	@Test
	public void testInsertFilterShortCircuitSkipsInsert() {
		AtomicBoolean proceeded = new AtomicBoolean(false);

		// First added = outermost. Outer returns without proceed => inner never runs, no insert.
		context.addInsertFilter((query, proceed) -> null);
		context.addInsertFilter((query, proceed) -> {
			proceeded.set(true);
			return proceed.apply(query);
		});

		CustomerEntity customer = new CustomerEntity();
		customer.setName("Skip");
		customer.setEmail("filter_skip_" + System.currentTimeMillis() + "@example.com");

		context.insert(customer);

		Assert.assertFalse(proceeded.get());
		Assert.assertEquals(0L, customer.getId());
	}

	@Test
	public void testInsertFilterObservesAfterProceed() {
		AtomicBoolean sawAfter = new AtomicBoolean(false);

		context.addInsertFilter((query, proceed) -> {
			proceed.apply(query);
			CustomerEntity customer = (CustomerEntity) query.getValues().get(0);
			sawAfter.set(customer.getId() > 0);
			return null;
		});

		CustomerEntity customer = new CustomerEntity();
		customer.setName("After");
		customer.setEmail("filter_after_" + System.currentTimeMillis() + "@example.com");

		context.insert(customer);

		Assert.assertTrue(sawAfter.get());
	}

	@Test
	public void testDeprecatedInsertInterceptorStillWorks() {
		context.insertInterceptor = (InsertQuery<?> insertQuery) -> {
			for(Object value : insertQuery.getValues()) {
				((CustomerEntity) value).setName("INTERCEPTED");
			}
		};

		CustomerEntity customer = new CustomerEntity();
		customer.setName("Bob");
		customer.setEmail("filter_legacy_" + System.currentTimeMillis() + "@example.com");

		context.insert(customer);

		Assert.assertEquals("INTERCEPTED", customer.getName());
	}

}
