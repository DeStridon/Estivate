package com.estivate.reconciliation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.context.Context;
import com.estivate.index.Annotations.ColumnDefaultValue;
import com.estivate.util.ColumnAnnotation;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity-side column projection built with a live {@link Context}.
 * Holds only entity-design metadata not present on {@link TableField}; the resolved
 * DB-comparable column is stored as {@link #tableField}.
 */
@Getter
@Builder
@AllArgsConstructor
public class ProjectedColumn {

	private final Field field;
	private Class<?> javaType;
	private ColumnAnnotation column;
	private ProjectedColumnDefinition projectedColumnDefinition;


	// TableField fields
	private final @NonNull String name;
	private @NonNull String type;

	private Integer dimension;
	private Integer scale;
	private boolean nullable = true;
	private String defaultValue;
	private boolean autoIncrement = false;


	/** Java field type (may be remapped for converters/enums). */

	private ColumnDimension dimensionType;
	private String charset;
	private String collation;
	private Boolean primaryKey;


	

	public ProjectedColumn(Context context, Field field) {
		
		this.field = field;
		this.primaryKey = field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class);
		this.nullable = FieldUtils.isNullable(field);
		this.autoIncrement = FieldUtils.isAutoIncrement(field);
		this.name = context.nameMapper.mapDatabaseField(field.getName());


		// 1. javaType
		// 1.1. convert annotation
		if (field.getDeclaredAnnotation(javax.persistence.Convert.class) != null || field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
			this.javaType = String.class;
		}
		// 1.2. default
		else {
			this.javaType = field.getType();
		}
		this.column = FieldUtils.getColumnAnnotation(field);


		// 2. designedType
		// 2.1. column definition
		if(StringUtils.isNotBlank(column.getColumnDefinition().getType())) {
			this.type = column.getColumnDefinition().getType();
			this.dimensionType = context.dialect.mainDimensionForColumn(this.type);
		}
		// 2.2. enum
		else if (field.getType().isEnum()) {
			if (FieldUtils.isEnumeratedAsString(field)) {
				String enumValues = Arrays.stream(field.getType().getEnumConstants())
						.map(c -> "'" + ((Enum<?>) c).name() + "'")
						.collect(Collectors.joining(","));
				this.type = "ENUM(" + enumValues + ")";
				this.dimensionType = ColumnDimension.NONE;
				this.dimension = null;
			}
			else{
				this.type = "TINYINT";
				this.dimensionType = ColumnDimension.NONE;
			}
		}
		// 2.4. default
		else {
			ProjectedColumnDefinition projectedColumnDefinition = context.dialect.databaseTypeFor(this.javaType);
			this.type = projectedColumnDefinition.getType();
			this.dimension = projectedColumnDefinition.getDimension();
			this.scale = projectedColumnDefinition.getScale();
			this.dimensionType = projectedColumnDefinition.getMainDimension();
		}

		// 3. dimensions
		dimensionType = context.dialect.mainDimensionForColumn(this.type);
		// 3.1. dimension is length
		if(dimensionType == ColumnDimension.LENGTH_OPTIONAL || dimensionType == ColumnDimension.LENGTH_REQUIRED) {
			// 3.1.1. length from annotation
			if(column.getLength() != null && column.getLength() > 0) {
				this.dimension = column.getLength();
			}
			// 3.1.2. length from column definition
			else if(column.getColumnDefinition().getDimension() != null) {
				this.dimension = column.getColumnDefinition().getDimension();
			}
		}

		// 3.2. dimension is precision
		else if(dimensionType == ColumnDimension.PRECISION_OPTIONAL) {
			// 3.2.1. precision from annotation
			if(column.getPrecision() != null && column.getPrecision() > 0) {
				this.dimension = column.getPrecision();
				if(column.getScale() != null) {
					this.scale = column.getScale();
				}
			}
			// 3.2.2. precision from column definition
			else if(column.getColumnDefinition().getDimension() != null) {
				this.dimension = column.getColumnDefinition().getDimension();
				if(column.getColumnDefinition().getScale() != null) {
					this.scale = column.getColumnDefinition().getScale();
				}
			}
		}

		// 4. nullable
		// 4.1. primitive or NotNull annotation => not nullable
        if (field.getType().isPrimitive() || FieldUtils.hasAnnotationByName(field, "javax.validation.constraints.NotNull") || FieldUtils.hasAnnotationByName(field, "jakarta.validation.constraints.NotNull")) {
           nullable = false;
        }
		// 4.2. column definition
		else if(column.getNullable() != null) {
			nullable = column.getNullable();
		}
		// 4.3. default => nullable
		else { 
			nullable = true;
		}

		// 5. defaultValue
		ColumnDefaultValue columnDefaultValue = field.getDeclaredAnnotation(ColumnDefaultValue.class);
		if (columnDefaultValue != null) {
			defaultValue = columnDefaultValue.value();
		}

		if(type == null) {
			return;
		}

		// buildTableField(context, nullable, autoIncrement, defaultValue);

	}


	// private void buildTableField(Context context,boolean nullable, boolean autoIncrement, String defaultValue) {
	// 	EntityColumn snapshot = EntityColumn.builder()
	// 			.name(field.getName())
	// 			.type(javaType)
	// 			.designedType(designedType)
	// 			.designedLength(designedLength)
	// 			.designedPrecision(designedPrecision)
	// 			.designedScale(designedScale)
	// 			.defaultValue(defaultValue)
	// 			.charset(charset)
	// 			.collation(collation)
	// 			.isNullable(nullable)
	// 			.isAutoIncrement(autoIncrement)
	// 			.isPrimaryKey(primaryKey)
	// 			.build();
	// 	ColumnFormat columnFormat = context.getColumnFormat(snapshot);
	// 	String sqlType = columnFormat.getType() != null ? columnFormat.getType() : designedType;
	// 	//this.mainDimension = context.mainDimensionForColumn(sqlType);
				
	// }

	public TableField getTableField() {
		TableField tableField = TableField.builder()
				.name(name)
				.type(type)
				.dimension(dimension)
				.scale(scale)
				.nullable(nullable)
				.defaultValue(defaultValue)
				.autoIncrement(autoIncrement)
				.build();

		return tableField;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProjectedColumnDefinition {
		private String type;
		private Integer dimension;
		private Integer scale;
		private ColumnDimension mainDimension;
	}

	public enum ColumnDimension {
		NONE,
		LENGTH_OPTIONAL,
		LENGTH_REQUIRED,
		PRECISION_OPTIONAL;
	}

}
