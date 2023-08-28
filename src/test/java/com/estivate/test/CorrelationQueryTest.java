package com.estivate.test;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.estivate.ConnectionExecutor;
import com.estivate.EstivateField;
import com.estivate.EstivateJoin;
import com.estivate.EstivateJoin.JoinType;
import com.estivate.EstivateQuery;
import com.estivate.EstivateQuery.Entity;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.SegmentEntity.MicroState;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.TaskHistoryEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorrelationQueryTest {
	
	ConnectionExecutor connection;
	
	@Before
	public void prepare() throws SQLException {
		this.connection = new ConnectionExecutor(DriverManager.getConnection("jdbc:h2:mem:test"));

		connection.create(TaskEntity.class);
		connection.create(SegmentEntity.class);
		connection.create(FragmentEntity.class);
		connection.create(TaskHistoryEntity.class);
		
		TaskEntity task1 = connection.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #1").status(MacroState.Delivered).build());
		SegmentEntity segment11 = generateSegment(task1, "content A");
		SegmentEntity segment12 = generateSegment(task1, "content B");
		
		TaskEntity task2 = connection.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #2").status(MacroState.Translation).build());
		SegmentEntity segment21 = generateSegment(task2, "content A");
		SegmentEntity segment22 = generateSegment(task2, "content C");
		
		TaskEntity task3 = connection.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #3").status(MacroState.Analysis).build());
		SegmentEntity segment31 = generateSegment(task3, "content A");
		SegmentEntity segment32 = generateSegment(task3, "content B");
		SegmentEntity segment33 = generateSegment(task3, "content C");
		
		
//		// Task 2 heritates from Task 1
//		EstivateQuery fetchQuery = correlationQuery(task2, List.of(segment21, segment22), CorrelationDirection.Fetch);
//		connection.listAs(fetchQuery, SegmentEntity.class);
//		
//
//		// Task 2 updates task 3
//		EstivateQuery patchQuery = correlationQuery(task2, List.of(segment21, segment22), CorrelationDirection.Patch);
//		connection.listAs(patchQuery, SegmentEntity.class);
		
		
	}
	
	EstivateQuery correlationQuery(TaskEntity task, List<SegmentEntity> segments, CorrelationDirection direction) {
		
		
		EstivateQuery query = new EstivateQuery(SegmentEntity.class)
				.join(TaskEntity.class)
				.eq(SegmentEntity.class, SegmentEntity.Fields.projectId, task.getProjectId())
				.eq(SegmentEntity.class, SegmentEntity.Fields.sourceLanguage, task.getSourceLanguage())
				.eq(SegmentEntity.class, SegmentEntity.Fields.targetLanguage, task.getTargetLanguage())
				.notEq(TaskEntity.class, TaskEntity.Fields.archived, true)
				.notEq(SegmentEntity.class, SegmentEntity.Fields.taskId, task.getId());
		
		if(direction == CorrelationDirection.Fetch) {
			query
				.gt(SegmentEntity.class, SegmentEntity.Fields.macroStatus, task.getStatus())
				.notEq(SegmentEntity.class, SegmentEntity.Fields.microStatus, MicroState.InProgress);
		}
		
		else if(direction == CorrelationDirection.Patch) {
			query
				.lt(SegmentEntity.class, SegmentEntity.Fields.macroStatus, task.getStatus())
				.eq(SegmentEntity.class, SegmentEntity.Fields.microStatus, MicroState.Waiting);
		}
		
		else {
			log.error("At least a direction should be given");
		}
				
		return query;
	}
	
	static enum CorrelationDirection{ Fetch, Patch };
	
	SegmentEntity generateSegment(TaskEntity task, String sourceContent) {

		SegmentEntity segment = SegmentEntity.builder().projectId(1).taskId(task.getId())
				
				.sourceContent(sourceContent)
				.build();
		return connection.saveOrUpdate(segment);		

	}
	
	@Test
	public void testRequest() {
		
		CorrelationScope scope = CorrelationScope.Task;
		
		
		// Upsegment choice criterias :
		// - fragment cant be something ordering : we shall have the different choices to be 
		// - scope ordering : task, fragment, project
		
		
		
		Entity correlatedSegment = new Entity(SegmentEntity.class, "CorrelatedSegment");
		EstivateJoin join = new EstivateJoin(SegmentEntity.class, correlatedSegment, SegmentEntity.Fields.projectId, SegmentEntity.Fields.projectId)
				.on(SegmentEntity.Fields.sourceLanguage, SegmentEntity.Fields.sourceLanguage)
				.on(SegmentEntity.Fields.targetLanguage, SegmentEntity.Fields.targetLanguage)
				.on(SegmentEntity.Fields.sourceContent, SegmentEntity.Fields.sourceContent)
				.joinType(JoinType.LEFT);
		
		if(scope == CorrelationScope.Task) {
			join.on(SegmentEntity.Fields.taskId, SegmentEntity.Fields.taskId);
		}
		else if(scope == CorrelationScope.Document) {
			
		}
		
		// Add where not equals to field value
		
		EstivateQuery query = new EstivateQuery(SegmentEntity.class)
				.join(join)
				.eq(SegmentEntity.class, SegmentEntity.Fields.taskId, 75)
				.eq(SegmentEntity.class, SegmentEntity.Fields.projectId, 1)
				.eq(SegmentEntity.class, SegmentEntity.Fields.microStatus, MicroState.Waiting)
				.gt(correlatedSegment, SegmentEntity.Fields.macroStatus, MacroState.Translation)
				.isNotNull(SegmentEntity.class, SegmentEntity.Fields.archived)
				.notEq(correlatedSegment, SegmentEntity.Fields.id, new EstivateField(SegmentEntity.class, SegmentEntity.Fields.id));
				
		System.out.println(connection.toStatement(query).query());
		
		connection.list(query);
		
	}
	
	public static enum CorrelationScope{
		Project,
		Document, // same document
		DocumentResubmission, //
		Fragment, // same fragment
		TaskResubmission,
		Task //
	}
	
}
