package com.estivate.test.query.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SelectProjectToAttributeTest {

    Context context = DatabaseGenerator.getContext();

    // Test data holder
    private CustomerEntity testCustomer1;
    private CustomerEntity testCustomer2;
    private CustomerEntity testCustomer3;
    private ProductEntity testProduct1;
    private ProductEntity testProduct2;


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


    // ==================== projectAttribute() TESTS ====================
    
    @Test
    public void testProjectAttribute_SingleValue() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId());

        Object name = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertNotNull(name, "Name should not be null");
        assertEquals(testCustomer1.getName(), name, "Name should match");
    }

    @Test
    public void testProjectAttribute_DifferentDataTypes() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId());

        Object id = query.fetchAsSingle(context, CustomerEntity.class, AbstractEntity.Fields.id);
        assertNotNull(id, "ID should not be null");
        
        Object emailVerified = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.emailVerified);
        assertNotNull(emailVerified, "Email verified should not be null");
        
        Object country = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.country);
        assertNotNull(country, "Country should not be null");
    }

    // ==================== projectAttributeOptional() TESTS ====================
    
    @Test
    public void testProjectAttributeOptional_WithExistingData() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, testCustomer1.getId());

        Optional<String> name = (Optional<String>) query.fetchAsOptional(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertTrue(name.isPresent(), "Name should be present");
        assertEquals(testCustomer1.getName(), name.get(), "Name should match");
    }

    @Test
    public void testProjectAttributeOptional_WithNonExistingData() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, 99999L);

        Optional<String> name = (Optional<String>) query.fetchAsOptional(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertFalse(name.isPresent(), "Name should not be present for non-existing data");
    }

    // ==================== projectAttributeList() TESTS ====================
    
    @Test
    public void testProjectAttributeList_AllNames() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        List<?> names = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertNotNull(names, "Names should not be null");
        assertEquals(3, names.size(), "Should return all 3 names");
        assertEquals("Alice Smith", names.get(0), "First name should match");
        assertEquals("Bob Johnson", names.get(1), "Second name should match");
        assertEquals("Charlie Brown", names.get(2), "Third name should match");
    }

    @Test
    public void testProjectAttributeList_WithFilter() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.USA)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        List<?> names = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertNotNull(names, "Names should not be null");
        assertEquals(2, names.size(), "Should return 2 names for USA customers");
        assertEquals("Alice Smith", names.get(0), "First name should be Alice");
        assertEquals("Bob Johnson", names.get(1), "Second name should be Bob");
    }

    @Test
    public void testProjectAttributeList_EmptyResult() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.JAPAN);

        List<?> names = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.name);

        assertNotNull(names, "Names should not be null");
        assertEquals(0, names.size(), "Should return empty list");
    }

    // ==================== projectAttributeSet() TESTS ====================
    
    @Test
    public void testProjectAttributeSet_UniqueCountries() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        Set<?> countries = query.fetchAsSet(context, CustomerEntity.class, CustomerEntity.Fields.country);

        assertNotNull(countries, "Countries should not be null");
        assertEquals(2, countries.size(), "Should return 2 unique countries");
        assertTrue(countries.contains(Country.USA), "Should contain USA");
        assertTrue(countries.contains(Country.UK), "Should contain UK");
    }

    @Test
    public void testProjectAttributeSet_WithDuplicates() {
        // USA appears twice in test data (testCustomer1 and testCustomer2)
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.USA);

        Set<?> countries = query.fetchAsSet(context, CustomerEntity.class, CustomerEntity.Fields.country);

        assertNotNull(countries, "Countries should not be null");
        assertEquals(1, countries.size(), "Should return 1 unique country despite duplicates");
        assertTrue(countries.contains(CustomerEntity.Country.USA), "Should contain USA");
    }

    @Test
    public void testProjectAttributeSet_EmptyResult() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.JAPAN);

        Set<?> countries = query.fetchAsSet(context, CustomerEntity.class, CustomerEntity.Fields.country);

        assertNotNull(countries, "Countries should not be null");
        assertEquals(0, countries.size(), "Should return empty set");
    }

    // ==================== projectCount() TESTS ====================
    
    @Test
    public void testProjectCount_AllRecords() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        Long count = query.fetchCountAll(context);

        assertNotNull(count, "Count should not be null");
        assertEquals(3L, count, "Should count all 3 customers");
    }

    @Test
    public void testProjectCount_WithFilter() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.USA);

        assertEquals(2L, query.fetch(context).size(), "Should count 2 USA customers");
        assertEquals(2L, query.fetchCountAll(context), "Should count 2 USA customers");
        
    }

    @Test
    public void testProjectCount_EmptyResult() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.JAPAN);

        Long count = query.fetchCountAll(context);

        assertNotNull(count, "Count should not be null");
        assertEquals(0L, count, "Should count 0 customers");
    }

    @Test
    public void testProjectCount_WithEmailVerifiedFilter() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.emailVerified, true);

        Long count = query.fetchCountAll(context);

        assertNotNull(count, "Count should not be null");
        assertEquals(2L, count, "Should count 2 verified customers");
    }

    @Test
    public void testProjectCount_IgnoresGroupByAndOrderBy() {
        // projectCount should clear group by and order by, so the count should be for all records
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .groupBy(CustomerEntity.class, CustomerEntity.Fields.country)
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);

        Long count = query.fetchCountAll(context);

        assertNotNull(count, "Count should not be null");
        assertEquals(3L, count, "Should count all 3 customers, ignoring group by");
    }

    // ==================== projectCountOptional() TESTS ====================
    
    @Test
    public void testProjectCountOptional_WithData() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        Optional<Long> count = query.fetchOptionalCountAll(context);

        assertTrue(count.isPresent(), "Count should be present");
        assertEquals(3L, count.get(), "Should count all 3 customers");
    }

    @Test
    public void testProjectCountOptional_WithFilter() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.UK);

        Optional<Long> count = query.fetchOptionalCountAll(context);

        assertTrue(count.isPresent(), "Count should be present");
        assertEquals(1L, count.get(), "Should count 1 UK customer");
    }

    @Test
    public void testProjectCountOptional_EmptyResult() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.JAPAN);

        Optional<Long> count = query.fetchOptionalCountAll(context);

        // Count should still be present even with 0 results
        assertTrue(count.isPresent(), "Count should be present even for empty results");
        assertEquals(0L, count.get(), "Should count 0 customers");
    }

    // ==================== COMPLEX QUERY TESTS ====================
    

    @Test
    public void testProjectAttributeList_NumericValues() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .orderByAsc(ProductEntity.class, ProductEntity.Fields.price);

        List<?> prices = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.price);

        assertNotNull(prices, "Prices should not be null");
        assertEquals(2, prices.size(), "Should return 2 prices");
    }

    @Test
    public void testProjectCount_WithMultipleFilters() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, CustomerEntity.Fields.country, CustomerEntity.Country.USA)
            .eq(CustomerEntity.class, CustomerEntity.Fields.emailVerified, true);

        Long count = query.fetchCountAll(context);

        assertNotNull(count, "Count should not be null");
        assertEquals(1L, count, "Should count 1 verified USA customer");
    }
    
}
