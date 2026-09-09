package com.estivate.util;

import java.lang.reflect.Field;

import com.estivate.reconciliation.DatabaseColumnDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Unified view of the {@code @Column} attributes Estivate uses, whether the
 * annotation is {@code javax.persistence.Column} or {@code jakarta.persistence.Column}.
 */
@Getter
@Builder
@AllArgsConstructor
public class DatabaseColumnAnnotation {

	private Integer length;
	private Integer precision;
	private Integer scale;
	private Boolean nullable;

	private DatabaseColumnDefinition columnDefinition;
	
	public DatabaseColumnDefinition getColumnDefinition() {
		if(columnDefinition == null) {
			return new DatabaseColumnDefinition(null, null, null);
		}
		return columnDefinition;
	}

	/**
	 * Returns the {@code @Column} attributes Estivate uses, whether javax or jakarta.
	 * {@code null} when the field has neither annotation.
	 */
	public static DatabaseColumnAnnotation fromField(Field field) {
        
		javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
		if (javaxColumn != null) {

			Integer dimension = javaxColumn.length() != 255 ? javaxColumn.length() : null;
			Integer precision = javaxColumn.precision();


			return new DatabaseColumnAnnotation(
					javaxColumn.length() != 255 ? javaxColumn.length() : null,
					javaxColumn.precision(),
					javaxColumn.scale(),
					javaxColumn.nullable(),
					DatabaseColumnDefinition.parse(javaxColumn.columnDefinition()));
		}

		jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
		if (jakartaColumn != null) {
			return new DatabaseColumnAnnotation(
					jakartaColumn.length() != 255 ? jakartaColumn.length() : null,
					jakartaColumn.precision(),
					jakartaColumn.scale(),
					jakartaColumn.nullable(),
                    DatabaseColumnDefinition.parse(jakartaColumn.columnDefinition()));
		}

		return DatabaseColumnAnnotation.builder().build();

	}



}
