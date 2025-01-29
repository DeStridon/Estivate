package com.estivate.test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

public class QuerySubTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void subTest1() throws SQLException{

		Query subQuery = new Query(ChildEntity.class)
				.select(ChildEntity.class, ChildEntity.Fields.parentId)
				.eq(ChildEntity.class, ChildEntity.Fields.parentId, new PropertyValue(ParentEntity.class, AbstractEntity.Fields.id));
		
		
		Query query = new Query(ParentEntity.class)
				.inSubQuery(ParentEntity.class, AbstractEntity.Fields.id, subQuery);
		
		try(Connection connection = context.datasource.getConnection()){
			Statement statement = Statement.toStatement(context, connection, query);
			
			System.out.println(statement.query());
		}
		
	}
	
	
}
