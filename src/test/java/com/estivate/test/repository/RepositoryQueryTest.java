package com.estivate.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.repository.Repository;
import com.estivate.repository.RepositoryQuery;
import com.estivate.repository.ReturnType;
import com.estivate.test.entities.ProductEntity;

class RepositoryQueryTest {

    @Test
    void findDistinct_selectFieldAndCriterion() throws Exception {
        RepositoryQuery w = new RepositoryQuery(CustomerManager.class, "findDistinctNameByCountry");
        assertEquals("name", w.selectField);
        assertEquals(1, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("country", w.criterions.get(0).getY());
    }

    @Test
    void findDistinct_noCriterion() throws Exception {
        RepositoryQuery w = new RepositoryQuery(CustomerManager.class, "findDistinctName");
        assertEquals("name", w.selectField);
        assertTrue(w.criterions.isEmpty());
    }

    @Test
    void findFieldBy_selectsSingleFieldAndCriterion() throws Exception {
        RepositoryQuery w = new RepositoryQuery(CustomerManager.class, "findNameById");
        assertEquals("name", w.selectField);
        assertEquals(1, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("id", w.criterions.get(0).getY());
    }

    @Test
    void getMethods_containsCoreCriterions() {
        List<String> methods = RepositoryQuery.getMethods();
        assertTrue(methods.contains("eq"));
        assertTrue(methods.contains("like"));
        assertTrue(methods.contains("between"));
        assertTrue(methods.contains("isNull"));
        assertTrue(methods.contains("isNotNull"));
        assertTrue(methods.contains("gt"));
        assertTrue(methods.contains("likeStartsWith"));
    }

    public static class ProductManager extends Repository<ProductEntity> { }

    @Test
    void findAll_initializesEmptyLists() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAll");
        assertEquals(ProductEntity.class, w.entityClass);
        assertEquals(ProductEntity.class, w.returnEntity);
        assertEquals(ReturnType.List, w.returnType);
        assertNotNull(w.criterions);
        assertNotNull(w.orders);
        assertTrue(w.criterions.isEmpty());
        assertTrue(w.orders.isEmpty());
    }

    @Test
    void findAllBy_returnType_isEntityList() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByName");
        assertEquals(ProductEntity.class, w.returnEntity);
        assertEquals(ReturnType.List, w.returnType);
    }

    @Test
    void findOneBy_returnType_isEntity() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findOneByName");
        assertEquals(ProductEntity.class, w.returnEntity);
        assertEquals(ReturnType.Entity, w.returnType);
    }

    @Test
    void countBy_returnType_isLong() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "countByStock");
        assertEquals(Long.class, w.returnEntity);
        assertEquals(ReturnType.Count, w.returnType);
    }

    @Test
    void existsBy_returnType_isBoolean() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "existsByNameLike");
        assertEquals(boolean.class, w.returnEntity);
        assertEquals(ReturnType.Count, w.returnType);
    }

    @Test
    void findDistinct_returnType_isFieldList() throws Exception {
        RepositoryQuery w = new RepositoryQuery(CustomerManager.class, "findDistinctNameByCountry");
        assertEquals(String.class, w.returnEntity);
        assertEquals(ReturnType.DistinctList, w.returnType);
    }

    @Test
    void findFieldBy_returnType_isField() throws Exception {
        RepositoryQuery w = new RepositoryQuery(CustomerManager.class, "findNameById");
        assertEquals(String.class, w.returnEntity);
        assertEquals(ReturnType.Entity, w.returnType);
    }

    @Test
    void findBy_returnType_isEntity_listAmbiguous() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findByCategory");
        assertEquals(ProductEntity.class, w.returnEntity);
        assertEquals(null, w.returnType);
    }

    @Test
    void findAllBy_implicitEq_singleCriterion() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByName");
        assertEquals(1, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
        assertTrue(w.orders.isEmpty());
    }

    @Test
    void findAllBy_explicitGt() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByPriceGt");
        assertEquals(1, w.criterions.size());
        assertEquals("gt", w.criterions.get(0).getX().getName());
        assertEquals("price", w.criterions.get(0).getY());
    }

    @Test
    void findAllBy_twoConditions_andSeparator() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByNameAndDescription");
        assertEquals(2, w.criterions.size());
        assertEquals("eq", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
        assertEquals("eq", w.criterions.get(1).getX().getName());
        assertEquals("description", w.criterions.get(1).getY());
    }

    @Test
    void countBy_prefix_parsed() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "countByStock");
        assertEquals(1, w.criterions.size());
        assertEquals("stock", w.criterions.get(0).getY());
    }

    @Test
    void existsBy_prefix_parsed() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "existsByNameLike");
        assertEquals(1, w.criterions.size());
        assertEquals("like", w.criterions.get(0).getX().getName());
        assertEquals("name", w.criterions.get(0).getY());
    }

    @Test
    void findBy_prefix_parsed() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findByCategory");
        assertEquals(1, w.criterions.size());
        assertEquals("category", w.criterions.get(0).getY());
    }

    @Test
    void orderBy_desc_and_defaultAsc() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByNameOrderByPriceDescStock");
        assertEquals(1, w.criterions.size());
        assertEquals(2, w.orders.size());
        assertEquals("price", w.orders.get(0).getX());
        assertEquals(Order.Direction.Desc, w.orders.get(0).getY());
        assertEquals("stock", w.orders.get(1).getX());
        assertEquals(Order.Direction.Asc, w.orders.get(1).getY());
    }

    @Test
    void getFieldName_resolvesLongestFieldFirst() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByName");
        w.position = 9;
        assertEquals("name", w.getFieldName());
    }

    @Test
    void getFieldName_unknown_throws() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByName");
        w.position = 9 + "Name".length();
        Exception ex = assertThrows(Exception.class, () -> w.getFieldName());
        assertTrue(ex.getMessage().contains("Unknown field"));
    }

    @Test
    void unsupportedMethod_throws() {
        assertThrows(Exception.class, () -> new RepositoryQuery(ProductManager.class, "deleteByName"));
    }

    @Test
    void missingAndSeparator_throws() {
        Exception ex = assertThrows(Exception.class,
            () -> new RepositoryQuery(ProductManager.class, "findAllByNameDescription"));
        assertTrue(ex.getMessage().contains("Expected 'And'"));
    }

    @Test
    void getQuery_appliesEqAndOrders() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByNameOrderByPriceDesc");
        SelectQuery<?> q = w.getQuery(new Object[] { "p1" });
        assertEquals(1, q.getCriterions().size());
        assertEquals(1, q.getOrders().size());
        assertEquals(Order.Direction.Desc, q.getOrders().get(0).getDirection());
    }

    @Test
    void getQuery_between_usesTwoArgs() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByPriceBetween");
        SelectQuery<?> q = w.getQuery(new Object[] { 1f, 9f });
        assertEquals(1, q.getCriterions().size());
    }

    @Test
    void getQuery_isNull_noArgs() throws Exception {
        RepositoryQuery w = new RepositoryQuery(ProductManager.class, "findAllByDescriptionIsNull");
        SelectQuery<?> q = w.getQuery(new Object[] {});
        assertEquals(1, q.getCriterions().size());
    }
}
