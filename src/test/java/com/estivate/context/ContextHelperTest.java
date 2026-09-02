package com.estivate.context;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.reconciliation.ColumnTypeParts;

public class ContextHelperTest {
	
	@Test
	public void varcharParsingTest() {
		
		Context context = new H2Context(null);
		ColumnTypeParts parts = context.parseColumnType("VARCHAR(24)");
		Assert.assertEquals("VARCHAR", parts.getType());
		Assert.assertEquals((Integer) 24, parts.getLength());
		Assert.assertNull(parts.getScale());
		
	}
	
	
	@Test
	public void booleanParsingTest() {
		
		Context context = new H2Context(null);
		ColumnTypeParts parts = context.parseColumnType("BIT(1)");
		Assert.assertEquals("BIT", parts.getType());
		Assert.assertEquals((Integer) 1, parts.getLength());
		Assert.assertNull(parts.getScale());
		
	}
	
	@Test
	public void textParsingTest() {
		
		Context context = new H2Context(null);
		ColumnTypeParts parts = context.parseColumnType("TEXT");
		Assert.assertEquals("TEXT", parts.getType());
		Assert.assertNull(parts.getLength());
		Assert.assertNull(parts.getScale());
		
	}

	@Test
	public void decimalParsingTest() {
		Context context = new H2Context(null);
		ColumnTypeParts parts = context.parseColumnType("decimal(10,2)");
		Assert.assertEquals("decimal", parts.getType());
		Assert.assertEquals((Integer) 10, parts.getLength());
		Assert.assertEquals((Integer) 2, parts.getScale());
	}

	@Test
	public void smallintUnsignedParsingTest() {
		Context context = new H2Context(null);
		ColumnTypeParts parts = context.parseColumnType("SMALLINT UNSIGNED");
		Assert.assertEquals("SMALLINT UNSIGNED", parts.getType());
		Assert.assertNull(parts.getLength());
		Assert.assertNull(parts.getScale());
	}

}
