package com.estivate.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.IndexColumn;
import com.estivate.reconciliation.ProjectedColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class CreateQuery<E> {


    @Getter
    private final Class<E> entity;

    @Getter
    private List<String> comments = new ArrayList<>();

    @Getter
    private List<ProjectedColumn> columns = new ArrayList<>();
    
    @Getter
    private PrimaryKey primaryKey;
    
    
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
    }

    public CreateQuery<E> column(ProjectedColumn column) {  columns.add(column); return this; }
    public CreateQuery<E> primaryKey(String... columns) {  this.primaryKey = new PrimaryKey(columns); return this; }
    public CreateQuery<E> primaryKey(List<String> columns) {  this.primaryKey = new PrimaryKey(columns); return this; }
    public CreateQuery<E> primaryKey(PrimaryKey primaryKey) { this.primaryKey = primaryKey; return this; }

    
    // Index methods
    public CreateQuery<E> index(Index index) { indexes.add(index); return this; }
    public CreateQuery<E> index(String indexName, IndexType type, List<IndexColumn> columns) { indexes.add(new Index(indexName, type, columns)); return this; }
    
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

    

    /**
     * Represents an index definition
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Index {
        private String name;
        private IndexType type;
        private List<IndexColumn> columns;
    }

    
}
