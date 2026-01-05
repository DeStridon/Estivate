package com.estivate.reconciliation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.reconciliation.ISchemaDiff.SchemaDiff;
import com.estivate.util.FieldUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Compares entity definitions in code with actual database table structures.
 * Useful for detecting schema drift and migration issues.
 */
@Slf4j
public class ReconciliationManager {

    final Context context;
    final Class<?> entity;

    @Getter
    EntityModel entityModel;

    @Getter
    EntityModel databaseModel;

    @Getter
    List<SchemaDiff> differences;

    public ReconciliationManager(Context context, Class<?> entity) {
        this.context = context;
        this.entity = entity;

        // Scan entity fields from code
        this.entityModel = scanEntityFields();

        // Get table information from database
        this.databaseModel = scanDatabaseTable();

        // Compare both
        this.differences = compare();
    }

    /**
     * Scans the entity class and builds an EntityModel from its fields
     */
    private EntityModel scanEntityFields() {
        EntityModel model = EntityModel.builder()
            .tableName(context.nameMapper.toTableName(entity))
            .entityClass(entity)
            .build();

        Set<Field> fields = FieldUtils.getEntityFields(entity);
        
        for (Field field : fields) {
            TableField tableField = TableField.builder()
                .name(field.getName())
                .type(javaTypeToSqlType(field))
                .nullable(isNullable(field))
                .autoIncrement(isAutoIncrement(field))
                .build();

            model.getFields().add(tableField);
        }

        return model;
    }

    /**
     * Queries the database to get the actual table structure
     */
    @SneakyThrows
    private EntityModel scanDatabaseTable() {
        EntityModel model = EntityModel.builder()
            .tableName(context.nameMapper.toTableName(entity))
            .build();

        try (Connection connection = context.datasource.getConnection();
             Statement statement = new Statement(context, connection)) {

            statement.appendQuery("SHOW COLUMNS FROM ").appendQuery(context.nameMapper.toTableName(entity));
            
            try (ResultSet resultSet = statement.executeForResultSet()) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("Field");
                    String columnType = resultSet.getString("Type");
                    String nullableStr = resultSet.getString("Null");
                    String keyStr = resultSet.getString("Key");
                    String defaultValue = resultSet.getString("Default");
                    String extra = resultSet.getString("Extra");

                    // Convert database column name back to entity field name
                    String fieldName = context.findEntityName(entity, columnName);
                    if (fieldName == null) {
                        fieldName = columnName; // Keep original if no match found
                    }

                    TableField tableField = TableField.builder()
                        .name(fieldName)
                        .type(columnType)
                        .nullable("YES".equalsIgnoreCase(nullableStr))
                        .autoIncrement(extra != null && extra.toLowerCase().contains("auto_increment"))
                        .defaultValue(defaultValue)
                        .length(extractLength(columnType))
                        .build();

                    model.getFields().add(tableField);
                }
            }
        }

        return model;
    }

    /**
     * Compares entity model with database model and returns all differences as ISchemaDiff objects
     */
    private List<SchemaDiff> compare() {
        List<SchemaDiff> diffs = new ArrayList<>();
        String tableName = entityModel.getTableName();

        // Check for fields in entity but not in database
        for (TableField entityField : entityModel.getFields()) {
            TableField dbField = databaseModel.findField(entityField.getName());
            
            if (dbField == null) {
                // Also check by mapped database column name
                String dbColumnName = context.nameMapper.mapDatabaseField(entityField.getName());
                dbField = databaseModel.getFields().stream()
                    .filter(f -> dbColumnName.equalsIgnoreCase(context.nameMapper.mapDatabaseField(f.getName())))
                    .findFirst()
                    .orElse(null);
            }

            if (dbField == null) {
                // Column missing in database
                ISchemaDiff.ColumnMissing columnMissing = new ISchemaDiff.ColumnMissing();
                columnMissing.tableName = tableName;
                columnMissing.columnName = context.nameMapper.mapDatabaseField(entityField.getName());
                diffs.add(columnMissing);
            } else {
                // Check type mismatch
                if (!entityField.typeMatches(dbField.getType())) {
                    ISchemaDiff.ColumnTypeMismatch typeMismatch = new ISchemaDiff.ColumnTypeMismatch();
                    typeMismatch.tableName = tableName;
                    typeMismatch.columnName = context.nameMapper.mapDatabaseField(entityField.getName());
                    typeMismatch.entityType = entityField.getType();
                    typeMismatch.databaseType = dbField.getType();
                    diffs.add(typeMismatch);
                }
                
                // Check nullable mismatch
                if (entityField.isNullable() != dbField.isNullable()) {
                    ISchemaDiff.ColumnNullableMismatch nullableMismatch = new ISchemaDiff.ColumnNullableMismatch();
                    nullableMismatch.tableName = tableName;
                    nullableMismatch.columnName = context.nameMapper.mapDatabaseField(entityField.getName());
                    nullableMismatch.entityNullable = entityField.isNullable();
                    nullableMismatch.databaseNullable = dbField.isNullable();
                    diffs.add(nullableMismatch);
                }
                
                // Check length mismatch (if both have lengths)
                if (entityField.getLength() != null && dbField.getLength() != null) {
                    if (!entityField.getLength().equals(dbField.getLength())) {
                        ISchemaDiff.ColumnLengthMismatch lengthMismatch = new ISchemaDiff.ColumnLengthMismatch();
                        lengthMismatch.tableName = tableName;
                        lengthMismatch.columnName = context.nameMapper.mapDatabaseField(entityField.getName());
                        lengthMismatch.entityLength = entityField.getLength();
                        lengthMismatch.databaseLength = dbField.getLength();
                        diffs.add(lengthMismatch);
                    }
                }
                
                // Check default value mismatch
                String entityDefault = entityField.getDefaultValue();
                String dbDefault = dbField.getDefaultValue();
                if (entityDefault != null || dbDefault != null) {
                    // Compare defaults (handle null vs empty string)
                    boolean defaultsMatch = (entityDefault == null && (dbDefault == null || dbDefault.isEmpty())) ||
                                           (dbDefault == null && (entityDefault == null || entityDefault.isEmpty())) ||
                                           (entityDefault != null && entityDefault.equals(dbDefault));
                    if (!defaultsMatch) {
                        ISchemaDiff.ColumnDefaultValueMismatch defaultValueMismatch = new ISchemaDiff.ColumnDefaultValueMismatch();
                        defaultValueMismatch.tableName = tableName;
                        defaultValueMismatch.columnName = context.nameMapper.mapDatabaseField(entityField.getName());
                        defaultValueMismatch.entityDefaultValue = entityDefault;
                        defaultValueMismatch.databaseDefaultValue = dbDefault;
                        diffs.add(defaultValueMismatch);
                    }
                }
            }
        }

        // Check for columns in database but not in entity
        // We need to check against the actual database column names
        // Since databaseModel stores entity field names (after conversion), we need to map back
        for (TableField dbField : databaseModel.getFields()) {
            TableField entityField = entityModel.findField(dbField.getName());
            
            // Also check by mapped database column name
            if (entityField == null) {
                String dbColumnName = context.nameMapper.mapDatabaseField(dbField.getName());
                entityField = entityModel.getFields().stream()
                    .filter(f -> dbColumnName.equalsIgnoreCase(context.nameMapper.mapDatabaseField(f.getName())))
                    .findFirst()
                    .orElse(null);
            }
            
            if (entityField == null) {
                // Column missing in entity
                // dbField.getName() could be either:
                // 1. Entity field name (if findEntityName found a match) -> need to map to DB column name
                // 2. Database column name (if findEntityName returned null) -> use directly
                // Check if dbField.getName() exists as an entity field to determine which case
                String columnName;
                if (entityModel.hasField(dbField.getName())) {
                    // It's an entity field name, map it to get the database column name
                    columnName = context.nameMapper.mapDatabaseField(dbField.getName());
                } else {
                    // It's already a database column name (no entity field match was found)
                    columnName = dbField.getName();
                }
                
                ISchemaDiff.ColumnMissing columnMissing = new ISchemaDiff.ColumnMissing();
                columnMissing.tableName = tableName;
                columnMissing.columnName = columnName;
                diffs.add(columnMissing);
            }
        }

        return diffs;
    }

   

    // ==================== Helper Methods ====================

    /**
     * Converts Java field type to SQL type string
     */
    private String javaTypeToSqlType(Field field) {
        Class<?> type = field.getType();

        // Check for @Convert annotation
        if (field.getDeclaredAnnotation(javax.persistence.Convert.class) != null ||
            field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
            return "VARCHAR";
        }

        // Handle enums
        if (type.isEnum()) {
            if (isEnumeratedAsString(field)) {
                return "VARCHAR";
            }
            return "INT";
        }

        // Primitive types and wrappers
        if (type == Integer.class || type == int.class) return "INT";
        if (type == Long.class || type == long.class) return "BIGINT";
        if (type == Short.class || type == short.class) return "SMALLINT";
        if (type == Byte.class || type == byte.class) return "TINYINT";
        if (type == Float.class || type == float.class) return "FLOAT";
        if (type == Double.class || type == double.class) return "DOUBLE";
        if (type == Boolean.class || type == boolean.class) return "BOOLEAN";
        if (type == String.class) return "VARCHAR";
        if (type == Date.class || type == java.sql.Date.class) return "DATETIME";
        if (type == LocalDateTime.class) return "DATETIME";
        if (type == LocalDate.class) return "DATE";
        if (type == byte[].class) return "BLOB";

        return "VARCHAR"; // Default fallback
    }

    /**
     * Checks if a field is nullable based on annotations
     */
    private boolean isNullable(Field field) {
        // Primitives are never nullable
        if (field.getType().isPrimitive()) {
            return false;
        }

        // Check for @Column(nullable = false)
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null) {
            return javaxColumn.nullable();
        }
        
        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null) {
            return jakartaColumn.nullable();
        }

       

        // Default to nullable for object types
        return true;
    }

    

    /**
     * Checks if a field is auto-increment
     */
    private boolean isAutoIncrement(Field field) {
        javax.persistence.GeneratedValue javaxGenerated = 
            field.getDeclaredAnnotation(javax.persistence.GeneratedValue.class);
        if (javaxGenerated != null && javaxGenerated.strategy() == javax.persistence.GenerationType.IDENTITY) {
            return true;
        }

        jakarta.persistence.GeneratedValue jakartaGenerated = 
            field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class);
        if (jakartaGenerated != null && jakartaGenerated.strategy() == jakarta.persistence.GenerationType.IDENTITY) {
            return true;
        }

        return false;
    }

    /**
     * Checks if an enum field is stored as STRING
     */
    private boolean isEnumeratedAsString(Field field) {
        javax.persistence.Enumerated javaxEnum = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
        if (javaxEnum != null && javaxEnum.value() == javax.persistence.EnumType.STRING) {
            return true;
        }

        jakarta.persistence.Enumerated jakartaEnum = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
        if (jakartaEnum != null && jakartaEnum.value() == jakarta.persistence.EnumType.STRING) {
            return true;
        }

        return false;
    }

    /**
     * Extracts length from SQL type (e.g., VARCHAR(255) -> 255)
     */
    private Integer extractLength(String sqlType) {
        if (sqlType == null || !sqlType.contains("(")) {
            return null;
        }
        try {
            int start = sqlType.indexOf("(") + 1;
            int end = sqlType.indexOf(")");
            if (end > start) {
                String lengthStr = sqlType.substring(start, end);
                // Handle cases like DECIMAL(10,2)
                if (lengthStr.contains(",")) {
                    lengthStr = lengthStr.split(",")[0];
                }
                return Integer.parseInt(lengthStr.trim());
            }
        } catch (NumberFormatException e) {
            // Ignore parsing errors
        }
        return null;
    }

}
