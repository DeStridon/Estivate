package com.estivate.context;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.util.Pair;

public class ContextHelperTest {
	
	@Test
	public void varcharParsingTest() {
		
		Context context = new H2Context(null);
		Pair<String, Integer> pair = context.parseColumnType("VARCHAR(24)");
		Assert.assertEquals("VARCHAR", pair.x);
		Assert.assertEquals((Integer) 24, pair.y);
		
	}
	
	
	@Test
	public void booleanParsingTest() {
		
		Context context = new H2Context(null);
		Pair<String, Integer> pair = context.parseColumnType("BIT(1)");
		Assert.assertEquals("BIT", pair.x);
		Assert.assertEquals((Integer) 1, pair.y);
		
	}
	
	@Test
	public void textParsingTest() {
		
		Context context = new H2Context(null);
		Pair<String, Integer> pair = context.parseColumnType("TEXT");
		Assert.assertEquals("TEXT", pair.x);
		Assert.assertNull(pair.y);
		
	}

}
