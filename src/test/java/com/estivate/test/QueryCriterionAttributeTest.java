package com.estivate.test;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.ParentEntity;

public class QueryCriterionAttributeTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void attributeTest() {
		
		Query<ParentEntity> query = Estivate.query(ParentEntity.class)
				.lt(Estivate.attribute(ParentEntity.class, ParentEntity.Fields.archived, Estivate.Functions.date_add(1, "DAY")), Estivate.attribute(ParentEntity.class, ParentEntity.Fields.created, Estivate.Functions.date_add(1, "DAY")));
		
		System.out.println(context.queryAsString(query));
		
	}

}
