package com.estivate.test.query;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.InsertQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.CustomerEntity.Country;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

public class InsertQueryTest {

    Context context = DatabaseGenerator.getContext();

    @BeforeEach
    public void setUp() {
        Estivate.Tools.createTableFullQuery(ProductEntity.class).ifNotExists().execute(context);
        Estivate.Tools.createTableFullQuery(CustomerEntity.class).ifNotExists().execute(context);
        context.truncateTable(ProductEntity.class);
        context.truncateTable(CustomerEntity.class);
    }

    // ==================== CREATION TESTS ====================

    @Test
    public void testCreateInsertQueryExposesEntity() {
        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class);

        Assert.assertEquals("Entity class should be ProductEntity", ProductEntity.class, insertQuery.getEntity());
    }

    @Test
    public void testCreateInsertQueryPopulatesNonIdFields() {
        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class);

        Set<String> fieldNames = insertQuery.getFields().stream()
                .map(Field::getName)
                .collect(Collectors.toSet());

        Assert.assertFalse("Fields should not be empty", fieldNames.isEmpty());
        Assert.assertTrue("Fields should include 'name'", fieldNames.contains(ProductEntity.Fields.name));
        Assert.assertTrue("Fields should include 'description'", fieldNames.contains(ProductEntity.Fields.description));
        Assert.assertTrue("Fields should include 'category'", fieldNames.contains(ProductEntity.Fields.category));
        Assert.assertTrue("Fields should include 'price'", fieldNames.contains(ProductEntity.Fields.price));
        Assert.assertTrue("Fields should include 'stock'", fieldNames.contains(ProductEntity.Fields.stock));
    }

    @Test
    public void testCreateInsertQuerySkipsIdField() {
        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class);

        Set<String> fieldNames = insertQuery.getFields().stream()
                .map(Field::getName)
                .collect(Collectors.toSet());

        Assert.assertFalse("@Id field 'id' must not be part of InsertQuery fields", fieldNames.contains("id"));
    }

    @Test
    public void testCreateInsertQueryHasNoValuesInitially() {
        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class);

        Assert.assertNotNull("Values list should not be null", insertQuery.getValues());
        Assert.assertTrue("Values list should be empty on creation", insertQuery.getValues().isEmpty());
    }

    // ==================== VALUE ACCUMULATION TESTS ====================

    @Test
    public void testValueChainAccumulatesMultipleObjects() {
        ProductEntity p1 = buildProduct("Widget", 9.99f, 10);
        ProductEntity p2 = buildProduct("Gadget", 19.99f, 20);
        ProductEntity p3 = buildProduct("Gizmo", 29.99f, 30);

        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class)
                .value(p1)
                .value(p2)
                .value(p3);

        Assert.assertEquals("Should have accumulated 3 values", 3, insertQuery.getValues().size());
        Assert.assertSame("First value should be p1", p1, insertQuery.getValues().get(0));
        Assert.assertSame("Second value should be p2", p2, insertQuery.getValues().get(1));
        Assert.assertSame("Third value should be p3", p3, insertQuery.getValues().get(2));
    }

    @Test
    public void testValueReturnsSameInstanceForChaining() {
        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class);
        InsertQuery<ProductEntity> chained = insertQuery.value(buildProduct("Chain", 1.0f, 1));

        Assert.assertSame("value() should return the same InsertQuery instance for chaining", insertQuery, chained);
    }

    @Test
    public void testValuesVarargsAccumulatesAll() {
        ProductEntity p1 = buildProduct("V1", 1.0f, 1);
        ProductEntity p2 = buildProduct("V2", 2.0f, 2);
        ProductEntity p3 = buildProduct("V3", 3.0f, 3);

        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class)
                .values(p1, p2, p3);

        Assert.assertEquals("Should have accumulated 3 values via varargs", 3, insertQuery.getValues().size());
    }

    @Test
    public void testValuesCollectionAccumulatesAll() {
        List<ProductEntity> products = Arrays.asList(
                buildProduct("C1", 1.0f, 1),
                buildProduct("C2", 2.0f, 2));

        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class)
                .values(products);

        Assert.assertEquals("Should have accumulated 2 values via collection", 2, insertQuery.getValues().size());
    }

    @Test
    public void testInsertDateIsAppliedWhenValueIsAdded() {
        CustomerEntity customer = new CustomerEntity();
        customer.setName("InsertDateCustomer");
        customer.setEmail("insertdate_" + System.nanoTime() + "@example.com");
        customer.setCountry(Country.FRANCE);

        Assert.assertNull("Precondition: 'created' should be null before adding to query", customer.getCreated());

        new InsertQuery<>(CustomerEntity.class).value(customer);

        Assert.assertNotNull("'created' field annotated with @InsertDate should be set after value()", customer.getCreated());
    }

    @Test
    public void testInsertDatePreservesUserProvidedValue() {
        Date userDate = new Date(0L);
        CustomerEntity customer = new CustomerEntity();
        customer.setName("KeepDateCustomer");
        customer.setEmail("keepdate_" + System.nanoTime() + "@example.com");
        customer.setCountry(Country.UK);
        customer.setCreated(userDate);

        new InsertQuery<>(CustomerEntity.class).value(customer);

        Assert.assertSame("User-provided @InsertDate value must not be overwritten", userDate, customer.getCreated());
    }

    // ==================== EXECUTION TESTS ====================

    @Test
    public void testExecuteInsertsMultipleProducts() {
        ProductEntity p1 = buildProduct("Exec-A", 10.0f, 1);
        ProductEntity p2 = buildProduct("Exec-B", 20.0f, 2);
        ProductEntity p3 = buildProduct("Exec-C", 30.0f, 3);

        InsertQuery<ProductEntity> insertQuery = new InsertQuery<>(ProductEntity.class)
                .values(p1, p2, p3);

        context.execute(insertQuery);

        Long rowCount = context.fetchCountAll(Estivate.selectQuery(ProductEntity.class));
        Assert.assertEquals("Three rows should be inserted", Long.valueOf(3L), rowCount);
    }

    @Test
    public void testExecutePersistsFieldValues() {
        ProductEntity p1 = buildProduct("FetchMe-1", 11.5f, 5);
        ProductEntity p2 = buildProduct("FetchMe-2", 22.5f, 7);

        context.execute(new InsertQuery<>(ProductEntity.class).values(p1, p2));

        SelectQuery<ProductEntity> q1 = Estivate.selectQuery(ProductEntity.class)
                .eq(ProductEntity.class, ProductEntity.Fields.name, "FetchMe-1");
        ProductEntity loaded1 = context.fetchSingle(q1);

        SelectQuery<ProductEntity> q2 = Estivate.selectQuery(ProductEntity.class)
                .eq(ProductEntity.class, ProductEntity.Fields.name, "FetchMe-2");
        ProductEntity loaded2 = context.fetchSingle(q2);

        Assert.assertNotNull("Row 'FetchMe-1' should be fetched back", loaded1);
        Assert.assertEquals(Float.valueOf(11.5f), loaded1.getPrice());
        Assert.assertEquals(Integer.valueOf(5), loaded1.getStock());

        Assert.assertNotNull("Row 'FetchMe-2' should be fetched back", loaded2);
        Assert.assertEquals(Float.valueOf(22.5f), loaded2.getPrice());
        Assert.assertEquals(Integer.valueOf(7), loaded2.getStock());
    }

    @Test
    public void testExecuteSetsInsertDateOnInsertedRows() {
        CustomerEntity c1 = buildCustomer("Created-1", Country.FRANCE);
        CustomerEntity c2 = buildCustomer("Created-2", Country.GERMANY);

        Assert.assertNull(c1.getCreated());
        Assert.assertNull(c2.getCreated());

        Date before = new Date(System.currentTimeMillis() - 1000);
        context.execute(new InsertQuery<>(CustomerEntity.class).values(c1, c2));
        Date after = new Date(System.currentTimeMillis() + 1000);

        Assert.assertNotNull("c1.created should have been set by InsertQuery", c1.getCreated());
        Assert.assertNotNull("c2.created should have been set by InsertQuery", c2.getCreated());
        Assert.assertFalse("c1.created should not be earlier than 'before'", c1.getCreated().before(before));
        Assert.assertFalse("c1.created should not be later than 'after'", c1.getCreated().after(after));

        Long rowCount = context.fetchCountAll(Estivate.selectQuery(CustomerEntity.class));
        Assert.assertEquals("Two customer rows should be inserted", Long.valueOf(2L), rowCount);
    }

    @Test
    public void testExecuteSingleValueInsertsOneRow() {
        ProductEntity single = buildProduct("OnlyOne", 99.9f, 1);

        context.execute(new InsertQuery<>(ProductEntity.class).value(single));

        Long rowCount = context.fetchCountAll(Estivate.selectQuery(ProductEntity.class));
        Assert.assertEquals("Single value insert should produce one row", Long.valueOf(1L), rowCount);
    }

    // ==================== HELPERS ====================

    private static ProductEntity buildProduct(String name, float price, int stock) {
        ProductEntity p = new ProductEntity();
        p.setName(name);
        p.setDescription(name + " description");
        p.setCategory(ProductCategory.Other);
        p.setPrice(price);
        p.setStock(stock);
        return p;
    }

    private static CustomerEntity buildCustomer(String name, Country country) {
        CustomerEntity c = new CustomerEntity();
        c.setName(name);
        c.setEmail(name.toLowerCase() + "_" + System.nanoTime() + "@example.com");
        c.setCountry(country);
        return c;
    }
}
