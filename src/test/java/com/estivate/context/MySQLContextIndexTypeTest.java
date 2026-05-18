package com.estivate.context;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.index.Annotations.IndexType;

/**
 * Unit tests for {@link MySQLContext#resolveIndexType(String, boolean, String)}.
 */
public class MySQLContextIndexTypeTest {

	@Test
	public void resolveIndexType_primaryKey() {
		Assert.assertEquals(IndexType.PRIMARY, MySQLContext.resolveIndexType("PRIMARY", false, "BTREE"));
	}

	@Test
	public void resolveIndexType_uniqueIndex() {
		Assert.assertEquals(IndexType.UNIQUE, MySQLContext.resolveIndexType("email_unique", false, "BTREE"));
	}

	@Test
	public void resolveIndexType_fullText() {
		Assert.assertEquals(IndexType.FULLTEXT, MySQLContext.resolveIndexType("idx_body", true, "FULLTEXT"));
		Assert.assertEquals(IndexType.FULLTEXT, MySQLContext.resolveIndexType("idx_body", true, "fulltext"));
	}

	@Test
	public void resolveIndexType_defaultBtree() {
		Assert.assertEquals(IndexType.DEFAULT, MySQLContext.resolveIndexType("idx_name", true, "BTREE"));
		Assert.assertEquals(IndexType.DEFAULT, MySQLContext.resolveIndexType("idx_name", true, null));
	}

}
