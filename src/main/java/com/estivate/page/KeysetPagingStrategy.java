package com.estivate.page;

import java.util.ArrayList;
import java.util.List;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultRow;
import com.estivate.result.ResultTable;


import lombok.Getter;


public class KeysetPagingStrategy implements PagingStrategy {
    
    @Getter
    private final int pageSize;
    private final Object[] keysetValues; // Values from the last row of previous page (null for first page)
    
    /**
     * Creates a keyset paging strategy for the first page
     * @param pageSize number of items per page
     */
    public KeysetPagingStrategy(int pageSize) {
        this(pageSize, null);
    }
    
    /**
     * Creates a keyset paging strategy with keyset values from previous page
     * @param pageSize number of items per page
     * @param keysetValues values from the last row of previous page (must match ORDER BY columns)
     */
    public KeysetPagingStrategy(int pageSize, Object[] keysetValues) {
        this.pageSize = pageSize;
        this.keysetValues = keysetValues;
    }

    @Override
    public PageResult fetch(Context context, SelectQuery<?> query) {
        // Validate that ORDER BY clauses exist (required for keyset pagination)
        List<Order> orders = query.getOrders();
        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("Keyset pagination requires at least one ORDER BY clause");
        }
        
        // Validate keyset values match ORDER BY count
        if (keysetValues != null && keysetValues.length != orders.size()) {
            throw new RuntimeException(String.format("Keyset values count (%d) must match ORDER BY clauses count (%d)", keysetValues.length, orders.size()));
        }
        
        // Clone the query to avoid modifying the original
        SelectQuery<?> pagedQuery = query.clone();
        
        // Apply keyset conditions if we have keyset values
        if (keysetValues != null) {
            applyKeysetConditions(pagedQuery, orders, keysetValues);
        }
        
        // Apply limit (fetch one extra to determine if there's a next page)
        pagedQuery.limit(pageSize + 1);
        pagedQuery.offset(null); // Clear any offset for keyset pagination
        
        // Execute query
        ResultTable resultTable = context.fetch(pagedQuery);
        
        // Determine if there's a next page (check raw result size)
        boolean hasNext = resultTable.size() > pageSize;
        
        // Extract keyset values from the last row for next page (before removing extra item)
        Object[] nextKeysetValues = null;
        if (!resultTable.isEmpty()) {
            // Get the last row from resultTable (which might be the extra item if hasNext)
            // For keyset, we want the last row that will be in the result
            int lastRowIndex = hasNext ? pageSize - 1 : resultTable.size() - 1;
            ResultRow lastRow = resultTable.get(lastRowIndex);
            nextKeysetValues = extractKeysetValues(lastRow, orders, context);
        }
        
        
        
        return PageResult.builder()
            .rows(limitToPageSize(resultTable, pageSize))
            .columnNames(resultTable.getColumnNames())
            .query(resultTable.getQuery())
            .pageSize(pageSize)
            .pagingStrategy(new KeysetPagingStrategy(pageSize, nextKeysetValues))
            .hasNext(hasNext)
            .build();
    }

    private List<ResultRow> limitToPageSize(ResultTable resultTable, int pageSize) {
        if (resultTable.size() > pageSize) {
            return new ArrayList<>(resultTable.getRows().subList(0, pageSize));
        }
        return resultTable.getRows();
    }
    
    /**
     * Applies keyset conditions to the query
     * For ORDER BY (col1 ASC, col2 DESC), generates:
     * WHERE (col1 > val1) OR (col1 = val1 AND col2 < val2)
     */
    private <E> void applyKeysetConditions(SelectQuery<E> query, List<Order> orders, Object[] keysetValues) {
        if (orders.isEmpty() || keysetValues == null) {
            return;
        }
        
        // Build composite condition: (col1 > val1) OR (col1 = val1 AND col2 > val2) OR ...
        List<com.estivate.query.EstivateNode> orConditions = new ArrayList<>();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Object value = keysetValues[i];
            
            // Build condition: (col1 = val1 AND col2 = val2 AND ... AND coli > vali)
            List<com.estivate.query.EstivateNode> andConditions = new ArrayList<>();
            
            // Add equality conditions for all previous columns
            for (int j = 0; j < i; j++) {
                Order prevOrder = orders.get(j);
                Object prevValue = keysetValues[j];
                Attribute attr = Estivate.attribute(prevOrder.entity, prevOrder.attribute, prevOrder.function);
                andConditions.add(Estivate.eq(attr, prevValue));
            }
            
            // Add comparison condition for current column
            Attribute currentAttr = Estivate.attribute(order.entity, order.attribute, order.function);
            com.estivate.query.EstivateNode comparison;
            
            if (order.direction == Order.Direction.Asc) {
                // For ASC: use > (greater than)
                comparison = Estivate.gt(currentAttr, value);
            } else {
                // For DESC: use < (less than)
                comparison = Estivate.lt(currentAttr, value);
            }
            
            andConditions.add(comparison);
            
            // Combine all conditions with AND
            if (andConditions.size() == 1) {
                orConditions.add(andConditions.get(0));
            } else {
                orConditions.add(Estivate.and(andConditions));
            }
        }
        
        // Combine all conditions with OR
        if (orConditions.size() == 1) {
            query.add(orConditions.get(0));
        } else {
            query.add(Estivate.or(orConditions));
        }
    }
    
    /**
     * Extracts keyset values from the last row based on ORDER BY columns
     */
    private Object[] extractKeysetValues(ResultRow lastRow, List<Order> orders, Context context) {
        Object[] values = new Object[orders.size()];
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            
            // Determine column name - prefer alias if available, otherwise use mapped attribute name
            String columnName;
            if (order.alias != null && !order.alias.isEmpty()) {
                columnName = order.alias;
            } else {
                columnName = context.nameMapper.toTableNameAttribute(
                    order.entity.entity, 
                    order.attribute
                );
            }
            
            // Use ResultRow's type-aware extraction method
            // This will automatically handle type conversion based on the entity field type
            try {
                values[i] = lastRow.as(order.entity.entity, order.attribute);
            } catch (Exception e) {
                // Fallback: try to get as string and convert manually
                String stringValue = lastRow.asString(columnName);
                if (stringValue != null) {
                    // Try to determine the type from the entity field
                    try {
                        java.lang.reflect.Field field = order.entity.entity.getDeclaredField(order.attribute);
                        Class<?> fieldType = field.getType();
                        
                        if (fieldType == Long.class || fieldType == long.class) {
                            values[i] = lastRow.asLong(columnName);
                        } else if (fieldType == Integer.class || fieldType == int.class) {
                            values[i] = lastRow.asInteger(columnName);
                        } else if (fieldType == String.class) {
                            values[i] = stringValue;
                        } else if (fieldType == java.util.Date.class || fieldType == java.sql.Date.class) {
                            values[i] = lastRow.asDate(columnName);
                        } else if (fieldType == java.time.LocalDateTime.class) {
                            values[i] = lastRow.asLocalDateTime(columnName);
                        } else if (fieldType == java.time.Instant.class) {
                            values[i] = lastRow.asInstant(columnName);
                        } else {
                            // Default to string
                            values[i] = stringValue;
                        }
                    } catch (NoSuchFieldException ex) {
                        // If field not found, default to string
                        values[i] = stringValue;
                    }
                } else {
                    values[i] = null;
                }
            }
        }
        
        return values;
    }
}
