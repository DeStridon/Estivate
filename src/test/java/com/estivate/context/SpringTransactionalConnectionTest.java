package com.estivate.context;

import com.estivate.test.TestData;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.estivate.Estivate;
import com.estivate.test.entities.PersistBenchEntity;

/**
 * Estivate must join Spring's transaction connection via DataSourceUtils.
 */
public class SpringTransactionalConnectionTest {

	private JdbcConnectionPool pool;
	private H2Context context;
	private TransactionTemplate transactionTemplate;

	@BeforeEach
	public void setUp() {
		pool = JdbcConnectionPool.create("jdbc:h2:mem:estivate_tx;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
		context = new H2Context(pool);
		context.nameMapper = new TestData.TestNameMapper();
		Estivate.Tools.createTableFullQuery(PersistBenchEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(PersistBenchEntity.class);

		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager((DataSource) pool);
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

	@AfterEach
	public void tearDown() {
		if(pool != null) {
			pool.dispose();
		}
	}

	@Test
	public void rollsBackInsertsWhenTransactionalCallbackFails() {
		try {
			transactionTemplate.executeWithoutResult(status -> {
				PersistBenchEntity entity = new PersistBenchEntity();
				entity.name = "tx";
				entity.email = "tx@example.com";
				entity.score = 1;
				entity.active = true;
				entity.note = "n";
				context.insert(entity);
				Assert.assertTrue(entity.id > 0);
				throw new IllegalStateException("force rollback");
			});
			Assert.fail("expected rollback exception");
		}
		catch(IllegalStateException expected) {
			Assert.assertEquals("force rollback", expected.getMessage());
		}

		Long count = Estivate.selectQuery(PersistBenchEntity.class).fetchCountAll(context);
		Assert.assertEquals(Long.valueOf(0L), count);
	}

	@Test
	public void commitsInsertsInsideTransactionalCallback() {
		transactionTemplate.executeWithoutResult(status -> {
			PersistBenchEntity entity = new PersistBenchEntity();
			entity.name = "ok";
			entity.email = "ok@example.com";
			entity.score = 2;
			entity.active = true;
			entity.note = "n";
			context.insert(entity);
		});

		Long count = Estivate.selectQuery(PersistBenchEntity.class).fetchCountAll(context);
		Assert.assertEquals(Long.valueOf(1L), count);
	}
}
