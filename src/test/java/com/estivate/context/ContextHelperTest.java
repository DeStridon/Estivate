package com.estivate.context;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.reconciliation.DatabaseColumnDefinition;

public class ContextHelperTest {
	
	@Test
	public void varcharParsingTest() {
		
		Context context = new H2Context(null);
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("VARCHAR(24)");
		Assert.assertEquals("VARCHAR", parts.getType());
		Assert.assertEquals((Integer) 24, parts.getDimension());
		Assert.assertNull(parts.getScale());
		
	}
	
	
	@Test
	public void booleanParsingTest() {
		
		Context context = new H2Context(null);
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("BIT(1)");
		Assert.assertEquals("BIT", parts.getType());
		Assert.assertEquals((Integer) 1, parts.getDimension());
		Assert.assertNull(parts.getScale());
		
	}
	
	@Test
	public void textParsingTest() {
		
		Context context = new H2Context(null);
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("TEXT");
		Assert.assertEquals("TEXT", parts.getType());
		Assert.assertNull(parts.getDimension());
		Assert.assertNull(parts.getScale());
		
	}

	@Test
	public void decimalParsingTest() {
		Context context = new H2Context(null);
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("decimal(10,2)");
		Assert.assertEquals("decimal", parts.getType());
		Assert.assertEquals((Integer) 10, parts.getDimension());
		Assert.assertEquals((Integer) 2, parts.getScale());
	}

	@Test
	public void smallintUnsignedParsingTest() {
		Context context = new H2Context(null);
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("SMALLINT UNSIGNED");
		Assert.assertEquals("SMALLINT UNSIGNED", parts.getType());
		Assert.assertNull(parts.getDimension());
		Assert.assertNull(parts.getScale());
	}

}
