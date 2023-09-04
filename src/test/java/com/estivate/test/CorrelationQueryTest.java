package com.estivate.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.estivate.Context;
import com.estivate.Statement;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.Property;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.SegmentEntity.MicroState;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.TaskHistoryEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorrelationQueryTest {
	
	Connection connection;
	
	Context connectionExecutor;
	
	@Before
	public void prepare() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:h2:mem:test");
	
		this.connectionExecutor = new Context(DriverManager.getConnection("jdbc:h2:mem:test"));

		connectionExecutor.create(TaskEntity.class);
		connectionExecutor.create(SegmentEntity.class);
		connectionExecutor.create(FragmentEntity.class);
		connectionExecutor.create(TaskHistoryEntity.class);
		
		TaskEntity task1 = connectionExecutor.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #1").status(MacroState.Delivered).build());
		SegmentEntity segment11 = generateSegment(task1, "content A");
		SegmentEntity segment12 = generateSegment(task1, "content B");
		
		TaskEntity task2 = connectionExecutor.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #2").status(MacroState.Translation).build());
		SegmentEntity segment21 = generateSegment(task2, "content A");
		SegmentEntity segment22 = generateSegment(task2, "content C");
		
		TaskEntity task3 = connectionExecutor.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #3").status(MacroState.Analysis).build());
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
	
	Query correlationQuery(TaskEntity task, List<SegmentEntity> segments, CorrelationDirection direction) {
		
		
		Query query = new Query(SegmentEntity.class)
				//.join(TaskEntity.class)
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
		return connectionExecutor.saveOrUpdate(segment);		

	}
	
	@Test
	public void testRequest() {
		
		CorrelationScope scope = CorrelationScope.Fragment;
		
		
		// Upsegment choice criterias :
		// - fragment cant be something ordering : we shall have the different choices to be 
		// - scope ordering : task, fragment, project
		
		Entity correlatedSegment = new Entity(SegmentEntity.class, "CorrelatedSegment");
		Join segmentJoin = new Join(SegmentEntity.class, correlatedSegment, SegmentEntity.Fields.projectId, SegmentEntity.Fields.projectId)
				.on(SegmentEntity.Fields.sourceLanguage, SegmentEntity.Fields.sourceLanguage)
				.on(SegmentEntity.Fields.targetLanguage, SegmentEntity.Fields.targetLanguage)
				.on(SegmentEntity.Fields.sourceContent, SegmentEntity.Fields.sourceContent)
				.joinType(JoinType.LEFT);

		Query query = new Query(SegmentEntity.class)
				.join(segmentJoin)
				.eq(SegmentEntity.class, SegmentEntity.Fields.taskId, 75)
				.eq(SegmentEntity.class, SegmentEntity.Fields.projectId, 1)
				.eq(SegmentEntity.class, SegmentEntity.Fields.microStatus, MicroState.Waiting)
				.isNull(TaskEntity.class, TaskEntity.Fields.archived)
				.gt(correlatedSegment, SegmentEntity.Fields.macroStatus, MacroState.Translation)
				.isNotNull(SegmentEntity.class, SegmentEntity.Fields.archived)
				.notEq(correlatedSegment, SegmentEntity.Fields.id, new Property(SegmentEntity.class, SegmentEntity.Fields.id))
				.orderDesc(SegmentEntity.class, SegmentEntity.Fields.macroStatus)
				;

		
		if(scope == CorrelationScope.Task) {
			segmentJoin.on(SegmentEntity.Fields.taskId, SegmentEntity.Fields.taskId);
		}
		else if(scope == CorrelationScope.Resubmission) {
			Entity correlatedTask = new Entity(TaskEntity.class, "CorrelatedTask");
			query.join(new Join(SegmentEntity.class, TaskEntity.class, SegmentEntity.Fields.taskId, TaskEntity.Fields.id));
			query.join(new Join(correlatedSegment, correlatedTask, SegmentEntity.Fields.taskId, TaskEntity.Fields.id));
			query.eq(TaskEntity.class, TaskEntity.Fields.externalName, new Property(correlatedTask, TaskEntity.Fields.externalName));
		}
		else if(scope == CorrelationScope.Fragment) {
			Entity correlatedFragment = new Entity(FragmentEntity.class, "CorrelatedFragment");
			query.join(new Join(SegmentEntity.class, FragmentEntity.class, SegmentEntity.Fields.sourceFragmentId, FragmentEntity.Fields.id));
			query.join(new Join(correlatedSegment, correlatedFragment, SegmentEntity.Fields.taskId, FragmentEntity.Fields.id));
			query.eq(FragmentEntity.class, FragmentEntity.Fields.externalName, new Property(correlatedFragment, TaskEntity.Fields.externalName));
		}
		else if(scope == CorrelationScope.Document) {
		}
		
		// Add where not equals to field value
		
				
		System.out.println(Statement.toStatement(connection, query).query());
		
		connectionExecutor.list(query);
		
	}
	
	public static enum CorrelationScope{
		Resubmission,
		Fragment,
		Task,
		Document;
	}
	
}
