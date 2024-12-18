package com.estivate.test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

public class QuerySubTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void subTest1() throws SQLException{

		Query subQuery = new Query(SegmentEntity.class)
				.select(SegmentEntity.class, SegmentEntity.Fields.taskId)
				.eq(SegmentEntity.class, SegmentEntity.Fields.taskId, new PropertyValue(TaskEntity.class, AbstractEntity.Fields.id));
		
		
		Query query = new Query(TaskEntity.class)
				.inSubQuery(TaskEntity.class, AbstractEntity.Fields.id, subQuery);
		
		try(Connection connection = context.datasource.getConnection()){
			Statement statement = Statement.toStatement(context, connection, query);
			
			System.out.println(statement.query());
		}
		
	}
	
	
}
