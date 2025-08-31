package com.estivate.query.test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

public class SelectQuerySubTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void inSubQueryTest() throws SQLException{

		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
				.select(ChildEntity.class, ChildEntity.Fields.parentId)
				.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		
		SelectQuery<ParentEntity> query = Estivate.query(ParentEntity.class)
				.inSubQuery(ParentEntity.class, AbstractEntity.Fields.id, subQuery);
		
		try(Connection connection = context.datasource.getConnection()){
			Statement statement = Statement.toStatement(context, connection, query);
			
			System.out.println(statement.query());
		}
		
	}


    @Test
    void fromSubQueryTest() throws SQLException {
        // Create a subquery selecting parent IDs from ChildEntity
        SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
                .select(ChildEntity.class, ChildEntity.Fields.parentId);

        // Create main query using the subquery
        SelectQuery<ChildEntity> mainQuery = Estivate.query(subQuery, "sub");

		String sql = context.queryAsString(mainQuery);
		System.out.println(sql);

        // Verify the query structure
		Assert.assertTrue(sql.contains("FROM (SELECT"));
		Assert.assertTrue(sql.contains("AS sub"));
		
    }

	@Test
	void joinSubQueryTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.selectMax(AbstractEntity.Fields.id);

		SelectQuery<ChildEntity> mainQuery = Estivate.query(ChildEntity.class)
			.joinInner(ChildEntity.class, Estivate.subQueryEntity(subQuery, "sub"), AbstractEntity.Fields.id, AbstractEntity.Fields.id);
			
		context.queryAsString(mainQuery);
	
	}
}
