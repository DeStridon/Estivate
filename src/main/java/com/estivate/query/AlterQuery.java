package com.estivate.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Statement;
import com.estivate.context.Context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AlterQuery<E> extends Query<AlterQuery<E>, E> {

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
        private final String columnType;
        private final String charset;
        private final String collation;
        private final Boolean nullable;

        public ModifyColumn(String columnName, String columnType) {
            this(columnName, columnType, null, null, null);
        }

        public ModifyColumn(String columnName, String columnType, String charset, String collation, Boolean nullable) {
            this.columnName = columnName;
            this.columnType = columnType;
            this.charset = charset;
            this.collation = collation;
            this.nullable = nullable;
        }

        @Override
        public void render(Context context, Statement statement) {
            statement.appendQuery("MODIFY COLUMN");
            statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
            statement.appendQuery(columnType);
            if (charset != null) {
                statement.appendQuery("CHARACTER SET");
                statement.appendQuery(charset);
            }
            if (collation != null) {
                statement.appendQuery("COLLATE");
                statement.appendQuery(collation);
            }
            if (nullable != null) {
                statement.appendQuery(nullable ? "NULL" : "NOT NULL");
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


