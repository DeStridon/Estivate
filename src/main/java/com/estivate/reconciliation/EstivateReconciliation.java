package com.estivate.reconciliation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import com.estivate.context.Context;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.IndexColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class EstivateReconciliation {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReconciliationScope {
        Class<?> entity() default void.class;
        // String field() default "";
        // String table() default "";       // noms des tables visées
        // String column() default "";      // noms des colonnes visées
    }

    public static enum ReconciliationResult {
        SOLVED, // treated successfully
        SKIPPED, // not treated but doesn't need to
        POSTPONED, // not treated but should be treated later
        FAILED; // not treated and should have
    }


    @Data
    public static abstract class ReconciliationDelta {
        private ReconciliationResult reconciliationResult;
        private String reconciliationReason;

        @Getter
        @ToString.Exclude
        private List<ReconciliationDelta> currentDeltas;

        public void closeSolved(String reason){
            reconciliationResult = ReconciliationResult.SOLVED;
            reconciliationReason = reason;
        }

        public void closeSkipped(String reason){
            reconciliationResult = ReconciliationResult.SKIPPED;
            reconciliationReason = reason;
        }

        public void closePostponed(String reason){
            reconciliationResult = ReconciliationResult.POSTPONED;
            reconciliationReason = reason;
        }

        public void closeFailed(String reason){
            reconciliationResult = ReconciliationResult.FAILED;
            reconciliationReason = reason;
        }

        public void closeSolved(){ closeSolved(null); }
        public void closeSkipped(){ closeSkipped(null);}
        public void closePostponed(){ closePostponed(null);}
        public void closeFailed(){ closeFailed(null); }
        
    }




    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTableDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        
    }
    

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropTableDelta extends ReconciliationDelta {
        public String tableName;
    }
    

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public EntityModel entityModel;
        public Field entityField;
        public String tableColumnName;
        public ColumnModel.EntityColumn entityColumnDefinition;
        public EntityModel tableModel;
    }

    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public EntityModel entityModel;
        public String tableColumnName;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModifyColumnDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public EntityModel entityModel;
        public Field entityField;
        
        public ColumnModel.EntityColumn entityColumnDefinition;
        public TableField projectedDefinition;
        public TableField databaseDefinition;
        
        List<Mismatch> mismatchs;
        
    }
    
    public static enum Mismatch{TYPE, DIMENSION, SCALE, NULLABLE, DEFAULT}




    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddIndexDelta extends ReconciliationDelta {
        public Class<?> entityClass;
        public EntityModel entityModel;
        public String indexName;
        public IndexType type;
        public List<IndexColumn> columns;
    }

    /**
     * Represents an index that exists in the database but has no corresponding definition in code.
     * Action: DROP INDEX
     */
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DropIndexDelta extends ReconciliationDelta {
    	public Class<?> entityClass;
        public EntityModel entityModel;
        public String indexName;
        public IndexType type;
        public List<IndexColumn> columns;
    }

    // /**
    //  * Definition of an index including its columns and properties.
    //  */
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class IndexDefinition {
    //     public Class<?> entity;
    //     public IndexType type;
    //     public List<IndexColumn> columns;

        
    //     public IndexDefinition(Class<?> entity, List<IndexColumn> columns) {
    //         this.entity = entity;
    //         this.type = IndexType.DEFAULT;
    //         this.columns = columns;
    //     }
    // }

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
