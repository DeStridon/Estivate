package com.estivate.test;
import java.util.List;

import org.junit.Test;

import com.estivate.EstivateJoin;
import com.estivate.EstivateQuery;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.NoUseEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskHistoryEntity;

public class SimpleQueryTest {
	
	
	@Test
	public void simpleQueryTest() {
		
		EstivateQuery query = new EstivateQuery(TaskEntity.class);
		
		//query.join(new EstivateJoin(SegmentEntity.class, FragmentEntity.class, SegmentEntity.Fields.targetFragmentId, FragmentEntity.Fields.id));
		
		query
		.join(SegmentEntity.class)
		.join(NoUseEntity.class)
		.eq(FragmentEntity.class, FragmentEntity.Fields.projectId, 1)
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalName, "externalNameTest")
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalReference, "externalReferenceTest")
		.eq(TaskHistoryEntity.class, TaskHistoryEntity.Fields.projectId, 1)
		.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, List.of("a", "b", "c"))
		
		// content search
		.in(TaskEntity.class, TaskEntity.Fields.id, List.of(1,2,3,4));
		
		// workers username
		query.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, List.of("jojo", "nanard", "andré"));
		
		// has comments
	
		
		System.out.println(query.compile());
	}

}
