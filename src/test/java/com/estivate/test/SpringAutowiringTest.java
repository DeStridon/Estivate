package com.estivate.test;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estivate.context.Context;
import com.estivate.spring.EnableEstivateManagers;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.managers.CustomerManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating Spring autowiring of Estivate managers.
 * 
 * <p>This test shows how to:</p>
 * <ul>
 *   <li>Use {@code @EnableEstivateManagers} to enable automatic manager registration</li>
 *   <li>Autowire managers directly without calling {@code Estivate.implementManager}</li>
 * </ul>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringAutowiringTest.TestConfig.class)
public class SpringAutowiringTest {

    @Autowired CustomerManager customerManager;
    @Autowired Context context;

    @Test
    public void testAutowiredManager() {


        context.insert(DatabaseGenerator.createRandomCustomer());
        context.insert(DatabaseGenerator.createRandomCustomer());
        context.insert(DatabaseGenerator.createRandomCustomer());
        context.insert(DatabaseGenerator.createRandomCustomer());
        context.insert(DatabaseGenerator.createRandomCustomer());

        // The manager is autowired - no need to call Estivate.implementManager!
        assertNotNull(customerManager, "CustomerManager should be autowired");
        
        // Use the manager as usual - methods should work even if they return null/empty
        CustomerEntity customer = customerManager.findById(1L);
        assertNotNull(customer, "findById should return a customer");
        // customer may be null if ID 1 doesn't exist, but the call should work
        
        List<CustomerEntity> customers = customerManager.findByIdInIfNotEmpty(Arrays.asList(1L, 2L, 3L));
        assertNotNull(customers, "findByIdInIfNotEmpty should return a list (possibly empty)");
        
        List<CustomerEntity> byName = customerManager.findByNameAndEmail("John", "john@example.com");
        assertNotNull(byName, "findByNameAndEmail should return a list (possibly empty)");
        
        // Test passes if all method calls execute without exceptions
        System.out.println("Spring autowiring test passed! Manager was injected and methods executed successfully.");
    }

    @Configuration
    @EnableEstivateManagers(basePackages = "com.estivate.test.managers")
    static class TestConfig {
        
        @Bean
        public Context estivateContext() {
            // Use the existing test context
            return DatabaseGenerator.getContext();
        }
    }
}

