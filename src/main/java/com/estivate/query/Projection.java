package com.estivate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

import com.estivate.util.FieldUtils;

public class Projection {

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Attribute {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Count {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface CountDistinct {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Sum {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Min {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Max {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Avg {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Function {
		public Class<?> entity();
		public String attribute();
		public String alias();
		public String functionPrefix();
		public String functionSuffix();
	}
	
	/**
	 * Maps a projection object to an entity object based on @Projection.Attribute annotations.
	 * Only fields with @Projection.Attribute annotations pointing to the target entity class are mapped.
	 * 
	 * @param <E> The target entity type
	 * @param projectionObject The source projection object containing @Projection.Attribute annotations
	 * @param targetEntity The target entity object to populate with values from the projection object
	 * @return The target entity object with values mapped from the projection object
	 * @throws RuntimeException if fields cannot be accessed
	 */
	public static void mapProjectionToEntity(Object projectionObject, Object targetEntity) {
		if (projectionObject == null || targetEntity == null) {
			return;
		}
		
		try {
			Class<?> targetEntityClass = targetEntity.getClass();
			
			// Get all fields from the projection class
			Set<Field> projectionFields = FieldUtils.getEntityFields(projectionObject.getClass());
			
			for (Field projectionField : projectionFields) {
				// Check if field has @Projection.Attribute annotation
				Attribute attributeAnnotation = projectionField.getDeclaredAnnotation(Attribute.class);
				if (attributeAnnotation == null) {
					continue;
				}
				
				// Check if the annotation points to the target entity class
				if (!attributeAnnotation.entity().equals(targetEntityClass)) {
					continue;
				}
				
				// Get the value from the projection field
				projectionField.setAccessible(true);
				Object value = projectionField.get(projectionObject);
				
				// Find the corresponding field in the target entity
				Field entityField = FieldUtils.findField(targetEntityClass, attributeAnnotation.attribute());
				if (entityField == null) {
					continue; // Skip if field not found in entity
				}
				
				// Set the value in the entity field
				entityField.setAccessible(true);
				entityField.set(targetEntity, value);
			}
			
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to map projection object to entity: " + targetEntity.getClass().getName(), e);
		}
	}
	
}
