package com.estivate.test.query;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.InsertQuery;
import com.estivate.reconciliation.ManualReconciliationManager.ManualReconciliationEntity;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class InsertInterceptorTest {

    Context context = DatabaseGenerator.getContext();

    
    @BeforeEach
    public void setUp() {
        // Clear existing products
        Estivate.Tools.createTableFullQuery(CustomerEntity.class, context).ifNotExists().execute(context);	
        context.truncateTable(CustomerEntity.class);
    }
    
    @Test
    public void testInsertInterceptorWithCollection() {
        // Set up interceptor to add prefix to names
        context.insertInterceptor = (InsertQuery<?> insertQuery) -> {

            if(insertQuery.getEntity() == CustomerEntity.class) {
                for(CustomerEntity customer : (List<CustomerEntity>) insertQuery.getValues()) {
                    if(customer.getName() == null || customer.getName().isEmpty()) {
                        customer.setName("Default Name");
                    }
                    // Add prefix to all names
                    customer.setName("BATCH_" + customer.getName());
                }
            }

        };

        // Create a list of customers
        java.util.List<CustomerEntity> customers = new java.util.ArrayList<>();
        
        CustomerEntity customer1 = new CustomerEntity();
        customer1.setName("Customer1");
        customer1.setEmail("batch_test_1_" + System.currentTimeMillis() + "@example.com");
        customers.add(customer1);
        
        CustomerEntity customer2 = new CustomerEntity();
        customer2.setName(""); // Empty name, should get default
        customer2.setEmail("batch_test_2_" + System.currentTimeMillis() + "@example.com");
        customers.add(customer2);
        
        CustomerEntity customer3 = new CustomerEntity();
        customer3.setName("Customer3");
        customer3.setEmail("batch_test_3_" + System.currentTimeMillis() + "@example.com");
        customers.add(customer3);

        // Insert the collection
        context.insert(customers);

        // Verify all customers were inserted with interceptor applied
        Assert.assertNotNull("Inserted customers should not be null", customers);
        Assert.assertEquals("Should have 3 customers", 3, customers.size());
        
        // Check that the interceptor was applied to each customer
        Assert.assertEquals("First customer name should have prefix", "BATCH_Customer1", customers.get(0).getName());
        Assert.assertEquals("Second customer should have default name with prefix", "BATCH_Default Name", customers.get(1).getName());
        Assert.assertEquals("Third customer name should have prefix", "BATCH_Customer3", customers.get(2).getName());
        
        // Verify all have valid IDs (were actually inserted)
        Assert.assertTrue("First customer should have ID", customers.get(0).getId() > 0);
        Assert.assertTrue("Second customer should have ID", customers.get(1).getId() > 0);
        Assert.assertTrue("Third customer should have ID", customers.get(2).getId() > 0);
        
        System.out.println("Batch insert successful:");
        System.out.println("  Customer 1: " + customers.get(0).getName() + " (ID: " + customers.get(0).getId() + ")");
        System.out.println("  Customer 2: " + customers.get(1).getName() + " (ID: " + customers.get(1).getId() + ")");
        System.out.println("  Customer 3: " + customers.get(2).getName() + " (ID: " + customers.get(2).getId() + ")");
        
        // Clean up
        context.insertInterceptor = null;
    }

    @Test
    public void testInsertInterceptorWithEmptyCollection() {
        // Counter to track interceptor calls
        final int[] interceptorCallCount = {0};
        
        context.insertInterceptor = (InsertQuery<?> insertQuery) -> {
            if(insertQuery.getEntity() == CustomerEntity.class) {
                interceptorCallCount[0]++;
            }
        };

        // Insert empty list
        java.util.List<CustomerEntity> emptyList = new java.util.ArrayList<>();
        context.insert(emptyList);

        Assert.assertNotNull("Result should not be null", emptyList);
        Assert.assertEquals("Result should be empty", 0, emptyList.size());
        Assert.assertEquals("Interceptor should not have been called", 0, interceptorCallCount[0]);
        
        // Clean up
        context.insertInterceptor = null;
    }

    @Test
    public void testInsertInterceptorWithNullCollection() {
        context.insertInterceptor = (InsertQuery<?> insertQuery) -> {
            if(insertQuery.getEntity() == CustomerEntity.class) {
                for(CustomerEntity customer : (List<CustomerEntity>) insertQuery.getValues()) { 
                    customer.setName("Should not be called");
                }
            }
        };

        // Insert null list
        List<CustomerEntity> nullList = null;
        context.insert(nullList);

        Assert.assertNull("Result should be null", nullList);
        
        // Clean up
        context.insertInterceptor = null;
    }
}
