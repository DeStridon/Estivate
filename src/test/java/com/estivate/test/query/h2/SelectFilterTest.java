package com.estivate.test.query.h2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultTable;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

public class SelectFilterTest {

	Context context = H2TestDatabase.getContext();

	@Before
	public void setUp() {
		clearFilters();
	}

	@After
	public void tearDown() {
		clearFilters();
	}

	private void clearFilters() {
		context.selectFilters.clear();
		context.selectInterceptor = null;
	}

	@Test
	public void testSelectFilterMutatesQuery() {
		context.addSelectFilter((query, proceed) -> {
			if(query.getOrders().isEmpty()) {
				query.orderByAsc(AbstractEntity.Fields.id);
			}
			return proceed.apply(query);
		});

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);
		String queryString = context.queryAsString(query);

		Assert.assertTrue(queryString.contains("ORDER BY"));
	}

	@Test
	public void testSelectFilterShortCircuitSkipsExecution() {
		AtomicInteger proceedCalls = new AtomicInteger();

		context.addSelectFilter((query, proceed) -> {
			proceedCalls.incrementAndGet();
			return new ResultTable(context, new String[0], query);
		});

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);
		ResultTable result = context.fetch(query);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.getRows().isEmpty());
		Assert.assertEquals(1, proceedCalls.get());
	}

	@Test
	public void testSelectFilterCapturesResult() {
		AtomicReference<ResultTable> captured = new AtomicReference<>();

		context.addSelectFilter((query, proceed) -> {
			ResultTable result = proceed.apply(query);
			captured.set(result);
			return result;
		});

		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class).limit(1);
		ResultTable result = context.fetch(query);

		Assert.assertSame(result, captured.get());
	}

	@Test
	public void testSelectFilterOrderingOuterFirst() {
		List<String> order = new ArrayList<>();

		context.addSelectFilter((query, proceed) -> {
			order.add("outer-before");
			ResultTable result = proceed.apply(query);
			order.add("outer-after");
			return result;
		});
		context.addSelectFilter((query, proceed) -> {
			order.add("inner-before");
			ResultTable result = proceed.apply(query);
			order.add("inner-after");
			return result;
		});

		context.fetch(Estivate.selectQuery(CustomerEntity.class).limit(1));

		Assert.assertEquals(List.of("outer-before", "inner-before", "inner-after", "outer-after"), order);
	}

	@Test
	public void testSelectFilterCanThrow() {
		context.addSelectFilter((query, proceed) -> {
			throw new SecurityException("denied");
		});

		try {
			context.fetch(Estivate.selectQuery(CustomerEntity.class));
			Assert.fail("Expected SecurityException");
		}
		catch(SecurityException e) {
			Assert.assertEquals("denied", e.getMessage());
		}
	}

	@Test
	public void testDeprecatedInterceptorStillWorksWithFilters() {
		context.selectInterceptor = query -> query.comment("from-interceptor");
		context.addSelectFilter((query, proceed) -> {
			query.orderByAsc(AbstractEntity.Fields.id);
			return proceed.apply(query);
		});

		String queryString = context.queryAsString(Estivate.selectQuery(CustomerEntity.class));

		Assert.assertTrue(queryString.contains("ORDER BY"));
		Assert.assertTrue(queryString.contains("from-interceptor"));
	}

}
