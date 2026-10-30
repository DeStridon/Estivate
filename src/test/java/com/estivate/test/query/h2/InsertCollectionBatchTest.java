package com.estivate.test.query.h2;

import com.estivate.test.TestData;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

/**
 * {@code Context.insert(Collection)} must issue one multi-row INSERT per entity type.
 */
public class InsertCollectionBatchTest {

    Context context = H2TestDatabase.getContext();

    @BeforeEach
    public void setUp() {
        Estivate.Tools.createTableFullQuery(ProductEntity.class, context).ifNotExists().execute(context);
        Estivate.Tools.createTableFullQuery(CustomerEntity.class, context).ifNotExists().execute(context);
        context.truncateTable(ProductEntity.class);
        context.truncateTable(CustomerEntity.class);
    }

    @Test
    public void insertsAHomogeneousCollectionInOneStatementAndAssignsIds() {
        ProductEntity first = ProductEntity.builder().name("A").description("a").price(1f).stock(1).category(ProductCategory.Clothing).build();
        ProductEntity second = ProductEntity.builder().name("B").description("b").price(2f).stock(2).category(ProductCategory.Shoes).build();

        context.insert(Arrays.asList(first, second));

        Assert.assertTrue(first.getId() > 0);
        Assert.assertTrue(second.getId() > 0);
        Assert.assertNotEquals(first.getId(), second.getId());
        Assert.assertEquals(2L, Estivate.selectQuery(ProductEntity.class).fetchCountAll(context).longValue());
    }

    @Test
    public void groupsMixedCollectionsByType() {
        CustomerEntity customer = TestData.createRandomCustomer();
        ProductEntity product = ProductEntity.builder().name("Mixed").description("m").price(3f).stock(3).category(ProductCategory.Clothing).build();

        context.insert(List.of(customer, product, TestData.createRandomCustomer()));

        Assert.assertTrue(customer.getId() > 0);
        Assert.assertTrue(product.getId() > 0);
        Assert.assertEquals(2L, Estivate.selectQuery(CustomerEntity.class).fetchCountAll(context).longValue());
        Assert.assertEquals(1L, Estivate.selectQuery(ProductEntity.class).fetchCountAll(context).longValue());
    }
}
