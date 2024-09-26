 package com.estivate.test;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class QuerySelectMethodTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectMaxTest() throws SQLException {
		
		Query query = new Query(TaskEntity.class)
				.selectMax(TaskEntity.class, AbstractEntity.Fields.id, "maxTaskId")
				.selectCount(TaskEntity.class, AbstractEntity.Fields.id, "countTaskId")
				.selectMin(TaskEntity.class, AbstractEntity.Fields.id, "minTaskId")
				.selectGroupConcat(TaskEntity.class, AbstractEntity.Fields.id, "groupTaskId")
				.selectSum(TaskEntity.class, AbstractEntity.Fields.id, "sumTaskId")
				;

		System.out.println(context.queryAsString(query));
		
		List<Result> results = context.list(query);
		
		
		Assert.assertEquals(1, results.size());
		
	}
	
	@Test
	public void countTest() throws SQLException {
		Query query = new Query(TaskEntity.class);
		
		query.selectCount();
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest() throws SQLException {
		Query query = new Query(TaskEntity.class)
				.select(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id);
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest2() throws SQLException {
		Query query = new Query(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.select(TaskEntity.class);
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest3() throws SQLException {
		Query query = new Query(TaskEntity.class)
				.selectDistinct(TaskEntity.class, AbstractEntity.Fields.id)
				.select(TaskEntity.class, AbstractEntity.Fields.id);

		System.out.println(context.queryAsString(query));
		
	}

}
