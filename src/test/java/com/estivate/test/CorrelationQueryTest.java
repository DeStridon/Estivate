package com.estivate.test;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;

import com.estivate.ConnectionExecutor;
import com.estivate.EstivateQuery;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.TaskHistoryEntity;

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
		
		
		
			
		
		
		
	}
	
	EstivateQuery correlationQuery(TaskEntity task) {
		EstivateQuery query = new EstivateQuery(SegmentEntity.class)
				.eq(SegmentEntity.class, SegmentEntity.Fields.projectId, task.getProjectId())
				.eq(SegmentEntity.class, SegmentEntity.Fields.sourceLanguage, task.getSourceLanguage())
				.eq(SegmentEntity.class, SegmentEntity.Fields.targetLanguage, task.getTargetLanguage())
				.notEq(SegmentEntity.class, SegmentEntity.Fields.taskId, task.getId());
		
		query.gt(SegmentEntity.class, SegmentEntity.Fields.macroStatus, task.getStatus());
				
		return query;
	}
	
	SegmentEntity generateSegment(TaskEntity task, String sourceContent) {

		SegmentEntity segment = SegmentEntity.builder().projectId(1).taskId(task.getId())
				
				.sourceContent(sourceContent)
				.build();
		return connection.saveOrUpdate(segment);		

	}
	
}
