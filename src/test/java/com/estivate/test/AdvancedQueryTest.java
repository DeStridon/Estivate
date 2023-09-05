package com.estivate.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.estivate.Context;
import com.estivate.Statement;
import com.estivate.query.Join;
import com.estivate.query.Join.JoinType;
import com.estivate.query.Query;
import com.estivate.test.entities.FragmentEntity;
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
		
		Query query = new Query(TaskEntity.class);
		
		//query.join(new EstivateJoin(SegmentEntity.class, FragmentEntity.class, SegmentEntity.Fields.targetFragmentId, FragmentEntity.Fields.id));
		
		query
		//.join(SegmentEntity.class)
		//.join(NoUseEntity.class)
		.eq(FragmentEntity.class, FragmentEntity.Fields.projectId, 1)
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalName, "externalNameTest")
		.eqIfNotNull(FragmentEntity.class, FragmentEntity.Fields.externalReference, "externalReferenceTest")
		.eq(TaskHistoryEntity.class, TaskHistoryEntity.Fields.projectId, 1)
		.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, Arrays.asList("a", "b", "c"))
		
		// content search
		.in(TaskEntity.class, TaskEntity.Fields.id, Arrays.asList(1,2,3,4));
		
		// workers username
		query.in(TaskHistoryEntity.class, TaskHistoryEntity.Fields.username, Arrays.asList("jojo", "nanard", "andré"));
		
		// has comments - right join, filter on task activity & segment activity
		// left join where not null
		query.join(new Join(TaskEntity.class, TaskActivityEntity.class, TaskEntity.Fields.id, TaskActivityEntity.Fields.taskId).joinType(JoinType.LEFT));
		
		query.isNotNull(TaskActivityEntity.class, TaskActivityEntity.Fields.id);
		
		System.out.println(Statement.toStatement(connection, query).query());
		
	}
	
	
	@Test
	public void taskEnumTest() {
		
		Query query = new Query(TaskEntity.class);
		
		query.in(TaskEntity.class, TaskEntity.Fields.projectId, 1, 2, 3, 4);
		
		query.in(TaskEntity.class, TaskEntity.Fields.sourceLanguage, Language.en_GB, Language.fr_FR);
		
		query.in(TaskEntity.class, TaskEntity.Fields.status, MacroState.Analysis, MacroState.Translation);
		
		Context ce = new Context(connection);
		
		System.out.println(Statement.toStatement(connection, query).query());
		
		
		
	}

}
