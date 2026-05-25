package com.estivate.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.reconciliation.ColumnModel.EntityColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class AlterQuery<E> {

    @Getter
    @NonNull
    final private Class<E> entity;

    public AlterQuery(Class<E> entity) {
        this.entity = entity;
    }
    /**
     * Column definition containing type, constraints, and encoding information
     */
    // @Data
    // @Builder
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class ColumnDefinition {
    //     public @NonNull String columnType; // SQL type (e.g., "VARCHAR", "INT", "BIGINT")
    //     public Integer length; 
    //     public Boolean nullable; 
    //     public String defaultValue;
    //     public Boolean autoIncrement;
    //     public String charset;
    //     public String collation;
        
    //     /**
    //      * Builds the full SQL column type string including length if applicable
    //      * @return SQL type string (e.g., "VARCHAR(255)", "INT", "BIGINT")
    //      */
    //     public String getFullColumnType() {
    //         if (length != null && needsLength(columnType)) {
    //             return columnType + "(" + length + ")";
    //         }
    //         return columnType;
    //     }
        
    //     /**
    //      * Checks if a column type typically requires a length specification
    //      */
    //     private boolean needsLength(String type) {
    //         if (type == null) return false;
    //         String upper = type.toUpperCase();
    //         return upper.contains("VARCHAR") || 
    //                upper.contains("CHAR") || 
    //                upper.contains("DECIMAL") ||
    //                upper.contains("NUMERIC");
    //     }

    //     public void appendToStatement(Context context, Statement statement){
    //         String columnType = getFullColumnType(); 
    //         if (columnType != null) {
    //             statement.appendQuery(columnType);
    //         }
    //         if (charset != null) {
    //             statement.appendQuery("CHARACTER SET");
    //             statement.appendQuery(charset);
    //         }
    //         if (collation != null) {
    //             statement.appendQuery("COLLATE");
    //             statement.appendQuery(collation);
    //         }
    //         if (nullable != null) {
    //             statement.appendQuery(nullable ? "NULL" : "NOT NULL");
    //         }
    //         if (defaultValue != null) {
    //             statement.appendQuery("DEFAULT");
    //             statement.appendQuery(defaultValue);
    //         }
    //         if (Boolean.TRUE.equals(autoIncrement)) {
    //             statement.appendQuery("AUTO_INCREMENT");
    //         }

    //     }
    // }

    /**
     * Base interface for ALTER TABLE operations
     */
    public interface Operation {
        // /**
        //  * Renders the SQL for this operation
        //  * @param context the database context
        //  * @param statement the statement builder to append SQL to
        //  */
        // void render(Context context, Statement statement);
    }

    /**
     * Operation to add a column to a table
     */
    @Getter
    @AllArgsConstructor
    public static class AddColumn implements Operation {
        private final String columnName;
        private final EntityColumn columnDefinition;

        // public AddColumn(String columnName, EntityColumn columnDefinition) {
        //     this.columnName = columnName;
        //     this.columnDefinition = columnDefinition;
        // }

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("ADD COLUMN");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        //     columnDefinition.appendToStatement(context, statement);
            
        // }
    }

    /**
     * Operation to drop a column from a table
     */
    @Getter
    @RequiredArgsConstructor
    public static class DropColumn implements Operation {
        private final String columnName;

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("DROP COLUMN");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        // }
    }

    /**
     * Operation to modify a column (type, charset, collation, nullability)
     */
    @Getter
    @AllArgsConstructor
    public static class ModifyColumn implements Operation {
        private final String columnName;
        private final EntityColumn columnDefinition;

        // public ModifyColumn(String columnName, EntityColumn columnDefinition) {
        //     this.columnName = columnName;
        //     this.columnDefinition = columnDefinition;
        // }

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("MODIFY COLUMN");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        //     columnDefinition.appendToStatement(context, statement);
        // }
    }

    /**
     * Operation to change a column (rename and/or modify type) - MySQL specific
     */
    @Getter
    @RequiredArgsConstructor
    public static class ChangeColumn implements Operation {
        private final String columnName;
        private final String newColumnName;
        private final String columnType;

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("CHANGE COLUMN");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(newColumnName));
        //     statement.appendQuery(columnType);
        // }
    }
    

    /**
     * Operation to rename a column
     */
    @Getter
    @AllArgsConstructor
    public static class RenameColumn implements Operation {
        private String columnName;
        private String newColumnName;

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("RENAME COLUMN");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        //     statement.appendQuery("TO");
        //     statement.appendQuery(context.nameMapper.mapDatabaseField(newColumnName));
        // }
    }

    /**
     * Operation to add an index to a table
     */
    @Getter
    @AllArgsConstructor
    public static class AddIndex implements Operation {
        private String indexName;
        private List<String> columns;

        // public AddIndex(String indexName, List<String> columns) {
        //     this.indexName = indexName;
        //     this.columns = columns != null ? new ArrayList<>(columns) : new ArrayList<>();
        // }

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("ADD INDEX");
        //     statement.appendQuery(indexName);
        //     statement.appendQuery("(");
        //     statement.appendQuery(columns.stream()
        //         .map(col -> context.nameMapper.mapDatabaseField(col))
        //         .collect(Collectors.joining(", ")));
        //     statement.appendQuery(")");
        // }
    }

    /**
     * Operation to drop an index from a table
     */
    @Getter
    @AllArgsConstructor
    public static class DropIndex implements Operation {
        private final String indexName;

        // public DropIndex(String indexName) {
        //     this.indexName = indexName;
        // }

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("DROP INDEX");
        //     statement.appendQuery(indexName);
        // }
    }

    /**
     * Operation to rename a table
     */
    @Getter
    @AllArgsConstructor
    public static class RenameTable implements Operation {
        private String newTableName;

        // @Override
        // public void render(Context context, Statement statement) {
        //     statement.appendQuery("RENAME TO");
        //     statement.appendQuery(newTableName);
        // }
    }

    @Getter
    private List<Operation> operations = new ArrayList<>();


    public AlterQuery<E> addColumn(EntityColumn columnDefinition) { return addOperation(new AddColumn(columnDefinition.getName(), columnDefinition)); }
    public AlterQuery<E> dropColumn(String columnName) { return addOperation(new DropColumn(columnName)); }
    public AlterQuery<E> modifyColumn(String columnName, EntityColumn columnDefinition) { return addOperation(new ModifyColumn(columnName, columnDefinition)); }
    public AlterQuery<E> changeColumn(String columnName, String newColumnName, String columnType) { return addOperation(new ChangeColumn(columnName, newColumnName, columnType)); }
    public AlterQuery<E> renameColumn(String columnName, String newColumnName) { return addOperation(new RenameColumn(columnName, newColumnName)); }
    public AlterQuery<E> addIndex(String indexName, List<String> columns) { return addOperation(new AddIndex(indexName, columns)); }
    public AlterQuery<E> dropIndex(String indexName) { return addOperation(new DropIndex(indexName)); }
    public AlterQuery<E> renameTable(String newTableName) { return addOperation(new RenameTable(newTableName)); }

    public AlterQuery<E> addOperation(Operation operation) {
        operations.add(operation);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AlterQuery<E> clone() {
        AlterQuery<E> queryClone = new AlterQuery<E>(entity);
        
        //queryClone.comments = new ArrayList<>(this.comments);
        queryClone.operations = new ArrayList<>(this.operations);
        
        return queryClone;
    }

    /**
     * Execute the ALTER TABLE query
     */
    public Boolean execute(Context context) {
        return context.execute(this);
    }
}


