package com.estivate.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.estivate.ConnectionExecutor;
import com.estivate.EstivateJoin;
import com.estivate.EstivateJoin.JoinType;
import com.estivate.EstivateQuery;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.NoUseEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskActivityEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.TaskHistoryEntity;
import com.estivate.test.entities.misc.Language;

public class AdvancedQueryTest {
	
	Connection connection;
	
	@Before
	public void prepare() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:h2:mem:test");
	}
	
	@Test
	public void simpleQueryTest() {
		
		EstivateQuery query = new EstivateQuery(TaskEntity.class);
		
		//query.join(new EstivateJoin(SegmentEntity.class, FragmentEntity.class, SegmentEntity.Fields.targetFragmentId, FragmentEntity.Fields.id));
		
		query
		.join(SegmentEntity.class)
		.join(NoUseEntity.class)
		.eq(FragmentEntity.class, FragmentEntity.Fields.projectId, 1)
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalName, "externalNameTest")
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalReference, "externalReferenceTest")
		.eq(TaskHistoryEntity.class, TaskHistoryEntity.Fields.projectId, 1)
		.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, List.of("a", "b", "c"))
		
		// content search
		.in(TaskEntity.class, TaskEntity.Fields.id, List.of(1,2,3,4));
		
		// workers username
		query.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, List.of("jojo", "nanard", "andré"));
		
		// has comments - right join, filter on task activity & segment activity
		// left join where not null
		query.join(new EstivateJoin(TaskEntity.class, TaskActivityEntity.class, TaskEntity.Fields.id, TaskActivityEntity.Fields.taskId).joinType(JoinType.LEFT));
		
		query.isNotNull(TaskActivityEntity.class, TaskActivityEntity.Fields.id);
		
		ConnectionExecutor ce = new ConnectionExecutor(connection);
		
		System.out.println(ce.toStatement(query).query());
		
	}
	
	
	@Test
	public void taskEnumTest() {
		
		EstivateQuery query = new EstivateQuery(TaskEntity.class);
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, 1, 2, 3, 4);
		
		query.in(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Language.en_GB, Language.fr_FR);
		
		query.in(TaskEntity.class, TaskEntity.Fields.status, MacroState.Analysis, MacroState.Translation);
		
		ConnectionExecutor ce = new ConnectionExecutor(connection);
		
		System.out.println(ce.toStatement(query).query());
		
		
		
	}

}
