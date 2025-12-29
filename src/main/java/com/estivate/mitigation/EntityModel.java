package com.estivate.mitigation;

import java.util.ArrayList;
import java.util.List;

import com.estivate.index.Annotations.TableIndex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityModel {

    /** The table name in the database */
    public String tableName;

    /** The entity class (null for database models) */
    public Class<?> entityClass;

    /** List of table indexes */
    @Builder.Default
    public List<TableIndex> indexes = new ArrayList<>();
    
    /** List of table fields/columns */
    @Builder.Default
    public List<TableField> fields = new ArrayList<>();

    /**
     * Finds a field by name (case-insensitive)
     */
    public TableField findField(String name) {
        return fields.stream()
            .filter(f -> f.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if a field with the given name exists
     */
    public boolean hasField(String name) {
        return findField(name) != null;
    }

}
