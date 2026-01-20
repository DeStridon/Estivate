package com.estivate.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Statement;
import com.estivate.context.Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class AlterQuery<E> extends Query<AlterQuery<E>, E> {

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

    /**
     * Base interface for ALTER TABLE operations
     */
    public interface Operation {
        /**
         * Renders the SQL for this operation
         * @param context the database context
         * @param statement the statement builder to append SQL to
         */
        void render(Context context, Statement statement);
    }

    /**
     * Operation to add a column to a table
     */
    @Getter
    @RequiredArgsConstructor
    public static class AddColumn implements Operation {
        private final String columnName;
        private final String columnType;

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("ADD COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
            statement.appendQuery(columnType);
        }
    }

    /**
     * Operation to drop a column from a table
     */
    @Getter
    @RequiredArgsConstructor
    public static class DropColumn implements Operation {
        private final String columnName;

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("DROP COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
        }
    }

    /**
     * Operation to modify a column (type, charset, collation, nullability)
     */
    @Getter
    public static class ModifyColumn implements Operation {
        private final String columnName;
        private final ColumnDefinition columnDefinition;

        public ModifyColumn(String columnName, String columnType) {
            this(columnName, new ColumnDefinition(columnType, null, null, null, null, null, null));
        }

        public ModifyColumn(String columnName, String columnType, String charset, String collation, Boolean nullable) {
            this(columnName, new ColumnDefinition(columnType, null, nullable, null, null, charset, collation));
        }

        public ModifyColumn(String columnName, ColumnDefinition columnDefinition) {
            this.columnName = columnName;
            this.columnDefinition = columnDefinition;
        }

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("MODIFY COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
            String columnType = columnDefinition != null && columnDefinition.columnType != null 
                ? columnDefinition.getFullColumnType() 
                : (columnDefinition != null ? columnDefinition.columnType : null);
            if (columnType != null) {
                statement.appendQuery(columnType);
            }
            if (columnDefinition != null && columnDefinition.charset != null) {
                statement.appendQuery("CHARACTER SET");
                statement.appendQuery(columnDefinition.charset);
            }
            if (columnDefinition != null && columnDefinition.collation != null) {
                statement.appendQuery("COLLATE");
                statement.appendQuery(columnDefinition.collation);
            }
            if (columnDefinition != null && columnDefinition.nullable != null) {
                statement.appendQuery(columnDefinition.nullable ? "NULL" : "NOT NULL");
            }
        }
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

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("CHANGE COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
            statement.appendQuery(context.nameMapper.mapDatabaseField(newColumnName));
            statement.appendQuery(columnType);
        }
    }
    

    /**
     * Operation to rename a column
     */
    @Getter
    @RequiredArgsConstructor
    public static class RenameColumn implements Operation {
        private final String columnName;
        private final String newColumnName;

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("RENAME COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
            statement.appendQuery("TO");
            statement.appendQuery(context.nameMapper.mapDatabaseField(newColumnName));
        }
    }

    /**
     * Operation to add an index to a table
     */
    @Getter
    public static class AddIndex implements Operation {
        private final String indexName;
        private final List<String> columns;

        public AddIndex(String indexName, List<String> columns) {
            this.indexName = indexName;
            this.columns = columns != null ? new ArrayList<>(columns) : new ArrayList<>();
        }

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("ADD INDEX");
            statement.appendQuery(indexName);
            statement.appendQuery("(");
            statement.appendQuery(columns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(")");
        }
    }

    /**
     * Operation to drop an index from a table
     */
    @Getter
    public static class DropIndex implements Operation {
        private final String indexName;

        public DropIndex(String indexName) {
            this.indexName = indexName;
        }

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("DROP INDEX");
            statement.appendQuery(indexName);
        }
    }

    /**
     * Operation to rename a table
     */
    @Getter
    @RequiredArgsConstructor
    public static class RenameTable implements Operation {
        private final String newTableName;

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("RENAME TO");
            statement.appendQuery(newTableName);
        }
    }

    @Getter
    private List<Operation> operations = new ArrayList<>();

    public AlterQuery(Class<E> baseClass) {
        super(baseClass);
    }

    public AlterQuery(Entity<E> entity) {
        super(entity);
    }

    public AlterQuery<E> addColumn(String columnName, String columnType) { return addOperation(new AddColumn(columnName, columnType)); }
    public AlterQuery<E> dropColumn(String columnName) { return addOperation(new DropColumn(columnName)); }
    public AlterQuery<E> modifyColumn(String columnName, String columnType) { return addOperation(new ModifyColumn(columnName, columnType)); }
    public AlterQuery<E> modifyColumn(String columnName, String columnType, String charset, String collation, Boolean nullable) { return addOperation(new ModifyColumn(columnName, columnType, charset, collation, nullable)); }
    public AlterQuery<E> modifyColumn(String columnName, ColumnDefinition columnDefinition) { return addOperation(new ModifyColumn(columnName, columnDefinition)); }
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
        
        queryClone.comments = new ArrayList<>(this.comments);
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


