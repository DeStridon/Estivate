package com.estivate.context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.estivate.context.H2Context.H2Types;
import com.estivate.reconciliation.EntityColumn.ColumnDimension;
import com.estivate.reconciliation.EntityColumn.ProjectedColumnDefinition;

/**
 * H2 vendor rules (types, dimensions). No connection required.
 */
public class H2Dialect extends Dialect {

	// @Override
	// public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
	// 	if (StringUtils.isNotBlank(entityColumn.getDesignedType())) {
	// 		if ("DECIMAL".equalsIgnoreCase(entityColumn.getDesignedType())
	// 				|| "NUMERIC".equalsIgnoreCase(entityColumn.getDesignedType())) {
	// 			return new ColumnModel.ColumnFormat("DECIMAL", entityColumn.getDesignedPrecision(),
	// 					entityColumn.getDesignedScale(), false);
	// 		}
	// 		if (Arrays.asList("TEXT", "MEDIUMTEXT", "LONGTEXT").contains(entityColumn.getDesignedType())) {
	// 			return new ColumnModel.ColumnFormat(entityColumn.getDesignedType(), null, false);
	// 		}
	// 		return new ColumnModel.ColumnFormat(entityColumn.getDesignedType(), entityColumn.getDesignedLength(), entityColumn.getDesignedLength() == null);
	// 	}
	// 	ProjectedColumnDefinition defaults = databaseTypeFor(entityColumn.getType());
	// 	if (entityColumn.getType() == java.math.BigDecimal.class) {
	// 		Integer precision = entityColumn.getDesignedPrecision() != null ? entityColumn.getDesignedPrecision()
	// 				: entityColumn.getDesignedLength();
	// 		return new ColumnModel.ColumnFormat(defaults.getType(), precision, entityColumn.getDesignedScale(),
	// 				precision == null && entityColumn.getDesignedScale() == null);
	// 	}
	// 	return toColumnFormat(defaults);
	// }

	@Override
	public ProjectedColumnDefinition databaseTypeFor(Class<?> javaType) {
		if (javaType == Integer.class || javaType == int.class
				|| javaType == Long.class || javaType == long.class
				|| javaType == Short.class || javaType == short.class) {
			return ProjectedColumnDefinition.builder().type("INTEGER").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Byte.class || javaType == byte.class) {
			return ProjectedColumnDefinition.builder().type("TINYINT").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Float.class || javaType == float.class) {
			return ProjectedColumnDefinition.builder().type("FLOAT").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == Double.class || javaType == double.class) {
			return ProjectedColumnDefinition.builder().type("DOUBLE").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == java.math.BigDecimal.class) {
			return ProjectedColumnDefinition.builder().type("DECIMAL").columnDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == Boolean.class || javaType == boolean.class) {
			return ProjectedColumnDefinition.builder().type("BOOLEAN").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == String.class) {
			return ProjectedColumnDefinition.builder().type("CHARACTER VARYING").columnDimension(ColumnDimension.LENGTH_OPTIONAL).build();
		}
		if (javaType == Date.class || javaType == java.sql.Date.class
				|| javaType == LocalDateTime.class
				|| javaType == Instant.class) {
			return ProjectedColumnDefinition.builder().type("TIMESTAMP").columnDimension(ColumnDimension.PRECISION_OPTIONAL).build();
		}
		if (javaType == LocalDate.class) {
			return ProjectedColumnDefinition.builder().type("DATE").columnDimension(ColumnDimension.NONE).build();
		}
		if (javaType == byte[].class) {
			return ProjectedColumnDefinition.builder().type("BLOB").columnDimension(ColumnDimension.NONE).build();
		}
		throw new IllegalArgumentException("Unsupported type: " + javaType);
	}

	// private static ColumnModel.ColumnFormat toColumnFormat(ProjectedColumnDefinition definition) {
	// 	boolean noLength = definition.getMainDimension() == null
	// 			|| definition.getMainDimension() == ColumnDimension.NONE
	// 			|| definition.getDimension() == null;
	// 	return new ColumnModel.ColumnFormat(definition.getType(), definition.getDimension(), definition.getScale(), noLength);
	// }

	@Override
	public ColumnDimension mainDimensionForColumn(String columnType) {
		H2Types h2Type = H2Context.resolveH2Type(columnType);
		return h2Type != null ? h2Type.mainDimension : ColumnDimension.LENGTH_OPTIONAL;
	}
}
