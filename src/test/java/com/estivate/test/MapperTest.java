package com.estivate.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

public class MapperTest {
	
	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testPerf() {
		
		CustomerEntity newParent = CustomerEntity.builder().name("parent 1 name").build();
		context.updateOrInsert(newParent);
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.eq(CustomerEntity.class, AbstractEntity.Fields.id, newParent.getId());
				
		List<CustomerEntity> parents = context.fetchAsList(query, CustomerEntity.class);
		
		Assert.assertEquals(1, parents.size());
		
		System.out.println(parents.get(0).getCreated());
		
	}

}
