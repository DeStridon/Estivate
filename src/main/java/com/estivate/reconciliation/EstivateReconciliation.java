package com.estivate.reconciliation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import com.estivate.context.Context;
import com.estivate.query.AlterQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class EstivateReconciliation {

    public static enum ReconciliationResult {
        SOLVED, // treated successfully
        SKIPPED, // not treated but doesn't need to
        FAILED; // not treated and should have
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReconciliationScope {
        String table() default "";       // noms des tables visées
        String column() default "";      // noms des colonnes visées
    }


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




    public static class CreateTableDelta extends ReconciliationDelta {
        public Class<?> entityClass;
    }
    

    public static class DropTableDelta extends ReconciliationDelta {
        public String tableName;
    }
    

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public String entityAttribute;
        public AlterQuery.ColumnDefinition entityColumnDefinition;

        public List<ReconciliationDelta> entityDeltas;
    }

    
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public String tableColumnName;

        public List<ReconciliationDelta> entityDeltas;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModifyColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public String attributeName;
        public AlterQuery.ColumnDefinition entityDefinition;
        public AlterQuery.ColumnDefinition databaseDefinition;

        public List<ReconciliationDelta> entityDeltas;

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




    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddIndexDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public String indexName;
        public IndexDefinition indexDefinition;

        public List<ReconciliationDelta> entityDeltas;
    }

    /**
     * Represents an index that exists in the database but has no corresponding definition in code.
     * Action: DROP INDEX
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropIndexDelta extends ReconciliationDelta {
    	public Class<?> entityClass;
        public String indexName;
        public IndexDefinition indexDefinition;

        public List<ReconciliationDelta> entityDeltas;
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
