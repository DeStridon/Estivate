package com.estivate.context;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.context.MySQLDialect.MySQLTypes;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.reconciliation.DatabaseColumnDefinition;
import com.estivate.reconciliation.EntityColumn.ColumnDimension;
import com.estivate.reconciliation.DatabaseColumn;
import com.estivate.result.ResultRow;
import com.estivate.util.FieldUtils;

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
		super(datasource, new MySQLDialect());
	}
		
	
	
	

	/** Maps {@code SHOW INDEX} metadata to {@link IndexType}. Package-private for unit tests. */
	static IndexType resolveIndexType(String keyName, boolean nonUnique, String physicalIndexType) {
		if("PRIMARY".equals(keyName)) {
			return IndexType.PRIMARY;
		}
		if(!nonUnique) {
			return IndexType.UNIQUE;
		}
		if(physicalIndexType != null && physicalIndexType.equalsIgnoreCase("FULLTEXT")) {
			return IndexType.FULLTEXT;
		}
		return IndexType.DEFAULT;
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
		/** MySQL {@code SHOW INDEX} {@code Index_type}: BTREE, HASH, FULLTEXT, SPATIAL, etc. */
		String physicalIndexType;
		
	}



	// @Override
	// public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
	// 	if (StringUtils.isNotBlank(entityColumn.getDesignedType())) {
	// 		String designedType = entityColumn.getDesignedType().trim();
	// 		if (designedType.toUpperCase().startsWith("ENUM")) {
	// 			return new ColumnModel.ColumnFormat(designedType, null, true);
	// 		}

	// 		MySQLTypes mysqlType = MySQLDialect.resolveMySQLType(designedType);
	// 		if (mysqlType == null) {
	// 			throw new IllegalArgumentException("Unsupported type: " + designedType);
	// 		}
	// 		else if (mysqlType.mainDimension == ColumnDimension.LENGTH_OPTIONAL) {
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedLength(), null, false);
	// 		}
	// 		else if(mysqlType.mainDimension == ColumnDimension.LENGTH_REQUIRED){
	// 			if (entityColumn.getDesignedLength() == null) {
	// 				throw new IllegalArgumentException("Length is required for " + designedType);
	// 			}
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedLength(), null, false);
	// 		}
	// 		else if (mysqlType.mainDimension == ColumnDimension.PRECISION_OPTIONAL) {
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedPrecision(), entityColumn.getDesignedScale(), false);
	// 		}

	// 		throw new IllegalArgumentException("Unsupported type: " + designedType);
	// 	}

	// 	if (entityColumn.getType() == Integer.class || entityColumn.getType() == int.class) {
	// 		return new ColumnModel.ColumnFormat("INT", null, true);
	// 	}
	// 	if (entityColumn.getType() == Long.class || entityColumn.getType() == long.class) {
	// 		return new ColumnModel.ColumnFormat("BIGINT", null, true);
	// 	}
	// 	if (entityColumn.getType() == Short.class || entityColumn.getType() == short.class) {
	// 		return new ColumnModel.ColumnFormat("SMALLINT", null, true);
	// 	}
	// 	if (entityColumn.getType() == Byte.class || entityColumn.getType() == byte.class) {
	// 		return new ColumnModel.ColumnFormat("TINYINT", null, true);
	// 	}
	// 	if (entityColumn.getType() == Float.class || entityColumn.getType() == float.class) {
	// 		return new ColumnModel.ColumnFormat("FLOAT", null, true);
	// 	}
	// 	if (entityColumn.getType() == Double.class || entityColumn.getType() == double.class) {
	// 		return new ColumnModel.ColumnFormat("DOUBLE", null, true);
	// 	}
	// 	if (entityColumn.getType() == java.math.BigDecimal.class) {
	// 		Integer precision = entityColumn.getDesignedPrecision() != null ? entityColumn.getDesignedPrecision() : entityColumn.getDesignedLength();
	// 		return new ColumnModel.ColumnFormat("DECIMAL", precision, entityColumn.getDesignedScale(), precision == null && entityColumn.getDesignedScale() == null);
	// 	}
	// 	if (entityColumn.getType() == Boolean.class || entityColumn.getType() == boolean.class) {
	// 		return new ColumnModel.ColumnFormat("BIT", 1, true);
	// 	}
	// 	if (entityColumn.getType() == String.class) {
	// 		return new ColumnModel.ColumnFormat("VARCHAR", entityColumn.getDesignedLength() != null ? entityColumn.getDesignedLength() : 255, false);
	// 	}
	// 	if (entityColumn.getType() == Date.class || entityColumn.getType() == java.sql.Date.class) {
	// 		return new ColumnModel.ColumnFormat("DATETIME", null, true);
	// 	}
	// 	if (entityColumn.getType() == java.sql.Timestamp.class) {
	// 		return new ColumnModel.ColumnFormat("DATETIME", null, true);
	// 	}
	// 	if (entityColumn.getType() == LocalDateTime.class) {
	// 		return new ColumnModel.ColumnFormat("DATETIME", null, true);
	// 	}
	// 	if (entityColumn.getType() == Instant.class) {
	// 		return new ColumnModel.ColumnFormat("DATETIME", null, true);
	// 	}
	// 	if (entityColumn.getType() == LocalDate.class) {
	// 		return new ColumnModel.ColumnFormat("DATE", null, true);
	// 	}
	// 	if (entityColumn.getType() == byte[].class) {
	// 		return new ColumnModel.ColumnFormat("BLOB", null, true);
	// 	}
	// 	throw new IllegalArgumentException("Unsupported type: " + entityColumn.getType() + " for column: " + entityColumn.getName());
	// }






    @SneakyThrows
	public List<DatabaseColumn> listFields(String tableName) {
		List<DatabaseColumn> fields = new ArrayList<>(); 
		try (Connection connection = datasource.getConnection();
             Statement statement = new Statement(this, connection)) {

            statement.appendQuery("SHOW COLUMNS FROM ").appendQuery(tableName);
            
            try (ResultSet resultSet = statement.executeForResultSet()) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("Field");
                    String columnType = resultSet.getString("Type");
                    String nullableStr = resultSet.getString("Null");
                    String defaultValue = resultSet.getString("Default");
                    if(defaultValue != null && defaultValue.startsWith("b'") && defaultValue.endsWith("'")) {
                    	defaultValue = defaultValue.substring(2, defaultValue.length()-1);
                    }
                    if(defaultValue != null && columnType.equalsIgnoreCase("bit(1)")) {
                    	defaultValue = FieldUtils.parseBoolean(defaultValue).toString();
                    }
                    // H2 doesn't have "Extra" column, so we need to handle it gracefully
                    String extra = null;
                    try {
                        extra = resultSet.getString("Extra");
                    } catch (Exception e) {
                        // H2 doesn't support Extra column, check auto_increment from column type or other means
                        // For H2, we can check if the column type contains AUTO_INCREMENT or check the default
                    }

                    // Convert database column name back to entity field name

					// TODO : decide on type if length or precision should be set
                    DatabaseColumnDefinition parsedColumnType = DatabaseColumnDefinition.parse(columnType);
                    
                    DatabaseColumn tableField = DatabaseColumn.builder()
                        .name(columnName)
                        .type(parsedColumnType.getType())
                        .nullable("YES".equalsIgnoreCase(nullableStr))
                        .autoIncrement(extra != null && extra.toLowerCase().contains("auto_increment"))
                        .defaultValue(defaultValue)
                        .dimension(parsedColumnType.getDimension())
                        .scale(parsedColumnType.getScale())
                        .build();

                    fields.add(tableField);
                }
            }
        }
		return fields;
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
				String physicalIndexType = null;
				if(result.getResultTable().getColumnNames().contains("Index_type")) {
					physicalIndexType = result.asString("Index_type");
				}

				IndexRow indexRow = IndexRow.builder()
					.table(result.asString("Table"))
					.keyName(result.asString("Key_name"))
					.nonUnique(result.asBoolean("Non_unique"))
					.seqInIndex(result.asInteger("Seq_in_index"))
					.columnName(result.asString("Column_name"))
					.columnLength(result.asInteger("Sub_part"))
					.physicalIndexType(physicalIndexType)
					.build();
			
				indexRows.add(indexRow);
				
			}
			
			Map<String, List<IndexRow>> indexRowMap = indexRows.stream().collect(Collectors.groupingBy(IndexRow::getKeyName));
	        
			
			for(Entry<String, List<IndexRow>> indexRowMapEntry : indexRowMap.entrySet()) {

				List<IndexColumn> indexColumns = indexRowMapEntry.getValue().stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.getColumnName()), x.getColumnLength() != null ? x.getColumnLength() : 0)).collect(Collectors.toList());

				IndexRow firstRow = indexRowMapEntry.getValue().get(0);
				IndexType indexType = resolveIndexType(
						indexRowMapEntry.getKey(),
						firstRow.getNonUnique(),
						firstRow.getPhysicalIndexType());
				
				TableIndex ci = Annotations.CompositeIndex(indexRowMapEntry.getKey(), indexType, indexColumns);
				indexes.add(ci);
			}
			
		}
		return indexes;
    }

	@Override
	protected void appendColumnTypeAndSize(Statement statement, DatabaseColumn tableField) {
		statement.appendQuery(formatMySQLColumnType(tableField));
	}

	static String formatMySQLColumnType(DatabaseColumn tableField) {
		if (tableField.getType() != null && tableField.getType().toUpperCase().startsWith("ENUM")) {
			return tableField.getType();
		}

		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse(tableField.getType());
		String typeSource = parts != null ? parts.getType() : tableField.getType();
		MySQLTypes mysqlType = MySQLDialect.resolveMySQLType(typeSource);
		if (mysqlType == null) {
			throw new IllegalArgumentException("Unsupported MySQL type: " + typeSource);
		}

		StringBuilder sqlType = new StringBuilder(mysqlType.baseType());

		Integer dimension = tableField.getDimension();
		if (dimension == null && parts != null) {
			dimension = parts.getDimension();
		}
		Integer scale = tableField.getScale();
		if (scale == null && parts != null) {
			scale = parts.getScale();
		}

		if (mysqlType.mainDimension == ColumnDimension.PRECISION_OPTIONAL && dimension != null) {
			sqlType.append("(").append(dimension);
			if (scale != null) {
				sqlType.append(",").append(scale);
			}
			sqlType.append(")");
		} 
		else if ((mysqlType.mainDimension == ColumnDimension.LENGTH_OPTIONAL || mysqlType.mainDimension == ColumnDimension.LENGTH_REQUIRED) && dimension != null) {
			sqlType.append("(").append(dimension).append(")");
		}

		if (mysqlType.isUnsigned()) {
			sqlType.append(" UNSIGNED");
		}

		return sqlType.toString();
	}




}
