package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.NameMapper;
import com.estivate.Result;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.TaskEntity.StringEnum;
import com.estivate.util.Chronometer;

public class ResultTest {
	
	Context context = DatabaseGenerator.getContext();
	
	@Test
	public void testPerf() {
		
		
		
		Map<String, String> map = new HashMap<>();
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.projectId), "1");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.sourceFragmentId), "2");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.targetFragmentId), "45");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.taskId), "555");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.sourceContent), "blablablablablabla");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.targetContent), "pihiphiphpih");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.sourceLanguage), "en_GB");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.targetLanguage), "fr_FR");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.macroStatus), "4");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.microStatus), "2");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.updated), "2024-11-19 22:02:03.254");
		map.put(Query.nameMapper.mapEntity(SegmentEntity.class, SegmentEntity.Fields.archived), "2023-11-19 22:02:03");
		
		
		
		
		List<Result> results = new ArrayList<>();
		for(int i = 0; i < 2; i++) {
			results.add(new Result(map));
		}

		Chronometer chrono = new Chronometer("bla");

		
		List<SegmentEntity> segments1 = results.stream().map(x -> x.mapTo(SegmentEntity.class)).collect(Collectors.toList());
		
		chrono.end("end");
		
	}
	
	@Test
	public void testParallel() {
		
		
		TaskEntity task = TaskEntity.builder()
				.projectId(10)
				.name("parallel test task")
				.updated(new Date())
				.build();
		
		for(int i = 0; i < 5000; i++) {
			 context.saveOrUpdate(task);
			 task.setId(0);
		}
		
		Query query = new Query(TaskEntity.class);
		query.eq(TaskEntity.class, TaskEntity.Fields.name, "parallel test task");
		
		List<TaskEntity> tasks = context.listAs(query, TaskEntity.class);
		
		
	}
	
	@Test
	public void testMapEnum() {
		
		TaskEntity task = TaskEntity.builder()
				.projectId(10)
				.name("parallel test task")
				.updated(new Date())
				.status(MacroState.Correction)
				.stringEnum(StringEnum.DEF)
				.build();
		
		context.saveOrUpdate(task);
		
		Query query = new Query(TaskEntity.class);
		query.eq(TaskEntity.class, AbstractEntity.Fields.id, task.getId());
		
		Result results = context.list(query).get(0);
		
		MacroState status = (MacroState) results.getAsEnum(TaskEntity.class, TaskEntity.Fields.status);
		assertEquals(MacroState.Correction, status);
		
		StringEnum stringEnum = (StringEnum) results.getAsEnum(TaskEntity.class, TaskEntity.Fields.stringEnum);
		assertEquals(StringEnum.DEF, stringEnum);
	
		Date taskDate = results.mapToDate(TaskEntity.class, TaskEntity.Fields.updated);
		
		
		
	}
	
	
	
	
	
	

}
