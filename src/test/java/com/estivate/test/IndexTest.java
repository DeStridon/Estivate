package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.index.IndexDiff;
import com.estivate.index.IndexScan;
import com.estivate.index.Annotations.CompositeIndex;
import com.estivate.test.entities.ParentEntity;

public class IndexTest {

	Context context = DatabaseGenerator.getContext();
	

	@Test
	public void scanTest() {
		IndexScan is = new IndexScan(context, "com.estivate.test.entities");
		System.out.println(is.getIndexDiffs());
	}
	
	@Test
	public void contextTest() {
		
			
		context.addIndex(ParentEntity.class, "yo", Arrays.asList(context.nameMapper.mapDatabaseField(ParentEntity.Fields.homeId)+" ASC"));
		
		List<CompositeIndex> indexes = context.listIndexes(ParentEntity.class);
		
		System.out.println(indexes);
		
	}
	
	@Test
	public void entityIndexTest() {
		
		IndexDiff id = new IndexDiff(context, ParentEntity.class);
		
		List<CompositeIndex> indexes = id.getEntityIndexes();
		Assert.assertEquals(2, indexes.size());
		
		id.applySpecific(indexes.get(0));
		
		id.cleanSpecific(indexes.get(0));
		
		List<CompositeIndex> missingDbIndexes = id.listToApply();
		Assert.assertEquals(1, missingDbIndexes.size());
	}
	
}
