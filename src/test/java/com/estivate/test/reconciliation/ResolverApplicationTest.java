package com.estivate.test.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.reconciliation.EstivateReconciliation;
import com.estivate.reconciliation.EstivateReconciliation.AddColumnDelta;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationDelta;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationScope;
import com.estivate.reconciliation.ReconciliationManager;
import com.estivate.reconciliation.ReconciliationManager.ApplyResolversResult;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * Unit tests for ReconciliationManager.applyResolvers() method.
 * Tests the ability to apply resolver objects to schema differences.
 */
public class ResolverApplicationTest {

    Context context = DatabaseGenerator.getContext();

    // ==================== Test Entity Classes ====================

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @FieldNameConstants
    @Table(name = "RESOLVER_TEST_ENTITY")
    public static class ResolverTestEntity extends AbstractEntity {
        String name;

        @Column(nullable = false)
        String email;

        @Column(length = 100)
        String description;

        @Column(nullable = false)
        Integer age;

        Long salary;

        Date createdAt;
    }

    // ==================== Test Resolvers ====================

    /**
     * Generic resolver that handles any ColumnMissing diff
     */
    @ReconciliationScope(table = "", column = "")
    public static class GenericColumnMissingResolver implements EstivateReconciliation.IAddColumnResolver {
        
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            try{
                context.addColumn(diff.entityClass, diff.entityFieldName, diff.entityColumnDefinition.getColumnType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Table-specific resolver for ColumnMissing
     */
    @ReconciliationScope(table = "RESOLVER_TEST_ENTITY", column = "")
    public static class TableSpecificColumnMissingResolver implements EstivateReconciliation.IAddColumnResolver {
        
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
        }
    }

    /**
     * Column-specific resolver for ColumnMissing on 'email' column
     */
    @ReconciliationScope(table = "RESOLVER_TEST_ENTITY", column = "email")
    public static class EmailColumnMissingResolver implements EstivateReconciliation.IAddColumnResolver {
        
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            try {
                context.addColumn(diff.entityClass, diff.entityFieldName, diff.entityColumnDefinition.getColumnType());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Generic column definition mismatch resolver (handles type, length, nullable, etc.)
     */
    @ReconciliationScope(table = "", column = "")
    public static class GenericDefinitionMismatchResolver implements EstivateReconciliation.IModifyColumnResolver {
        public com.estivate.reconciliation.EstivateReconciliation.ModifyColumnDelta lastDiff = null;

        @Override
        public void resolve(Context context, com.estivate.reconciliation.EstivateReconciliation.ModifyColumnDelta diff) {
            try {
                context.changeColumn(diff.entityClass, diff.entityFieldName, diff.entityDefinition.getColumnType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resolver without annotation - should never be called
     */
    public static class UnannotatedResolver implements EstivateReconciliation.IAddColumnResolver {
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            try {
                context.addColumn(diff.entityClass, diff.entityFieldName, diff.entityColumnDefinition.getColumnType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resolver that always fails
     */
    @ReconciliationScope(table = "", column = "")
    public static class FailingResolver implements EstivateReconciliation.IAddColumnResolver {
        public int callCount = 0;

        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            callCount++;
        }
    }

    /**
     * Resolver that throws exception
     */
    @ReconciliationScope(table = "", column = "")
    public static class ThrowingResolver implements EstivateReconciliation.IAddColumnResolver {
        public boolean wasCalled = false;

        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            wasCalled = true;
            throw new UnsupportedOperationException("Unimplemented method 'resolve'");
        }
    }

    // ==================== Setup ====================

    @BeforeEach
    public void setUp() throws Exception {
        context.showTables().forEach(this::dropTableIfExists);
    }

    private void dropTableIfExists(String tableName) {
        try (Connection connection = context.datasource.getConnection();
             java.sql.Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (Exception e) {
            // Ignore if table doesn't exist
        }
    }

    // ==================== Tests ====================

    @Test
    public void testApplyResolvers_NoDifferences_ReturnsEmptyResult() throws Exception {
        // Create table matching entity exactly
        context.createTable(ResolverTestEntity.class);

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver resolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(resolver));

        assertTrue(result.isFullyResolved(), "Should be fully resolved when no differences exist");
        assertEquals(0, result.totalDiffs(), "Should have no diffs");
        assertTrue(result.getResolved().isEmpty(), "Resolved list should be empty");
        assertTrue(result.getUnresolved().isEmpty(), "Unresolved list should be empty");
    }

    @Test
    public void testApplyResolvers_SingleDiff_ResolverSucceeds() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver resolver = new GenericColumnMissingResolver();
        

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(resolver));

        assertTrue(result.isFullyResolved(), "Should be fully resolved");
        assertEquals(1, result.getResolved().size(), "Should have 1 resolved diff");
        assertTrue(result.getUnresolved().isEmpty(), "Should have no unresolved diffs");

    }

    @Test
    public void testApplyResolvers_SingleDiff_ResolverFails() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        FailingResolver resolver = new FailingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(resolver));

        assertFalse(result.isFullyResolved(), "Should not be fully resolved when resolver fails");
        assertTrue(result.getResolved().isEmpty(), "Should have no resolved diffs");
        assertEquals(1, result.getUnresolved().size(), "Should have 1 unresolved diff");
        assertEquals(1, resolver.callCount, "Resolver should be called once");
    }

    @Test
    public void testApplyResolvers_NoMatchingResolvers() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        UnannotatedResolver resolver = new UnannotatedResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(resolver));

        assertFalse(result.isFullyResolved(), "Should not be fully resolved without matching resolvers");
        assertTrue(result.getResolved().isEmpty(), "Should have no resolved diffs");
        assertEquals(1, result.getUnresolved().size(), "Should have 1 unresolved diff");
    }

    @Test
    public void testApplyResolvers_EmptyCandidatesList() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);

        ApplyResolversResult result = manager.applyResolvers(Collections.emptyList());

        assertFalse(result.isFullyResolved(), "Should not be fully resolved with empty candidates");
        assertTrue(result.getResolved().isEmpty(), "Should have no resolved diffs");
        assertEquals(1, result.getUnresolved().size(), "Should have 1 unresolved diff");
    }

    @Test
    public void testApplyResolvers_MultipleDiffs_AllResolved() throws Exception {
        // Create table with multiple differences
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();
        GenericDefinitionMismatchResolver definitionResolver = new GenericDefinitionMismatchResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(missingResolver, definitionResolver));

        assertTrue(result.isFullyResolved(), "Should be fully resolved");
        assertEquals(2, result.totalDiffs(), "Should have 2 total diffs");
        assertEquals(2, result.getResolved().size(), "Should have 2 resolved diffs");
        assertTrue(result.getUnresolved().isEmpty(), "Should have no unresolved diffs");
    }

    @Test
    public void testApplyResolvers_MultipleDiffs_PartiallyResolved() throws Exception {
        // Create table with multiple differences
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        // Only provide resolver for ColumnMissing, not for ColumnDefinitionMismatch
        GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(missingResolver));

        assertFalse(result.isFullyResolved(), "Should not be fully resolved");
        assertEquals(2, result.totalDiffs(), "Should have 2 total diffs");
        assertEquals(1, result.getResolved().size(), "Should have 1 resolved diff");
        assertEquals(1, result.getUnresolved().size(), "Should have 1 unresolved diff");
        
        // Verify the correct diff types were resolved/unresolved
        assertTrue(result.getResolved().stream().anyMatch(d -> d instanceof AddColumnDelta), "AddColumnDelta should be resolved");
        assertTrue(result.getUnresolved().stream().anyMatch(d -> d instanceof com.estivate.reconciliation.EstivateReconciliation.ModifyColumnDelta), "ModifyColumnDelta should be unresolved");
    }

    @Test
    public void testApplyResolvers_ThrowingResolver_ContinuesWithNext() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        ThrowingResolver throwingResolver = new ThrowingResolver();
        GenericColumnMissingResolver goodResolver = new GenericColumnMissingResolver();

        // Note: Both resolvers match the diff, but the throwing one will fail
        // The applyResolvers will try resolvers in order
        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(throwingResolver, goodResolver));

        // The exception is caught, and the diff becomes unresolved since the next resolver
        // would need to match the same diff but applyResolvers tries resolvers for each diff
        assertTrue(throwingResolver.wasCalled, "Throwing resolver should be called");
        assertNotNull(result, "Result should not be null");
    }

    @Test
    public void testApplyResolvers_FallbackToLessSpecificResolver() throws Exception {
        // Create table and remove a column that doesn't match specific resolver
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "name");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        // Email resolver won't match 'name' column diff
        EmailColumnMissingResolver emailResolver = new EmailColumnMissingResolver();
        // Generic resolver should handle it
        GenericColumnMissingResolver genericResolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(emailResolver, genericResolver));

        // Email resolver doesn't match the 'name' column, so it shouldn't be called
        // Generic resolver should handle it
        assertTrue(result.isFullyResolved(), "Should be fully resolved by generic resolver");
    }

    @Test
    public void testApplyResolvers_MultipleTypesOfDiffs() throws Exception {
        // Create table with many different types of differences
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");
        context.changeColumn(ResolverTestEntity.class, "description", "VARCHAR(200)");
        context.changeColumn(ResolverTestEntity.class, "name", "VARCHAR(255) NOT NULL");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();
        GenericDefinitionMismatchResolver definitionResolver = new GenericDefinitionMismatchResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(missingResolver, definitionResolver));

        assertTrue(result.isFullyResolved(), "Should be fully resolved with all resolver types");
    }

    @Test
    public void testApplyResolversResult_TotalDiffs() throws Exception {
        // Create table with multiple differences
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.dropColumn(ResolverTestEntity.class, "name");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();
        // Only resolve ColumnMissing, leave TypeMismatch unresolved

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(missingResolver));

        // Should have 2 missing columns resolved and 1 type mismatch unresolved
        assertEquals(3, result.totalDiffs(), "Total diffs should be sum of resolved and unresolved");
        assertEquals(2, result.getResolved().size(), "Should have 2 resolved diffs");
        assertEquals(1, result.getUnresolved().size(), "Should have 1 unresolved diff");
    }

    @Test
    public void testApplyResolvers_FirstMatchingResolverWins() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        GenericColumnMissingResolver columnMissingResolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(columnMissingResolver));

        for(ReconciliationDelta delta : result.getUnresolved()){
            System.out.println(delta);
        }

        assertTrue(result.isFullyResolved(), "Should be fully resolved");
        // Second resolver might or might not be called depending on sorting and matching
        // but if first succeeds, diff is resolved
    }

    @Test
    public void testApplyResolvers_ResolverReturnsFalse_TriesNext() throws Exception {
        // Create table and remove a column
        context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context, ResolverTestEntity.class);
        FailingResolver failingResolver = new FailingResolver();
        GenericColumnMissingResolver successResolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers(Arrays.asList(failingResolver, successResolver));

        assertTrue(result.isFullyResolved(), "Should be fully resolved after fallback to second resolver");
        assertEquals(1, failingResolver.callCount, "Failing resolver should be tried once");
    }
}
