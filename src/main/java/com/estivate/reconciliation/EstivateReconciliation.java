package com.estivate.reconciliation;

import com.estivate.context.Context;
import com.estivate.query.AlterQuery;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class EstivateReconciliation {


    public static interface SchemaDiff {}

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableMissing implements SchemaDiff {
        public String tableName;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnMissing implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public AlterQuery.ColumnDefinition columnDefinition;
        
        /**
         * Constructor with basic information (no column definition)
         */
        public ColumnMissing(String tableName, String attributeName) {
            this.tableName = tableName;
            this.attributeName = attributeName;
            this.columnDefinition = null;
        }
    }

    /**
     * Represents a mismatch between entity column definition and database column definition.
     * Consolidates type, length, nullable, default value, and encoding mismatches.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDefinitionMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
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


    public static interface ITableMissingResolver { public boolean resolve(Context context, TableMissing diff); }
    public static interface IColumnMissingResolver { public boolean resolve(Context context, ColumnMissing diff); }
    public static interface IColumnDefinitionMismatchResolver { public boolean resolve(Context context, ColumnDefinitionMismatch diff); }

    public static interface IReconciliationResolver extends 
                            ITableMissingResolver, 
                            IColumnMissingResolver, 
                            IColumnDefinitionMismatchResolver {}
    

}
