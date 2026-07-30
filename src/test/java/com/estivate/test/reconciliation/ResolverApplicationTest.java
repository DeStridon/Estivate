package com.estivate.test.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.reconciliation.EstivateReconciliation;
import com.estivate.reconciliation.EstivateReconciliation.AddColumnDelta;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationResult;
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
    @ReconciliationScope
    public static class GenericColumnMissingResolver implements EstivateReconciliation.IAddColumnResolver {
        public boolean wasCalled = false;
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            try{
                wasCalled = true;
                Estivate.alterQuery(diff.entityClass)
                    .addColumn(diff.entityColumnDefinition)
                    .execute(context);
                diff.closeSolved();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ReconciliationScope(entity = ResolverTestEntity.class)
    public static class ResolverTestEntityResolver implements EstivateReconciliation.IAddColumnResolver {
        public boolean wasCalled = false;
        @Override
        public void resolve(Context context, AddColumnDelta diff) {
            try{
                wasCalled = true;
                Estivate.alterQuery(diff.entityClass)
                    .addColumn(diff.entityColumnDefinition)
                    .execute(context);
                diff.closeSolved();
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
                Estivate.alterQuery(diff.entityClass)
                    .addColumn(diff.entityColumnDefinition)
                    .execute(context);
                diff.closeSolved();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ==================== Setup ====================

    @BeforeEach
    public void setUp() throws Exception {
        dropTableIfExists("RESOLVER_TEST_ENTITY");
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
    @Disabled
    public void testApplyResolvers_NoDifferences_ReturnsEmptyResult() throws Exception {
        // Create table matching entity exactly
        //context.createTable(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);

        ReconciliationManager manager = new ReconciliationManager(context)
        .addEntities( ResolverTestEntity.class)
        .addResolvers( GenericColumnMissingResolver.class);

        ApplyResolversResult result = manager.applyResolvers();

        assertTrue(result.isFullyResolved(), "Should be fully resolved when no differences exist");
        assertEquals(0, result.totalDiffs(), "Should have no diffs");
    }

    @Test
    @Disabled
    public void testApplyResolvers_SingleDiff_ResolverSucceeds() throws Exception {
        // Create table and remove a column
        //context.createTableIfNotExists(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context)
        .addEntities( ResolverTestEntity.class)
        .addResolvers( GenericColumnMissingResolver.class);
        

        ApplyResolversResult result = manager.applyResolvers();

        assertTrue(result.isFullyResolved(), "Should be fully resolved");
        assertEquals(1, result.getDeltas().size(), "Should have 1 resolved diff");

    }

    @Test
    public void testApplyResolvers_MultipleResolvers() throws Exception {
        // Create table and remove a column
    	Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        //context.createTableIfNotExists(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");

        GenericColumnMissingResolver genericResolver = new GenericColumnMissingResolver();
        ResolverTestEntityResolver resolverTestEntityResolver = new ResolverTestEntityResolver();

        ReconciliationManager manager = new ReconciliationManager(context)
        .addEntities( ResolverTestEntity.class)
        .addResolvers( genericResolver, resolverTestEntityResolver);

        manager.applyResolvers();

        assertTrue(resolverTestEntityResolver.wasCalled, "ResolverTestEntity resolver should be called");
        assertFalse(genericResolver.wasCalled, "Generic resolver should not be called");
        
    }

    @Test
    public void testApplyResolvers_NoMatchingResolvers() throws Exception {
        // Create table and remove a column
        //context.createTable(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context)
            .addEntities( ResolverTestEntity.class)
            .addResolvers( UnannotatedResolver.class);

        ApplyResolversResult result = manager.applyResolvers();

        assertFalse(result.isFullyResolved(), "Should not be fully resolved without matching resolvers");
    }

    @Test
    @Disabled
    public void testApplyResolvers_EmptyCandidatesList() throws Exception {
        // Create table and remove a column
        //context.createTable(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context).addEntities( ResolverTestEntity.class);

        ApplyResolversResult result = manager.applyResolvers();

        assertFalse(result.isFullyResolved(), "Should not be fully resolved with empty candidates");
        assertEquals(1, result.getDeltas().size(), "Should have 1 diff");
    }

    // @Test
    // public void testApplyResolvers_MultipleDiffs_AllResolved() throws Exception {
    //     // Create table with multiple differences
    //     context.createTable(ResolverTestEntity.class);
    //     context.dropColumn(ResolverTestEntity.class, "email");
    //     context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

    //     ReconciliationManager manager = new ReconciliationManager(context)
    //     .addEntities( ResolverTestEntity.class)
    //     .addResolvers( GenericColumnMissingResolver.class, GenericDefinitionMismatchResolver.class);
    //     GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();
    //     GenericDefinitionMismatchResolver definitionResolver = new GenericDefinitionMismatchResolver();

    //     ApplyResolversResult result = manager.applyResolvers();

    //     assertTrue(result.isFullyResolved(), "Should be fully resolved");
    //     assertEquals(2, result.totalDiffs(), "Should have 2 total diffs");
    // }

    @Test
    @Disabled
    public void testApplyResolvers_MultipleDiffs_PartiallyResolved() throws Exception {
        // Create table with multiple differences
    	Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
    	//context.createTable(ResolverTestEntity.class);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

        ReconciliationManager manager = new ReconciliationManager(context)
        .addEntities( ResolverTestEntity.class)
        .addResolvers( GenericColumnMissingResolver.class);
        // Only provide resolver for ColumnMissing, not for ColumnDefinitionMismatch
        GenericColumnMissingResolver missingResolver = new GenericColumnMissingResolver();

        ApplyResolversResult result = manager.applyResolvers();

        assertFalse(result.isFullyResolved(), "Should not be fully resolved");
        assertEquals(2, result.totalDiffs(), "Should have 2 total diffs");
        assertEquals(1, result.getDeltas().stream().filter(x -> x.getReconciliationResult() == ReconciliationResult.SOLVED).count(), "Should have 1 diff");
        
        // Verify the correct diff types were resolved/unresolved
        assertTrue(result.getDeltas().stream().anyMatch(d -> d instanceof AddColumnDelta), "AddColumnDelta should be resolved");
    }

    // @Test
    // public void testApplyResolvers_ThrowingResolver_ContinuesWithNext() throws Exception {
    //     // Create table and remove a column
    //     context.createTable(ResolverTestEntity.class);
    //     context.dropColumn(ResolverTestEntity.class, "email");

    //     ReconciliationManager manager = new ReconciliationManager(context)
    //         .addEntities( ResolverTestEntity.class)
    //         .addResolvers( ThrowingResolver.class, GenericColumnMissingResolver.class);

    //     // Note: Both resolvers match the diff, but the throwing one will fail
    //     // The applyResolvers will try resolvers in order
    //     ApplyResolversResult result = manager.applyResolvers();

    //     // The exception is caught, and the diff becomes unresolved since the next resolver
    //     // would need to match the same diff but applyResolvers tries resolvers for each diff
    //     // assertTrue(throwingResolver.wasCalled, "Throwing resolver should be called");
    //     assertNotNull(result, "Result should not be null");
    // }

    // @Test
    // public void testApplyResolvers_FallbackToLessSpecificResolver() throws Exception {
    //     // Create table and remove a column that doesn't match specific resolver
    //     context.createTable(ResolverTestEntity.class);
    //     context.dropColumn(ResolverTestEntity.class, "name");

    //     ReconciliationManager manager = new ReconciliationManager(context)
    //     .addEntities( ResolverTestEntity.class)
    //     .addResolvers( EmailColumnMissingResolver.class, GenericColumnMissingResolver.class);
    //     // Email resolver won't match 'name' column diff
    //     EmailColumnMissingResolver emailResolver = new EmailColumnMissingResolver();
    //     // Generic resolver should handle it
    //     GenericColumnMissingResolver genericResolver = new GenericColumnMissingResolver();

    //     ApplyResolversResult result = manager.applyResolvers();

    //     // Email resolver doesn't match the 'name' column, so it shouldn't be called
    //     // Generic resolver should handle it
    //     assertTrue(result.isFullyResolved(), "Should be fully resolved by generic resolver");
    // }

    // @Test
    // public void testApplyResolvers_MultipleTypesOfDiffs() throws Exception {
    //     // Create table with many different types of differences
    //     context.createTable(ResolverTestEntity.class);
    //     context.dropColumn(ResolverTestEntity.class, "email");
    //     context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");
    //     context.changeColumn(ResolverTestEntity.class, "description", "VARCHAR(200)");
    //     context.changeColumn(ResolverTestEntity.class, "name", "VARCHAR(255) NOT NULL");

    //     ReconciliationManager manager = new ReconciliationManager(context)
    //         .addEntities( ResolverTestEntity.class)
    //         .addResolvers( GenericColumnMissingResolver.class, GenericDefinitionMismatchResolver.class);

    //     ApplyResolversResult result = manager.applyResolvers();

    //     assertTrue(result.isFullyResolved(), "Should be fully resolved with all resolver types");
    // }

    @Test
    @Disabled
    public void testApplyResolversResult_TotalDiffs() throws Exception {
        // Create table with multiple differences
        //context.createTable(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        context.dropColumn(ResolverTestEntity.class, "email");
        context.dropColumn(ResolverTestEntity.class, "name");
        context.changeColumn(ResolverTestEntity.class, "age", "BIGINT");

        ReconciliationManager manager = new ReconciliationManager(context)
            .addEntities( ResolverTestEntity.class)
            .addResolvers( GenericColumnMissingResolver.class);
        // Only resolve ColumnMissing, leave TypeMismatch unresolved

        ApplyResolversResult result = manager.applyResolvers();

        // Should have 2 missing columns resolved and 1 type mismatch unresolved
        assertEquals(3, result.totalDiffs(), "Total diffs should be sum of resolved and unresolved");
        assertEquals(2, result.getDeltas().stream().filter(x -> x.getReconciliationResult() == ReconciliationResult.SOLVED).count(), "Should have 2 resolved diffs");
    }

    @Test
    @Disabled
    public void testApplyResolvers_FirstMatchingResolverWins() throws Exception {
        // Create table and remove a column
        //context.createTable(ResolverTestEntity.class);
        Estivate.Tools.createTableFullQuery(ResolverTestEntity.class).ifNotExists().execute(context);
        context.dropColumn(ResolverTestEntity.class, "email");

        ReconciliationManager manager = new ReconciliationManager(context)
        .addEntities( ResolverTestEntity.class)
        .addResolvers( GenericColumnMissingResolver.class);

        ApplyResolversResult result = manager.applyResolvers();


        assertTrue(result.isFullyResolved(), "Should be fully resolved");
        // Second resolver might or might not be called depending on sorting and matching
        // but if first succeeds, diff is resolved
    }


}
