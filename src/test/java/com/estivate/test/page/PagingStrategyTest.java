package com.estivate.test.page;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.page.KeysetPagingStrategy;
import com.estivate.page.OffsetPagingStrategy;
import com.estivate.page.PageResult;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PagingStrategy implementations:
 * - OffsetPagingStrategy
 * - KeysetPagingStrategy
 */
public class PagingStrategyTest {

    Context context = DatabaseGenerator.getContext();
    
    List<ProductEntity> testProducts = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        // Clear existing products
        context.createTableIfNotExists(ProductEntity.class);
        context.truncateTable(ProductEntity.class);
        testProducts.clear();

        // Insert 25 test products with sequential IDs and varying prices
        for (int i = 1; i <= 25; i++) {
            ProductEntity product = ProductEntity.builder()
                .name("Product " + String.format("%02d", i))
                .description("Description for product " + i)
                .category(ProductCategory.values()[i % ProductCategory.values().length])
                .price((float) (10.0 + i))
                .stock(100 - i)
                .build();
            context.insert(product);
            testProducts.add(product);
        }
    }

    // ==================== OffsetPagingStrategy Tests ====================

    @Nested
    class OffsetPagingStrategyTests {

        @Test
        public void testFirstPage_ReturnsCorrectItems() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(5, result.size(), "Should return 5 items");
            assertTrue(result.isHasNext(), "Should have next page");
            assertEquals(5, result.getPageSize(), "Page size should be 5");
        }

        @Test
        public void testSecondPage_ReturnsCorrectItems() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(1, 5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(5, result.size(), "Should return 5 items");
            assertTrue(result.isHasNext(), "Should have next page (more than 10 items)");
        }

        @Test
        public void testLastPage_HasNextIsFalse() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // With 25 items and pageSize 5, page 4 (0-indexed) is the last page
            OffsetPagingStrategy strategy = new OffsetPagingStrategy(4, 5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(5, result.size(), "Last page should have 5 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testPartialLastPage() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // With 25 items and pageSize 7, page 3 (0-indexed) should have 4 items
            OffsetPagingStrategy strategy = new OffsetPagingStrategy(3, 7);
            PageResult result = strategy.fetch(context, query);

            assertEquals(4, result.size(), "Last page should have 4 items (25 - 3*7)");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testEmptyResult() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .eq(ProductEntity.class, ProductEntity.Fields.name, "NonExistent");

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(0, result.size(), "Should return 0 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testPageBeyondData() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // Page 100 is way beyond our 25 items
            OffsetPagingStrategy strategy = new OffsetPagingStrategy(100, 5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(0, result.size(), "Should return 0 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testPagingStrategyForNextPage() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 5);
            PageResult result = strategy.fetch(context, query);

            // The returned paging strategy should be for the next page
            assertNotNull(result.getPagingStrategy(), "Should have next page strategy");
            assertTrue(result.getPagingStrategy() instanceof OffsetPagingStrategy, 
                "Next strategy should be OffsetPagingStrategy");
            
            OffsetPagingStrategy nextStrategy = (OffsetPagingStrategy) result.getPagingStrategy();
            assertEquals(1, nextStrategy.getPageNumber(), "Next page number should be 1");
            assertEquals(5, nextStrategy.getPageSize(), "Page size should be preserved");
        }

        @Test
        public void testIterateThroughAllPages() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            int totalItems = 0;
            int pageCount = 0;
            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 5);

            while (true) {
                PageResult result = strategy.fetch(context, query);
                totalItems += result.size();
                pageCount++;

                if (!result.isHasNext()) {
                    break;
                }
                strategy = (OffsetPagingStrategy) result.getPagingStrategy();
            }

            assertEquals(25, totalItems, "Should have fetched all 25 items");
            assertEquals(5, pageCount, "Should have 5 pages (25 items / 5 per page)");
        }

        @Test
        public void testWithFilters() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .gte(ProductEntity.class, ProductEntity.Fields.price, 20.0f)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 5);
            PageResult result = strategy.fetch(context, query);

            // Products with price >= 20 are those with i >= 10 (price = 10 + i)
            // So products 10-25 = 16 products
            assertTrue(result.size() <= 5, "Should return at most 5 items");
            assertTrue(result.isHasNext(), "Should have more pages");
        }

        @Test
        public void testPageSizeOne() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 1);
            PageResult result = strategy.fetch(context, query);

            assertEquals(1, result.size(), "Should return 1 item");
            assertTrue(result.isHasNext(), "Should have next page");
        }

        @Test
        public void testLargePageSize() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy strategy = new OffsetPagingStrategy(0, 100);
            PageResult result = strategy.fetch(context, query);

            assertEquals(25, result.size(), "Should return all 25 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testGetters() {
            OffsetPagingStrategy strategy = new OffsetPagingStrategy(3, 10, true);
            
            assertEquals(3, strategy.getPageNumber(), "Page number should be 3");
            assertEquals(10, strategy.getPageSize(), "Page size should be 10");
        }
    }

    // ==================== KeysetPagingStrategy Tests ====================

    @Nested
    class KeysetPagingStrategyTests {

        @Test
        public void testFirstPage_ReturnsCorrectItems() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(5, result.size(), "Should return 5 items");
            assertTrue(result.isHasNext(), "Should have next page");
            assertEquals(5, result.getPageSize(), "Page size should be 5");
        }

        @Test
        public void testSecondPage_ReturnsCorrectItems() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // First, get the first page
            KeysetPagingStrategy firstStrategy = new KeysetPagingStrategy(5);
            PageResult firstResult = firstStrategy.fetch(context, query);

            // Use the returned strategy for the next page
            KeysetPagingStrategy secondStrategy = (KeysetPagingStrategy) firstResult.getPagingStrategy();
            PageResult secondResult = secondStrategy.fetch(context, query);

            assertEquals(5, secondResult.size(), "Should return 5 items");
            assertTrue(secondResult.isHasNext(), "Should have next page (more than 10 items)");
        }

        @Test
        public void testLastPage_HasNextIsFalse() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // Iterate to the last page
            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult result = null;

            for (int i = 0; i < 5; i++) {
                result = strategy.fetch(context, query);
                if (!result.isHasNext()) {
                    break;
                }
                strategy = (KeysetPagingStrategy) result.getPagingStrategy();
            }

            assertNotNull(result, "Result should not be null");
            assertFalse(result.isHasNext(), "Last page should not have next");
        }

        @Test
        public void testNoOrderBy_ThrowsException() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class);
            // No ORDER BY clause

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);

            assertThrows(RuntimeException.class, () -> {
                strategy.fetch(context, query);
            }, "Should throw exception when no ORDER BY clause");
        }

        @Test
        public void testKeysetValuesMismatch_ThrowsException() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // Provide wrong number of keyset values (2 values for 1 ORDER BY column)
            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5, new Object[]{1L, "extra"});

            assertThrows(RuntimeException.class, () -> {
                strategy.fetch(context, query);
            }, "Should throw exception when keyset values count doesn't match ORDER BY count");
        }

        @Test
        public void testEmptyResult() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .eq(ProductEntity.class, ProductEntity.Fields.name, "NonExistent")
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult result = strategy.fetch(context, query);

            assertEquals(0, result.size(), "Should return 0 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testIterateThroughAllPages() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            int totalItems = 0;
            int pageCount = 0;
            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);

            while (true) {
                PageResult result = strategy.fetch(context, query);
                totalItems += result.size();
                pageCount++;

                if (!result.isHasNext()) {
                    break;
                }
                strategy = (KeysetPagingStrategy) result.getPagingStrategy();
            }

            assertEquals(25, totalItems, "Should have fetched all 25 items");
            assertEquals(5, pageCount, "Should have 5 pages (25 items / 5 per page)");
        }

        @Test
        public void testWithDescendingOrder() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByDesc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult firstResult = strategy.fetch(context, query);

            assertEquals(5, firstResult.size(), "Should return 5 items");
            assertTrue(firstResult.isHasNext(), "Should have next page");

            // Get second page
            KeysetPagingStrategy secondStrategy = (KeysetPagingStrategy) firstResult.getPagingStrategy();
            PageResult secondResult = secondStrategy.fetch(context, query);

            assertEquals(5, secondResult.size(), "Second page should have 5 items");
        }

        @Test
        public void testWithMultipleOrderByColumns() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, ProductEntity.Fields.category)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult firstResult = strategy.fetch(context, query);

            assertEquals(5, firstResult.size(), "Should return 5 items");

            // Get second page
            KeysetPagingStrategy secondStrategy = (KeysetPagingStrategy) firstResult.getPagingStrategy();
            PageResult secondResult = secondStrategy.fetch(context, query);

            assertEquals(5, secondResult.size(), "Second page should have 5 items");
        }

        @Test
        public void testWithFilters() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .gte(ProductEntity.class, ProductEntity.Fields.price, 20.0f)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult result = strategy.fetch(context, query);

            assertTrue(result.size() <= 5, "Should return at most 5 items");
        }

        @Test
        public void testPageSizeOne() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(1);
            PageResult result = strategy.fetch(context, query);

            assertEquals(1, result.size(), "Should return 1 item");
            assertTrue(result.isHasNext(), "Should have next page");
        }

        @Test
        public void testLargePageSize() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(100);
            PageResult result = strategy.fetch(context, query);

            assertEquals(25, result.size(), "Should return all 25 items");
            assertFalse(result.isHasNext(), "Should not have next page");
        }

        @Test
        public void testPagingStrategyReturned() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);
            PageResult result = strategy.fetch(context, query);

            assertNotNull(result.getPagingStrategy(), "Should have paging strategy");
            assertTrue(result.getPagingStrategy() instanceof KeysetPagingStrategy,
                "Should be KeysetPagingStrategy");
            
            KeysetPagingStrategy nextStrategy = (KeysetPagingStrategy) result.getPagingStrategy();
            assertEquals(5, nextStrategy.getPageSize(), "Page size should be preserved");
        }

        @Test
        public void testGetters() {
            KeysetPagingStrategy strategy = new KeysetPagingStrategy(10, new Object[]{5L});
            
            assertEquals(10, strategy.getPageSize(), "Page size should be 10");
        }

        @Test
        public void testNoDuplicatesAcrossPages() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            List<Long> allIds = new ArrayList<>();
            KeysetPagingStrategy strategy = new KeysetPagingStrategy(5);

            while (true) {
                PageResult result = strategy.fetch(context, query);
                
                // Extract IDs from this page
                for (int i = 0; i < result.size(); i++) {
                	
                    Long id = result.get(i).asLong(ProductEntity.class, AbstractEntity.Fields.id);
                    assertFalse(allIds.contains(id), "Should not have duplicate ID: " + id);
                    allIds.add(id);
                }

                if (!result.isHasNext()) {
                    break;
                }
                strategy = (KeysetPagingStrategy) result.getPagingStrategy();
            }

            assertEquals(25, allIds.size(), "Should have 25 unique IDs");
        }
    }

    // ==================== Comparison Tests ====================

    @Nested
    class ComparisonTests {

        @Test
        public void testBothStrategies_ReturnSameTotalItems() {
            for(int i = 0; i < 25; i++) {
                ProductEntity product = DatabaseGenerator.createRandomProduct();
                context.insert(product);
            }
            
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            // Count with offset paging
            int offsetTotal = 0;
            OffsetPagingStrategy offsetStrategy = new OffsetPagingStrategy(0, 7);
            while (true) {
                PageResult result = offsetStrategy.fetch(context, query);
                offsetTotal += result.size();
                if (!result.isHasNext()) break;
                offsetStrategy = (OffsetPagingStrategy) result.getPagingStrategy();
            }

            // Count with keyset paging
            int keysetTotal = 0;
            KeysetPagingStrategy keysetStrategy = new KeysetPagingStrategy(7);
            while (true) {
                PageResult result = keysetStrategy.fetch(context, query);
                keysetTotal += result.size();
                if (!result.isHasNext()) break;
                keysetStrategy = (KeysetPagingStrategy) result.getPagingStrategy();
            }
            
            Long count = context.fetchCountAll(Estivate.selectQuery(ProductEntity.class));
            

            assertEquals(offsetTotal, keysetTotal, "Both strategies should return same total items");
            assertEquals(count, offsetTotal, "Should have 25 total items");
        }

        @Test
        public void testBothStrategies_FirstPageContentMatches() {
            SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
                .orderByAsc(ProductEntity.class, AbstractEntity.Fields.id);

            OffsetPagingStrategy offsetStrategy = new OffsetPagingStrategy(0, 5);
            PageResult offsetResult = offsetStrategy.fetch(context, query);

            KeysetPagingStrategy keysetStrategy = new KeysetPagingStrategy(5);
            PageResult keysetResult = keysetStrategy.fetch(context, query);

            assertEquals(offsetResult.size(), keysetResult.size(), 
                "Both should return same number of items");

            // Compare IDs
            for (int i = 0; i < offsetResult.size(); i++) {
                String idColumn = context.nameMapper.toTableNameAttribute(ProductEntity.class, AbstractEntity.Fields.id);
                Long offsetId = offsetResult.get(i).asLong(idColumn);
                Long keysetId = keysetResult.get(i).asLong(idColumn);
                assertEquals(offsetId, keysetId, "IDs should match at position " + i);
            }
        }
    }
}
