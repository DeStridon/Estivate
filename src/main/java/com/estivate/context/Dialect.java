package com.estivate.context;

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

}
