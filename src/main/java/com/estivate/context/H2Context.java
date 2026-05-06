package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.query.CreateQuery;
import com.estivate.result.ResultRow;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class H2Context extends Context {
	
	
	public boolean tracePerformances = false;
	
	public H2Context(DataSource datasource) {
		super(datasource);
	}
		
	@SneakyThrows
	public List<TableIndex> listIndexes(Class<?> c) {
		
		List<TableIndex> indexes = new ArrayList<>();
		
		try(Connection connection = datasource.getConnection(); 
			Statement indexQueryStatement = new Statement(this, connection);
			Statement indexColumnQueryStatement = new Statement(this, connection); ){
			
			indexQueryStatement.appendQuery("SELECT * FROM information_schema.indexes WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			indexColumnQueryStatement.appendQuery("SELECT * FROM information_schema.index_columns WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			
			List<ResultRow> indexResults = fetchListAsResults(indexQueryStatement);
			List<ResultRow> columnResults = fetchListAsResults(indexColumnQueryStatement);
			
			for(ResultRow indexResult : indexResults) {
				List<ResultRow> indexColumnResults = columnResults.stream().filter(x -> x.asString("INDEX_NAME").equals(indexResult.asString("INDEX_NAME"))).collect(Collectors.toList());

				List<IndexColumn> indexColumns = indexColumnResults.stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.asString("COLUMN_NAME")), 0)).collect(Collectors.toList());

				TableIndex ci = Annotations.CompositeIndex(indexResult.asString("INDEX_NAME"), getIndexType(indexResult.asString("INDEX_TYPE_NAME")), indexColumns);
				indexes.add(ci);
			}
			
			return indexes;
		}
    }


	public IndexType getIndexType(String typeName) {
		switch(typeName) {
			case "PRIMARY KEY": return IndexType.PRIMARY;
			case "UNIQUE INDEX": return IndexType.UNIQUE;
			default: return IndexType.DEFAULT;
		}
	}
	
	/**
	 * Helper method to execute H2-compatible ALTER TABLE statements
	 */
	@SneakyThrows
	private void executeH2AlterTable(String sql) {
		try (Connection connection = datasource.getConnection();
			 java.sql.Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		}
	}

	/**
	 * Helper method to change column type in H2 (uses ALTER COLUMN instead of CHANGE COLUMN)
	 */
	@SneakyThrows
	public void changeColumn(Class<?> c, String fieldName, String columnType) {
		String columnName = nameMapper.mapDatabaseField(fieldName);
		String tableName = nameMapper.toTableName(c);
		executeH2AlterTable("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " " + columnType);
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
			else{
				type = Integer.class;
			}
        }

        // Primitive types and wrappers
        if (type == Integer.class || type == int.class) return "INTEGER";
        if (type == Long.class || type == long.class) return "INTEGER";
        if (type == Short.class || type == short.class) return "INTEGER";
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
            return "CHARACTER VARYING";
        } 
        if (type == Date.class || type == java.sql.Date.class) return "TIMESTAMP";
        if (type == LocalDateTime.class) return "TIMESTAMP";
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
	public String getTypeForColumn(CreateQuery.ColumnDefinition columnDefinition) {


		if(columnDefinition.getExplicitType() != null) {
			return columnDefinition.getExplicitType();
		}

		// Primitive types and wrappers
		if (columnDefinition.getType() == Integer.class || columnDefinition.getType() == int.class) return "INTEGER";
		if (columnDefinition.getType() == Long.class || columnDefinition.getType() == long.class) return "INTEGER";
		if (columnDefinition.getType() == Short.class || columnDefinition.getType() == short.class) return "INTEGER";
		if (columnDefinition.getType() == Byte.class || columnDefinition.getType() == byte.class) return "TINYINT";
		if (columnDefinition.getType() == Float.class || columnDefinition.getType() == float.class) return "FLOAT";
		if (columnDefinition.getType() == Double.class || columnDefinition.getType() == double.class) return "DOUBLE";
		if (columnDefinition.getType() == Boolean.class || columnDefinition.getType() == boolean.class) return "BOOLEAN";
		if (columnDefinition.getType() == String.class) return "CHARACTER VARYING"; 
		if (columnDefinition.getType() == Date.class || columnDefinition.getType() == java.sql.Date.class) return "TIMESTAMP";
		if (columnDefinition.getType() == LocalDateTime.class) return "TIMESTAMP";
		if (columnDefinition.getType() == LocalDate.class) return "DATE";
		if (columnDefinition.getType() == byte[].class) return "BLOB";

		return "VARCHAR"; // Default fallback


	}

	@Override
	public Integer getDefaultLengthForColumn(String type, CreateQuery.ColumnDefinition columnDefinition) {
		if(type.equalsIgnoreCase("VARCHAR")) {
			return 255;
		}
		return null;
	}

}
