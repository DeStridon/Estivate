package com.estivate.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.TaskEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuerySelectMethodTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectMaxTest() {
		
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

}
