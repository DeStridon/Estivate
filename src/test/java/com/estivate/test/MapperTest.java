package com.estivate.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

public class MapperTest {
	
	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testPerf() {
		
		ParentEntity newParent = context.updateOrInsert(ParentEntity.builder().name("parent 1 name").build());	
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
				.eq(ParentEntity.class, AbstractEntity.Fields.id, newParent.getId());
				
		List<ParentEntity> parents = context.fetchListAs(query, ParentEntity.class);
		
		Assert.assertEquals(1, parents.size());
		
		System.out.println(parents.get(0).getCreated());
		
				
	}

}
