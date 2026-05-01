package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.result.ResultRow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySQLContext extends Context {
	
	
	public boolean tracePerformances = false;
	
	public MySQLContext(DataSource datasource) {
		super(datasource);
	}
		
	
	
	@SneakyThrows
	public List<TableIndex> listIndexes(Class<?> c) {
		List<TableIndex> indexes = new ArrayList<>();

		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){

			statement.appendQuery("SHOW INDEX FROM ").appendQuery(nameMapper.toTableName(c));
			
			List<ResultRow> results = this.fetchListAsResults(statement);
			List<IndexRow> indexRows = new ArrayList<>();
			for(ResultRow result : results) {
				
				IndexRow indexRow = IndexRow.builder()
					.table(result.asString("Table"))
					.keyName(result.asString("Key_name"))
					.nonUnique(result.asBoolean("Non_unique"))
					.seqInIndex(result.asInteger("Seq_in_index"))
					.columnName(result.asString("Column_name"))
					.columnLength(result.asInteger("Sub_part"))
					.build();
			
				indexRows.add(indexRow);
				
			}
			
			Map<String, List<IndexRow>> indexRowMap = indexRows.stream().collect(Collectors.groupingBy(IndexRow::getKeyName));
	        
			
			for(Entry<String, List<IndexRow>> indexRowMapEntry : indexRowMap.entrySet()) {

				List<IndexColumn> indexColumns = indexRowMapEntry.getValue().stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.getColumnName()), x.getColumnLength() != null ? x.getColumnLength() : 0)).collect(Collectors.toList());

				IndexType indexType = IndexType.DEFAULT;
				if(indexRowMapEntry.getKey().equals("PRIMARY")) {
					indexType = IndexType.PRIMARY;
				}
				else if(!indexRowMapEntry.getValue().get(0).getNonUnique()) {
					indexType = IndexType.UNIQUE;
				}
				
				TableIndex ci = Annotations.CompositeIndex(indexRowMapEntry.getKey(), indexType, indexColumns);
				indexes.add(ci);
			}
			
		}
		return indexes;
    }
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IndexRow{
		String table;
		String keyName;
		Boolean nonUnique;
		Integer seqInIndex;
		String columnName; 
		Integer columnLength;
		
	}


	/**
     * Converts Java field type to SQL type string
     */
    public String javaTypeToSqlType(Field field) {
        Class<?> type = field.getType();

        // Check for @Convert annotation
        if (field.getDeclaredAnnotation(javax.persistence.Convert.class) != null || field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
            type = String.class;
        }

        // Handle enums
        if (type.isEnum()) {
            if (isEnumeratedAsString(field)) {
                type = String.class;
            }
            type = Integer.class;
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
	
	@Override
	public Integer getDefaultLength(String columnType) {
		if(columnType.equalsIgnoreCase("VARCHAR")) {
			return 255;
		}
		return null; 
	}
	

}
