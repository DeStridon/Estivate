package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.entity.CompositeIndex;
import com.estivate.query.Query;
import com.estivate.test.entities.TaskEntity;
import com.estivate.util.IndexDiff;

public class IndexTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void contextTest() {
		
			
		context.addIndex(TaskEntity.class, "yo", Arrays.asList(Query.nameMapper.mapDatabaseField(TaskEntity.Fields.projectId)+" ASC"));
		
		List<CompositeIndex> indexes = context.listIndexes(TaskEntity.class);
		
		System.out.println(indexes);
		
	}
	
	@Test
	public void entityIndexTest() {
		
		IndexDiff id = new IndexDiff(context, TaskEntity.class);
		
		List<CompositeIndex> indexes = id.getEntityIndexes();
		Assert.assertEquals(1, indexes.size());
		
		id.applyIndex(indexes.get(0));
		
		List<CompositeIndex> missingDbIndexes = id.getMissingDatabaseIndex();
		Assert.assertEquals(0, missingDbIndexes.size());
	}
	
}
