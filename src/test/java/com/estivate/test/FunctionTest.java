package com.estivate.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.query.AttributeFunction;

public class FunctionTest {
	
	@Test
	public void composeTest() {
		
		AttributeFunction.Function function = Estivate.function(Estivate.Functions.charLength, Estivate.Functions.json_extract("path"));

		Assert.assertEquals("char_length(JSON_EXTRACT(attribute, \"path\"))", function.render("attribute"));
		
	}

}
