package com.estivate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

import com.estivate.util.FieldUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Projection {

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Attribute {
		public Class<?> entity();
		public String attribute();
		public String alias() default "";
		Class<? extends java.util.function.Function> transformer() default IdentityFunction.class;
	}
	
	public static class IdentityFunction implements java.util.function.Function{
		@Override public Object apply(Object t) { return t; }
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
	 * <p>
	 * Two directions are supported:
	 * <ul>
	 *   <li><b>Source → target:</b> Fields in the source (projection) with @Projection.Attribute pointing to the target entity class are copied to the target's corresponding attribute.</li>
	 *   <li><b>Target pulls from source:</b> Fields in the target with @Projection.Attribute (entity, attribute) are filled from the source projection field that has the same entity/attribute in its annotation.</li>
	 * </ul>
	 *
	 * @param source The source projection object containing @Projection.Attribute annotations
	 * @param target The target entity object to populate with values from the projection object
	 * @throws RuntimeException if fields cannot be accessed
	 */
	public static <T>  T mapTo(Object source, T target) {
		if (source == null || target == null) {
			return null;
		}
		
		try {
			Class<?> targetEntityClass = target.getClass();
			
			// 1) Source → target: fields in source with @Attribute pointing to target entity
			Set<Field> sourceFields = FieldUtils.getEntityFields(source.getClass());
			for (Field projectionField : sourceFields) {
				Attribute attributeAnnotation = projectionField.getDeclaredAnnotation(Attribute.class);
				if (attributeAnnotation == null) {
					continue;
				}
				if (!attributeAnnotation.entity().equals(targetEntityClass)) {
					continue;
				}
				projectionField.setAccessible(true);
				Object value = projectionField.get(source);

				if(attributeAnnotation.transformer() != IdentityFunction.class){
					java.util.function.Function<?, ?> transformer = null;
					try {
						transformer = attributeAnnotation.transformer().getConstructor().newInstance();
					} catch (Exception e) {
						log.error("Impossible to create transformer for field " + projectionField.getName(), e);
					}
					value = ((java.util.function.Function<Object, Object>) transformer).apply(value);
				}

				Field entityField = FieldUtils.findField(targetEntityClass, attributeAnnotation.attribute());
				if (entityField == null) {
					continue;
				}
				entityField.setAccessible(true);
				entityField.set(target, value);
			}

			// 2) Target pulls from source: fields in target with @Attribute (entity, attribute) get value from matching source field
			Set<Field> targetFields = FieldUtils.getEntityFields(targetEntityClass);
			for (Field targetField : targetFields) {
				Attribute attributeAnnotation = targetField.getDeclaredAnnotation(Attribute.class);
				if (attributeAnnotation == null) {
					continue;
				}
				Field sourceField = findSourceFieldByAttribute(source.getClass(), attributeAnnotation.entity(), attributeAnnotation.attribute());
				if (sourceField == null) {
					continue;
				}
				sourceField.setAccessible(true);
				Object value = sourceField.get(source);
				if(attributeAnnotation.transformer() != IdentityFunction.class){
					java.util.function.Function<?, ?> transformer = null;
					try {
						transformer = attributeAnnotation.transformer().getConstructor().newInstance();
					} catch (Exception e) {
						log.error("Impossible to create transformer for field " + sourceField.getName(), e);
					}
					value = ((java.util.function.Function<Object, Object>) transformer).apply(value);
				}
				targetField.setAccessible(true);
				targetField.set(target, value);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to map projection object to entity: " + target.getClass().getName(), e);
		}

		return target;
	}

	/**
	 * Maps a projection object to a new instance of the given target class.
	 * Instantiates the target class via its no-arg constructor, then delegates to {@link #mapTo(Object, Object)}.
	 *
	 * @param source The source projection object
	 * @param targetClass The class of the target entity to instantiate and populate
	 * @param <T> The target type
	 * @return A new instance of targetClass populated from source, or null if source is null
	 * @throws RuntimeException if instantiation or mapping fails
	 */
	public static <T> T mapTo(Object source, Class<T> targetClass) {
		if (source == null || targetClass == null) {
			return null;
		}
		try {
			T target = targetClass.getDeclaredConstructor().newInstance();
			mapTo(source, target);
			return target;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate or map to: " + targetClass.getName(), e);
		}
	}

	/**
	 * Finds a field in the given class (or its superclasses) that has @Projection.Attribute with the given entity and attribute.
	 */
	private static Field findSourceFieldByAttribute(Class<?> sourceClass, Class<?> entity, String attribute) {
		for (Field field : FieldUtils.getEntityFields(sourceClass)) {
			Attribute ann = field.getDeclaredAnnotation(Attribute.class);
			if (ann != null && ann.entity().equals(entity) && ann.attribute().equals(attribute)) {
				return field;
			}
		}
		return null;
	}
	
}
