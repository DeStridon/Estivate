package com.estivate.context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.estivate.reconciliation.ColumnTypeParts;
import com.estivate.reconciliation.ProjectedColumn.ColumnDimension;
import com.estivate.reconciliation.ProjectedColumn.ProjectedColumnDefinition;
import com.estivate.reconciliation.TableField;

/**
 * MySQL vendor rules (types, dimensions, DDL formatting). No connection required.
 */
public class MySQLDialect extends Dialect {

	// @Override
	// public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
	// 	if (StringUtils.isNotBlank(entityColumn.getDesignedType())) {
	// 		String designedType = entityColumn.getDesignedType().trim();
	// 		if (designedType.toUpperCase().startsWith("ENUM")) {
	// 			return new ColumnModel.ColumnFormat(designedType, null, true);
	// 		}

	// 		MySQLDialect.MySQLTypes mysqlType = MySQLDialect.resolveMySQLType(designedType);
	// 		if (mysqlType == null) {
	// 			throw new IllegalArgumentException("Unsupported type: " + designedType);
	// 		}
	// 		else if(mysqlType.mainDimension == ColumnDimension.LENGTH_REQUIRED){
	// 			if (entityColumn.getDesignedLength() == null) {
	// 				throw new IllegalArgumentException("Length is required for " + designedType);
	// 			}
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedLength(), null, false);
	// 		}
	// 		else if (mysqlType.mainDimension == ColumnDimension.LENGTH_OPTIONAL) {
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedLength(), null, false);
	// 		}
			
	// 		else if (mysqlType.mainDimension == ColumnDimension.PRECISION_OPTIONAL) {
	// 			return new ColumnModel.ColumnFormat(designedType, entityColumn.getDesignedPrecision(), entityColumn.getDesignedScale(), false);
	// 		}

	// 		throw new IllegalArgumentException("Unsupported type: " + designedType);
	// 	}

	// 	ProjectedColumnDefinition defaults = databaseTypeFor(entityColumn.getType());
	// 	if (entityColumn.getType() == java.math.BigDecimal.class) {
	// 		Integer precision = entityColumn.getDesignedPrecision() != null ? entityColumn.getDesignedPrecision() : entityColumn.getDesignedLength();
	// 		return new ColumnModel.ColumnFormat(defaults.getType(), precision, entityColumn.getDesignedScale(), precision == null && entityColumn.getDesignedScale() == null);
	// 	}
	// 	if (entityColumn.getType() == String.class && entityColumn.getDesignedLength() != null) {
	// 		return new ColumnModel.ColumnFormat(defaults.getType(), entityColumn.getDesignedLength(), false);
	// 	}
	// 	return toColumnFormat(defaults);
	// }

	@Override
	public ProjectedColumnDefinition databaseTypeFor(Class<?> javaType) {
		if (javaType == Integer.class || javaType == int.class) {
			return ProjectedColumnDefinition.builder().type("INT").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Long.class || javaType == long.class) {
			return ProjectedColumnDefinition.builder().type("BIGINT").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Short.class || javaType == short.class) {
			return ProjectedColumnDefinition.builder().type("SMALLINT").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Byte.class || javaType == byte.class) {
			return ProjectedColumnDefinition.builder().type("TINYINT").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Float.class || javaType == float.class) {
			return ProjectedColumnDefinition.builder().type("FLOAT").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Double.class || javaType == double.class) {
			return ProjectedColumnDefinition.builder().type("DOUBLE").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == java.math.BigDecimal.class) {
			return ProjectedColumnDefinition.builder().type("DECIMAL").mainDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == Boolean.class || javaType == boolean.class) {
			return ProjectedColumnDefinition.builder().type("BIT").dimension(1).mainDimension(ColumnDimension.LENGTH_REQUIRED).build();
		}
		if (javaType == String.class) {
			return ProjectedColumnDefinition.builder().type("VARCHAR").dimension(255).mainDimension(ColumnDimension.LENGTH_REQUIRED).build();
		}
		if (javaType == Date.class || javaType == java.sql.Date.class
				|| javaType == java.sql.Timestamp.class
				|| javaType == LocalDateTime.class
				|| javaType == Instant.class) {
			return ProjectedColumnDefinition.builder().type("DATETIME").mainDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == LocalDate.class) {
			return ProjectedColumnDefinition.builder().type("DATE").mainDimension(ColumnDimension.NONE).build();
		}
		if (javaType == byte[].class) {
			return ProjectedColumnDefinition.builder().type("BLOB").mainDimension(ColumnDimension.NONE).build();
		}
		throw new IllegalArgumentException("Unsupported type: " + javaType);
	}

	// private static ColumnModel.ColumnFormat toColumnFormat(ProjectedColumnDefinition definition) {
	// 	boolean noLength = definition.getMainDimension() == null
	// 			|| definition.getMainDimension() == ColumnDimension.NONE
	// 			|| definition.getDimension() == null;
	// 	return new ColumnModel.ColumnFormat(definition.getType(), definition.getDimension(), definition.getScale(), noLength);
	// }

	// @Override
	// public MainDimension mainDimensionForColumn(String columnType) {
	// 	MySQLTypes mysqlType = MySQLContext.resolveMySQLType(columnType);
	// 	return mysqlType != null ? mysqlType.mainDimension : MainDimension.LENGTH;
	// }


	public ColumnDimension mainDimensionForColumn(String columnType) {
		MySQLTypes mysqlType = MySQLDialect.resolveMySQLType(columnType);
		return mysqlType != null ? mysqlType.mainDimension : ColumnDimension.LENGTH_OPTIONAL;
	}

	@Override
	public String formatColumnTypeAndSize(TableField tableField) {
		if (tableField.getType() != null && tableField.getType().toUpperCase().startsWith("ENUM")) {
			return tableField.getType();
		}

		ColumnTypeParts parts = ColumnTypeParts.parse(tableField.getType());
		String typeSource = parts != null ? parts.getType() : tableField.getType();
		MySQLTypes mysqlType = MySQLDialect.resolveMySQLType(typeSource);
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

		if (mysqlType.mainDimension == ColumnDimension.PRECISION_OPTIONAL && dimension != null) {
			sqlType.append("(").append(dimension);
			if (scale != null) {
				sqlType.append(",").append(scale);
			}
			sqlType.append(")");
		} else if ((mysqlType.mainDimension == ColumnDimension.LENGTH_OPTIONAL || mysqlType.mainDimension == ColumnDimension.LENGTH_REQUIRED) && dimension != null) {
			sqlType.append("(").append(dimension).append(")");
		}

		if (mysqlType.isUnsigned()) {
			sqlType.append(" UNSIGNED");
		}

		return sqlType.toString();
	}

	public enum MySQLTypes {
		// Numeric types
		DECIMAL(ColumnDimension.PRECISION_OPTIONAL),
		DECIMAL_UNSIGNED(ColumnDimension.PRECISION_OPTIONAL),
		NUMERIC(ColumnDimension.PRECISION_OPTIONAL),
		NUMERIC_UNSIGNED(ColumnDimension.PRECISION_OPTIONAL),
		FLOAT(ColumnDimension.PRECISION_OPTIONAL),
		FLOAT_UNSIGNED(ColumnDimension.PRECISION_OPTIONAL),
		DOUBLE(ColumnDimension.PRECISION_OPTIONAL),
		DOUBLE_UNSIGNED(ColumnDimension.PRECISION_OPTIONAL),
		TINYINT(ColumnDimension.LENGTH_OPTIONAL),
		TINYINT_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		SMALLINT(ColumnDimension.LENGTH_OPTIONAL),
		SMALLINT_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		MEDIUMINT(ColumnDimension.LENGTH_OPTIONAL),
		MEDIUMINT_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		INT(ColumnDimension.LENGTH_OPTIONAL),
		INT_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		INTEGER(ColumnDimension.LENGTH_OPTIONAL),
		INTEGER_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		BIGINT(ColumnDimension.LENGTH_OPTIONAL),
		BIGINT_UNSIGNED(ColumnDimension.LENGTH_OPTIONAL),
		BIT(ColumnDimension.LENGTH_OPTIONAL),

		// Date and time types
		DATE(ColumnDimension.NONE),
		DATETIME(ColumnDimension.PRECISION_OPTIONAL),
		TIMESTAMP(ColumnDimension.PRECISION_OPTIONAL),
		TIME(ColumnDimension.PRECISION_OPTIONAL),
		YEAR(ColumnDimension.LENGTH_OPTIONAL),

		// String types
		CHAR(ColumnDimension.LENGTH_REQUIRED),
		VARCHAR(ColumnDimension.LENGTH_REQUIRED),
		TEXT(ColumnDimension.NONE),
		TINYTEXT(ColumnDimension.NONE),
		MEDIUMTEXT(ColumnDimension.NONE),
		LONGTEXT(ColumnDimension.NONE),

		// Binary types
		BINARY(ColumnDimension.LENGTH_OPTIONAL),
		VARBINARY(ColumnDimension.LENGTH_REQUIRED),
		BLOB(ColumnDimension.NONE),
		TINYBLOB(ColumnDimension.NONE),
		MEDIUMBLOB(ColumnDimension.NONE),
		LONGBLOB(ColumnDimension.NONE),

		// Misc types
		JSON(ColumnDimension.NONE),
		ENUM(ColumnDimension.NONE),
		SET(ColumnDimension.NONE),
		BOOLEAN(ColumnDimension.NONE),
		BOOL(ColumnDimension.NONE);

		public final ColumnDimension mainDimension;
		
		MySQLTypes(ColumnDimension mainDimension) {
			this.mainDimension = mainDimension;
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

	static MySQLTypes resolveMySQLType(String sqlType) {
		if (sqlType == null || sqlType.isBlank()) {
			return null;
		}
		String upper = sqlType.trim().toUpperCase();
		if (upper.startsWith("ENUM")) {
			return MySQLTypes.ENUM;
		}
		String normalized = upper.replace(' ', '_');
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
}
