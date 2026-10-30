package com.estivate.context;

import com.estivate.test.query.h2.H2TestDatabase;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.test.entities.PersistBenchEntity;

public class ConnectionReuseTest {

	Context context = H2TestDatabase.getContext();

	@BeforeEach
	public void setUp() {
		Estivate.Tools.createTableFullQuery(PersistBenchEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(PersistBenchEntity.class);
	}

	@Test
	public void withConnectionReusesSameConnectionForNestedOps() {
		List<Connection> seen = new ArrayList<>();

		context.withConnection(outer -> {
			seen.add(outer);
			context.withConnection(inner -> {
				seen.add(inner);
				Assert.assertSame(outer, inner);
			});

			PersistBenchEntity entity = new PersistBenchEntity();
			entity.name = "reuse";
			entity.email = "reuse@example.com";
			entity.score = 1;
			entity.active = true;
			entity.note = "n";
			context.insert(entity);
			Assert.assertTrue(entity.id > 0);
		});

		Assert.assertEquals(2, seen.size());
		Assert.assertSame(seen.get(0), seen.get(1));
	}

	@Test
	public void withConnectionCachesInsertPreparedStatement() {
		Assert.assertEquals(0, context.cachedInsertStatementCount());

		context.withConnection(ignored -> {
			Assert.assertEquals(0, context.cachedInsertStatementCount());

			PersistBenchEntity first = row("first");
			context.insert(first);
			Assert.assertTrue(first.id > 0);
			Assert.assertEquals(1, context.cachedInsertStatementCount());

			PersistBenchEntity second = row("second");
			context.insert(second);
			Assert.assertTrue(second.id > first.id);
			Assert.assertEquals(1, context.cachedInsertStatementCount());
		});

		Assert.assertEquals(0, context.cachedInsertStatementCount());
	}

	private static PersistBenchEntity row(String name) {
		PersistBenchEntity entity = new PersistBenchEntity();
		entity.name = name;
		entity.email = name + "@example.com";
		entity.score = 1;
		entity.active = true;
		entity.note = "n";
		return entity;
	}
}
