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

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnMissing implements SchemaDiff {
        public String tableName;
        public String attributeName;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnTypeMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public String entityType;
        public String databaseType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnLengthMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public int entityLength;
        public int databaseLength;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDefaultValueMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public String entityDefaultValue;
        public String databaseDefaultValue;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnNullableMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public boolean entityNullable;
        public boolean databaseNullable;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnEncodingMismatch implements SchemaDiff {
        public String tableName;
        public String attributeName;
        public String entityEncoding;
        public String databaseEncoding;
    }


    public static interface ITableMissingResolver { public boolean resolve(Context context, TableMissing diff); }
    public static interface IColumnMissingResolver { public boolean resolve(Context context, ColumnMissing diff); }
    public static interface IColumnTypeMismatchResolver { public boolean resolve(Context context, ColumnTypeMismatch diff); }
    public static interface IColumnLengthMismatchResolver { public boolean resolve(Context context, ColumnLengthMismatch diff); }
    public static interface IColumnDefaultValueMismatchResolver { public boolean resolve(Context context, ColumnDefaultValueMismatch diff); }
    public static interface IColumnNullableMismatchResolver { public boolean resolve(Context context, ColumnNullableMismatch diff); }
    public static interface IColumnEncodingMismatchResolver { public boolean resolve(Context context, ColumnEncodingMismatch diff); }

    public static interface IReconciliationResolver extends 
                            ITableMissingResolver, 
                            IColumnMissingResolver, 
                            IColumnTypeMismatchResolver, 
                            IColumnLengthMismatchResolver, 
                            IColumnDefaultValueMismatchResolver, 
                            IColumnNullableMismatchResolver, 
                            IColumnEncodingMismatchResolver {}
    

}
