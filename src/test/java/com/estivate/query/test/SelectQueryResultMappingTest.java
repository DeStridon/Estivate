package com.estivate.query.test;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.ParentEntity;

public class SelectQueryResultMappingTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void resultMappingTest() throws SQLException {
		// Create test data - insert ParentEntity and ChildEntity records
		ParentEntity parent = ParentEntity.builder()
			.homeId(1001L)
			.name("Test Parent")
			.status(ParentEntity.JobEnum.Analysis)
			.stringEnum(ParentEntity.StringEnum.ABC)
			.matchingPoint(85.5)
			.archived(false)
			.externalName("external_test")
			.build();
		
		context.insert(parent);
		
		
		// Create SelectQuery with join between ParentEntity and ChildEntity
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
			.selectAll(ParentEntity.class)
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001L);
		
		// Execute query and fetch results as entity objects
		Result result = query.fetchSingleAsResult(context);
		
		ParentEntity resultParent = result.mapTo(ParentEntity.class);
		Assert.assertEquals("Parent name should match", "Test Parent", resultParent.getName());
		Assert.assertEquals("Parent homeId should match", 1001L, resultParent.getHomeId());
		Assert.assertEquals("Parent status should match", ParentEntity.JobEnum.Analysis, resultParent.getStatus());
		Assert.assertEquals("Parent stringEnum should match", ParentEntity.StringEnum.ABC, resultParent.getStringEnum());
		Assert.assertEquals("Parent matchingPoint should match", 85.5, resultParent.getMatchingPoint(), 0.01);
		Assert.assertEquals("Parent archived should match", false, resultParent.isArchived());
		Assert.assertEquals("Parent externalName should match", "external_test", resultParent.getExternalName());
		
		// Test Result::attributeAs methods for different data types
		Assert.assertEquals("Result attributeAsString should work", "Test Parent",  result.attributeAsString(ParentEntity.class, ParentEntity.Fields.name));
		Assert.assertEquals("Result attributeAsLong should work", (Long) 1001L,  result.attributeAsLong(ParentEntity.class, ParentEntity.Fields.homeId));
		Assert.assertEquals("Result attributeAsDouble should work", 85.5, result.attributeAsDouble(ParentEntity.class, ParentEntity.Fields.matchingPoint), 0.01);
		Assert.assertEquals("Result attributeAsBoolean should work", Boolean.FALSE, result.attributeAsBoolean(ParentEntity.class, ParentEntity.Fields.archived));
		Assert.assertEquals("Result attributeAsString for externalName should work", "external_test", result.attributeAsString(ParentEntity.class, ParentEntity.Fields.externalName));
		
		// Test enum handling with attributeAsEnum
		Assert.assertEquals("Result attributeAsEnum should work for JobEnum", ParentEntity.JobEnum.Analysis, result.attributeAsEnum(ParentEntity.class, ParentEntity.Fields.status));
		Assert.assertEquals("Result attributeAsEnum should work for StringEnum", ParentEntity.StringEnum.ABC, result.attributeAsEnum(ParentEntity.class, ParentEntity.Fields.stringEnum));
		
		// Test date fields - created should exist, updated might be null for new records
		Assert.assertNotNull("Result attributeAsDate should work for created", result.attributeAsDate(ParentEntity.class, ParentEntity.Fields.created));
		// Updated field is null for newly inserted records (only set on updates) so we test that the method works but don't assert the value
		result.attributeAsDate(ParentEntity.class, ParentEntity.Fields.updated); // Should not throw exception
		
		// Test additional attributeAs methods for comprehensive coverage
		// Test Entity-based methods (using Entity instead of Class)
		com.estivate.Entity<ParentEntity> parentEntity = new com.estivate.Entity<>(ParentEntity.class);
		Assert.assertEquals("Result attributeAsString with Entity should work", "Test Parent", result.attributeAsString(parentEntity, ParentEntity.Fields.name));
		Assert.assertEquals("Result attributeAsLong with Entity should work", (Long) 1001L, result.attributeAsLong(parentEntity, ParentEntity.Fields.homeId));
		Assert.assertEquals("Result attributeAsDouble with Entity should work", 85.5, result.attributeAsDouble(parentEntity, ParentEntity.Fields.matchingPoint), 0.01);
		Assert.assertEquals("Result attributeAsBoolean with Entity should work", Boolean.FALSE, result.attributeAsBoolean(parentEntity, ParentEntity.Fields.archived));
		Assert.assertNotNull("Result attributeAsDate with Entity should work for created",  result.attributeAsDate(parentEntity, ParentEntity.Fields.created));
		
		// Test additional numeric type methods
		Integer homeIdAsInt = result.attributeAsInteger(ParentEntity.class, ParentEntity.Fields.homeId);
		Assert.assertEquals("Result attributeAsInteger should work", (Integer) 1001, homeIdAsInt);
		
		Float matchingPointAsFloat = result.attributeAsFloat(ParentEntity.class, ParentEntity.Fields.matchingPoint);
		Assert.assertEquals("Result attributeAsFloat should work", 85.5f, matchingPointAsFloat, 0.01f);
		
	}


  
}
