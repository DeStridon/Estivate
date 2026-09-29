package com.estivate.test.repository;

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
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

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

        boolean exists = customerManager.existsById(1L);
        assertTrue(exists, "existsById should return true");
        
        // Test passes if all method calls execute without exceptions
        System.out.println("Spring autowiring test passed! Manager was injected and methods executed successfully.");
    }

    @Test
    public void testFindDistinctNameByCountry() {
        CustomerEntity aliceFr = DatabaseGenerator.createRandomCustomer();
        aliceFr.setName("Alice");
        aliceFr.setCountry(CustomerEntity.Country.FRANCE);
        context.insert(aliceFr);

        CustomerEntity aliceFrDup = DatabaseGenerator.createRandomCustomer();
        aliceFrDup.setName("Alice");
        aliceFrDup.setCountry(CustomerEntity.Country.FRANCE);
        context.insert(aliceFrDup);

        CustomerEntity bobFr = DatabaseGenerator.createRandomCustomer();
        bobFr.setName("Bob");
        bobFr.setCountry(CustomerEntity.Country.FRANCE);
        context.insert(bobFr);

        CustomerEntity charlieUs = DatabaseGenerator.createRandomCustomer();
        charlieUs.setName("Charlie");
        charlieUs.setCountry(CustomerEntity.Country.USA);
        context.insert(charlieUs);

        List<String> names = customerManager.findDistinctNameByCountry(CustomerEntity.Country.FRANCE);

        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));
        assertFalse(names.contains("Charlie"));
    }

    @Test
    public void testFindNameById() {
        CustomerEntity alice = DatabaseGenerator.createRandomCustomer();
        alice.setName("Alice");
        context.insert(alice);

        CustomerEntity bob = DatabaseGenerator.createRandomCustomer();
        bob.setName("Bob");
        context.insert(bob);

        String name = customerManager.findNameById(alice.getId());

        assertEquals("Alice", name);
        assertNull(customerManager.findNameById(-1L));
    }

    @Configuration
    @EnableEstivateManagers(basePackages = "com.estivate.test.repository")
    static class TestConfig {
        
        @Bean
        public Context estivateContext() {
            // Use the existing test context
            return DatabaseGenerator.getContext();
        }
    }
}

