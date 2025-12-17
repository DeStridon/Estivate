package com.estivate.test.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultRow;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

public class SelectQuerySubTest {

	Context context = DatabaseGenerator.getContext();
	
	
	
	@Test
	void inSubQueryTest() throws SQLException{

		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
				.select(OrderEntity.class, OrderEntity.Fields.customerId)
				.eq(OrderEntity.class, OrderEntity.Fields.customerId, Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id));
		
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
				.inSubQuery(CustomerEntity.class, AbstractEntity.Fields.id, subQuery);
		
		try(Connection connection = context.datasource.getConnection()){
			Statement statement = new Statement(context, connection, query);
			
			System.out.println(statement.query());
		}
		
	}


    @Test
    void fromSubQueryTest() throws SQLException {
        // Create a subquery selecting parent IDs from OrderEntity
        SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.select(OrderEntity.class, AbstractEntity.Fields.id);

        // Create main query using the subquery
        SelectQuery<OrderEntity> mainQuery = Estivate.selectQuery(subQuery, "sub");

		String sql = context.queryAsString(mainQuery);
		System.out.println(sql);

        // Verify the query structure
		Assert.assertTrue(sql.contains("FROM (SELECT"));
		Assert.assertTrue(sql.contains("AS sub"));
		
    }

	@Test
	void joinSubQueryTest() throws SQLException {
		SelectQuery<OrderEntity> subQuery = Estivate.selectQuery(OrderEntity.class)
			.selectMaxAs(AbstractEntity.Fields.id, "maxId");

		SelectQuery<OrderEntity> mainQuery = Estivate.selectQuery(OrderEntity.class)
			.joinInner(OrderEntity.class, Estivate.subQueryEntity(subQuery, "sub"), AbstractEntity.Fields.id, "maxId");
			
		String query = context.queryAsString(mainQuery);
		
		System.out.println(query);
	
	}
	
	@Test
	void joinSubQueryTest2() {
		 SelectQuery<ProductEntity> subQuery = Estivate.selectQuery(ProductEntity.class)
		            .selectMaxAs(ProductEntity.class, AbstractEntity.Fields.id, "MAXID")
		            .groupBy(ProductEntity.class, ProductEntity.Fields.category);

        // Main query to get step ID, status, and count grouped by step and status
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .select(ProductEntity.class, AbstractEntity.Fields.id)
            .select(ProductEntity.class, ProductEntity.Fields.category)
            .joinInner(ProductEntity.class, Estivate.subQueryEntity(subQuery, "latest"), AbstractEntity.Fields.id, "maxId");
        
        String queryString = context.queryAsString(query);
        
        context.fetchList(query);
        
        List<ResultRow<ProductEntity>> results = context.fetchListAsResults(query);
        
        for(ResultRow<ProductEntity> result : results) {
        	ProductCategory productCategory = (ProductCategory) result.attributeAsEnum(ProductEntity.class, ProductEntity.Fields.category);
        }
        
        
	}
	
	/*
	 * SELECT PRODUCTENTITY_D.ID as `PRODUCTENTITY_E.ID_E`, PRODUCTENTITY_D.CATEGORY_D as `PRODUCTENTITY_E.CATEGORY_E`
 FROM PRODUCTENTITY_D INNER JOIN (SELECT max(PRODUCTENTITY_D.ID) as `maxId`
 FROM PRODUCTENTITY_D GROUP BY PRODUCTENTITY_D.CATEGORY_D
 ) AS latest ON PRODUCTENTITY_D.ID = latest.MAXID 

	 */
}
