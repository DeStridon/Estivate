package com.estivate.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.Test;

import com.estivate.EstivateJoin;
import com.estivate.EstivateJoin.JoinType;
import com.estivate.EstivateQuery;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.NoUseEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskActivityEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskHistoryEntity;

public class SimpleQueryTest {
	
	
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
		query.join(new EstivateJoin(TaskEntity.class, TaskActivityEntity.class).joinerAttribute(TaskEntity.Fields.id).joinedAttribute(TaskActivityEntity.Fields.taskId).joinType(JoinType.LEFT));
		
		query.isNotNull(TaskActivityEntity.class, TaskActivityEntity.Fields.id);
		System.out.println(query.compile());
		
	}
	
	@Test
	public void h2Test() throws SQLException {
        
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
 
        System.out.println("Connected to H2 in-memory database.");
 
        String sql = "Create table students (ID int primary key, name varchar(50))";
         
        Statement statement = connection.createStatement();
         
        statement.execute(sql);
         
        System.out.println("Created table students.");
         
        sql = "Insert into students (ID, name) values (1, 'Nam Ha Minh')";
         
        int rows = statement.executeUpdate(sql);
         
        if (rows > 0) {
            System.out.println("Inserted a new row.");
        }
 
        connection.close();
 

		
	}

}
