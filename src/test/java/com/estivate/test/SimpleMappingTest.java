package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

public class SimpleMappingTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectJoiningTest() {
		
		
		
		context.saveOrUpdate(SegmentEntity.builder().projectId(1).taskId(2).sourceContent("source content 1").wordcount(3).sourceFragmentId(5).build());
		context.saveOrUpdate(SegmentEntity.builder().projectId(1).taskId(2).sourceContent("source content 2").wordcount(3).sourceFragmentId(6).build());
		
		Query query = new Query(SegmentEntity.class)
				.eq(SegmentEntity.class, SegmentEntity.Fields.taskId, 2);
		
		List<SegmentEntity> results = context.listAsNew(query, SegmentEntity.class);
		
		assertEquals(2, results.size());
		
	}
}
