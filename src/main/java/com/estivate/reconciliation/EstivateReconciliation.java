package com.estivate.reconciliation;

import com.estivate.context.Context;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class EstivateReconciliation {


    public static interface SchemaDiff {}

    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableMissing implements SchemaDiff {
        public String tableName;
    }

    /**
     * Column definition containing type, constraints, and encoding information
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDefinition {
        /** SQL type (e.g., "VARCHAR", "INT", "BIGINT") */
        public String columnType;
        
        /** Column length (e.g., VARCHAR(255) -> 255) */
        public Integer length;
        
        /** Whether the column is nullable */
        public Boolean nullable;
        
        /** Default value if any */
        public String defaultValue;
        
        /** Whether the column is auto-increment */
        public Boolean autoIncrement;
        
        /** Character set (e.g., "utf8mb4", "utf8") */
        public String charset;
        
        /** Collation (e.g., "utf8mb4_unicode_ci") */
        public String collation;
        
        /**
         * Builds the full SQL column type string including length if applicable
         * @return SQL type string (e.g., "VARCHAR(255)", "INT", "BIGINT")
         */
        public String getFullColumnType() {
            if (columnType == null) {
                return null;
            }
            
            if (length != null && needsLength(columnType)) {
                return columnType + "(" + length + ")";
            }
            
            return columnType;
        }
        
        /**
         * Checks if a column type typically requires a length specification
         */
        private boolean needsLength(String type) {
            if (type == null) return false;
            String upper = type.toUpperCase();
            return upper.contains("VARCHAR") || 
                   upper.contains("CHAR") || 
                   upper.contains("DECIMAL") ||
                   upper.contains("NUMERIC");
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnMissing implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public ColumnDefinition columnDefinition;
        
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
        public ColumnDefinition entityDefinition;
        public ColumnDefinition databaseDefinition;
        
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
