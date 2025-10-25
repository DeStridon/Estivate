package com.estivate.test.query;

import org.junit.jupiter.api.Test;
import org.junit.Assert;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.DeleteQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;

public class DeleteQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testDeleteQueryWithInnerJoin() {
        // Test deleting orders for a specific customer using inner join
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.name, "John Doe");
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with INNER JOIN:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain customer name condition", queryString.contains("NAME = ?"));
    }

    @Test
    public void testDeleteQueryWithLeftJoin() {
        // Test deleting customers who have no orders using left join
        DeleteQuery<CustomerEntity> deleteQuery = new DeleteQuery<>(CustomerEntity.class)
            .joinLeft(CustomerEntity.class, OrderEntity.class)
            .isNull(OrderEntity.class, AbstractEntity.Fields.id);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with LEFT JOIN:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain LEFT JOIN", queryString.contains("LEFT JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain IS NULL condition", queryString.contains("IS NULL"));
    }

    @Test
    public void testDeleteQueryWithMultipleJoins() {
        // Test deleting order lines for orders from customers in France
        DeleteQuery<OrderLineEntity> deleteQuery = new DeleteQuery<>(OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with MULTIPLE INNER JOINS:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain country condition", queryString.contains("COUNTRY = ?"));
        
        // Count INNER JOIN occurrences - should be 2
        long joinCount = queryString.split("INNER JOIN").length - 1;
        Assert.assertEquals("Should have 2 INNER JOINs", 2, joinCount);
    }

    @Test
    public void testDeleteQueryWithComplexJoinConditions() {
        // Test deleting orders with specific status and customer country
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(OrderEntity.class, OrderEntity.Fields.status, OrderEntity.OrderStatus.PENDING)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE)
            .gt(OrderEntity.class, OrderEntity.Fields.totalAmount, 50.0f);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with COMPLEX JOIN CONDITIONS:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain status condition", queryString.contains("STATUS = ?"));
        Assert.assertTrue("Query should contain country condition", queryString.contains("COUNTRY = ?"));
        Assert.assertTrue("Query should contain amount condition", queryString.contains("TOTALAMOUNT > ?"));
    }

    @Test
    public void testDeleteQueryWithRightJoin() {
        // Test deleting orders using right join
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .joinRight(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.emailVerified, false);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with RIGHT JOIN:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain RIGHT JOIN", queryString.contains("RIGHT JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain email verified condition", queryString.contains("EMAILVERIFIED = ?"));
    }

    @Test
    public void testDeleteQueryWithOuterJoin() {
        // Test deleting customers using outer join
        DeleteQuery<CustomerEntity> deleteQuery = new DeleteQuery<>(CustomerEntity.class)
            .joinOuter(CustomerEntity.class, OrderEntity.class)
            .isNull(OrderEntity.class, AbstractEntity.Fields.id);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with OUTER JOIN:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain OUTER JOIN", queryString.contains("OUTER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain IS NULL condition", queryString.contains("IS NULL"));
    }

    @Test
    public void testDeleteQueryWithSubQueryJoin() {
        // Test deleting orders using subquery join
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .joinInner(OrderEntity.class, 
                Estivate.selectQuery(CustomerEntity.class)
                    .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE)
                    .asSubQueryEntity("frenchCustomers"),
                AbstractEntity.Fields.id, OrderEntity.Fields.customerId)
            .gt(OrderEntity.class, OrderEntity.Fields.totalAmount, 100.0f);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with SUBQUERY JOIN:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain subquery", queryString.contains("SELECT"));
        Assert.assertTrue("Query should contain amount condition", queryString.contains("TOTALAMOUNT > ?"));
    }


    @Test
    public void testDeleteQueryWithLimit() {
        // Test deleting orders with limit
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE)
            .limit(5);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with LIMIT:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain LIMIT", queryString.contains("LIMIT"));
        Assert.assertTrue("Query should contain limit value", queryString.contains("5"));
    }

    

    @Test
    public void testDeleteQueryWithComments() {
        // Test deleting orders with comments
        DeleteQuery<OrderEntity> deleteQuery = new DeleteQuery<>(OrderEntity.class)
            .comment("Delete orders for French customers")
            .comment("This is a test query")
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with COMMENTS:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain comments", queryString.contains("--"));
        Assert.assertTrue("Query should contain first comment", queryString.contains("Delete orders for French customers"));
        Assert.assertTrue("Query should contain second comment", queryString.contains("This is a test query"));
    }

    @Test
    public void testDeleteQueryWithComplexMultipleJoins() {
        // Test deleting order lines with complex multiple joins
        DeleteQuery<OrderLineEntity> deleteQuery = new DeleteQuery<>(OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .joinLeft(OrderLineEntity.class, ProductEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.FRANCE)
            .eq(OrderEntity.class, OrderEntity.Fields.status, OrderEntity.OrderStatus.PENDING)
            .gt(ProductEntity.class, ProductEntity.Fields.price, 50.0f);
        
        String queryString = context.queryAsString(deleteQuery);
        System.out.println("DELETE with COMPLEX MULTIPLE JOINS:");
        System.out.println(queryString);
        System.out.println();
        
        // Verify the query string contains expected elements
        Assert.assertTrue("Query should contain DELETE", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain FROM", queryString.contains("FROM"));
        Assert.assertTrue("Query should contain INNER JOIN", queryString.contains("INNER JOIN"));
        Assert.assertTrue("Query should contain LEFT JOIN", queryString.contains("LEFT JOIN"));
        Assert.assertTrue("Query should contain WHERE", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain country condition", queryString.contains("COUNTRY = ?"));
        Assert.assertTrue("Query should contain status condition", queryString.contains("STATUS = ?"));
        Assert.assertTrue("Query should contain price condition", queryString.contains("PRICE > ?"));
        
        // Count JOIN occurrences - should be 3 (2 INNER + 1 LEFT)
        long innerJoinCount = queryString.split("INNER JOIN").length - 1;
        long leftJoinCount = queryString.split("LEFT JOIN").length - 1;
        Assert.assertEquals("Should have 2 INNER JOINs", 2, innerJoinCount);
        Assert.assertEquals("Should have 1 LEFT JOIN", 1, leftJoinCount);
    }

}
