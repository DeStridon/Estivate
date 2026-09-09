package com.estivate.reconciliation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.context.Context;
import com.estivate.index.Annotations.ColumnDefaultValue;
import com.estivate.util.DatabaseColumnAnnotation;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity-side column projection built with a live {@link Context}.
 * Holds only entity-design metadata not present on {@link DatabaseColumn}; the resolved
 * DB-comparable column is stored as {@link #tableField}.
 */
@Getter
@Builder
@AllArgsConstructor
public class EntityColumn {

	private final Field field;
	private Class<?> javaType;
	private DatabaseColumnAnnotation column;
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


	

	public EntityColumn(Context context, Field field) {
		
		this.field = field;
		this.primaryKey = field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class);
		this.nullable = FieldUtils.isNullable(field);
		this.autoIncrement = FieldUtils.isAutoIncrement(field);
		this.name = context.nameMapper.mapDatabaseField(field.getName());


		// 1. javaType & column
		boolean converted = FieldUtils.hasConvertAnnotation(field);
		if (converted) {
			this.javaType = FieldUtils.resolveConvertDatabaseType(field);
		} else {
			this.javaType = field.getType();
		}
		this.column = DatabaseColumnAnnotation.fromField(field);


		// 2. type & dimensionType
		// 2.1. column definition
		if(StringUtils.isNotBlank(column.getColumnDefinition().getType())) {
			this.type = column.getColumnDefinition().getType();
			this.dimensionType = context.dialect.mainDimensionForColumn(this.type);
		}
		// 2.2. enum (skipped when @Convert remapped javaType away from the enum)
		else if (javaType.isEnum()) {
			if (FieldUtils.isEnumeratedAsString(field)) {
				String enumValues = Arrays.stream(field.getType().getEnumConstants())
						.map(c -> "'" + ((Enum<?>) c).name() + "'")
						.collect(Collectors.joining(","));
				this.type = "ENUM(" + enumValues + ")";
				this.dimensionType = ColumnDimension.NONE;

			}
			else{
				this.type = "TINYINT";
				this.dimensionType = ColumnDimension.NONE;
			}
		}
		// 2.3. default (including @Convert → dialect type for converter DB type)
		else {
			projectedColumnDefinition = context.dialect.databaseTypeFor(this.javaType);
			this.type = projectedColumnDefinition.getType();
			this.dimensionType = projectedColumnDefinition.getColumnDimension();
		}


		// 3. dimension & scale
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

		if(dimensionType == ColumnDimension.LENGTH_REQUIRED && dimension == null) {
			if(projectedColumnDefinition == null){
				projectedColumnDefinition = context.dialect.databaseTypeFor(this.javaType);
			}
			this.dimension = projectedColumnDefinition.getDefaultDimension();
			this.scale = projectedColumnDefinition.getDefaultScale();
			if(this.dimension == null){
				throw new IllegalArgumentException("Length is required for column " + name);
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




	public DatabaseColumn asDatabaseColumn() {
		DatabaseColumn tableField = DatabaseColumn.builder()
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
		private ColumnDimension columnDimension;
		private Integer defaultDimension;
		private Integer defaultScale;
		
	}

	public enum ColumnDimension {
		NONE,
		LENGTH_OPTIONAL,
		LENGTH_REQUIRED,
		PRECISION_OPTIONAL;
	}

}
