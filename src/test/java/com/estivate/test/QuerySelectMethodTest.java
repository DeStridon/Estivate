 package com.estivate.test;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class QuerySelectMethodTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectMaxTest() throws SQLException {
		
		Query query = new Query(ParentEntity.class)
				.selectMaxAs(ParentEntity.class, AbstractEntity.Fields.id, "maxTaskId")
				.selectCountAs(ParentEntity.class, AbstractEntity.Fields.id, "countTaskId")
				.selectMinAs(ParentEntity.class, AbstractEntity.Fields.id, "minTaskId")
				.selectGroupConcatAs(ParentEntity.class, AbstractEntity.Fields.id, "groupTaskId")
				.selectSumAs(ParentEntity.class, AbstractEntity.Fields.id, "sumTaskId")
				;

		System.out.println(context.queryAsString(query));
		
		List<Result> results = context.fetchList(query);
		
		
		Assert.assertEquals(1, results.size());
		
	}
	
	@Test
	public void countTest() throws SQLException {
		Query query = new Query(ParentEntity.class);
		
		query.selectCount();
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest() throws SQLException {
		Query query = new Query(ParentEntity.class)
				.selectAll(ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id);
		
		Assert.assertTrue(context.queryAsString(query).toUpperCase().startsWith("SELECT DISTINCT"));
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest2() throws SQLException {
		Query query = new Query(ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id)
				.selectAll(ParentEntity.class);
		
		System.out.println(context.queryAsString(query));
		
	}
	
	@Test
	public void selectDistinctTest3() throws SQLException {
		Query query = new Query(ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id)
				.select(ParentEntity.class, AbstractEntity.Fields.id);

		context.fetchList(query);
		System.out.println(context.queryAsString(query));
		
	}

	@Test
	public void selectDistinctTest4() throws SQLException {
		Query query = new Query(ParentEntity.class)
				.selectDistinct(ParentEntity.class, AbstractEntity.Fields.id)
				.selectDistinct(ParentEntity.class, ParentEntity.Fields.sourceLanguage)
				.select(ParentEntity.class, AbstractEntity.Fields.id);

		context.fetchList(query);
		
	}

	

}
