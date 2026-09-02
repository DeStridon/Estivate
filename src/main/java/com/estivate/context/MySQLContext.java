package com.estivate.context;


import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.reconciliation.ColumnModel;
import com.estivate.reconciliation.ColumnTypeParts;
import com.estivate.reconciliation.EntityModel;
import com.estivate.reconciliation.TableField;
import com.estivate.result.ResultRow;
import com.estivate.util.FieldUtils;
import com.estivate.util.Pair;

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



	@Override
	public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
		if (StringUtils.isNotBlank(entityColumn.getDesignedType())) {
			String designedType = entityColumn.getDesignedType().trim();
			if (designedType.toUpperCase().startsWith("ENUM")) {
				return new ColumnModel.ColumnFormat(designedType, null, true);
			}

			MySQLTypes mysqlType = resolveMySQLType(designedType);
			if (mysqlType != null && mysqlType.mainDimension == MainDimension.LENGTH) {
				if (mysqlType.parameterType == ParameterType.REQUIRED && entityColumn.getDesignedLength() == null) {
					throw new IllegalArgumentException("Length is required for " + designedType);
				}
				boolean noLength = mysqlType.parameterType == ParameterType.OPTIONAL
						|| mysqlType.parameterType == ParameterType.FORBIDDEN;
				return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedLength(), null, noLength);
			}
			if (mysqlType != null && mysqlType.mainDimension == MainDimension.PRECISION) {
				if (mysqlType.parameterType == ParameterType.REQUIRED && entityColumn.getDesignedPrecision() == null) {
					throw new IllegalArgumentException("Precision is required for " + designedType);
				}
				boolean noLength = mysqlType.parameterType == ParameterType.OPTIONAL || mysqlType.parameterType == ParameterType.FORBIDDEN;
				return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedPrecision(), entityColumn.getDesignedScale(), noLength);
			}

			throw new IllegalArgumentException("Unsupported type: " + designedType);
		}

		if (entityColumn.getType() == Integer.class || entityColumn.getType() == int.class) {
			return new ColumnModel.ColumnFormat("INT", null, true);
		}
		if (entityColumn.getType() == Long.class || entityColumn.getType() == long.class) {
			return new ColumnModel.ColumnFormat("BIGINT", null, true);
		}
		if (entityColumn.getType() == Short.class || entityColumn.getType() == short.class) {
			return new ColumnModel.ColumnFormat("SMALLINT", null, true);
		}
		if (entityColumn.getType() == Byte.class || entityColumn.getType() == byte.class) {
			return new ColumnModel.ColumnFormat("TINYINT", null, true);
		}
		if (entityColumn.getType() == Float.class || entityColumn.getType() == float.class) {
			return new ColumnModel.ColumnFormat("FLOAT", null, true);
		}
		if (entityColumn.getType() == Double.class || entityColumn.getType() == double.class) {
			return new ColumnModel.ColumnFormat("DOUBLE", null, true);
		}
		if (entityColumn.getType() == java.math.BigDecimal.class) {
			Integer precision = entityColumn.getDesignedPrecision() != null ? entityColumn.getDesignedPrecision()
					: entityColumn.getDesignedLength();
			return new ColumnModel.ColumnFormat("DECIMAL", precision, entityColumn.getDesignedScale(),
					precision == null && entityColumn.getDesignedScale() == null);
		}
		if (entityColumn.getType() == Boolean.class || entityColumn.getType() == boolean.class) {
			return new ColumnModel.ColumnFormat("BIT", 1, true);
		}
		if (entityColumn.getType() == String.class) {
			return new ColumnModel.ColumnFormat("VARCHAR",
					entityColumn.getDesignedLength() != null ? entityColumn.getDesignedLength() : 255, false);
		}
		if (entityColumn.getType() == Date.class || entityColumn.getType() == java.sql.Date.class) {
			return new ColumnModel.ColumnFormat("DATETIME", null, true);
		}
		if (entityColumn.getType() == java.sql.Timestamp.class) {
			return new ColumnModel.ColumnFormat("DATETIME", null, true);
		}
		if (entityColumn.getType() == LocalDateTime.class) {
			return new ColumnModel.ColumnFormat("DATETIME", null, true);
		}
		if (entityColumn.getType() == Instant.class) {
			return new ColumnModel.ColumnFormat("DATETIME", null, true);
		}
		if (entityColumn.getType() == LocalDate.class) {
			return new ColumnModel.ColumnFormat("DATE", null, true);
		}
		if (entityColumn.getType() == byte[].class) {
			return new ColumnModel.ColumnFormat("BLOB", null, true);
		}
		throw new IllegalArgumentException("Unsupported type: " + entityColumn.getType() + " for column: " + entityColumn.getName());
	}






    @SneakyThrows
	public List<TableField> listFields(String tableName) {
		List<TableField> fields = new ArrayList<>(); 
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
                    ColumnTypeParts parsedColumnType = parseColumnType(columnType);
                    
                    TableField tableField = TableField.builder()
                        .name(columnName)
                        .type(parsedColumnType.getType())
                        .nullable("YES".equalsIgnoreCase(nullableStr))
                        .autoIncrement(extra != null && extra.toLowerCase().contains("auto_increment"))
                        .defaultValue(defaultValue)
                        .dimension(parsedColumnType.getLength())
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
	protected void appendColumnTypeAndSize(Statement statement, TableField tableField) {
		statement.appendQuery(formatMySQLColumnType(tableField));
	}

	static String formatMySQLColumnType(TableField tableField) {
		if (tableField.getType() != null && tableField.getType().toUpperCase().startsWith("ENUM")) {
			return tableField.getType();
		}

		ColumnTypeParts parts = ColumnTypeParts.parse(tableField.getType());
		String typeSource = parts != null ? parts.getType() : tableField.getType();
		MySQLTypes mysqlType = resolveMySQLType(typeSource);
		if (mysqlType == null) {
			throw new IllegalArgumentException("Unsupported MySQL type: " + typeSource);
		}

		StringBuilder sqlType = new StringBuilder(mysqlType.baseType());

		Integer dimension = tableField.getDimension();
		if (dimension == null && parts != null) {
			dimension = parts.getLength();
		}
		Integer scale = tableField.getScale();
		if (scale == null && parts != null) {
			scale = parts.getScale();
		}

		if (mysqlType.mainDimension == MainDimension.PRECISION && dimension != null) {
			sqlType.append("(").append(dimension);
			if (scale != null) {
				sqlType.append(",").append(scale);
			}
			sqlType.append(")");
		} else if (mysqlType.mainDimension == MainDimension.LENGTH
				&& mysqlType.parameterType != ParameterType.FORBIDDEN
				&& dimension != null) {
			sqlType.append("(").append(dimension).append(")");
		}

		if (mysqlType.isUnsigned()) {
			sqlType.append(" UNSIGNED");
		}

		return sqlType.toString();
	}

	static MySQLTypes resolveMySQLType(String sqlType) {
		if (sqlType == null) {
			return null;
		}
		String normalized = sqlType.trim().toUpperCase().replace(' ', '_');
		try {
			return MySQLTypes.valueOf(normalized);
		} catch (IllegalArgumentException ignored) {
			String base = normalized.replace("_UNSIGNED", "").replace("_SIGNED", "");
			try {
				return MySQLTypes.valueOf(base);
			} catch (IllegalArgumentException ignoredAgain) {
				return null;
			}
		}
	}


	public enum MySQLTypes {
		// Numeric types
		DECIMAL(MainDimension.PRECISION, ParameterType.OPTIONAL),
		DECIMAL_UNSIGNED(MainDimension.PRECISION, ParameterType.OPTIONAL),
		NUMERIC(MainDimension.PRECISION, ParameterType.OPTIONAL),
		NUMERIC_UNSIGNED(MainDimension.PRECISION, ParameterType.OPTIONAL),
		FLOAT(MainDimension.PRECISION, ParameterType.OPTIONAL),
		FLOAT_UNSIGNED(MainDimension.PRECISION, ParameterType.OPTIONAL),
		DOUBLE(MainDimension.PRECISION, ParameterType.OPTIONAL),
		DOUBLE_UNSIGNED(MainDimension.PRECISION, ParameterType.OPTIONAL),
		TINYINT(MainDimension.LENGTH, ParameterType.OPTIONAL),
		TINYINT_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		SMALLINT(MainDimension.LENGTH, ParameterType.OPTIONAL),
		SMALLINT_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		MEDIUMINT(MainDimension.LENGTH, ParameterType.OPTIONAL),
		MEDIUMINT_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		INT(MainDimension.LENGTH, ParameterType.OPTIONAL),
		INT_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		INTEGER(MainDimension.LENGTH, ParameterType.OPTIONAL),
		INTEGER_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		BIGINT(MainDimension.LENGTH, ParameterType.OPTIONAL),
		BIGINT_UNSIGNED(MainDimension.LENGTH, ParameterType.OPTIONAL),
		BIT(MainDimension.LENGTH, ParameterType.OPTIONAL),

		// Date and time types
		DATE(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		DATETIME(MainDimension.PRECISION, ParameterType.OPTIONAL),
		TIMESTAMP(MainDimension.PRECISION, ParameterType.OPTIONAL),
		TIME(MainDimension.PRECISION, ParameterType.OPTIONAL),
		YEAR(MainDimension.LENGTH, ParameterType.OPTIONAL),

		// String types
		CHAR(MainDimension.LENGTH, ParameterType.REQUIRED),
		VARCHAR(MainDimension.LENGTH, ParameterType.REQUIRED),
		TEXT(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		TINYTEXT(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		MEDIUMTEXT(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		LONGTEXT(MainDimension.LENGTH, ParameterType.FORBIDDEN),

		// Binary types
		BINARY(MainDimension.LENGTH, ParameterType.OPTIONAL),
		VARBINARY(MainDimension.LENGTH, ParameterType.REQUIRED),
		BLOB(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		TINYBLOB(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		MEDIUMBLOB(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		LONGBLOB(MainDimension.LENGTH, ParameterType.FORBIDDEN),

		// Misc types
		JSON(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		ENUM(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		SET(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		BOOLEAN(MainDimension.LENGTH, ParameterType.FORBIDDEN),
		BOOL(MainDimension.LENGTH, ParameterType.FORBIDDEN);

		public final MainDimension mainDimension;
		public final ParameterType parameterType;

		MySQLTypes(MainDimension mainDimension, ParameterType parameterType) {
			this.mainDimension = mainDimension;
			this.parameterType = parameterType;
		}

		/** Base MySQL type name without signedness, e.g. {@code SMALLINT_UNSIGNED} → {@code SMALLINT}. */
		public String baseType() {
			String name = name();
			if (name.endsWith("_UNSIGNED")) {
				return name.substring(0, name.length() - "_UNSIGNED".length());
			}
			if (name.endsWith("_SIGNED")) {
				return name.substring(0, name.length() - "_SIGNED".length());
			}
			return name;
		}

		/** {@code true} when this enum constant is an unsigned numeric variant. */
		public boolean isUnsigned() {
			return name().endsWith("_UNSIGNED");
		}

	}

	public enum ParameterType {
		FORBIDDEN,
		OPTIONAL, 
		REQUIRED;
	}

	public enum MainDimension {
		LENGTH,
		PRECISION;
	}
	

}
