package com.estivate.context;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.PersistBenchEntity;

public class ConnectionReuseTest {

	Context context = DatabaseGenerator.getContext();

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
}
