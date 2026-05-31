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
    

    public interface Operation { }

    /**
     * Operation to add a column to a table
     */
    @Getter
    @AllArgsConstructor
    public static class AddColumn implements Operation {
        private final String columnName;
        private final EntityColumn columnDefinition;
    }

    /**
     * Operation to drop a column from a table
     */
    @Getter
    @RequiredArgsConstructor
    public static class DropColumn implements Operation {
        private final String columnName;
    }

    /**
     * Operation to modify a column (type, charset, collation, nullability)
     */
    @Getter
    @AllArgsConstructor
    public static class ModifyColumn implements Operation {
        private final String columnName;
        private final EntityColumn columnDefinition;
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
    }
    

    /**
     * Operation to rename a column
     */
    @Getter
    @AllArgsConstructor
    public static class RenameColumn implements Operation {
        private String columnName;
        private String newColumnName;
    }

    /**
     * Operation to add an index to a table
     */
    @Getter
    @AllArgsConstructor
    public static class AddIndex implements Operation {
        private String indexName;
        private List<String> columns;
    }

    /**
     * Operation to drop an index from a table
     */
    @Getter
    @AllArgsConstructor
    public static class DropIndex implements Operation {
        private final String indexName;
    }

    /**
     * Operation to rename a table
     */
    @Getter
    @AllArgsConstructor
    public static class RenameTable implements Operation {
        private String newTableName;
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


