package com.estivate.query.test;

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
import com.estivate.test.entities.ParentEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
public class SelectQueryOrderTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void orderTest1() throws SQLException {
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
				.orderAsc(ParentEntity.class, ParentEntity.Fields.name, Estivate.Functions.isNull);
		
		String queryString = context.queryAsString(query);
		
		Assert.assertTrue(queryString.contains("IS NULL ASC"));
		
	}
	
	@Test
	void orderLimitTest(){
		
		context.truncateTable(ParentEntity.class);

		List<ParentEntity> list = Arrays.asList(
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent()),
			context.updateOrInsert(DatabaseGenerator.createRandomParent())
		);
		
		
		SelectQuery<ParentEntity> projectIdAscOrderedTaskQuery = Estivate.selectQuery(ParentEntity.class).orderAsc(ParentEntity.class, ParentEntity.Fields.homeId).limit(2);
		List<ParentEntity> projectIdAscOrderedTasks = context.fetchListAs(projectIdAscOrderedTaskQuery, ParentEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getHomeId()).min().orElse(0), projectIdAscOrderedTasks.get(0).getHomeId());
		Assert.assertEquals(2, projectIdAscOrderedTasks.size());
		
		SelectQuery<ParentEntity> idDescOrderedTaskQuery = Estivate.selectQuery(ParentEntity.class).orderDesc(ParentEntity.class, AbstractEntity.Fields.id);
		List<ParentEntity> idDescOrderedTasks = context.fetchListAs(idDescOrderedTaskQuery, ParentEntity.class);
		Assert.assertEquals(list.stream().mapToLong(x -> x.getId()).max().orElse(0), idDescOrderedTasks.get(0).getId());
		
	}
	

	
	
}
