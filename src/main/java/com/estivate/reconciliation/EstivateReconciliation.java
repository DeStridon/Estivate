package com.estivate.reconciliation;

import com.estivate.context.Context;
import com.estivate.query.AlterQuery;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class EstivateReconciliation {


    public static abstract class ReconciliationDelta {
        private ReconciliationResult reconciliationResult;
        private String reconciliationReason;

        public void closeSolved(String reason){
            reconciliationResult = ReconciliationResult.SOLVED;
            reconciliationReason = reason;
        }

        public void closeSkipped(String reason){
            reconciliationResult = ReconciliationResult.SKIPPED;
            reconciliationReason = reason;
        }

        public void closeFailed(String reason){
            reconciliationResult = ReconciliationResult.FAILED;
            reconciliationReason = reason;
        }

        public void closeSolved(){ closeSolved(null); }
        public void closeSkipped(){ closeSkipped(null);}
        public void closeFailed(){ closeFailed(null); }
    }

    /**
     * Represents a table that exists in code but not in the database.
     * Action: CREATE TABLE
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTableDelta extends ReconciliationDelta {
        public String tableName;
    }

    /**
     * Represents a column that exists in code but not in the database.
     * Action: ALTER TABLE ADD COLUMN
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddColumnDelta extends ReconciliationDelta {
        public String tableName;
        public String columnName;
        public AlterQuery.ColumnDefinition columnDefinition;

        
        /**
         * Constructor with basic information (no column definition)
         */
        public AddColumnDelta(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.columnDefinition = null;
        }
    }


    public static enum ReconciliationResult {
        SOLVED, // treated successfully
        SKIPPED, // not treated but doesn't need to
        FAILED; // not treated and should have
    }

    /**
     * Represents a table that exists in the database but has no corresponding entity in code.
     * Action: DROP TABLE
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropTableDelta extends ReconciliationDelta {
        public String tableName;
    }

    /**
     * Represents a column that exists in the database but has no corresponding field in the entity.
     * Action: ALTER TABLE DROP COLUMN
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropColumnDelta extends ReconciliationDelta {
        public String tableName;
        public String columnName;
        public AlterQuery.ColumnDefinition columnDefinition;
        
        /**
         * Constructor with basic information (no column definition)
         */
        public DropColumnDelta(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.columnDefinition = null;
        }
    }

    /**
     * Represents an index that exists in code but not in the database.
     * Action: CREATE INDEX
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddIndexDelta extends ReconciliationDelta {
        public String tableName;
        public String indexName;
        public IndexDefinition indexDefinition;
        
        /**
         * Constructor with basic information (no index definition)
         */
        public AddIndexDelta(String tableName, String indexName) {
            this.tableName = tableName;
            this.indexName = indexName;
            this.indexDefinition = null;
        }
    }

    /**
     * Represents an index that exists in the database but has no corresponding definition in code.
     * Action: DROP INDEX
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropIndexDelta extends ReconciliationDelta {
        public String tableName;
        public String indexName;
        public IndexDefinition indexDefinition;
        
        /**
         * Constructor with basic information (no index definition)
         */
        public DropIndexDelta(String tableName, String indexName) {
            this.tableName = tableName;
            this.indexName = indexName;
            this.indexDefinition = null;
        }
    }

    /**
     * Definition of an index including its columns and properties.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexDefinition {
        public String[] columns;
        public boolean unique;
        
        public IndexDefinition(String... columns) {
            this.columns = columns;
            this.unique = false;
        }
    }

    /**
     * Represents a mismatch between entity column definition and database column definition.
     * Action: ALTER TABLE MODIFY COLUMN
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModifyColumnDelta extends ReconciliationDelta {
        public String tableName;
        public String columnName;
        public AlterQuery.ColumnDefinition entityDefinition;
        public AlterQuery.ColumnDefinition databaseDefinition;
        
        public boolean hasTypeMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.columnType, databaseDefinition.columnType);
        }
        
        public boolean hasLengthMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.length, databaseDefinition.length);
        }
        
        public boolean hasNullableMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.nullable, databaseDefinition.nullable);
        }
        
        public boolean hasDefaultValueMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.defaultValue, databaseDefinition.defaultValue);
        }
        
        public boolean hasCharsetMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.charset, databaseDefinition.charset);
        }
        
        public boolean hasCollationMismatch() {
            if (entityDefinition == null || databaseDefinition == null) return false;
            return !java.util.Objects.equals(entityDefinition.collation, databaseDefinition.collation);
        }
    }


    public static interface ICreateTableResolver { public void resolve(Context context, CreateTableDelta delta); }
    public static interface IAddColumnResolver { public void resolve(Context context, AddColumnDelta delta); }
    public static interface IModifyColumnResolver { public void resolve(Context context, ModifyColumnDelta delta); }
    public static interface IDropTableResolver { public void resolve(Context context, DropTableDelta delta); }
    public static interface IDropColumnResolver { public void resolve(Context context, DropColumnDelta delta); }
    public static interface IAddIndexResolver { public void resolve(Context context, AddIndexDelta delta); }
    public static interface IDropIndexResolver { public void resolve(Context context, DropIndexDelta delta); }

    public static interface IManualResolver { public void resolve(Context context); }

    public static interface IReconciliationResolver extends 
                            ICreateTableResolver, 
                            IAddColumnResolver, 
                            IModifyColumnResolver,
                            IDropTableResolver,
                            IDropColumnResolver,
                            IAddIndexResolver,
                            IDropIndexResolver {}
    

}
