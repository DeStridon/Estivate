package com.estivate.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;

public class SelectFetchAsTest {

    Context context = DatabaseGenerator.getContext();
    
    private CustomerEntity customer;
    private ProductEntity product;
    private OrderEntity order;
    private OrderLineEntity orderLine;

    @BeforeEach
    public void setUp() {
        // Clean up tables
        context.truncateTable(OrderLineEntity.class);
        context.truncateTable(OrderEntity.class);
        context.truncateTable(ProductEntity.class);
        context.truncateTable(CustomerEntity.class);
        
        // Create test customer with various attribute types
        customer = CustomerEntity.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .address("123 Main Street")
            .country(CustomerEntity.Country.FRANCE)
            .emailVerified(true)
            .created(new Date())
            .updated(new Date())
            .build();
        context.updateOrInsert(customer);
        
        // Create test product with enum and converted attributes
        product = ProductEntity.builder()
            .name("Test Product")
            .description("A great test product")
            .category(ProductEntity.ProductCategory.Electronics)
            .price(99.99f)
            .stock(50)
            .tags(Arrays.asList("tag1", "tag2", "tag3"))
            .availableCountries(Arrays.asList(
                CustomerEntity.Country.FRANCE, 
                CustomerEntity.Country.GERMANY, 
                CustomerEntity.Country.SPAIN
            ))
            .build();
        context.updateOrInsert(product);
        
        // Create test order with ORDINAL enum
        order = OrderEntity.builder()
            .customerId(customer.getId())
            .status(OrderEntity.OrderStatus.PROCESSING)
            .created(new Date())
            .updated(new Date())
            .totalAmount(199.98f)
            .build();
        context.updateOrInsert(order);
        
        // Create test order line
        orderLine = OrderLineEntity.builder()
            .orderId(order.getId())
            .productId(product.getId())
            .amount(2)
            .unitPrice(99.99f)
            .totalPrice(199.98f)
            .build();
        context.updateOrInsert(orderLine);
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - String attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_CustomerName() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.name);
        
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals("John Doe", result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_CustomerEmail() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.email);
        
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals("john.doe@example.com", result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_ProductDescription() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.description);
        
        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals("A great test product", result);
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Long/long attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_CustomerId() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, AbstractEntity.Fields.id);
        
        assertNotNull(result);
        assertTrue(result instanceof Long);
        assertEquals(customer.getId(), ((Long) result).longValue());
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderCustomerId() {
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .eq(OrderEntity.class, AbstractEntity.Fields.id, order.getId());
        
        Object result = query.fetchAsSingle(context, OrderEntity.class, OrderEntity.Fields.customerId);
        
        assertNotNull(result);
        assertTrue(result instanceof Long);
        assertEquals(customer.getId(), ((Long) result).longValue());
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Integer/int attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_ProductStock() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.stock);
        
        assertNotNull(result);
        assertTrue(result instanceof Integer);
        assertEquals(50, ((Integer) result).intValue());
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderLineAmount() {
        SelectQuery<OrderLineEntity> query = Estivate.selectQuery(OrderLineEntity.class)
            .eq(OrderLineEntity.class, OrderLineEntity.Fields.id, orderLine.getId());
        
        Object result = query.fetchAsSingle(context, OrderLineEntity.class, OrderLineEntity.Fields.amount);
        
        assertNotNull(result);
        assertTrue(result instanceof Integer);
        assertEquals(2, ((Integer) result).intValue());
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Float/float attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_ProductPrice() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.price);
        
        assertNotNull(result);
        assertTrue(result instanceof Float);
        assertEquals(99.99f, ((Float) result).floatValue(), 0.01f);
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderTotalAmount() {
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .eq(OrderEntity.class, AbstractEntity.Fields.id, order.getId());
        
        Object result = query.fetchAsSingle(context, OrderEntity.class, OrderEntity.Fields.totalAmount);
        
        assertNotNull(result);
        assertTrue(result instanceof Float);
        assertEquals(199.98f, ((Float) result).floatValue(), 0.01f);
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderLineUnitPrice() {
        SelectQuery<OrderLineEntity> query = Estivate.selectQuery(OrderLineEntity.class)
            .eq(OrderLineEntity.class, OrderLineEntity.Fields.id, orderLine.getId());
        
        Object result = query.fetchAsSingle(context, OrderLineEntity.class, OrderLineEntity.Fields.unitPrice);
        
        assertNotNull(result);
        assertTrue(result instanceof Float);
        assertEquals(99.99f, ((Float) result).floatValue(), 0.01f);
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Boolean/boolean attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_CustomerEmailVerified() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.emailVerified);
        
        assertNotNull(result);
        assertTrue(result instanceof Boolean);
        assertEquals(true, result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_CustomerEmailVerified_False() {
        customer.setEmailVerified(false);
        context.update(customer);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.emailVerified);
        
        assertNotNull(result);
        assertTrue(result instanceof Boolean);
        assertEquals(false, result);
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Date attributes
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_CustomerCreated() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.created);
        
        assertNotNull(result);
        assertTrue(result instanceof Date);
        assertNotNull((Date) result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderUpdated() {
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .eq(OrderEntity.class, AbstractEntity.Fields.id, order.getId());
        
        Object result = query.fetchAsSingle(context, OrderEntity.class, OrderEntity.Fields.updated);
        
        assertNotNull(result);
        assertTrue(result instanceof Date);
        assertNotNull((Date) result);
    }

    // ========================================================================================
    // fetchSingleAsAttribute Tests - Enum attributes (STRING and ORDINAL)
    // ========================================================================================
    
    @Test
    public void testFetchSingleAsAttribute_CustomerCountry_EnumString() {
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId());
        
        Object result = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.country);
        
        assertNotNull(result);
        assertTrue(result instanceof CustomerEntity.Country);
        assertEquals(CustomerEntity.Country.FRANCE, result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_OrderStatus_EnumOrdinal() {
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .eq(OrderEntity.class, AbstractEntity.Fields.id, order.getId());
        
        Object result = query.fetchAsSingle(context, OrderEntity.class, OrderEntity.Fields.status);
        
        assertNotNull(result);
        assertTrue(result instanceof OrderEntity.OrderStatus);
        assertEquals(OrderEntity.OrderStatus.PROCESSING, result);
    }
    
    @Test
    public void testFetchSingleAsAttribute_ProductCategory_EnumDefault() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.category);
        
        assertNotNull(result);
        assertTrue(result instanceof ProductEntity.ProductCategory);
        assertEquals(ProductEntity.ProductCategory.Electronics, result);
    }

    // ========================================================================================
    // projectAttribute Tests - Converted attributes
    // ========================================================================================
    
    @Test
    public void testProjectAttribute_ProductTags_ConvertedStringList() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.tags);
        
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) result;
        assertEquals(3, tags.size());
        assertTrue(tags.contains("tag1"));
        assertTrue(tags.contains("tag2"));
        assertTrue(tags.contains("tag3"));
    }
        
    @Test
    public void testProjectAttribute_ProductAvailableCountries_ConvertedEnumList() {
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
        
        Object result = query.fetchAsSingle(context, ProductEntity.class, ProductEntity.Fields.availableCountries);
        
        assertNotNull(result);
        assertTrue(result instanceof List);
        @SuppressWarnings("unchecked")
        List<CustomerEntity.Country> countries = (List<CustomerEntity.Country>) result;
        assertEquals(3, countries.size());
        assertTrue(countries.contains(CustomerEntity.Country.FRANCE));
        assertTrue(countries.contains(CustomerEntity.Country.GERMANY));
        assertTrue(countries.contains(CustomerEntity.Country.SPAIN));
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - String attributes
    // ========================================================================================
    
    @Test
    public void testProjectAttributeList_CustomerNames() {
        // Create additional customers
        CustomerEntity customer2 = CustomerEntity.builder()
            .name("Jane Smith")
            .email("jane.smith@example.com")
            .country(CustomerEntity.Country.GERMANY)
            .build();
        context.updateOrInsert(customer2);
        
        CustomerEntity customer3 = CustomerEntity.builder()
            .name("Bob Johnson")
            .email("bob.johnson@example.com")
            .country(CustomerEntity.Country.SPAIN)
            .build();
        context.updateOrInsert(customer3);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(customer.getId(), customer2.getId(), customer3.getId()));
        
        List<?> result = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.name);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Jane Smith"));
        assertTrue(result.contains("Bob Johnson"));
    }
    
    @Test
    public void testProjectAttributeList_ProductNames() {
        // Create additional products
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Books)
            .price(19.99f)
            .stock(100)
            .build();
        context.updateOrInsert(product2);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.name);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Test Product"));
        assertTrue(result.contains("Product 2"));
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Long/long attributes
    // ========================================================================================
    
    @Test
    public void testProjectAttributeList_CustomerIds() {
        CustomerEntity customer2 = CustomerEntity.builder()
            .name("Customer 2")
            .email("customer2@example.com")
            .country(CustomerEntity.Country.USA)
            .build();
        context.updateOrInsert(customer2);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(customer.getId(), customer2.getId()));
        
        List<?> result = query.fetchAsList(context, CustomerEntity.class, AbstractEntity.Fields.id);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(id -> id instanceof Long));
        assertTrue(result.contains(customer.getId()));
        assertTrue(result.contains(customer2.getId()));
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Integer/int attributes
    // ========================================================================================
    
    @Test
    public void testProjectAttributeList_ProductStocks() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Clothing)
            .price(29.99f)
            .stock(75)
            .build();
        context.updateOrInsert(product2);
        
        ProductEntity product3 = ProductEntity.builder()
            .name("Product 3")
            .category(ProductEntity.ProductCategory.Toys)
            .price(39.99f)
            .stock(25)
            .build();
        context.updateOrInsert(product3);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId(), product3.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.stock);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(stock -> stock instanceof Integer));
        assertTrue(result.contains(50));
        assertTrue(result.contains(75));
        assertTrue(result.contains(25));
    }

    // ========================================================================================
    // projectAttributeList Tests - Float/float attributes
    // ========================================================================================
    
    @Test
    public void testProjectAttributeList_ProductPrices() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Food)
            .price(5.99f)
            .stock(200)
            .build();
        context.updateOrInsert(product2);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.price);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(price -> price instanceof Float));
        
        // Check prices with tolerance
        boolean hasPrice1 = result.stream()
            .anyMatch(p -> Math.abs(((Float) p) - 99.99f) < 0.01f);
        boolean hasPrice2 = result.stream()
            .anyMatch(p -> Math.abs(((Float) p) - 5.99f) < 0.01f);
        assertTrue(hasPrice1);
        assertTrue(hasPrice2);
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Boolean/boolean attributes
    // ========================================================================================
    
    @Test
    public void testFetchListAsAttribute_CustomerEmailVerified() {
        CustomerEntity customer2 = CustomerEntity.builder()
            .name("Verified Customer")
            .email("verified@example.com")
            .country(CustomerEntity.Country.UK)
            .emailVerified(true)
            .build();
        context.updateOrInsert(customer2);
        
        CustomerEntity customer3 = CustomerEntity.builder()
            .name("Unverified Customer")
            .email("unverified@example.com")
            .country(CustomerEntity.Country.AUSTRALIA)
            .emailVerified(false)
            .build();
        context.updateOrInsert(customer3);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(customer.getId(), customer2.getId(), customer3.getId()));
        
        List<?> result = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.emailVerified);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(verified -> verified instanceof Boolean));
        
        long trueCount = result.stream().filter(v -> (Boolean) v).count();
        long falseCount = result.stream().filter(v -> !(Boolean) v).count();
        assertEquals(2, trueCount);
        assertEquals(1, falseCount);
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Date attributes
    // ========================================================================================
    
    @Test
    public void testFetchListAsAttribute_OrderCreatedDates() {
        OrderEntity order2 = OrderEntity.builder()
            .customerId(customer.getId())
            .status(OrderEntity.OrderStatus.PENDING)
            .created(new Date())
            .totalAmount(50.0f)
            .build();
        context.updateOrInsert(order2);
        
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .in(OrderEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(order.getId(), order2.getId()));
        
        List<?> result = query.fetchAsList(context, OrderEntity.class, OrderEntity.Fields.created);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(date -> date instanceof Date));
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Enum attributes (STRING and ORDINAL)
    // ========================================================================================
    
    @Test
    public void testFetchListAsAttribute_CustomerCountries_EnumString() {
        CustomerEntity customer2 = CustomerEntity.builder()
            .name("Customer 2")
            .email("customer2@example.com")
            .country(CustomerEntity.Country.GERMANY)
            .build();
        context.updateOrInsert(customer2);
        
        CustomerEntity customer3 = CustomerEntity.builder()
            .name("Customer 3")
            .email("customer3@example.com")
            .country(CustomerEntity.Country.SPAIN)
            .build();
        context.updateOrInsert(customer3);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(customer.getId(), customer2.getId(), customer3.getId()));
        
        List<?> result = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.country);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(country -> country instanceof CustomerEntity.Country));
        assertTrue(result.contains(CustomerEntity.Country.FRANCE));
        assertTrue(result.contains(CustomerEntity.Country.GERMANY));
        assertTrue(result.contains(CustomerEntity.Country.SPAIN));
    }
    
    @Test
    public void testFetchListAsAttribute_OrderStatuses_EnumOrdinal() {
        OrderEntity order2 = OrderEntity.builder()
            .customerId(customer.getId())
            .status(OrderEntity.OrderStatus.COMPLETED)
            .created(new Date())
            .totalAmount(99.99f)
            .build();
        context.updateOrInsert(order2);
        
        OrderEntity order3 = OrderEntity.builder()
            .customerId(customer.getId())
            .status(OrderEntity.OrderStatus.CANCELLED)
            .created(new Date())
            .totalAmount(49.99f)
            .build();
        context.updateOrInsert(order3);
        
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .in(OrderEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(order.getId(), order2.getId(), order3.getId()));
        
        List<?> result = query.fetchAsList(context, OrderEntity.class, OrderEntity.Fields.status);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(status -> status instanceof OrderEntity.OrderStatus));
        assertTrue(result.contains(OrderEntity.OrderStatus.PROCESSING));
        assertTrue(result.contains(OrderEntity.OrderStatus.COMPLETED));
        assertTrue(result.contains(OrderEntity.OrderStatus.CANCELLED));
    }
    
    @Test
    public void testFetchListAsAttribute_ProductCategories_EnumDefault() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Books)
            .price(15.99f)
            .stock(100)
            .build();
        context.updateOrInsert(product2);
        
        ProductEntity product3 = ProductEntity.builder()
            .name("Product 3")
            .category(ProductEntity.ProductCategory.Clothing)
            .price(45.99f)
            .stock(30)
            .build();
        context.updateOrInsert(product3);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId(), product3.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.category);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(cat -> cat instanceof ProductEntity.ProductCategory));
        assertTrue(result.contains(ProductEntity.ProductCategory.Electronics));
        assertTrue(result.contains(ProductEntity.ProductCategory.Books));
        assertTrue(result.contains(ProductEntity.ProductCategory.Clothing));
    }

    // ========================================================================================
    // fetchListAsAttribute Tests - Converted attributes
    // ========================================================================================
    
    @Test
    public void testFetchListAsAttribute_ProductTags_ConvertedStringList() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Sports)
            .price(79.99f)
            .stock(40)
            .tags(Arrays.asList("sports", "outdoor"))
            .build();
        context.updateOrInsert(product2);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.tags);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(tags -> tags instanceof List));
        
        @SuppressWarnings("unchecked")
        List<String> tags1 = (List<String>) result.get(0);
        @SuppressWarnings("unchecked")
        List<String> tags2 = (List<String>) result.get(1);
        
        // Check that one list has 3 tags and the other has 2
        assertTrue((tags1.size() == 3 && tags2.size() == 2) || (tags1.size() == 2 && tags2.size() == 3));
    }
    
    @Test
    public void testFetchListAsAttribute_ProductAvailableCountries_ConvertedEnumList() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Product 2")
            .category(ProductEntity.ProductCategory.Home)
            .price(129.99f)
            .stock(20)
            .availableCountries(Arrays.asList(
                CustomerEntity.Country.USA,
                CustomerEntity.Country.UK
            ))
            .build();
        context.updateOrInsert(product2);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .in(ProductEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(product.getId(), product2.getId()));
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.availableCountries);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(countries -> countries instanceof List));
        
        @SuppressWarnings("unchecked")
        List<CustomerEntity.Country> countries1 = (List<CustomerEntity.Country>) result.get(0);
        @SuppressWarnings("unchecked")
        List<CustomerEntity.Country> countries2 = (List<CustomerEntity.Country>) result.get(1);
        
        // Check that one list has 3 countries and the other has 2
        assertTrue((countries1.size() == 3 && countries2.size() == 2) || 
                   (countries1.size() == 2 && countries2.size() == 3));
    }

    // ========================================================================================
    // Edge Cases and Complex Queries
    // ========================================================================================
    
    @Test
    public void testFetchListAsAttribute_WithOrderBy() {
        CustomerEntity customer2 = CustomerEntity.builder()
            .name("Alice")
            .email("alice@example.com")
            .country(CustomerEntity.Country.CHINA)
            .build();
        context.updateOrInsert(customer2);
        
        CustomerEntity customer3 = CustomerEntity.builder()
            .name("Charlie")
            .email("charlie@example.com")
            .country(CustomerEntity.Country.JAPAN)
            .build();
        context.updateOrInsert(customer3);
        
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, 
                Arrays.asList(customer.getId(), customer2.getId(), customer3.getId()))
            .orderByAsc(CustomerEntity.class, CustomerEntity.Fields.name);
        
        List<?> result = query.fetchAsList(context, CustomerEntity.class, CustomerEntity.Fields.name);
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Alice", result.get(0));
        assertEquals("Charlie", result.get(1));
        assertEquals("John Doe", result.get(2));
    }
    
    @Test
    public void testFetchListAsAttribute_WithWhereCondition() {
        ProductEntity product2 = ProductEntity.builder()
            .name("Expensive Product")
            .category(ProductEntity.ProductCategory.Jewelry)
            .price(999.99f)
            .stock(5)
            .build();
        context.updateOrInsert(product2);
        
        ProductEntity product3 = ProductEntity.builder()
            .name("Cheap Product")
            .category(ProductEntity.ProductCategory.Other)
            .price(9.99f)
            .stock(500)
            .build();
        context.updateOrInsert(product3);
        
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .gt(ProductEntity.class, ProductEntity.Fields.price, 50f);
        
        List<?> result = query.fetchAsList(context, ProductEntity.class, ProductEntity.Fields.name);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Test Product"));
        assertTrue(result.contains("Expensive Product"));
        assertFalse(result.contains("Cheap Product"));
    }
    
    @Test
    public void testFetchSingleAsAttribute_WithJoin() {
        SelectQuery<OrderEntity> query = Estivate.selectQuery(OrderEntity.class)
            .joinInner(OrderEntity.class, CustomerEntity.class)
            .eq(OrderEntity.class, AbstractEntity.Fields.id, order.getId());
        
        Object customerName = query.fetchAsSingle(context, CustomerEntity.class, CustomerEntity.Fields.name);
        
        assertNotNull(customerName);
        assertEquals("John Doe", customerName);
    }
    
}
