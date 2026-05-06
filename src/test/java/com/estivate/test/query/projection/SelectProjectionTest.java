package com.estivate.test.query.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.CustomerEntity.Country;
import com.estivate.test.entities.ProductEntity;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SelectProjectionTest {

    Context context = DatabaseGenerator.getContext();

    // Test data holder
    private CustomerEntity testCustomer1;
    private CustomerEntity testCustomer2;
    private CustomerEntity testCustomer3;
    private ProductEntity testProduct1;
    private ProductEntity testProduct2;

    // ==================== TEST PROJECTION CLASSES ====================
    // Note: CustomerBasicProjection is now a standalone class in the projection package

    /**
     * Projection class with aggregate functions
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountAliasProjection {
        @Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id, alias = "customerCount")
        private Long customerCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountProjection {
        @Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
        private Long customerCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountDistinctProjection {
        @Projection.CountDistinct(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
        private Long customerCount;
    }

    /**
     * Projection class with multiple aggregate functions
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStatsProjection {

        @Projection.Min(entity = ProductEntity.class, attribute = ProductEntity.Fields.price, alias = "minPrice")
        private Float minPrice;
        
        @Projection.Max(entity = ProductEntity.class, attribute = ProductEntity.Fields.price, alias = "maxPrice")
        private Float maxPrice;
        
        @Projection.Avg(entity = ProductEntity.class, attribute = ProductEntity.Fields.price, alias = "avgPrice")
        private Double avgPrice;
        
        @Projection.Sum(entity = ProductEntity.class, attribute = ProductEntity.Fields.stock, alias = "totalStock")
        private Long totalStock;

        @Transient
        private String transientAttribute;
        
    }

    /**
     * Projection class with country grouping
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountAliasByCountryProjection {
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.country)
        private Country country;
        
        @Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id, alias = "count")
        private Long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountByCountryProjection {
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.country)
        private Country country;
        
        @Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
        private Long count;
    }

    @Data
    public static class CustomerBasicProjection {
        @Projection.Attribute(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
        Long id;
        
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.name)
        String name;
        
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.email)
        String email;
        
    }

    // ==================== SETUP AND TEARDOWN ====================
    
    @BeforeEach
    public void setUp() {
        // Clean up existing test data
        context.createTableIfNotExists(CustomerEntity.class);
        context.createTableIfNotExists(ProductEntity.class);
        context.truncateTable(CustomerEntity.class);
        context.truncateTable(ProductEntity.class);
        
        // Create test customers
        testCustomer1 = new CustomerEntity();
        testCustomer1.setName("Alice Smith");
        testCustomer1.setEmail("alice@example.com");
        testCustomer1.setAddress("123 Main St");
        testCustomer1.setCountry(CustomerEntity.Country.USA);
        testCustomer1.setEmailVerified(true);
        testCustomer1.setCreated(new Date());


        testCustomer2 = new CustomerEntity();
        testCustomer2.setName("Bob Johnson");
        testCustomer2.setEmail("bob@example.com");
        testCustomer2.setAddress("456 Oak Ave");
        testCustomer2.setCountry(CustomerEntity.Country.USA);
        testCustomer2.setEmailVerified(false);
        testCustomer2.setCreated(new Date());


        testCustomer3 = new CustomerEntity();
        testCustomer3.setName("Charlie Brown");
        testCustomer3.setEmail("charlie@example.com");
        testCustomer3.setAddress("789 Pine Rd");
        testCustomer3.setCountry(CustomerEntity.Country.UK);
        testCustomer3.setEmailVerified(true);
        testCustomer3.setCreated(new Date());


        // Create test products
        testProduct1 = new ProductEntity();
        testProduct1.setName("Widget A");
        testProduct1.setDescription("A high-quality widget");
        testProduct1.setCategory(ProductEntity.ProductCategory.Electronics);
        testProduct1.setPrice(29.99f);
        testProduct1.setStock(100);


        testProduct2 = new ProductEntity();
        testProduct2.setName("Gadget B");
        testProduct2.setDescription("An innovative gadget");
        testProduct2.setCategory(ProductEntity.ProductCategory.Electronics);
        testProduct2.setPrice(49.99f);
        testProduct2.setStock(50);

        context.insert(Arrays.asList(testCustomer1, testCustomer2, testCustomer3, testProduct1, testProduct2));

    }

    // ==================== project() TESTS ====================
    
    @Test
    public void testProject_BasicAttributes() {
        // Debug: Print test customer details
        System.out.println("testCustomer1: id=" + testCustomer1.getId() + ", name=" + testCustomer1.getName() + ", email=" + testCustomer1.getEmail());
        
        // Verify data exists in database
        CustomerEntity fromDb = context.fetchSingle(
            Estivate.selectQuery(CustomerEntity.class)
                .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId())
        );
        System.out.println("fromDb: id=" + fromDb.getId() + ", name=" + fromDb.getName() + ", email=" + fromDb.getEmail());
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId())
            .selectAll(CustomerBasicProjection.class);

        // Debug: Print the query before and after
        System.out.println("After importSelectFromResultMapping - Selects: " + query.getSelects());
        
        CustomerBasicProjection result = query.fetchAsSingle(context, CustomerBasicProjection.class);

        System.out.println("Result: " + result);
        
        assertNotNull(result, "Result should not be null");
        assertEquals(testCustomer1.getId(), result.getId(), "ID should match");
        assertEquals(testCustomer1.getName(), result.getName(), "Name should match");
        assertEquals(testCustomer1.getEmail(), result.getEmail(), "Email should match");
    }

    @Test
    public void projectionToCount() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        CustomerCountProjection count = query.fetchAsSingle(context, CustomerCountProjection.class);

        assertNotNull(count, "Result should not be null");
        assertEquals(3L, count.getCustomerCount(), "Should count all 3 customers");
    }

    @Test
    public void projectionToCountAlias() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        CustomerCountAliasProjection count = query.fetchAsSingle(context, CustomerCountAliasProjection.class);

        assertNotNull(count, "Result should not be null");
        assertEquals(3L, count.getCustomerCount(), "Should count all 3 customers");
    }

    @Test
    public void projectionToCountDistinct() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        CustomerCountDistinctProjection count = query.fetchAsSingle(context, CustomerCountDistinctProjection.class);

        assertNotNull(count, "Result should not be null");
        assertEquals(3L, count.getCustomerCount(), "Should count all 3 customers");
    }

    

    

    @Test
    public void testProject_WithMultipleAggregateFunctions() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class);

        ProductStatsProjection result = query.fetchAsSingle(context, ProductStatsProjection.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(29.99f, result.getMinPrice(), 0.01f, "Min price should match");
        assertEquals(49.99f, result.getMaxPrice(), 0.01f, "Max price should match");
        assertEquals(39.99, result.getAvgPrice(), 0.01, "Average price should match");
        assertEquals(150L, result.getTotalStock(), "Total stock should match");
    }

    @Test
    public void testProject_WithAliasAndGroupBy() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .groupBy(CustomerEntity.class, CustomerEntity.Fields.country);

        List<CustomerCountAliasByCountryProjection> results = query.fetchAsList(context, CustomerCountAliasByCountryProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should have 2 country groups");
        
        // Find USA group
        Optional<CustomerCountAliasByCountryProjection> usaGroup = results.stream()
            .filter(r -> Country.USA.equals(r.getCountry()))
            .findFirst();
        assertTrue(usaGroup.isPresent(), "USA group should exist");
        assertEquals(2L, usaGroup.get().getCount(), "USA should have 2 customers");
        
        // Find UK group
        Optional<CustomerCountAliasByCountryProjection> ukGroup = results.stream()
            .filter(r -> Country.UK.equals(r.getCountry()))
            .findFirst();
        assertTrue(ukGroup.isPresent(), "UK group should exist");
        assertEquals(1L, ukGroup.get().getCount(), "UK should have 1 customer");
    }

    
    @Test
    public void testProject_WithGroupBy() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .groupBy(CustomerEntity.class, CustomerEntity.Fields.country);

        List<CustomerCountByCountryProjection> results = query.fetchAsList(context, CustomerCountByCountryProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should have 2 country groups");
        
        // Find USA group
        Optional<CustomerCountByCountryProjection> usaGroup = results.stream()
            .filter(r -> Country.USA.equals(r.getCountry()))
            .findFirst();
        assertTrue(usaGroup.isPresent(), "USA group should exist");
        assertEquals(2L, usaGroup.get().getCount(), "USA should have 2 customers");
        
        // Find UK group
        Optional<CustomerCountByCountryProjection> ukGroup = results.stream()
            .filter(r -> Country.UK.equals(r.getCountry()))
            .findFirst();
        assertTrue(ukGroup.isPresent(), "UK group should exist");
        assertEquals(1L, ukGroup.get().getCount(), "UK should have 1 customer");
    }

    // ==================== projectOptional() TESTS ====================
    
    @Test
    public void testProjectOptional_WithExistingData() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId());

        Optional<CustomerBasicProjection> result = query.fetchAsOptional(context, CustomerBasicProjection.class);

        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(testCustomer1.getId(), result.get().getId(), "ID should match");
        assertEquals(testCustomer1.getName(), result.get().getName(), "Name should match");
    }

    @Test
    public void testProjectOptional_WithNonExistingData() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, 99999L);

        Optional<CustomerBasicProjection> result = query.fetchAsOptional(context, CustomerBasicProjection.class);

        assertFalse(result.isPresent(), "Result should not be present for non-existing data");
    }

    // ==================== projectList() TESTS ====================
    
    @Test
    public void testProjectList_AllRecords() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        List<CustomerBasicProjection> results = query.fetchAsList(context, CustomerBasicProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(3, results.size(), "Should return all 3 customers");
        assertEquals("Alice Smith", results.get(0).getName(), "First customer name should match");
        assertEquals("Bob Johnson", results.get(1).getName(), "Second customer name should match");
        assertEquals("Charlie Brown", results.get(2).getName(), "Third customer name should match");
    }

    @Test
    public void testProjectList_WithFilter() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.USA)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        List<CustomerBasicProjection> results = query.fetchAsList(context, CustomerBasicProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should return only USA customers");
        assertEquals("Alice Smith", results.get(0).getName(), "First customer should be Alice");
        assertEquals("Bob Johnson", results.get(1).getName(), "Second customer should be Bob");
    }

    @Test
    public void testProjectList_WithLimit() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name)
            .limit(2);

        List<CustomerBasicProjection> results = query.fetchAsList(context, CustomerBasicProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "Should return only 2 customers due to limit");
    }

    @Test
    public void testProjectList_EmptyResult() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.JAPAN);

        List<CustomerBasicProjection> results = query.fetchAsList(context, CustomerBasicProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(0, results.size(), "Should return empty list");
    }

    // ==================== COMPLEX QUERY TESTS ====================
    
    @Test
    public void testProjectList_WithComplexQuery() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.emailVerified, true)
            .likeContains(CustomerEntity.class, CustomerEntity.Fields.name, "Smith")
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        List<CustomerBasicProjection> results = query.fetchAsList(context, CustomerBasicProjection.class);

        assertNotNull(results, "Results should not be null");
        assertEquals(1, results.size(), "Should return 1 customer matching criteria");
        assertEquals("Alice Smith", results.get(0).getName(), "Should be Alice Smith");
    }
    
}
