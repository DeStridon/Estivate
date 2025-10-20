package com.estivate.test.query;

import org.junit.Assert;
import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.DeleteQuery;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;


public class SelectInterceptorTest {
 
    Context context = DatabaseGenerator.getContext();

    @Test
    public void testDeprecatedGenericPreProcessor() {
    	
        context.selectInterceptor = (query) -> {
            if(query.getOrders().isEmpty()) {
                query.orderAsc(AbstractEntity.Fields.id);
            }
        };

        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        String queryString = context.queryAsString(query);

        System.out.println(queryString);
        Assert.assertTrue(queryString.contains("ORDER BY"));
        
        // Clean up
        context.selectInterceptor = null;
    }

    @Test
    public void testSelectQueryPreProcessor() {
        context.selectInterceptor = (query) -> {
            if(query.getOrders().isEmpty()) {
                query.orderAsc(AbstractEntity.Fields.id);
            }
        };

        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        String queryString = context.queryAsString(query);

        System.out.println("Select query: " + queryString);
        Assert.assertTrue("Query should contain ORDER BY", queryString.contains("ORDER BY"));
        Assert.assertTrue("Query should order by id", queryString.toUpperCase().contains("ID"));
        
        // Clean up
        context.selectInterceptor = null;
    }

    @Test
    public void testUpdateQueryPreProcessor() {
        context.updateInterceptor = (query) -> {
            query.set("name", "PreProcessed Name");
        };

        UpdateQuery<CustomerEntity> query = Estivate.updateQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, "id", 1L);

        String queryString = context.queryAsString(query);

        System.out.println("Update query: " + queryString);
        Assert.assertTrue("Query should be UPDATE query", queryString.contains("UPDATE"));
        Assert.assertTrue("Query should set name field", queryString.toUpperCase().contains("NAME"));
        Assert.assertTrue("Query should contain WHERE clause", queryString.contains("WHERE"));
        
        // Clean up
        context.updateInterceptor = null;
    }

    @Test
    public void testDeleteQueryPreProcessor() {
        context.deleteInterceptor = (query) -> {
            // Add a limit to the delete query for safety
            query.limit(100);
        };

        DeleteQuery<CustomerEntity> query = Estivate.deleteQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, "id", 1L);

        String queryString = context.queryAsString(query);

        System.out.println("Delete query: " + queryString);
        Assert.assertTrue("Query should be DELETE query", queryString.contains("DELETE"));
        Assert.assertTrue("Query should contain WHERE clause", queryString.contains("WHERE"));
        Assert.assertTrue("Query should contain LIMIT", queryString.contains("LIMIT"));
        
        // Clean up
        context.deleteInterceptor = null;
    }

    @Test
    public void testInsertPreProcessor() {
        context.insertInterceptor = (object) -> {
            if(object instanceof CustomerEntity) {
                CustomerEntity customer = (CustomerEntity) object;
                // Set a default value during preprocessing
                if(customer.getName() == null || customer.getName().isEmpty()) {
                    customer.setName("Default Customer Name");
                }
            }
        };

        CustomerEntity customer = new CustomerEntity();
        customer.setName(""); // Empty name, should be replaced by preprocessor
        // Use a unique email based on timestamp to avoid constraint violations
        customer.setEmail("test_preprocessor_" + System.currentTimeMillis() + "@example.com");

        CustomerEntity insertedCustomer = context.insert(customer);

        Assert.assertNotNull("Inserted customer should not be null", insertedCustomer);
        Assert.assertEquals("Name should be set by preprocessor", "Default Customer Name", insertedCustomer.getName());
        Assert.assertTrue("Email should contain test_preprocessor", insertedCustomer.getEmail().contains("test_preprocessor"));
        
        // Clean up
        context.insertInterceptor = null;
    }

    @Test
    public void testMultiplePreProcessorsWorking() {
        // Set up multiple preprocessors
        context.selectInterceptor = (query) -> {
            query.orderAsc(AbstractEntity.Fields.id);
        };
        
        context.updateInterceptor = (query) -> {
            query.limit(50);
        };

        // Test select
        SelectQuery<CustomerEntity> selectQuery = Estivate.selectQuery(CustomerEntity.class);
        String selectQueryString = context.queryAsString(selectQuery);
        Assert.assertTrue("Select should have ORDER BY", selectQueryString.contains("ORDER BY"));

        // Test update
        UpdateQuery<CustomerEntity> updateQuery = Estivate.updateQuery(CustomerEntity.class)
            .set("name", "Test")
            .eq(CustomerEntity.class, "id", 1L);
        String updateQueryString = context.queryAsString(updateQuery);
        Assert.assertTrue("Update should have LIMIT", updateQueryString.contains("LIMIT"));

        // Clean up
        context.selectInterceptor = null;
        context.updateInterceptor = null;
    }

    @Test
    public void testPreProcessorWithBackwardCompatibility() {
        // Set both old and new preprocessors
        context.selectInterceptor = (query) -> {
            query.comment("Generic preprocessor");
        };
        
        context.selectInterceptor = (query) -> {
            query.orderAsc(AbstractEntity.Fields.id);
        };

        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);
        String queryString = context.queryAsString(query);

        System.out.println("Backward compatibility query: " + queryString);
        // Both preprocessors should be applied
        Assert.assertTrue("Query should have ORDER BY from specific preprocessor", queryString.contains("ORDER BY"));
        
        // Clean up
        context.selectInterceptor = null;
    }

    @Test
    public void testSelectQueryPreProcessorDoesNotAffectOthers() {
        context.selectInterceptor = (query) -> {
            query.orderAsc(AbstractEntity.Fields.id);
        };

        // Test that UpdateQuery is not affected by SelectQuery preprocessor
        UpdateQuery<CustomerEntity> updateQuery = Estivate.updateQuery(CustomerEntity.class)
            .set("name", "Test")
            .eq(CustomerEntity.class, "id", 1L);
        
        String updateQueryString = context.queryAsString(updateQuery);
        
        System.out.println("Update query (should not be affected by select preprocessor): " + updateQueryString);
        Assert.assertFalse("Update query should not have ORDER BY", updateQueryString.contains("ORDER BY"));
        
        // Clean up
        context.selectInterceptor = null;
    }


    @Test
    public void testPreProcessorWithInsertQuery() {
        context.insertInterceptor = (object) -> {
            if(object instanceof CustomerEntity) {
                CustomerEntity customer = (CustomerEntity) object;
                customer.setName("PreProcessed Name");
            }
        };

        CustomerEntity customer = new CustomerEntity();
        customer.setName("");
    }

    


}