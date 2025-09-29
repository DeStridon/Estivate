package com.estivate.test.query;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.DeleteQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;

public class DeleteMultipleQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void deleteMultiple() throws SQLException {
		DeleteQuery<OrderEntity> query = Estivate.deleteQuery(OrderEntity.class)
				.joinLeft(OrderEntity.class, CustomerEntity.class)
				.isNull(CustomerEntity.class, AbstractEntity.Fields.id);
		
		System.out.println(context.queryAsString(query));
	}
	
}
