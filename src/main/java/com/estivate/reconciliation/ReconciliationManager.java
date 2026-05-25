package com.estivate.reconciliation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.AlterQuery;
import com.estivate.reconciliation.ColumnModel.EntityColumn;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationDelta;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationResult;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationScope;
import com.estivate.util.FieldUtils;
import com.estivate.util.ReflectionUtils;

import lombok.Data;
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
    
    @Getter
    Set<Class<?>> entityClasses = new HashSet<>();

    @Getter
    List<Object> resolvers = new ArrayList<>();

    @Getter
    List<ReconciliationDelta> differences = new ArrayList<>();



    /**
     * Creates a ReconciliationManager that scans the specified packages for entity classes.
     * 
     * @param context The database context
     * @param packageNames List of package names to scan for @Entity annotated classes
     */
    public ReconciliationManager(Context context) {
        this.context = context;
    }

    public ReconciliationManager addEntitiesFromPackages(String... packageNames) {
        List<Class<?>> newEntities = ReflectionUtils.scanPackagesForEntities(Arrays.asList(packageNames));
        this.entityClasses.addAll(newEntities);
        scanAllEntities();
        return this;
    }

    public ReconciliationManager addEntities( Class<?>... entityClasses) {
        this.entityClasses.addAll(Arrays.asList(entityClasses));
        scanAllEntities();
        return this;
    }

    public ReconciliationManager addResolvers(String... resolverPackageNames) {
        List<Class<?>> resolverClasses = ReflectionUtils.scanPackagesForAnnotatedClasses(resolverPackageNames, ReconciliationScope.class);
        List<Object> resolverInstances = new ArrayList<>();
        for (Class<?> resolverClass : resolverClasses) {
            try {
                resolverInstances.add(resolverClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.warn("Could not instantiate resolver class {}: {}", resolverClass.getName(), e.getMessage());
            }
        }
        this.resolvers.addAll(resolverInstances);
        return this;
    }

    public ReconciliationManager addResolvers(Class<?>... resolverClasses) {
        List<Object> resolverInstances = new ArrayList<>();
        for (Class<?> resolverClass : resolverClasses) {
            try {
                resolverInstances.add(resolverClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                log.warn("Could not instantiate resolver class {}: {}", resolverClass.getName(), e.getMessage());
            }
        }
        this.resolvers.addAll(resolverInstances);
        return this;
    }

    public ReconciliationManager addResolvers(Object... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
        return this;
    }


    private void scanAllEntities() {

        List<String> databaseTables = context.showTables();
        // Process each entity
        for (Class<?> entityClass : entityClasses) {
            // Scan entity fields from code
            // EntityModel entityModel = scanEntityFields(entityClass);
            // entityModels.put(entityClass, entityModel);

            String tableName = context.nameMapper.toTableName(entityClass);
            if(!databaseTables.contains(tableName)) {
                EstivateReconciliation.CreateTableDelta createTable = EstivateReconciliation.CreateTableDelta.builder()
                    .entityClass(entityClass)
                    .build();
                differences.add(createTable);
                continue;
            }

            databaseTables.remove(tableName);
            EntityModel databaseModel = scanDatabaseTable(tableName);

            List<ReconciliationDelta> entityDiffs = compare(entityClass, databaseModel);
            differences.addAll(entityDiffs);

        }

        // Tables that are in database but not in entity classes
        for(String tableName : databaseTables) {
            EstivateReconciliation.DropTableDelta dropTable = EstivateReconciliation.DropTableDelta.builder()
                .tableName(tableName)
                .build();
            differences.add(dropTable);
        }

        this.differences.stream().forEach(x -> x.setCurrentDeltas(differences));

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
     * Queries the database to get the actual table structure
     */
    @SneakyThrows
    private EntityModel scanDatabaseTable(String tableName) {
        EntityModel model = EntityModel.builder()
            .tableName(tableName)
            .build();

        try (Connection connection = context.datasource.getConnection();
             Statement statement = new Statement(context, connection)) {

            statement.appendQuery("SHOW COLUMNS FROM ").appendQuery(tableName);
            
            try (ResultSet resultSet = statement.executeForResultSet()) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("Field");
                    String columnType = resultSet.getString("Type");
                    String nullableStr = resultSet.getString("Null");
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


                    TableField tableField = TableField.builder()
                        .name(columnName)
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
     * Compares entity model with database model and returns all differences as ISchemaDiff objects
     */
    private List<ReconciliationDelta> compare(Class<?> entityClass, EntityModel tableModel) {
        List<ReconciliationDelta> diffs = new ArrayList<>();
        List<String> projectedFieldNames = new ArrayList<>();

        // Check for fields in entity but not in database
        for (Field entityField : FieldUtils.getEntityFields(entityClass)) {

            
            EntityColumn entityColumn = context.getEntityColumn(entityField);
            ColumnModel.ColumnFormat columnFormat = context.getColumnFormat(entityColumn);
            TableField tableField = context.getTableField(entityField);
            TableField dbColumn = tableModel.findField(entityField.getName());

            if (dbColumn == null) {
                // Also check by mapped database column name
                String dbColumnName = context.nameMapper.mapDatabaseField(entityField.getName());
                dbColumn = tableModel.getFields().stream()
                    .filter(f -> dbColumnName.equalsIgnoreCase(context.nameMapper.mapDatabaseField(f.getName())))
                    .findFirst()
                    .orElse(null);
            }

            if (dbColumn == null) {
                // Column missing in database - needs to be added
                
                EstivateReconciliation.AddColumnDelta addColumn = EstivateReconciliation.AddColumnDelta.builder()
                    .entityClass(entityClass)
                    .entityField(entityField)
                    .tableColumnName(context.nameMapper.mapDatabaseClass(entityClass))
                    .entityColumnDefinition(entityColumn)
                    .tableModel(tableModel)
                    .build();
                diffs.add(addColumn);
            } 
            else {
                // Check for any column definition mismatches
                boolean hasTypeMismatch = !tableField.typeMatches(dbColumn.getType());
                boolean hasNullableMismatch = tableField.isNullable() != dbColumn.isNullable();
                boolean hasLengthMismatch = tableField.getLength() != null && dbColumn.getLength() != null 
                    && !tableField.getLength().equals(dbColumn.getLength());
                
                String entityDefault = tableField.getDefaultValue() == null ? "NULL" : tableField.getDefaultValue();
                String dbDefault = dbColumn.getDefaultValue() == null ? "NULL" : dbColumn.getDefaultValue();
                boolean hasDefaultMismatch = !entityDefault.equals(dbDefault);
                
                if (hasTypeMismatch || hasNullableMismatch || hasLengthMismatch || hasDefaultMismatch) {
                    
                    

                    
                    EstivateReconciliation.ModifyColumnDelta modifyColumn = EstivateReconciliation.ModifyColumnDelta.builder()
                        .entityClass(entityClass)
                        .entityField(entityField)
                        //.tableColumnName(projectedField.getName())
                        .projectedDefinition(tableField)
                        .databaseDefinition(dbColumn)
                        .build();
                    diffs.add(modifyColumn);
                }
            }
        }

        // Check for columns in database but not in entity
        // We need to check against the actual database column names
        // Since databaseModel stores entity field names (after conversion), we need to map back
        for (TableField dbField : tableModel.getFields()) {
            // If found in known fields, skip
            if(projectedFieldNames.contains(dbField.getName())) {
                continue;
            }
            
            // Column exists in database but not in entity - needs to be removed            
            EstivateReconciliation.DropColumnDelta dropColumn = EstivateReconciliation.DropColumnDelta.builder()
                .entityClass(entityClass)
                .tableColumnName(dbField.getName())
                .build();
            diffs.add(dropColumn);
            
        }

        return diffs;
    }

    // ==================== Resolver Discovery ====================





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
    public <T> T findBestResolver(ReconciliationDelta diff, Collection<T> candidates) throws Exception {

        
        List<T> matchingCandidates = candidates.stream()
            .filter(candidate -> isResolverMatchingDiff(candidate, diff))
            //.sorted(ReconciliationManager.handlesDiffComparator)
            .collect(Collectors.toList());

        // First, filter matchingCandidates for those with ReconciliationScope whose entity matches diff's entity class
        List<T> entityScoped = matchingCandidates.stream()
            .filter(candidate -> {
                ReconciliationScope scope = candidate.getClass().getAnnotation(ReconciliationScope.class);
                // Ensure annotation present and scope.entity is set and not void
                if (scope != null && scope.entity() != void.class) {
                    // Compare with diff's entity class, if available
                    Class<?> diffEntity = extractEntityClass(diff);
                    return diffEntity != null && scope.entity().equals(diffEntity);
                }
                return false;
            })
            .collect(Collectors.toList());

        if (entityScoped.size() == 1) {
            return entityScoped.get(0);
        } else if (entityScoped.size() > 1) {
            throw new Exception("Multiple resolvers with matching entity for diff: " + diff + ". Conflict.");
        }

        // Next, filter matchingCandidates for generic ReconciliationScope (entity == void.class)
        List<T> genericScoped = matchingCandidates.stream()
            .filter(candidate -> {
                ReconciliationScope scope = candidate.getClass().getAnnotation(ReconciliationScope.class);
                // entity is unset/generic
                return scope != null && scope.entity() == void.class;
            })
            .collect(Collectors.toList());

        if (genericScoped.size() == 1) {
            return genericScoped.get(0);
        } else if (genericScoped.size() > 1) {
            throw new Exception("Multiple generic resolvers for diff: " + diff + ". Conflict.");
        }

        // No resolver found
        return null;
            
    }

    // /**
    //  * Checks if a class has @ReconciliationScope annotation that matches the given diff
    //  */
    // private boolean hasMatchingHandlesDiff(Class<?> candidateClass, String diffTable, String diffColumn) {
    //     ReconciliationScope annotation = candidateClass.getAnnotation(ReconciliationScope.class);
    //     if (annotation == null) {
    //         return false;
    //     }
        
    //     // Check table match
    //     String table = annotation.table();
    //     if (table != null && !table.equals("") && !table.equals(diffTable)) {
    //         return false;
    //     }
        
    //     // Check column match
    //     String columns = annotation.column();
    //     if (columns != null && !columns.equals("") && !columns.equals(diffColumn)) {
    //         return false;
    //     }
        
    //     return true;
    // }

    

    // ==================== Resolver Application ====================

    private boolean isResolverMatchingDiff(Object resolver, ReconciliationDelta delta) {
        if(resolver instanceof EstivateReconciliation.ICreateTableResolver && delta instanceof EstivateReconciliation.CreateTableDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IAddColumnResolver && delta instanceof EstivateReconciliation.AddColumnDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IModifyColumnResolver && delta instanceof EstivateReconciliation.ModifyColumnDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IDropTableResolver && delta instanceof EstivateReconciliation.DropTableDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IDropColumnResolver && delta instanceof EstivateReconciliation.DropColumnDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IAddIndexResolver && delta instanceof EstivateReconciliation.AddIndexDelta) {
            return true;
        }
        if(resolver instanceof EstivateReconciliation.IDropIndexResolver && delta instanceof EstivateReconciliation.DropIndexDelta) {
            return true;
        }
        return false;
    }

    /**
     * Result of applying resolvers to schema differences.
     */
    @Data
    @Getter
    public static class ApplyResolversResult {
        private final List<ReconciliationDelta> deltas = new ArrayList<>();
        
        public boolean isFullyResolved() {
            return deltas.stream().allMatch(d -> d.getReconciliationResult() == ReconciliationResult.SOLVED);
        }
        
        public int totalDiffs() {
            return deltas.size();
        }
    }

    /**
     * Applies resolvers to all schema differences.
     * For each diff, finds matching resolvers sorted by specificity (most specific first),
     * and applies the first resolver that successfully handles the diff.
     * 
     * @param resolverCandidates Collection of resolver objects to search through
     * @return ApplyResolversResult containing resolved and unresolved diffs
     */
    // public ApplyResolversResult applyResolvers(Collection<Object> resolverCandidates) {
    //     ApplyResolversResult result = new ApplyResolversResult();
    //     result.deltas.addAll(differences);
        
    //     for (ReconciliationDelta diff : differences) {
    //         List<Object> resolvers = findResolver(diff, resolverCandidates);
            
    //         for (Object resolver : resolvers) {
    //             if (tryApplyResolver(resolver, diff)) {
    //                 break;
    //             }
    //         }
            
    //     }
        
    //     return result; 
    // }

    public ApplyResolversResult applyResolvers(){
        ApplyResolversResult result = new ApplyResolversResult();
        result.deltas.addAll(differences);
        
        for (ReconciliationDelta diff : differences) {
            try{
                Object bestResolver = findBestResolver(diff, this.resolvers);
                tryApplyResolver(bestResolver, diff);
            }
            catch (Exception e) {
                log.warn("Resolver {} failed for diff {}: {}", e.getMessage());
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
                return true;
            }
            if (diff instanceof EstivateReconciliation.AddColumnDelta && resolver instanceof EstivateReconciliation.IAddColumnResolver) {
                ((EstivateReconciliation.IAddColumnResolver) resolver).resolve(context, (EstivateReconciliation.AddColumnDelta) diff);
                return true;
            }
            if (diff instanceof EstivateReconciliation.ModifyColumnDelta && resolver instanceof EstivateReconciliation.IModifyColumnResolver) {
                ((EstivateReconciliation.IModifyColumnResolver) resolver).resolve(context, (EstivateReconciliation.ModifyColumnDelta) diff);
                return true;
            }
            if (diff instanceof EstivateReconciliation.DropTableDelta && resolver instanceof EstivateReconciliation.IDropTableResolver) {
                ((EstivateReconciliation.IDropTableResolver) resolver).resolve(context, (EstivateReconciliation.DropTableDelta) diff);
                return true;
            }
            if (diff instanceof EstivateReconciliation.DropColumnDelta && resolver instanceof EstivateReconciliation.IDropColumnResolver) {
                ((EstivateReconciliation.IDropColumnResolver) resolver).resolve(context, (EstivateReconciliation.DropColumnDelta) diff);
                return true;
            }
            if (diff instanceof EstivateReconciliation.AddIndexDelta && resolver instanceof EstivateReconciliation.IAddIndexResolver) {
                ((EstivateReconciliation.IAddIndexResolver) resolver).resolve(context, (EstivateReconciliation.AddIndexDelta) diff);
                return true;
            }
            if (diff instanceof EstivateReconciliation.DropIndexDelta && resolver instanceof EstivateReconciliation.IDropIndexResolver) {
                ((EstivateReconciliation.IDropIndexResolver) resolver).resolve(context, (EstivateReconciliation.DropIndexDelta) diff);
                return true;
            }
        } catch (Exception e) {
            log.warn("Resolver {} failed for diff {}: {}", resolver.getClass().getSimpleName(), diff, e.getMessage());
        }
        return false;
    }


    private Class<?> extractEntityClass(ReconciliationDelta diff) {
        if(diff instanceof EstivateReconciliation.CreateTableDelta) {
            return ((EstivateReconciliation.CreateTableDelta) diff).getEntityClass();
        }
        if(diff instanceof EstivateReconciliation.AddColumnDelta) {
            return ((EstivateReconciliation.AddColumnDelta) diff).getEntityClass();
        }
        if(diff instanceof EstivateReconciliation.DropColumnDelta) {
            return ((EstivateReconciliation.DropColumnDelta) diff).getEntityClass();
        }
        if(diff instanceof EstivateReconciliation.ModifyColumnDelta) {
            return ((EstivateReconciliation.ModifyColumnDelta) diff).getEntityClass();
        }
        return null;
    }

    // ==================== Helper Methods ====================

    

    



    

    



    
    
    
    
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
        if (columnType == null) return null;
        String upper = columnType.toUpperCase().trim();
        if (upper.startsWith("VARCHAR")) {
            return "VARCHAR";
        }
        // Normalize BIT(1) to BOOLEAN so entity boolean matches DB (avoids false positive mismatch)
        if (upper.startsWith("BIT(") || "BIT".equals(upper)) {
            return "BOOLEAN";
        }
        return columnType;
    }

    

}
