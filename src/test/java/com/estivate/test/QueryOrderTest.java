package com.estivate.test;

import java.sql.SQLException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.ParentEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class QueryOrderTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void orderTest1() throws SQLException {
		
		Query<ParentEntity> query = Estivate.query(ParentEntity.class)
				.orderAsc(ParentEntity.class, ParentEntity.Fields.name, Estivate.Functions.isNull);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("IS NULL ASC"));
		
	}
	
	

	
	
}
