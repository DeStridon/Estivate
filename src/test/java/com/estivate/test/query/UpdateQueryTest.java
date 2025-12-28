package com.estivate.test.query;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.UpdateQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;

public class UpdateQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testUpdateQuery() {
        UpdateQuery<CustomerEntity> updateQuery = new UpdateQuery<>(CustomerEntity.class)
        .set(CustomerEntity.Fields.name, "John Doe")
        .set(CustomerEntity.Fields.email, "john.doe@example.com")
        .eq(AbstractEntity.Fields.id, 1);

        String query = context.queryAsString(updateQuery);
        Assert.assertEquals("UPDATE CUSTOMERENTITY SET CUSTOMERENTITY.NAME = ? , CUSTOMERENTITY.EMAIL = ? WHERE CUSTOMERENTITY.ID = ?", query);
        
    }

    @Test
    public void testUpdateNullEntity() {
        context.update(null);
    }

    @Test 
    public void testUpdateUnchangedEntity() {
        // 1. Create entity
        CustomerEntity customer = CustomerEntity.builder()
            .name("Test Customer")
            .email("test@example.com")
            .address("123 Test Street")
            .country(CustomerEntity.Country.USA)
            .emailVerified(true)
            .build();

        // 2. Save it
        context.insert(customer);
        Assert.assertTrue("Customer should have an ID", customer.getId() > 0);

        // 3. Load the entity again from id
        SelectQuery<CustomerEntity> selectQuery = new SelectQuery<>(CustomerEntity.class)
            .eq(AbstractEntity.Fields.id, customer.getId());
        CustomerEntity loadedCustomer = context.fetchAs(selectQuery);
        Assert.assertNotNull("Customer should be loaded", loadedCustomer);
        Assert.assertEquals("Names should match", customer.getName(), loadedCustomer.getName());

        // 4. Update without changing any fields - this should work without issues
        context.update(loadedCustomer);
        
        // Verify the entity is still the same after update
        CustomerEntity updatedCustomer = context.fetchAs(selectQuery);
        Assert.assertNotNull("Customer should still exist after update", updatedCustomer);
        Assert.assertEquals("ID should remain the same", loadedCustomer.getId(), updatedCustomer.getId());
        Assert.assertEquals("Name should remain unchanged", loadedCustomer.getName(), updatedCustomer.getName());
        Assert.assertEquals("Email should remain unchanged", loadedCustomer.getEmail(), updatedCustomer.getEmail());
    }

    @Test
    public void testUpdateQueryWithJoinsSqlGeneration() {
        // Test UPDATE query with JOIN SQL generation (without executing due to H2 limitations)
        UpdateQuery<OrderEntity> updateQuery = new UpdateQuery<>(OrderEntity.class)
            .set(OrderEntity.Fields.status, OrderEntity.OrderStatus.COMPLETED)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(CustomerEntity.Fields.name, "Test Customer")
            .eq(OrderEntity.Fields.totalAmount, 100.0f);

        String query = context.queryAsString(updateQuery);
        Assert.assertTrue(query.contains("UPDATE ORDERENTITY INNER JOIN CUSTOMERENTITY"));
        
    }

}
