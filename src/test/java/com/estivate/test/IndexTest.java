package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Index;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.util.IndexDiff;

public class IndexTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void contextTest() {
		
			
		context.addIndex(TaskEntity.class, "yo", Arrays.asList(Query.nameMapper.mapDatabaseField(TaskEntity.Fields.projectId)+" ASC"));
		
		List<String> indexes = context.listIndexes(TaskEntity.class);
		
		System.out.println(indexes);
		
	}
	
	@Test
	public void entityIndexTest() {
		
		IndexDiff id = new IndexDiff(context, FragmentEntity.class);
		
		List<Index> indexes = id.getCodeIndex();
		Assert.assertEquals(2, indexes.size());
		
		
	}
	
}
