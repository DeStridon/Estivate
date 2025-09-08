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
import com.estivate.test.entities.ProductEntity;

public class SelectQuerySubTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void inSubQueryTest() throws SQLException{

		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
				.select(ChildEntity.class, ChildEntity.Fields.parentId)
				.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class)
				.inSubQuery(ParentEntity.class, AbstractEntity.Fields.id, subQuery);
		
		try(Connection connection = context.datasource.getConnection()){
			Statement statement = Statement.toStatement(context, connection, query);
			
			System.out.println(statement.query());
		}
		
	}


    @Test
    void fromSubQueryTest() throws SQLException {
        // Create a subquery selecting parent IDs from ChildEntity
        SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
                .select(ChildEntity.class, ChildEntity.Fields.parentId);

        // Create main query using the subquery
        SelectQuery<ChildEntity> mainQuery = Estivate.selectQuery(subQuery, "sub");

		String sql = context.queryAsString(mainQuery);
		System.out.println(sql);

        // Verify the query structure
		Assert.assertTrue(sql.contains("FROM (SELECT"));
		Assert.assertTrue(sql.contains("AS sub"));
		
    }

	@Test
	void joinSubQueryTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
			.selectMax(AbstractEntity.Fields.id);

		SelectQuery<ChildEntity> mainQuery = Estivate.selectQuery(ChildEntity.class)
			.joinInner(ChildEntity.class, Estivate.subQueryEntity(subQuery, "sub"), AbstractEntity.Fields.id, AbstractEntity.Fields.id);
			
		context.queryAsString(mainQuery);
	
	}
	
	@Test
	void joinSubQueryTest2() {
		 SelectQuery<ProductEntity> subQuery = Estivate.selectQuery(ProductEntity.class)
		            .selectMaxAs(ProductEntity.class, ProductEntity.Fields.id, "maxId")
		            .groupBy(ProductEntity.class, ProductEntity.Fields.category);

        // Main query to get step ID, status, and count grouped by step and status
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .select(ProductEntity.class, ProductEntity.Fields.id)
            .select(ProductEntity.class, ProductEntity.Fields.category)
            .joinInner(ProductEntity.class, Estivate.subQueryEntity(subQuery, "latest"), AbstractEntity.Fields.id, "maxId");
        
        String queryString = context.queryAsString(query);
        
        context.fetchList(query);
	}
	
	/*
	 * SELECT PRODUCTENTITY_D.ID_D as `PRODUCTENTITY_E.ID_E`, PRODUCTENTITY_D.CATEGORY_D as `PRODUCTENTITY_E.CATEGORY_E`
 FROM PRODUCTENTITY_D INNER JOIN (SELECT max(PRODUCTENTITY_D.ID_D) as `maxId`
 FROM PRODUCTENTITY_D GROUP BY PRODUCTENTITY_D.CATEGORY_D
 ) AS latest ON PRODUCTENTITY_D.ID_D = latest.MAXID_D 

	 */
}
