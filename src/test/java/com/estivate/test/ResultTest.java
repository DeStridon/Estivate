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
	public void testPerf() {
		
		
		
		Map<String, String> map = new HashMap<>();
		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.homeId), "1");
		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.parentId), "555");
		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.description), "blablablablablabla");

		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.job), "4");
		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.mood), "2");
		map.put(context.nameMapper.mapEntity(ChildEntity.class, ChildEntity.Fields.lastSeen), "2024-11-19 22:02:03.254");
		
		Statement statement = new Statement(context, null);
		
		
		
		List<Result> results = new ArrayList<>();
		for(int i = 0; i < 2; i++) {
			results.add(new Result(statement, map));
		}

		Chronometer chrono = new Chronometer("bla");

		
		List<ChildEntity> children = results.stream().map(x -> x.mapTo(ChildEntity.class)).collect(Collectors.toList());
		
		chrono.end("end");
		
	}
	
	@Test
	public void testParallel() {
		
		
		ParentEntity task = ParentEntity.builder()
				.homeId(10)
				.name("parallel test task")
				.updated(new Date())
				.build();
		
		for(int i = 0; i < 5000; i++) {
			 context.saveOrUpdate(task);
			 task.setId(0);
		}
		
		Query query = new Query(ParentEntity.class);
		query.eq(ParentEntity.class, ParentEntity.Fields.name, "parallel test task");
		
		List<ParentEntity> parents = context.fetchListAs(query, ParentEntity.class);
		
		
	}
	
	@Test
	public void testMapEnum() {
		
		ParentEntity task = ParentEntity.builder()
				.homeId(10)
				.name("parallel test task")
				.updated(new Date())
				.status(JobEnum.Correction)
				.stringEnum(StringEnum.DEF)
				.build();
		
		context.saveOrUpdate(task);
		
		Query query = new Query(ParentEntity.class);
		query.eq(ParentEntity.class, AbstractEntity.Fields.id, task.getId());
		
		Result results = context.fetchList(query).get(0);
		
		JobEnum status = (JobEnum) results.getAsEnum(ParentEntity.class, ParentEntity.Fields.status);
		assertEquals(JobEnum.Correction, status);
		
		StringEnum stringEnum = (StringEnum) results.getAsEnum(ParentEntity.class, ParentEntity.Fields.stringEnum);
		assertEquals(StringEnum.DEF, stringEnum);
	
		Date updatedDate = results.mapToDate(ParentEntity.class, ParentEntity.Fields.updated);
		
		
		
	}
	
	
	
	
	
	

}
