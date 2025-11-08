package com.estivate.test.query;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class SelectQueryOrderTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void orderTest1() throws SQLException {
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.isNull);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("IS NULL ASC"));
		
	}
	
	@Test
	void orderLimitTest(){
		
		context.truncateTable(CustomerEntity.class);

		List<CustomerEntity> list = Arrays.asList(
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer(),
			DatabaseGenerator.createRandomCustomer()
		);
		
		context.updateOrInsert(list);
		
		SelectQuery<CustomerEntity> projectIdAscOrderedTaskQuery = Estivate.selectQuery(CustomerEntity.class).orderByAsc(CustomerEntity.class, AbstractEntity.Fields.id).limit(2);
		List<CustomerEntity> projectIdAscOrderedTasks = context.fetchListAs(projectIdAscOrderedTaskQuery, CustomerEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getId()).min().orElse(0), projectIdAscOrderedTasks.get(0).getId());
		Assert.assertEquals(2, projectIdAscOrderedTasks.size());
		
		SelectQuery<CustomerEntity> idDescOrderedTaskQuery = Estivate.selectQuery(CustomerEntity.class).orderByDesc(CustomerEntity.class, AbstractEntity.Fields.id);
		List<CustomerEntity> idDescOrderedTasks = context.fetchListAs(idDescOrderedTaskQuery, CustomerEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getId()).max().orElse(0), idDescOrderedTasks.get(0).getId());
		
	}
	

	
	
}
