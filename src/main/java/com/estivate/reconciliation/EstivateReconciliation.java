package com.estivate.reconciliation;

import com.estivate.context.Context;
import com.estivate.query.AlterQuery;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class EstivateReconciliation {


    public static interface ReconciliationDelta {}

    /**
     * Represents a table that exists in code but not in the database.
     * Action: CREATE TABLE
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTableDelta implements ReconciliationDelta {
        public String tableName;
    }

    /**
     * Represents a column that exists in code but not in the database.
     * Action: ALTER TABLE ADD COLUMN
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddColumnDelta implements ReconciliationDelta {
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

    /**
     * Represents a table that exists in the database but has no corresponding entity in code.
     * Action: DROP TABLE
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropTableDelta implements ReconciliationDelta {
        public String tableName;
    }

    /**
     * Represents a column that exists in the database but has no corresponding field in the entity.
     * Action: ALTER TABLE DROP COLUMN
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropColumnDelta implements ReconciliationDelta {
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
    public static class AddIndexDelta implements ReconciliationDelta {
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
    public static class DropIndexDelta implements ReconciliationDelta {
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
    public static class ModifyColumnDelta implements ReconciliationDelta {
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


    public static interface ICreateTableDeltaResolver { public void resolve(Context context, CreateTableDelta diff); }
    public static interface IAddColumnDeltaResolver { public void resolve(Context context, AddColumnDelta diff); }
    public static interface IModifyColumnDeltaResolver { public void resolve(Context context, ModifyColumnDelta diff); }
    public static interface IDropTableDeltaResolver { public void resolve(Context context, DropTableDelta diff); }
    public static interface IDropColumnDeltaResolver { public void resolve(Context context, DropColumnDelta diff); }
    public static interface IAddIndexDeltaResolver { public void resolve(Context context, AddIndexDelta diff); }
    public static interface IDropIndexDeltaResolver { public void resolve(Context context, DropIndexDelta diff); }

    public static interface IReconciliationResolver extends 
                            ICreateTableDeltaResolver, 
                            IAddColumnDeltaResolver, 
                            IModifyColumnDeltaResolver,
                            IDropTableDeltaResolver,
                            IDropColumnDeltaResolver,
                            IAddIndexDeltaResolver,
                            IDropIndexDeltaResolver {}
    

}
