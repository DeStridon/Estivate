package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;
import com.estivate.test.entities.ParentEntity.JobEnum;
import com.estivate.test.entities.ParentEntity.StringEnum;
import com.estivate.util.Chronometer;

public class ResultTest {
	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void testParallel() {
		
		
		ParentEntity parent = ParentEntity.builder()
				.homeId(10)
				.name("parallel test parent")
				.updated(new Date())
				.build();
		
		for(int i = 0; i < 5000; i++) {
			parent.setName(parent.getName()+" - "+i);
			parent.setId(0);
			context.updateOrInsert(parent);
		}
		
		Query<ParentEntity> query = new Query<>(ParentEntity.class);
		query.eq(ParentEntity.class, ParentEntity.Fields.name, "parallel test task");
		
		List<ParentEntity> parents = context.fetchList(query);
		
		
	}
	
	@Test
	public void testMapEnum() {
		
		ParentEntity parent = ParentEntity.builder()
				.homeId(10)
				.name("parallel test task")
				.updated(new Date())
				.status(JobEnum.Correction)
				.stringEnum(StringEnum.DEF)
				.build();
		
		context.updateOrInsert(parent);
		
		Query<ParentEntity> query = new Query<>(ParentEntity.class);
		query.eq(AbstractEntity.Fields.id, parent.getId());
		
		Result results = context.fetchListAsResults(query).get(0);
		
		JobEnum status = (JobEnum) results.attributeAsEnum(ParentEntity.class, ParentEntity.Fields.status);
		assertEquals(JobEnum.Correction, status);
		
		StringEnum stringEnum = (StringEnum) results.attributeAsEnum(ParentEntity.class, ParentEntity.Fields.stringEnum);
		assertEquals(StringEnum.DEF, stringEnum);
	
		Date updatedDate = results.attributeAsDate(ParentEntity.class, ParentEntity.Fields.updated);
		
		
		
	}

	@Test
	public void testMap() {
		
		ParentEntity parent1 = ParentEntity.builder()
				.homeId(22)
				.name("map test 1")
				.status(JobEnum.Legal)
				.updated(new Date())
				.build();
		
		context.insert(parent1);
		
		ParentEntity parent2 = ParentEntity.builder()
				.homeId(23)
				.name("map test 2")
				.status(JobEnum.Analysis)
				.updated(new Date())
				.build();
		
		context.insert(parent2);
		
		
		
		Query<ParentEntity> query = new Query<>(ParentEntity.class)
				.likeStartsWith(ParentEntity.Fields.name, "map test");
		
		Map<String, JobEnum> map = context.aggregateToMap(query, x -> x.attributeAsString(ParentEntity.class, ParentEntity.Fields.name), x -> (JobEnum) x.attributeAsEnum(ParentEntity.class, ParentEntity.Fields.status));
		
		assertEquals(JobEnum.Legal, map.get("map test 1"));
		assertEquals(JobEnum.Analysis, map.get("map test 2"));
		
		
	}
	
	
	
	
	
	

}
