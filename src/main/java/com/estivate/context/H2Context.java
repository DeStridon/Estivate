package com.estivate.context;


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
import com.estivate.reconciliation.ColumnModel;
import com.estivate.reconciliation.EntityModel;
import com.estivate.reconciliation.TableField;
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
			Statement indexColumnQueryStatement = new Statement(this, connection);
			Statement fulltextIndexQueryStatement = new Statement(this, connection); ){
			
			indexQueryStatement.appendQuery("SELECT * FROM information_schema.indexes WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			indexColumnQueryStatement.appendQuery("SELECT * FROM information_schema.index_columns WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			//fulltextIndexQueryStatement.appendQuery("SELECT * FROM FT.INDEXES;").appendQuery("'"+nameMapper.toTableName(c)+"'");

			List<ResultRow> indexResults = fetchListAsResults(indexQueryStatement);
			List<ResultRow> columnResults = fetchListAsResults(indexColumnQueryStatement);
			//List<ResultRow> fulltextIndexResults = fetchListAsResults(fulltextIndexQueryStatement);
			
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
			case "PRIMARY KEY"	: return IndexType.PRIMARY;
			case "UNIQUE INDEX"	: return IndexType.UNIQUE;
			case "FULLTEXT"		: return IndexType.FULLTEXT;
			default				: return IndexType.DEFAULT;
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

	@SneakyThrows
	public boolean addIndex(Class<?> c, String name, IndexType type, List<String> columns) {

		if(type == IndexType.FULLTEXT) {
			try(Connection connection = datasource.getConnection(); 
			Statement statement = new Statement(this, connection); ){
				statement.appendQuery("CREATE ALIAS IF NOT EXISTS FT_INIT FOR \"org.h2.fulltext.FullText.init\";");
				//statement.executeForValidation();
				
				//statement = new Statement(this, connection);
				statement.appendQuery("CALL FT_INIT();");
				statement.executeForValidation();

				//statement = new Statement(this, connection);
				statement.appendQuery("CALL FT_CREATE_INDEX('PUBLIC', '"+nameMapper.toTableName(c)+"', '"+columns.stream().collect(Collectors.joining(", "))+"');");

				return statement.executeForValidation();
			}
		}

		return super.addIndex(c, name, type, columns);

	}

	@Override
	public boolean indexEquals(TableIndex left, TableIndex right) {
		// H2 FULLTEXT indexes are not named, so we compare the columns
		if(left.type() == IndexType.FULLTEXT && right.type() == IndexType.FULLTEXT) {
			return indexColumnsEquals(left, right);
		}
		else{
			return super.indexEquals(left, right);
		}
	}



	// /**
    //  * Converts Java field type to SQL type string
    //  */
    // public String javaTypeToSqlType(Field field) {
    //     Class<?> type = field.getType();

    //     // Check for @Convert annotation
    //     if (field.getDeclaredAnnotation(javax.persistence.Convert.class) != null || field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
    //         type = String.class;
    //     }

    //     // Handle enums
    //     if (type.isEnum()) {
    //         if (isEnumeratedAsString(field)) {
    //             type = String.class;
    //         }
	// 		else{
	// 			type = Integer.class;
	// 		}
    //     }

    //     // Primitive types and wrappers
    //     if (type == Integer.class || type == int.class) return "INTEGER";
    //     if (type == Long.class || type == long.class) return "INTEGER";
    //     if (type == Short.class || type == short.class) return "INTEGER";
    //     if (type == Byte.class || type == byte.class) return "TINYINT";
    //     if (type == Float.class || type == float.class) return "FLOAT";
    //     if (type == Double.class || type == double.class) return "DOUBLE";
    //     if (type == Boolean.class || type == boolean.class) return "BOOLEAN";
    //     if (type == String.class) {
    //         javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
    //         jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
    //         if (javaxColumn != null || jakartaColumn != null) {
    //             String columnDef = javaxColumn != null ? javaxColumn.columnDefinition() : jakartaColumn.columnDefinition();
    //             if (columnDef != null && columnDef.trim().equalsIgnoreCase("text")) {
    //                 return "TEXT";
    //             }
    //         }
    //         return "CHARACTER VARYING";
    //     } 
    //     if (type == Date.class || type == java.sql.Date.class) return "TIMESTAMP";
    //     if (type == LocalDateTime.class) return "TIMESTAMP";
    //     if (type == LocalDate.class) return "DATE";
    //     if (type == byte[].class) return "BLOB";

    //     return "VARCHAR"; // Default fallback
    // }

	/**
	 * Checks if an enum field is stored as STRING
	 */
	// private boolean isEnumeratedAsString(Field field) {
	// 	javax.persistence.Enumerated javaxEnum = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
	// 	if (javaxEnum != null && javaxEnum.value() == javax.persistence.EnumType.STRING) {
	// 		return true;
	// 	}

	// 	jakarta.persistence.Enumerated jakartaEnum = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
	// 	if (jakartaEnum != null && jakartaEnum.value() == jakarta.persistence.EnumType.STRING) {
	// 		return true;
	// 	}

	// 	return false;
	// }




	@Override
	public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
		if (entityColumn.getType() == Integer.class || entityColumn.getType() == int.class) return new ColumnModel.ColumnFormat("INTEGER");
		if (entityColumn.getType() == Long.class || entityColumn.getType() == long.class) return new ColumnModel.ColumnFormat("INTEGER");
		if (entityColumn.getType() == Short.class || entityColumn.getType() == short.class) return new ColumnModel.ColumnFormat("INTEGER");
		if (entityColumn.getType() == Byte.class || entityColumn.getType() == byte.class) return new ColumnModel.ColumnFormat("TINYINT");
		if (entityColumn.getType() == Float.class || entityColumn.getType() == float.class) return new ColumnModel.ColumnFormat("FLOAT");
		if (entityColumn.getType() == Double.class || entityColumn.getType() == double.class) return new ColumnModel.ColumnFormat("DOUBLE");
		if (entityColumn.getType() == Boolean.class || entityColumn.getType() == boolean.class) return new ColumnModel.ColumnFormat("BOOLEAN");
		if (entityColumn.getType() == String.class) return new ColumnModel.ColumnFormat("CHARACTER VARYING"); 
		if (entityColumn.getType() == Date.class || entityColumn.getType() == java.sql.Date.class) return new ColumnModel.ColumnFormat("TIMESTAMP");
		if (entityColumn.getType() == LocalDateTime.class) return new ColumnModel.ColumnFormat("TIMESTAMP");
		if (entityColumn.getType() == LocalDate.class) return new ColumnModel.ColumnFormat("DATE");
		if (entityColumn.getType() == byte[].class) return new ColumnModel.ColumnFormat("BLOB");
		throw new IllegalArgumentException("Unsupported type: " + entityColumn.getType());
	}

	@SneakyThrows
	private List<TableField> fetchInformationSchemaColumns(String tableName) {
		try (Connection connection = datasource.getConnection();
				Statement statement = new Statement(this, connection)) {

			statement.appendQuery(
					"SELECT TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, "
							+ "NUMERIC_PRECISION, NUMERIC_SCALE, IS_IDENTITY, IDENTITY_GENERATION "
							+ "FROM INFORMATION_SCHEMA.COLUMNS "
							+ "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = '" + tableName + "' ORDER BY ORDINAL_POSITION");

			return fetchListAsResults(statement).stream()
					.map(this::toTableField)
					.collect(Collectors.toList());
		}
	}

	private TableField toTableField(ResultRow row) {
		String dataType = row.asString("DATA_TYPE");
		Integer characterMaximumLength = row.asInteger("CHARACTER_MAXIMUM_LENGTH");
		Integer numericPrecision = row.asInteger("NUMERIC_PRECISION");
		Integer numericScale = row.asInteger("NUMERIC_SCALE");
		boolean identity = "YES".equalsIgnoreCase(row.asString("IS_IDENTITY"));
		String columnType = formatColumnType(dataType, characterMaximumLength, numericPrecision, numericScale);

		return TableField.builder()
				.name(row.asString("COLUMN_NAME"))
				.type(extractColumnType(columnType))
				.nullable("YES".equalsIgnoreCase(row.asString("IS_NULLABLE")))
				.autoIncrement(identity)
				.defaultValue(identity ? null : row.asString("COLUMN_DEFAULT"))
				.length(characterMaximumLength != null ? characterMaximumLength : extractLength(columnType))
				.build();
	}


	private String formatColumnType(String dataType, Integer characterMaximumLength, Integer numericPrecision, Integer numericScale) {
		if (dataType == null) {
			return null;
		}
		if (characterMaximumLength != null) {
			return dataType + "(" + characterMaximumLength + ")";
		}
		String upper = dataType.toUpperCase();
		if ("DECIMAL".equals(upper) || "NUMERIC".equals(upper)) {
			if (numericPrecision != null && numericScale != null && numericScale > 0) {
				return dataType + "(" + numericPrecision + "," + numericScale + ")";
			}
			if (numericPrecision != null) {
				return dataType + "(" + numericPrecision + ")";
			}
		}
		return dataType;
	}

	@SneakyThrows
    public EntityModel scanDatabaseTable(String tableName) {
        EntityModel model = EntityModel.builder()
            .tableName(tableName)
            .build();

        model.getFields().addAll(fetchInformationSchemaColumns(tableName));
        return model;
    }

}
