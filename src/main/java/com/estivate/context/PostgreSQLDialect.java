package com.estivate.context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.estivate.reconciliation.EntityColumn.ColumnDimension;
import com.estivate.reconciliation.EntityColumn.ProjectedColumnDefinition;

/**
 * PostgreSQL vendor rules (types, dimensions). No connection required.
 */
public class PostgreSQLDialect extends Dialect {

	@Override
	public ProjectedColumnDefinition databaseTypeFor(Class<?> javaType) {
		if (javaType == Integer.class || javaType == int.class) {
			return ProjectedColumnDefinition.builder().type("INTEGER").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Long.class || javaType == long.class) {
			return ProjectedColumnDefinition.builder().type("BIGINT").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Short.class || javaType == short.class || javaType == Byte.class || javaType == byte.class) {
			return ProjectedColumnDefinition.builder().type("SMALLINT").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Float.class || javaType == float.class) {
			return ProjectedColumnDefinition.builder().type("REAL").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Double.class || javaType == double.class) {
			return ProjectedColumnDefinition.builder().type("DOUBLE PRECISION").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == java.math.BigDecimal.class) {
			return ProjectedColumnDefinition.builder().type("DECIMAL").columnDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == Boolean.class || javaType == boolean.class) {
			return ProjectedColumnDefinition.builder().type("BOOLEAN").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == String.class) {
			return ProjectedColumnDefinition.builder().type("VARCHAR").columnDimension(ColumnDimension.LENGTH_OPTIONAL).build();
		}
		if (javaType == Date.class || javaType == java.sql.Date.class
				|| javaType == java.sql.Timestamp.class
				|| javaType == LocalDateTime.class
				|| javaType == Instant.class) {
			return ProjectedColumnDefinition.builder().type("TIMESTAMP").columnDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == LocalDate.class) {
			return ProjectedColumnDefinition.builder().type("DATE").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == byte[].class) {
			return ProjectedColumnDefinition.builder().type("BYTEA").columnDimension(ColumnDimension.NONE).build();
		}
		throw new IllegalArgumentException("Unsupported type: " + javaType);
	}

	@Override
	public ColumnDimension mainDimensionForColumn(String columnType) {
		PostgreSQLTypes postgreSQLType = resolvePostgreSQLType(columnType);
		return postgreSQLType != null ? postgreSQLType.mainDimension : ColumnDimension.LENGTH_OPTIONAL;
	}

	@Override
	public ProjectedColumnDefinition stringEnumColumn(Class<?> enumType) {
		return ProjectedColumnDefinition.builder()
				.type("VARCHAR")
				.columnDimension(ColumnDimension.LENGTH_OPTIONAL)
				.build();
	}

	@Override
	public ProjectedColumnDefinition ordinalEnumColumn() {
		return ProjectedColumnDefinition.builder()
				.type("SMALLINT")
				.columnDimension(ColumnDimension.NONE)
				.build();
	}

	@Override
	protected String identifierQuote() {
		return "\"";
	}

	@Override
	public boolean qualifyUpdateTarget() {
		return false;
	}

	static PostgreSQLTypes resolvePostgreSQLType(String sqlType) {
		if (StringUtils.isBlank(sqlType)) {
			return null;
		}
		String upper = sqlType.trim().toUpperCase();
		int parenthesis = upper.indexOf('(');
		if (parenthesis > 0) {
			upper = upper.substring(0, parenthesis).trim();
		}
		String normalized = upper.replace(' ', '_');
		try {
			return PostgreSQLTypes.valueOf(normalized);
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	public enum PostgreSQLTypes {
		DECIMAL(ColumnDimension.PRECISION_OPTIONAL),
		NUMERIC(ColumnDimension.PRECISION_OPTIONAL),
		REAL(ColumnDimension.NONE),
		DOUBLE_PRECISION(ColumnDimension.NONE),
		SMALLINT(ColumnDimension.NONE),
		INTEGER(ColumnDimension.NONE),
		BIGINT(ColumnDimension.NONE),
		BOOLEAN(ColumnDimension.NONE),
		DATE(ColumnDimension.NONE),
		TIMESTAMP(ColumnDimension.PRECISION_OPTIONAL),
		TIME(ColumnDimension.PRECISION_OPTIONAL),
		CHARACTER(ColumnDimension.LENGTH_OPTIONAL),
		CHARACTER_VARYING(ColumnDimension.LENGTH_OPTIONAL),
		VARCHAR(ColumnDimension.LENGTH_OPTIONAL),
		TEXT(ColumnDimension.NONE),
		BYTEA(ColumnDimension.NONE),
		JSON(ColumnDimension.NONE),
		JSONB(ColumnDimension.NONE);

		public final ColumnDimension mainDimension;

		PostgreSQLTypes(ColumnDimension mainDimension) {
			this.mainDimension = mainDimension;
		}
	}

}
