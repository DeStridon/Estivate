package com.estivate.test.query.h2;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryCriterionAttributeTest {

	Context context = H2TestDatabase.getContext();
	
	
	@Test
	public void attributeTest() {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.lt(Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.archived, Estivate.Functions.date_add(1, "DAY")), Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.created, Estivate.Functions.date_add(1, "DAY")));
		
		System.out.println(context.queryAsString(query));
		
	}

}
