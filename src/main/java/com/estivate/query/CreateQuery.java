package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CreateQuery<E> {


    @Getter
    private final Class<E> entity;

    @Getter
    private List<String> comments = new ArrayList<>();

    @Getter
    private List<ColumnDefinition> columns = new ArrayList<>();
    
    @Getter
    private PrimaryKey primaryKey;
    
    // @Getter
    // private List<ForeignKey> foreignKeys = new ArrayList<>();
    
    @Getter
    private List<Index> indexes = new ArrayList<>();
    
    /** Table options */
    @Getter 
    private String tableEngine;
    @Getter
    private String tableCharset;
    @Getter
    private String tableCollation;
    @Getter
    private String tableComment;
    @Getter
    private Integer tableAutoIncrement;
    
    @Getter
    private boolean ifNotExists = false;
    
    @Getter
    private boolean temporary = false;

    public CreateQuery(Class<E> entity) { 
        this.entity = entity;
		for(Field field : FieldUtils.getEntityFields(entity)) {
			column(ColumnDefinition.fromField(field));
		}
    }

    public CreateQuery<E> column(ColumnDefinition column) {  columns.add(column); return this; }
    public CreateQuery<E> primaryKey(String... columns) {  this.primaryKey = new PrimaryKey(columns); return this; }
    public CreateQuery<E> primaryKey(List<String> columns) {  this.primaryKey = new PrimaryKey(columns); return this; }
    public CreateQuery<E> primaryKey(PrimaryKey primaryKey) { this.primaryKey = primaryKey; return this; }
    // public CreateQuery<E> foreignKey(ForeignKey foreignKey) { foreignKeys.add(foreignKey); return this; }


    // public CreateQuery<E> foreignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns) {
    //     foreignKeys.add(new ForeignKey(constraintName, columns, referencedTable, referencedColumns));
    //     return this;
    // }
    
    // public CreateQuery<E> foreignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns, ForeignKey.ReferentialAction onDelete, ForeignKey.ReferentialAction onUpdate) {
    //     foreignKeys.add(new ForeignKey(constraintName, columns, referencedTable, referencedColumns, onDelete, onUpdate));
    //     return this;
    // }
    
    // Index methods
    public CreateQuery<E> index(Index index) { indexes.add(index); return this; }
    public CreateQuery<E> index(String indexName, String... columns) { indexes.add(new Index(indexName, Arrays.asList(columns), false)); return this; }
    public CreateQuery<E> index(String indexName, List<String> columns) { indexes.add(new Index(indexName, columns, false)); return this; }
    public CreateQuery<E> index(String indexName, List<String> columns, boolean unique) { indexes.add(new Index(indexName, columns, unique)); return this; }
    
    // Table options methods
    public CreateQuery<E> tableEngine(String engine) { this.tableEngine = engine; return this; }
    public CreateQuery<E> tableCharset(String charset) { this.tableCharset = charset; return this; }
    public CreateQuery<E> tableCollation(String collation) { this.tableCollation = collation; return this; }
    public CreateQuery<E> tableComment(String comment) { this.tableComment = comment; return this; }
    public CreateQuery<E> tableAutoIncrement(Integer value) { this.tableAutoIncrement = value; return this; }

    
    // Flags
    public CreateQuery<E> ifNotExists(boolean ifNotExists) { this.ifNotExists = ifNotExists; return this; }
    public CreateQuery<E> ifNotExists() { return ifNotExists(true); }
    public CreateQuery<E> temporary(boolean temporary) { this.temporary = temporary; return this; }
    public CreateQuery<E> temporary() { return temporary(true); }
    

    public CreateQuery<E> clone() {
        CreateQuery<E> queryClone = new CreateQuery<E>(this.entity);
        queryClone.comments = new ArrayList<>(this.comments);
        queryClone.columns = new ArrayList<>(this.columns);
        queryClone.primaryKey = this.primaryKey;
        // queryClone.foreignKeys = new ArrayList<>(this.foreignKeys);
        queryClone.indexes = new ArrayList<>(this.indexes);
        queryClone.tableEngine = this.tableEngine;
        queryClone.tableCharset = this.tableCharset;
        queryClone.tableCollation = this.tableCollation;
        queryClone.tableComment = this.tableComment;
        queryClone.tableAutoIncrement = this.tableAutoIncrement;
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

    /**
     * Column definition containing type, constraints, and encoding information
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDefinition {
        
        public String name;
        public Class<?> type;
        public String explicitType;
        public Integer length;
        public Boolean nullable;
        public String defaultValue;
        public Boolean autoIncrement;
        public String charset;
        public String collation;
        public Boolean primaryKey;
        public String comment;
        
        
        public static ColumnDefinition fromField(Field field) {
            ColumnDefinition columnDefinition = new ColumnDefinition();
            columnDefinition.name = field.getName();

            Class<?> type = field.getType();

            // Check for @Convert annotation
            if (field.getDeclaredAnnotation(javax.persistence.Convert.class) != null || field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
                columnDefinition.type = String.class;
            }
            // Handle enums
            else if (type.isEnum()) {
                columnDefinition.type = FieldUtils.isEnumeratedAsString(field) ? String.class : Integer.class;
            }
            else{
                columnDefinition.type = type;
            }

            columnDefinition.explicitType = FieldUtils.readFieldForExplicitType(field);

            columnDefinition.length = FieldUtils.readFieldForLength(field);
            columnDefinition.nullable = FieldUtils.isNullable(field);
            columnDefinition.defaultValue = FieldUtils.extractDefaultValue(field);
            columnDefinition.autoIncrement = FieldUtils.isAutoIncrement(field);
            columnDefinition.primaryKey = field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class);
			
            return columnDefinition;
        }
        
        // /**
        //  * Builds the full SQL column type string including length if applicable
        //  * @return SQL type string (e.g., "VARCHAR(255)", "INT", "BIGINT")
        //  */
        // public String getFullColumnType(Context context) {
        //     if (type == null) {
        //         return null;
        //     }

        //     if(length == null){
        //         length = context.getDefaultLength(type);
        //     }
            
        //     if (length != null) {
        //         return type + "(" + length + ")";
        //     }
            
        //     return type;
        // }
        
    }

    /**
     * Represents a column to be created in the table
     */
    // @Getter
    // public static class Column {
        
    //     private final ColumnDefinition columnDefinition;


    //     public Column(String columnName, ColumnDefinition columnDefinition) {
    //         this.columnName = columnName;
    //         this.columnDefinition = columnDefinition;
    //     }

    //     public void render(Context context, Statement statement) {
    //         statement.appendQuery(context.nameMapper.mapDatabaseField(columnName));
    //         String columnType = columnDefinition != null && columnDefinition.columnType != null 
    //             ? columnDefinition.getFullColumnType(context) 
    //             : (columnDefinition != null ? columnDefinition.columnType : null);
    //         if (columnType != null) {
    //             statement.appendQuery(columnType);
    //         }
    //         if (columnDefinition != null && columnDefinition.charset != null) {
    //             statement.appendQuery("CHARACTER SET");
    //             statement.appendQuery(columnDefinition.charset);
    //         }
    //         if (columnDefinition != null && columnDefinition.collation != null) {
    //             statement.appendQuery("COLLATE");
    //             statement.appendQuery(columnDefinition.collation);
    //         }
    //         if (columnDefinition != null && columnDefinition.nullable != null) {
    //             statement.appendQuery(columnDefinition.nullable ? "NULL" : "NOT NULL");
    //         }
    //         if (columnDefinition != null && columnDefinition.defaultValue != null) {
    //             statement.appendQuery("DEFAULT");
    //             statement.appendQuery(columnDefinition.defaultValue);
    //         }
    //         if (columnDefinition != null && Boolean.TRUE.equals(columnDefinition.autoIncrement)) {
    //             statement.appendQuery("AUTO_INCREMENT");
    //         }
    //         if (columnDefinition != null && Boolean.TRUE.equals(columnDefinition.primaryKey)) {
    //             statement.appendQuery("PRIMARY KEY");
    //         }
    //         if (columnDefinition != null && columnDefinition.comment != null) {
    //             statement.appendQuery("COMMENT");
    //             statement.appendQuery("'" + columnDefinition.comment.replace("'", "''") + "'");
    //         }
    //     }
    // }

    /**
     * Represents a primary key constraint
     */
    @Getter
    public static class PrimaryKey {
        private final List<String> columns;

        public PrimaryKey(List<String> columns) { this.columns = new ArrayList<>(columns); }
        public PrimaryKey(String... columns) { this(Arrays.asList(columns)); }

        public void render(Context context, Statement statement) {
            statement.appendQuery("PRIMARY KEY (");
            statement.appendQuery(columns.stream()
                .map(col -> context.nameMapper.mapDatabaseField(col))
                .collect(Collectors.joining(", ")));
            statement.appendQuery(")");
        }
    }

    // /**
    //  * Represents a foreign key constraint
    //  */
    // @Getter
    // public static class ForeignKey {
    //     private final String constraintName;
    //     private final List<String> columns;
    //     private final String referencedTable;
    //     private final List<String> referencedColumns;
    //     private final ReferentialAction onDelete;
    //     private final ReferentialAction onUpdate;

    //     public enum ReferentialAction {
    //         CASCADE, SET_NULL, SET_DEFAULT, RESTRICT, NO_ACTION
    //     }

    //     public ForeignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns) {
    //         this(constraintName, columns, referencedTable, referencedColumns, null, null);
    //     }

    //     public ForeignKey(String constraintName, List<String> columns, String referencedTable, List<String> referencedColumns, ReferentialAction onDelete, ReferentialAction onUpdate) {
    //         this.constraintName = constraintName;
    //         this.columns = new ArrayList<>(columns);
    //         this.referencedTable = referencedTable;
    //         this.referencedColumns = new ArrayList<>(referencedColumns);
    //         this.onDelete = onDelete;
    //         this.onUpdate = onUpdate;
    //     }

    //     public void render(Context context, Statement statement) {
    //         if (constraintName != null) {
    //             statement.appendQuery("CONSTRAINT");
    //             statement.appendQuery(constraintName);
    //         }
    //         statement.appendQuery("FOREIGN KEY (");
    //         statement.appendQuery(columns.stream()
    //             .map(col -> context.nameMapper.mapDatabaseField(col))
    //             .collect(Collectors.joining(", ")));
    //         statement.appendQuery(") REFERENCES");
    //         statement.appendQuery(referencedTable);
    //         statement.appendQuery("(");
    //         statement.appendQuery(referencedColumns.stream()
    //             .map(col -> context.nameMapper.mapDatabaseField(col))
    //             .collect(Collectors.joining(", ")));
    //         statement.appendQuery(")");
    //         if (onDelete != null) {
    //             statement.appendQuery("ON DELETE");
    //             statement.appendQuery(onDelete.name().replace("_", " "));
    //         }
    //         if (onUpdate != null) {
    //             statement.appendQuery("ON UPDATE");
    //             statement.appendQuery(onUpdate.name().replace("_", " "));
    //         }
    //     }
    // }

    /**
     * Represents an index definition
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Index {
        private final String indexName;
        private final List<String> columns;
        private final boolean unique;

        // public void render(Context context, Statement statement) {
        //     if (unique) {
        //         statement.appendQuery("UNIQUE");
        //     }
        //     statement.appendQuery("INDEX");
        //     if (indexName != null) {
        //         statement.appendQuery(indexName);
        //     }
        //     statement.appendQuery("(");
        //     statement.appendQuery(columns.stream()
        //         .map(col -> context.nameMapper.mapDatabaseField(col))
        //         .collect(Collectors.joining(", ")));
        //     statement.appendQuery(")");
        // }
    }

    /**
     * Table options like ENGINE, CHARSET, COLLATE
     */
    // @Getter
    // @NoArgsConstructor
    // @AllArgsConstructor
    // public static class TableOptions {
    //     private String engine;
    //     private String charset;
    //     private String collation;
    //     private String comment;
    //     private Integer autoIncrement;

    //     public void render(Context context, Statement statement) {
    //         if (engine != null) {
    //             statement.appendQuery("ENGINE =");
    //             statement.appendQuery(engine);
    //         }
    //         if (charset != null) {
    //             statement.appendQuery("DEFAULT CHARSET =");
    //             statement.appendQuery(charset);
    //         }
    //         if (collation != null) {
    //             statement.appendQuery("COLLATE =");
    //             statement.appendQuery(collation);
    //         }
    //         if (autoIncrement != null) {
    //             statement.appendQuery("AUTO_INCREMENT =");
    //             statement.appendQuery(autoIncrement.toString());
    //         }
    //         if (comment != null) {
    //             statement.appendQuery("COMMENT =");
    //             statement.appendQuery("'" + comment.replace("'", "''") + "'");
    //         }
    //     }
    // }


    
}
