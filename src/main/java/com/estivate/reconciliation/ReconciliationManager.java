package com.estivate.reconciliation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.AlterQuery;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationDelta;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationScope;
import com.estivate.util.FieldUtils;
import com.estivate.util.ReflectionUtils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Compares entity definitions in code with actual database table structures.
 * Useful for detecting schema drift and migration issues.
 * 
 * Can be initialized with:
 * - A list of package names to scan for @Entity annotated classes
 * - A single entity class (for backward compatibility)
 */
@Slf4j
public class ReconciliationManager {

    final Context context;
    final List<String> packageNames;
    
    @Getter
    final List<Class<?>> entityClasses;

    @Getter
    Map<Class<?>, EntityModel> entityModels = new LinkedHashMap<>();

    @Getter
    Map<Class<?>, EntityModel> databaseModels = new LinkedHashMap<>();

    @Getter
    List<ReconciliationDelta> differences;

    /**
     * Creates a ReconciliationManager that scans the specified packages for entity classes.
     * 
     * @param context The database context
     * @param packageNames Package names to scan for @Entity annotated classes
     */
    public ReconciliationManager(Context context, String... packageNames) {
        this(context, Arrays.asList(packageNames));
    }

    /**
     * Creates a ReconciliationManager that scans the specified packages for entity classes.
     * 
     * @param context The database context
     * @param packageNames List of package names to scan for @Entity annotated classes
     */
    public ReconciliationManager(Context context, List<String> packageNames) {
        this.context = context;
        this.packageNames = packageNames;
        
        // Scan packages for entity classes
        this.entityClasses = ReflectionUtils.scanPackagesForEntities(packageNames);
        
        log.info("Found {} entity classes in packages: {}", entityClasses.size(), packageNames);
        
        // Process each entity
        for (Class<?> entityClass : entityClasses) {
            // Scan entity fields from code
            EntityModel entityModel = scanEntityFields(entityClass);
            entityModels.put(entityClass, entityModel);

            // Get table information from database
            EntityModel databaseModel = scanDatabaseTable(entityClass);
            databaseModels.put(entityClass, databaseModel);
        }

        // Compare all entities
        this.differences = compareAll();
    }
    
    /**
     * Creates a ReconciliationManager for a single entity class.
     * Kept for backward compatibility.
     * 
     * @param context The database context
     * @param entityClass The entity class to reconcile
     */
    public static ReconciliationManager forEntity(Context context, Class<?> entityClass) {
        ReconciliationManager manager = new ReconciliationManager(context, new ArrayList<>());
        manager.entityClasses.add(entityClass);
        
        EntityModel entityModel = manager.scanEntityFields(entityClass);
        manager.entityModels.put(entityClass, entityModel);
        
        EntityModel databaseModel = manager.scanDatabaseTable(entityClass);
        manager.databaseModels.put(entityClass, databaseModel);
        
        manager.differences = manager.compareAll();
        
        return manager;
    }
    
   
    
    
    /**
     * Returns differences for a specific entity class
     */
    public List<ReconciliationDelta> getDifferences(Class<?> entityClass) {
        return differences.stream()
            .filter(diff -> {
                Class<?> diffEntityClass = extractEntityClass(diff);
                return diffEntityClass != null && diffEntityClass.equals(entityClass);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Extracts the entity class from a ReconciliationDelta
     */
    private Class<?> extractEntityClass(ReconciliationDelta diff) {
        try {
            Field entityClassField = diff.getClass().getDeclaredField("entityClass");
            entityClassField.setAccessible(true);
            return (Class<?>) entityClassField.get(diff);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Scans the entity class and builds an EntityModel from its fields
     */
    private EntityModel scanEntityFields(Class<?> entityClass) {
        EntityModel model = EntityModel.builder()
            .tableName(context.nameMapper.toTableName(entityClass))
            .entityClass(entityClass)
            .build();

        Set<Field> fields = FieldUtils.getEntityFields(entityClass);
        
        for (Field field : fields) {
            String sqlType = javaTypeToSqlType(field);
            Integer length = extractLengthFromField(field, sqlType);
            
            TableField tableField = TableField.builder()
                .name(field.getName())
                .type(sqlType)
                .nullable(isNullable(field))
                .autoIncrement(isAutoIncrement(field))
                .length(length)
                .defaultValue(extractDefaultValue(field))
                .build();

            model.getFields().add(tableField);
        }

        return model;
    }

    /**
     * Queries the database to get the actual table structure
     */
    @SneakyThrows
    private EntityModel scanDatabaseTable(Class<?> entityClass) {
        EntityModel model = EntityModel.builder()
            .tableName(context.nameMapper.toTableName(entityClass))
            .build();

        try (Connection connection = context.datasource.getConnection();
             Statement statement = new Statement(context, connection)) {

            statement.appendQuery("SHOW COLUMNS FROM ").appendQuery(context.nameMapper.toTableName(entityClass));
            
            try (ResultSet resultSet = statement.executeForResultSet()) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("Field");
                    String columnType = resultSet.getString("Type");
                    String nullableStr = resultSet.getString("Null");
                    String keyStr = resultSet.getString("Key");
                    String defaultValue = resultSet.getString("Default");
                    // H2 doesn't have "Extra" column, so we need to handle it gracefully
                    String extra = null;
                    try {
                        extra = resultSet.getString("Extra");
                    } catch (Exception e) {
                        // H2 doesn't support Extra column, check auto_increment from column type or other means
                        // For H2, we can check if the column type contains AUTO_INCREMENT or check the default
                    }

                    // Convert database column name back to entity field name
                    String fieldName = context.findEntityName(entityClass, columnName);
                    if (fieldName == null) {
                        fieldName = columnName; // Keep original if no match found
                    }

                    TableField tableField = TableField.builder()
                        .name(fieldName)
                        .type(extractColumnType(columnType))
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
     * Compares all entity models with their database models and returns all differences
     */
    private List<ReconciliationDelta> compareAll() {
        List<ReconciliationDelta> allDiffs = new ArrayList<>();
        
        for (Class<?> entityClass : entityClasses) {
            EntityModel entityModel = entityModels.get(entityClass);
            EntityModel databaseModel = databaseModels.get(entityClass);
            
            if (entityModel != null && databaseModel != null) {
                List<ReconciliationDelta> entityDiffs = compare(entityClass, entityModel, databaseModel);
                allDiffs.addAll(entityDiffs);
            }
        }
        
        return allDiffs;
    }

    /**
     * Compares entity model with database model and returns all differences as ISchemaDiff objects
     */
    private List<ReconciliationDelta> compare(Class<?> entityClass, EntityModel entityModel, EntityModel databaseModel) {
        List<ReconciliationDelta> diffs = new ArrayList<>();

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
                // Column missing in database - needs to be added
                AlterQuery.ColumnDefinition columnDef = new AlterQuery.ColumnDefinition(
                    entityField.getType(),
                    entityField.getLength(),
                    entityField.isNullable(),
                    entityField.getDefaultValue(),
                    entityField.isAutoIncrement(),
                    null, // charset not available from entity field
                    null  // collation not available from entity field
                );
                
                EstivateReconciliation.AddColumnDelta addColumn = EstivateReconciliation.AddColumnDelta.builder()
                    .entityClass(entityClass)
                    .entityFieldName(entityField.getName())
                    .entityColumnDefinition(columnDef)
                    .build();
                diffs.add(addColumn);
            } 
            else {
                // Check for any column definition mismatches
                boolean hasTypeMismatch = !entityField.typeMatches(dbField.getType());
                boolean hasNullableMismatch = entityField.isNullable() != dbField.isNullable();
                boolean hasLengthMismatch = entityField.getLength() != null && dbField.getLength() != null 
                    && !entityField.getLength().equals(dbField.getLength());
                
                String entityDefault = entityField.getDefaultValue();
                String dbDefault = dbField.getDefaultValue();
                boolean hasDefaultMismatch = false;
                if (entityDefault != null || dbDefault != null) {
                    boolean defaultsMatch = (entityDefault == null && (dbDefault == null || dbDefault.isEmpty())) ||
                                           (dbDefault == null && (entityDefault == null || entityDefault.isEmpty())) ||
                                           (entityDefault != null && entityDefault.equals(dbDefault));
                    hasDefaultMismatch = !defaultsMatch;
                }
                
                if (hasTypeMismatch || hasNullableMismatch || hasLengthMismatch || hasDefaultMismatch) {
                    AlterQuery.ColumnDefinition entityDef = new AlterQuery.ColumnDefinition(
                        entityField.getType(),
                        entityField.getLength(),
                        entityField.isNullable(),
                        entityField.getDefaultValue(),
                        entityField.isAutoIncrement(),
                        null, // charset
                        null  // collation
                    );
                    
                    AlterQuery.ColumnDefinition dbDef = new AlterQuery.ColumnDefinition(
                        dbField.getType(),
                        dbField.getLength(),
                        dbField.isNullable(),
                        dbField.getDefaultValue(),
                        dbField.isAutoIncrement(),
                        null, // charset
                        null  // collation
                    );
                    
                    EstivateReconciliation.ModifyColumnDelta modifyColumn = EstivateReconciliation.ModifyColumnDelta.builder()
                        .entityClass(entityClass)
                        .entityFieldName(entityField.getName())
                        .entityDefinition(entityDef)
                        .databaseDefinition(dbDef)
                        .build();
                    diffs.add(modifyColumn);
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
                // Column exists in database but not in entity - needs to be removed
                String columnName = dbField.getName();
                
                AlterQuery.ColumnDefinition columnDef = new AlterQuery.ColumnDefinition(
                    dbField.getType(),
                    dbField.getLength(),
                    dbField.isNullable(),
                    dbField.getDefaultValue(),
                    dbField.isAutoIncrement(),
                    null, // charset not available from SHOW COLUMNS
                    null  // collation not available from SHOW COLUMNS
                );
                
                EstivateReconciliation.DropColumnDelta dropColumn = EstivateReconciliation.DropColumnDelta.builder()
                    .entityClass(entityClass)
                    .tableColumnName(columnName)
                    .build();
                diffs.add(dropColumn);
            }
        }

        return diffs;
    }

    // ==================== Resolver Discovery ====================


    public static final Comparator<Object> handlesDiffComparator = (objectA, objectB) -> {

        ReconciliationScope a = objectA == null ? null : objectA.getClass().getAnnotation(ReconciliationScope.class);
        ReconciliationScope b = objectB == null ? null : objectB.getClass().getAnnotation(ReconciliationScope.class);

        return or(
            compare(a == null, b == null),
            compare(StringUtils.isBlank(a.table()), StringUtils.isBlank(b.table())),
            compare(StringUtils.isBlank(a.column()), StringUtils.isBlank(b.column()))
        ).orElse(0);
        
        
    };

    private static Optional<Integer> or(Optional<Integer>... values){
        for(Optional<Integer> value : values){
            if(value.isPresent()){
                return value;
            }
        }
        return Optional.empty();
    }

    // 
    private static Optional<Integer> compare(boolean a, boolean b) {
        if(a && b){ return Optional.of(0); }
        // a true : a is more specific
        if (a && !b) { return Optional.of(1); }
        // b true : b is more specific
        if (!a && b) { return Optional.of(-1);}
        // both false
        return Optional.empty();
    }


    /**
     * Finds resolver classes that can handle the given diff.
     * Resolvers must:
     * - Implement the appropriate resolver interface (e.g., IAddColumnDeltaResolver)
     * - Have @ReconciliationScope annotation
     * - Match the table/column criteria from the annotation
     * 
     * Results are sorted from most specific to most generic:
     * 1. Table AND Field (most specific)
     * 2. Table only
     * 3. None
     * 4. Null (no annotation)
     * 
     * @param diff The schema diff to find resolvers for
     * @param candidates Collection of potential resolver classes to search through
     * @return List of matching resolvers, sorted from most specific to most generic
     */
    public <T> List<T> findResolver(ReconciliationDelta diff, Collection<T> candidates) {
        // Extract table and column from the diff
        String diffTable = extractTableName(diff);
        String diffColumn = extractColumnName(diff);
        
        return candidates.stream()
            .filter(candidate -> hasMatchingHandlesDiff(candidate.getClass(), diffTable, diffColumn))
            .sorted(ReconciliationManager.handlesDiffComparator)
            .collect(Collectors.toList());
    }

    /**
     * Checks if a class has @ReconciliationScope annotation that matches the given diff
     */
    private boolean hasMatchingHandlesDiff(Class<?> candidateClass, String diffTable, String diffColumn) {
        ReconciliationScope annotation = candidateClass.getAnnotation(ReconciliationScope.class);
        if (annotation == null) {
            return false;
        }
        
        // Check table match
        String table = annotation.table();
        if (table != null && !table.equals("") && !table.equals(diffTable)) {
            return false;
        }
        
        // Check column match
        String columns = annotation.column();
        if (columns != null && !columns.equals("") && !columns.equals(diffColumn)) {
            return false;
        }
        
        return true;
    }

    

    // ==================== Resolver Application ====================

    /**
     * Result of applying resolvers to schema differences.
     */
    @Getter
    public static class ApplyResolversResult {
        private final List<ReconciliationDelta> resolved = new ArrayList<>();
        private final List<ReconciliationDelta> unresolved = new ArrayList<>();
        
        public boolean isFullyResolved() {
            return unresolved.isEmpty();
        }
        
        public int totalDiffs() {
            return resolved.size() + unresolved.size();
        }
    }

    /**
     * Applies resolvers to all schema differences.
     * For each diff, finds matching resolvers sorted by specificity (most specific first),
     * and applies the first resolver that successfully handles the diff.
     * 
     * @param candidates Collection of resolver objects to search through
     * @return ApplyResolversResult containing resolved and unresolved diffs
     */
    public ApplyResolversResult applyResolvers(Collection<Object> candidates) {
        ApplyResolversResult result = new ApplyResolversResult();
        
        for (ReconciliationDelta diff : differences) {
            List<Object> resolvers = findResolver(diff, candidates);
            
            boolean resolved = false;
            for (Object resolver : resolvers) {
                if (tryApplyResolver(resolver, diff)) {
                    result.resolved.add(diff);
                    resolved = true;
                    break;
                }
            }
            
            if (!resolved) {
                result.unresolved.add(diff);
            }
        }
        
        return result;
    }

    /**
     * Attempts to apply a resolver to a schema diff.
     * Checks the diff type and resolver interface compatibility, then invokes the resolver.
     * 
     * @param resolver The resolver object to use
     * @param diff The schema diff to resolve
     * @return true if the resolver successfully handled the diff, false otherwise
     */
    private boolean tryApplyResolver(Object resolver, ReconciliationDelta diff) {
        try {
            if (diff instanceof EstivateReconciliation.CreateTableDelta && resolver instanceof EstivateReconciliation.ICreateTableResolver) {
                ((EstivateReconciliation.ICreateTableResolver) resolver).resolve(context, (EstivateReconciliation.CreateTableDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.AddColumnDelta && resolver instanceof EstivateReconciliation.IAddColumnResolver) {
                ((EstivateReconciliation.IAddColumnResolver) resolver).resolve(context, (EstivateReconciliation.AddColumnDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.ModifyColumnDelta && resolver instanceof EstivateReconciliation.IModifyColumnResolver) {
                ((EstivateReconciliation.IModifyColumnResolver) resolver).resolve(context, (EstivateReconciliation.ModifyColumnDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.DropTableDelta && resolver instanceof EstivateReconciliation.IDropTableResolver) {
                ((EstivateReconciliation.IDropTableResolver) resolver).resolve(context, (EstivateReconciliation.DropTableDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.DropColumnDelta && resolver instanceof EstivateReconciliation.IDropColumnResolver) {
                ((EstivateReconciliation.IDropColumnResolver) resolver).resolve(context, (EstivateReconciliation.DropColumnDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.AddIndexDelta && resolver instanceof EstivateReconciliation.IAddIndexResolver) {
                ((EstivateReconciliation.IAddIndexResolver) resolver).resolve(context, (EstivateReconciliation.AddIndexDelta) diff);
            }
            if (diff instanceof EstivateReconciliation.DropIndexDelta && resolver instanceof EstivateReconciliation.IDropIndexResolver) {
                ((EstivateReconciliation.IDropIndexResolver) resolver).resolve(context, (EstivateReconciliation.DropIndexDelta) diff);
            }
        } catch (Exception e) {
            log.warn("Resolver {} failed for diff {}: {}", resolver.getClass().getSimpleName(), diff, e.getMessage());
        }
        return false;
    }

    /**
     * Extracts table name from a SchemaDiff using reflection
     */
    private String extractTableName(ReconciliationDelta diff) {
        try {
            Field tableField = diff.getClass().getField("tableName");
            return (String) tableField.get(diff);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Extracts column name or index name from a SchemaDiff using reflection.
     * Checks for 'columnName' first, then 'indexName'.
     */
    private String extractColumnName(ReconciliationDelta diff) {
        // Try columnName first (used by AddColumnDelta, DropColumnDelta, ModifyColumnDelta)
        try {
            Field columnField = diff.getClass().getField("columnName");
            return (String) columnField.get(diff);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Try indexName (used by AddIndexDelta, DropIndexDelta)
            try {
                Field indexField = diff.getClass().getField("indexName");
                return (String) indexField.get(diff);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                return null;
            }
        }
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
        if (type == String.class) {
            javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
            jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
            if (javaxColumn != null || jakartaColumn != null) {
                String columnDef = javaxColumn != null ? javaxColumn.columnDefinition() : jakartaColumn.columnDefinition();
                if (columnDef != null && columnDef.trim().equalsIgnoreCase("text")) {
                    return "TEXT";
                }
            }
            return "VARCHAR";
        } 
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

        // Check for @NotNull (javax.validation / jakarta.validation)
        if (hasAnnotationByName(field, "javax.validation.constraints.NotNull") || hasAnnotationByName(field, "jakarta.validation.constraints.NotNull")) {
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

    private boolean hasAnnotationByName(Field field, String annotationClassName) {
        for (java.lang.annotation.Annotation a : field.getDeclaredAnnotations()) {
            if (annotationClassName.equals(a.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

    

    /**
     * Checks if a field is auto-increment
     */
    private boolean isAutoIncrement(Field field) {
        javax.persistence.GeneratedValue javaxGenerated = field.getDeclaredAnnotation(javax.persistence.GeneratedValue.class);
        if (javaxGenerated != null && javaxGenerated.strategy() == javax.persistence.GenerationType.IDENTITY) {
            return true;
        }

        jakarta.persistence.GeneratedValue jakartaGenerated = field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class);
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
     * Extracts length from entity field annotations (@Column, @Size, etc.)
     */
    private Integer extractLengthFromField(Field field, String sqlType) {
        // Check @Column(length = ...)
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null && javaxColumn.length() > 0) {
            return javaxColumn.length();
        }
        
        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null && jakartaColumn.length() > 0) {
            return jakartaColumn.length();
        }
        
        // For String fields, default to 255 if no annotation
        if (field.getType() == String.class && sqlType != null && sqlType.toUpperCase().contains("VARCHAR")) {
            return 255; // Default VARCHAR length
        }
        
        return null;
    }
    
    /**
     * Extracts default value from entity field annotations
     */
    private String extractDefaultValue(Field field) {
        // Check @Column(columnDefinition = ...) which might contain DEFAULT
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null && javaxColumn.columnDefinition() != null && !javaxColumn.columnDefinition().isEmpty()) {
            String columnDef = javaxColumn.columnDefinition();
            // Try to extract DEFAULT value from columnDefinition
            if (columnDef.toUpperCase().contains("DEFAULT")) {
                // This is a simplified extraction - may need refinement
                int defaultIndex = columnDef.toUpperCase().indexOf("DEFAULT");
                if (defaultIndex >= 0) {
                    String afterDefault = columnDef.substring(defaultIndex + 7).trim();
                    // Extract the value (handles quoted and unquoted values)
                    if (afterDefault.startsWith("'") && afterDefault.contains("'")) {
                        int endQuote = afterDefault.indexOf("'", 1);
                        return afterDefault.substring(1, endQuote);
                    } else {
                        // Unquoted value - take until space or end
                        int spaceIndex = afterDefault.indexOf(" ");
                        if (spaceIndex > 0) {
                            return afterDefault.substring(0, spaceIndex);
                        }
                        return afterDefault;
                    }
                }
            }
        }
        
        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null && jakartaColumn.columnDefinition() != null && !jakartaColumn.columnDefinition().isEmpty()) {
            String columnDef = jakartaColumn.columnDefinition();
            if (columnDef.toUpperCase().contains("DEFAULT")) {
                int defaultIndex = columnDef.toUpperCase().indexOf("DEFAULT");
                if (defaultIndex >= 0) {
                    String afterDefault = columnDef.substring(defaultIndex + 7).trim();
                    if (afterDefault.startsWith("'") && afterDefault.contains("'")) {
                        int endQuote = afterDefault.indexOf("'", 1);
                        return afterDefault.substring(1, endQuote);
                    } else {
                        int spaceIndex = afterDefault.indexOf(" ");
                        if (spaceIndex > 0) {
                            return afterDefault.substring(0, spaceIndex);
                        }
                        return afterDefault;
                    }
                }
            }
        }
        
        return null;
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

    private String extractColumnType(String columnType) {
        if(columnType != null && columnType.toUpperCase().startsWith("VARCHAR")) {
            return "VARCHAR";
        }
        return columnType;
    }

}
