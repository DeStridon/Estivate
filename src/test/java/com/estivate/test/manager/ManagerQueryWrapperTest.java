package com.estivate.test.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.manager.ManagerInterceptor;
import com.estivate.manager.ManagerQueryWrapper;
import com.estivate.manager.ManagerInterceptor.EntityManager;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.ProductEntity;

class ManagerQueryWrapperTest {

    @Test
    void getMethods_containsCoreCriterions() {
        List<String> methods = ManagerQueryWrapper.getMethods();
        assertTrue(methods.contains("eq"));
        assertTrue(methods.contains("like"));
        assertTrue(methods.contains("between"));
        assertTrue(methods.contains("isNull"));
        assertTrue(methods.contains("isNotNull"));
        assertTrue(methods.contains("gt"));
        assertTrue(methods.contains("likeStartsWith"));
    }

    public static class ProductManager extends EntityManager<ProductEntity> { }

    @Test
    void findAll_initializesEmptyLists() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAll");
        assertEquals(ProductEntity.class, w.entityClass);
        assertNotNull(w.criterions);
        assertNotNull(w.orders);
        assertTrue(w.criterions.isEmpty());
        assertTrue(w.orders.isEmpty());
    }

    @Test
    void findAllBy_implicitEq_singleCriterion() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByName");
        assertEquals(1, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
        assertTrue(w.orders.isEmpty());
    }

    @Test
    void findAllBy_explicitGt() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByPriceGt");
        assertEquals(1, w.criterions.size());
        assertEquals("gt", w.criterions.get(0).getX().getName());
        assertEquals("price", w.criterions.get(0).getY());
    }

    @Test
    void findAllBy_twoConditions_andSeparator() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByNameAndDescription");
        assertEquals(2, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
        assertEquals("eq", w.criterions.get(1).getX().getName());
        assertEquals("description", w.criterions.get(1).getY());
    }

    @Test
    void countBy_prefix_parsed() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "countByStock");
        assertEquals(1, w.criterions.size());
        assertEquals("stock", w.criterions.get(0).getY());
    }

    @Test
    void existsBy_prefix_parsed() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "existsByNameLike");
        assertEquals(1, w.criterions.size());
        assertEquals("like", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
    }

    @Test
    void findBy_prefix_parsed() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findByCategory");
        assertEquals(1, w.criterions.size());
        assertEquals("category", w.criterions.get(0).getY());
    }

    @Test
    void orderBy_desc_and_defaultAsc() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByNameOrderByPriceDescStock");
        assertEquals(1, w.criterions.size());
        assertEquals(2, w.orders.size());
        assertEquals("price", w.orders.get(0).getX());
        assertEquals(Order.Direction.Desc, w.orders.get(0).getY());
        assertEquals("stock", w.orders.get(1).getX());
        assertEquals(Order.Direction.Asc, w.orders.get(1).getY());
    }

    @Test
    void getFieldName_resolvesLongestFieldFirst() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByName");
        assertEquals("name", w.getFieldName("findAllByName", 9));
    }

    @Test
    void getFieldName_unknown_throws() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByName");
        Exception ex = assertThrows(Exception.class, () -> w.getFieldName("findAllByName", 9 + "Name".length()));
        assertTrue(ex.getMessage().contains("Unknown field"));
    }

    @Test
    void unsupportedMethod_throws() {
        assertThrows(Exception.class, () -> new ManagerQueryWrapper(ProductManager.class, "deleteByName"));
    }

    @Test
    void missingAndSeparator_throws() {
        Exception ex = assertThrows(Exception.class,
            () -> new ManagerQueryWrapper(ProductManager.class, "findAllByNameDescription"));
        assertTrue(ex.getMessage().contains("Expected 'And'"));
    }

    @Test
    void getQuery_appliesEqAndOrders() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByNameOrderByPriceDesc");
        SelectQuery<?> q = w.getQuery(new Object[] { "p1" });
        assertEquals(1, q.getCriterions().size());
        assertEquals(1, q.getOrders().size());
        assertEquals(Order.Direction.Desc, q.getOrders().get(0).getDirection());
    }

    @Test
    void getQuery_between_usesTwoArgs() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByPriceBetween");
        SelectQuery<?> q = w.getQuery(new Object[] { 1f, 9f });
        assertEquals(1, q.getCriterions().size());
    }

    @Test
    void getQuery_isNull_noArgs() throws Exception {
        ManagerQueryWrapper w = new ManagerQueryWrapper(ProductManager.class, "findAllByDescriptionIsNull");
        SelectQuery<?> q = w.getQuery(new Object[] {});
        assertEquals(1, q.getCriterions().size());
    }
}
