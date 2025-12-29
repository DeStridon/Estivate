package com.estivate.mitigation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a difference between an entity field and a database column
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDiff {

    public enum DiffType {
        /** Field exists in entity but not in database */
        MISSING_IN_DATABASE,
        /** Column exists in database but not in entity */
        MISSING_IN_ENTITY,
        /** Type mismatch between entity and database */
        TYPE_MISMATCH,
        /** Nullable mismatch between entity and database */
        NULLABLE_MISMATCH
    }

    /** The type of difference */
    public DiffType diffType;

    /** The field name */
    public String fieldName;

    /** Field definition from entity (null if missing in entity) */
    public TableField entityField;

    /** Field definition from database (null if missing in database) */
    public TableField databaseField;

    /** Human-readable description of the difference */
    public String getDescription() {
        switch (diffType) {
            case MISSING_IN_DATABASE:
                return "Field '" + fieldName + "' exists in entity but not in database table";
            case MISSING_IN_ENTITY:
                return "Column '" + fieldName + "' exists in database but not in entity";
            case TYPE_MISMATCH:
                return "Type mismatch for '" + fieldName + "': entity=" + 
                    (entityField != null ? entityField.getType() : "null") + 
                    ", database=" + (databaseField != null ? databaseField.getType() : "null");
            case NULLABLE_MISMATCH:
                return "Nullable mismatch for '" + fieldName + "': entity=" + 
                    (entityField != null ? entityField.isNullable() : "null") + 
                    ", database=" + (databaseField != null ? databaseField.isNullable() : "null");
            default:
                return "Unknown difference for field '" + fieldName + "'";
        }
    }

    public static FieldDiff missingInDatabase(TableField entityField) {
        return FieldDiff.builder()
            .diffType(DiffType.MISSING_IN_DATABASE)
            .fieldName(entityField.getName())
            .entityField(entityField)
            .build();
    }

    public static FieldDiff missingInEntity(TableField databaseField) {
        return FieldDiff.builder()
            .diffType(DiffType.MISSING_IN_ENTITY)
            .fieldName(databaseField.getName())
            .databaseField(databaseField)
            .build();
    }

    public static FieldDiff typeMismatch(TableField entityField, TableField databaseField) {
        return FieldDiff.builder()
            .diffType(DiffType.TYPE_MISMATCH)
            .fieldName(entityField.getName())
            .entityField(entityField)
            .databaseField(databaseField)
            .build();
    }

    public static FieldDiff nullableMismatch(TableField entityField, TableField databaseField) {
        return FieldDiff.builder()
            .diffType(DiffType.NULLABLE_MISMATCH)
            .fieldName(entityField.getName())
            .entityField(entityField)
            .databaseField(databaseField)
            .build();
    }

}

