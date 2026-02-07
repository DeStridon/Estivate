package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Statement;
import com.estivate.context.Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CreateQuery<E> extends Query<CreateQuery<E>, E> {

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
        
        /** Whether this column is a primary key */
        public Boolean primaryKey;
        
        /** Comment for the column */
        public String comment;
        
        public ColumnDefinition(String columnType, Integer length, Boolean nullable, String defaultValue, Boolean autoIncrement, String charset, String collation) {
            this(columnType, length, nullable, defaultValue, autoIncrement, charset, collation, null, null);
        }
        
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
     * Represents a column to be created in the table
     */
    @Getter
    public static class Column {
        private final String columnName;
        private final ColumnDefinition columnDefinition;

        public Column(String columnName, String columnType) {
            this(columnName, new ColumnDefinition(columnType, null, null, null, null, null, null, null, null));
        }

        public Column(String columnName, ColumnDefinition columnDefinition) {
            this.columnName = columnName;
            this.columnDefinition = columnDefinition;
        }

        public void render(Context context, Statement statement) {
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
            if (columnDefinition != null && columnDefinition.defaultValue != null) {
                statement.appendQuery("DEFAULT");
                statement.appendQuery(columnDefinition.defaultValue);
            }
            if (columnDefinition != null && Boolean.TRUE.equals(columnDefinition.autoIncrement)) {
                statement.appendQuery("AUTO_INCREMENT");
            }
            if (columnDefinition != null && Boolean.TRUE.equals(columnDefinition.primaryKey)) {
                statement.appendQuery("PRIMARY KEY");
            }
            if (columnDefinition != null && columnDefinition.comment != null) {
                statement.appendQuery("COMMENT");
                statement.appendQuery("'" + columnDefinition.comment.replace("'", "''") + "'");
            }
        }
    }

    /**
     * Represents a primary key constraint
     */
    @Getter
    public static class PrimaryKey {
        private final List<String> columns;

        public PrimaryKey(String... columns) {
            this.columns = new ArrayList<>(Arrays.asList(columns));
        }

        public PrimaryKey(List<String> columns) {
            this.columns = new ArrayList<>(columns);
        }

        public void render(Context context, Statement statement) {
            statement.appendQuery("PRIMARY KEY (");
            statement.appendQuery(columns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(")");
        }
    }

    /**
     * Represents a foreign key constraint
     */
    @Getter
    public static class ForeignKey {
        private final String constraintName;
        private final List<String> columns;
        private final String referencedTable;
        private final List<String> referencedColumns;
        private final ReferentialAction onDelete;
        private final ReferentialAction onUpdate;

        public enum ReferentialAction {
            CASCADE, SET_NULL, SET_DEFAULT, RESTRICT, NO_ACTION
        }

        public ForeignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns) {
            this(constraintName, columns, referencedTable, referencedColumns, null, null);
        }

        public ForeignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns, 
                          ReferentialAction onDelete, ReferentialAction onUpdate) {
            this.constraintName = constraintName;
            this.columns = new ArrayList<>(columns);
            this.referencedTable = referencedTable;
            this.referencedColumns = new ArrayList<>(referencedColumns);
            this.onDelete = onDelete;
            this.onUpdate = onUpdate;
        }

        public void render(Context context, Statement statement) {
            if (constraintName != null) {
                statement.appendQuery("CONSTRAINT");
                statement.appendQuery(constraintName);
            }
            statement.appendQuery("FOREIGN KEY (");
            statement.appendQuery(columns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(") REFERENCES");
            statement.appendQuery(referencedTable);
            statement.appendQuery("(");
            statement.appendQuery(referencedColumns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(")");
            if (onDelete != null) {
                statement.appendQuery("ON DELETE");
                statement.appendQuery(onDelete.name().replace("_", " "));
            }
            if (onUpdate != null) {
                statement.appendQuery("ON UPDATE");
                statement.appendQuery(onUpdate.name().replace("_", " "));
            }
        }
    }

    /**
     * Represents an index definition
     */
    @Getter
    public static class Index {
        private final String indexName;
        private final List<String> columns;
        private final boolean unique;

        public Index(String indexName, List<String> columns) {
            this(indexName, columns, false);
        }

        public Index(String indexName, List<String> columns, boolean unique) {
            this.indexName = indexName;
            this.columns = new ArrayList<>(columns);
            this.unique = unique;
        }

        public void render(Context context, Statement statement) {
            if (unique) {
                statement.appendQuery("UNIQUE");
            }
            statement.appendQuery("INDEX");
            if (indexName != null) {
                statement.appendQuery(indexName);
            }
            statement.appendQuery("(");
            statement.appendQuery(columns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(")");
        }
    }

    /**
     * Table options like ENGINE, CHARSET, COLLATE
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableOptions {
        private String engine;
        private String charset;
        private String collation;
        private String comment;
        private Integer autoIncrement;

        public void render(Context context, Statement statement) {
            if (engine != null) {
                statement.appendQuery("ENGINE =");
                statement.appendQuery(engine);
            }
            if (charset != null) {
                statement.appendQuery("DEFAULT CHARSET =");
                statement.appendQuery(charset);
            }
            if (collation != null) {
                statement.appendQuery("COLLATE =");
                statement.appendQuery(collation);
            }
            if (autoIncrement != null) {
                statement.appendQuery("AUTO_INCREMENT =");
                statement.appendQuery(autoIncrement.toString());
            }
            if (comment != null) {
                statement.appendQuery("COMMENT =");
                statement.appendQuery("'" + comment.replace("'", "''") + "'");
            }
        }
    }

    @Getter
    private LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
    
    @Getter
    private PrimaryKey primaryKey;
    
    @Getter
    private List<ForeignKey> foreignKeys = new ArrayList<>();
    
    @Getter
    private List<Index> indexes = new ArrayList<>();
    
    @Getter
    private TableOptions tableOptions;
    
    @Getter
    private boolean ifNotExists = false;
    
    @Getter
    private boolean temporary = false;

    public CreateQuery(Class<E> baseClass) {
        super(baseClass);
    }

    public CreateQuery(Entity<E> entity) {
        super(entity);
    }

    // Column methods
    public CreateQuery<E> column(String columnName, String columnType) { 
        columns.put(columnName, new Column(columnName, columnType)); 
        return this; 
    }
    
    public CreateQuery<E> column(String columnName, ColumnDefinition columnDefinition) { 
        columns.put(columnName, new Column(columnName, columnDefinition)); 
        return this; 
    }
    
    public CreateQuery<E> column(Column column) { 
        columns.put(column.getColumnName(), column); 
        return this; 
    }

    // Primary key methods
    public CreateQuery<E> primaryKey(String... columns) { 
        this.primaryKey = new PrimaryKey(columns); 
        return this; 
    }
    
    public CreateQuery<E> primaryKey(List<String> columns) { 
        this.primaryKey = new PrimaryKey(columns); 
        return this; 
    }
    
    public CreateQuery<E> primaryKey(PrimaryKey primaryKey) { 
        this.primaryKey = primaryKey; 
        return this; 
    }

    // Foreign key methods
    public CreateQuery<E> foreignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns) {
        foreignKeys.add(new ForeignKey(constraintName, columns, referencedTable, referencedColumns));
        return this;
    }
    
    public CreateQuery<E> foreignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns,
                                      ForeignKey.ReferentialAction onDelete, ForeignKey.ReferentialAction onUpdate) {
        foreignKeys.add(new ForeignKey(constraintName, columns, referencedTable, referencedColumns, onDelete, onUpdate));
        return this;
    }
    
    public CreateQuery<E> foreignKey(ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
        return this;
    }

    // Index methods
    public CreateQuery<E> index(String indexName, String... columns) {
        indexes.add(new Index(indexName, Arrays.asList(columns)));
        return this;
    }
    
    public CreateQuery<E> index(String indexName, List<String> columns) {
        indexes.add(new Index(indexName, columns));
        return this;
    }
    
    public CreateQuery<E> uniqueIndex(String indexName, String... columns) {
        indexes.add(new Index(indexName, Arrays.asList(columns), true));
        return this;
    }
    
    public CreateQuery<E> uniqueIndex(String indexName, List<String> columns) {
        indexes.add(new Index(indexName, columns, true));
        return this;
    }
    
    public CreateQuery<E> index(Index index) {
        indexes.add(index);
        return this;
    }

    // Table options methods
    public CreateQuery<E> engine(String engine) {
        if (tableOptions == null) tableOptions = new TableOptions();
        tableOptions.engine = engine;
        return this;
    }
    
    public CreateQuery<E> charset(String charset) {
        if (tableOptions == null) tableOptions = new TableOptions();
        tableOptions.charset = charset;
        return this;
    }
    
    public CreateQuery<E> collation(String collation) {
        if (tableOptions == null) tableOptions = new TableOptions();
        tableOptions.collation = collation;
        return this;
    }
    
    public CreateQuery<E> tableComment(String comment) {
        if (tableOptions == null) tableOptions = new TableOptions();
        tableOptions.comment = comment;
        return this;
    }
    
    public CreateQuery<E> autoIncrement(Integer value) {
        if (tableOptions == null) tableOptions = new TableOptions();
        tableOptions.autoIncrement = value;
        return this;
    }
    
    public CreateQuery<E> tableOptions(TableOptions tableOptions) {
        this.tableOptions = tableOptions;
        return this;
    }

    // Flags
    public CreateQuery<E> ifNotExists() {
        this.ifNotExists = true;
        return this;
    }
    
    public CreateQuery<E> ifNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
        return this;
    }
    
    public CreateQuery<E> temporary() {
        this.temporary = true;
        return this;
    }
    
    public CreateQuery<E> temporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }

    public CreateQuery<E> clone() {
        CreateQuery<E> queryClone = new CreateQuery<E>(entity);
        
        queryClone.comments = new ArrayList<>(this.comments);
        queryClone.columns = new LinkedHashMap<>(this.columns);
        queryClone.primaryKey = this.primaryKey;
        queryClone.foreignKeys = new ArrayList<>(this.foreignKeys);
        queryClone.indexes = new ArrayList<>(this.indexes);
        queryClone.tableOptions = this.tableOptions;
        queryClone.ifNotExists = this.ifNotExists;
        queryClone.temporary = this.temporary;
        
        return queryClone;
    }

    /**
     * Execute the CREATE TABLE query
     */
    public Boolean execute(Context context) {
        return context.execute(this);
    }
}
