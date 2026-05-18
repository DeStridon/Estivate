package com.estivate.test.reconciliation;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.H2Context;
import com.estivate.index.Annotations.IndexType;

/**
 * Unit tests for {@link H2Context#getIndexType(String)}.
 */
public class H2ContextIndexTypeTest {

	private final H2Context context = new H2Context(
			JdbcConnectionPool.create("jdbc:h2:mem:h2_index_type_unit;DB_CLOSE_DELAY=-1", "sa", ""));

	@Test
	public void getIndexType_primaryKey() {
		Assert.assertEquals(IndexType.PRIMARY, context.getIndexType("PRIMARY KEY"));
	}

	@Test
	public void getIndexType_uniqueIndex() {
		Assert.assertEquals(IndexType.UNIQUE, context.getIndexType("UNIQUE INDEX"));
	}

	@Test
	public void getIndexType_fullText() {
		Assert.assertEquals(IndexType.FULLTEXT, context.getIndexType("FULLTEXT"));
	}

	@Test
	public void getIndexType_defaultForBtree() {
		Assert.assertEquals(IndexType.DEFAULT, context.getIndexType("BTREE"));
		Assert.assertEquals(IndexType.DEFAULT, context.getIndexType("INDEX"));
	}

}
