package com.estivate.reconciliation;

public class ISchemaDiff {


    public static interface SchemaDiff {}

    public static class TableMissing implements SchemaDiff {
        public String tableName;
    }

    public static class ColumnMissing implements SchemaDiff {
        public String tableName;
        public String columnName;
    }

    public static class ColumnTypeMismatch implements SchemaDiff {
        public String tableName;
        public String columnName;
        public String entityType;
        public String databaseType;
    }

    public static class ColumnLengthMismatch implements SchemaDiff {
        public String tableName;
        public String columnName;
        public int entityLength;
        public int databaseLength;
    }

    public static class ColumnDefaultValueMismatch implements SchemaDiff {
        public String tableName;
        public String columnName;
        public String entityDefaultValue;
        public String databaseDefaultValue;
    }

    public static class ColumnNullableMismatch implements SchemaDiff {
        public String tableName;
        public String columnName;
        public boolean entityNullable;
        public boolean databaseNullable;
    }

    

}
