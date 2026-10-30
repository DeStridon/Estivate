package com.estivate.context;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.estivate.reconciliation.EntityColumn.ColumnDimension;
import com.estivate.reconciliation.EntityColumn.ProjectedColumnDefinition;
import com.estivate.reconciliation.DatabaseColumn;

/**
 * Vendor-specific database rules that do not require a live connection
 * (Java→SQL defaults, type dimensions, DDL type formatting).
 */
public abstract class Dialect {

	//public abstract ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn);

	/**
	 * Default SQL type (and length/precision when the dialect has one) for a Java field type
	 * when no {@code columnDefinition} is set.
	 * <p>Examples (MySQL): {@code String} → {@code VARCHAR(255)}, {@code Boolean} → {@code BIT(1)}.
	 */
	public abstract ProjectedColumnDefinition databaseTypeFor(Class<?> javaType);

	public abstract ColumnDimension mainDimensionForColumn(String columnType);

	/**
	 * Formats a {@link DatabaseColumn} type for DDL (e.g. {@code VARCHAR(255)}, {@code DECIMAL(10,2)}).
	 */
	public String formatColumnTypeAndSize(DatabaseColumn tableField) {
		StringBuilder sqlType = new StringBuilder(tableField.getType());
		if (tableField.getDimension() != null) {
			sqlType.append("(").append(tableField.getDimension());
			if (tableField.getScale() != null) {
				sqlType.append(",").append(tableField.getScale());
			}
			sqlType.append(")");
		}
		return sqlType.toString();
	}

	/**
	 * SQL type for a {@code @Enumerated(EnumType.STRING)} field.
	 * MySQL and H2 (MySQL mode) keep a native {@code ENUM}; PostgreSQL uses a string type.
	 */
	public ProjectedColumnDefinition stringEnumColumn(Class<?> enumType) {
		String enumValues = Arrays.stream(enumType.getEnumConstants())
				.map(constant -> "'" + ((Enum<?>) constant).name() + "'")
				.collect(Collectors.joining(","));
		return ProjectedColumnDefinition.builder()
				.type("ENUM(" + enumValues + ")")
				.columnDimension(ColumnDimension.NONE)
				.build();
	}

	/** SQL type for an ordinal {@code @Enumerated} field. */
	public ProjectedColumnDefinition ordinalEnumColumn() {
		return ProjectedColumnDefinition.builder()
				.type("TINYINT")
				.columnDimension(ColumnDimension.NONE)
				.build();
	}

	/** Quotes an identifier with the dialect's quote character ({@code `} or {@code "}). */
	public String quoteIdentifier(String identifier) {
		String quote = identifierQuote();
		return quote + identifier.replace(quote, quote + quote) + quote;
	}

	protected String identifierQuote() {
		return "`";
	}

	/**
	 * MySQL and H2 accept {@code SET table.column}. PostgreSQL only accepts the column name.
	 */
	public boolean qualifyUpdateTarget() {
		return true;
	}

}
