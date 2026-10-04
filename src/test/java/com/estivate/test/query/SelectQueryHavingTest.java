package com.estivate.test.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultTable;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryHavingTest {

	Context context = DatabaseGenerator.getContext();

	@Test
	void havingCountGreaterThanOne_isRenderedAndSurvivesClone() {
		SelectQuery<?> uniqueQuery = Estivate.selectQuery(CustomerEntity.class)
			.having(Estivate.gt(Estivate.attribute(new Entity<>(null), null, Estivate.Functions.count), 1))
			.select(CustomerEntity.Fields.email)
			.groupBy(CustomerEntity.Fields.email)
			.selectCountAll();

		String queryString = context.queryAsString(uniqueQuery);

		assertTrue(queryString.toUpperCase().contains("GROUP BY"), queryString);
		assertTrue(queryString.toUpperCase().contains("HAVING"), queryString);
		assertTrue(queryString.toUpperCase().contains("COUNT(*)"), queryString);
		assertTrue(queryString.contains(">"), queryString);

		SelectQuery<?> cloned = uniqueQuery.clone();
		assertEquals(uniqueQuery.getHaving().getClass(), cloned.getHaving().getClass());
		assertTrue(context.queryAsString(cloned).toUpperCase().contains("HAVING"));
	}

	@Test
	void havingCountAllHelper_isRendered() {
		SelectQuery<?> query = Estivate.selectQuery(CustomerEntity.class)
			.select(CustomerEntity.Fields.country)
			.groupBy(CustomerEntity.Fields.country)
			.having(Estivate.gt(Estivate.countAll(), 1));

		String queryString = context.queryAsString(query);

		assertTrue(queryString.toUpperCase().contains("HAVING COUNT(*) >"), queryString);
	}

	@Test
	void havingQuery_canBeFetched() {
		SelectQuery<?> uniqueQuery = Estivate.selectQuery(CustomerEntity.class)
			.having(Estivate.gt(Estivate.countAll(), 1))
			.select(CustomerEntity.Fields.email)
			.groupBy(CustomerEntity.Fields.email)
			.selectCountAll();

		ResultTable resultTable = uniqueQuery.fetch(context);

		assertTrue(resultTable != null);
	}
}
