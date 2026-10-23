package com.estivate.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Cached reflection snapshot for one entity class hierarchy.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassMeta {

	private static final Map<Class<?>, ClassMeta> CACHE = new ConcurrentHashMap<>();

	static final ClassMeta EMPTY = new ClassMeta(
		Collections.emptySet(),
		Collections.emptySet(),
		Collections.emptyMap(),
		null,
		Collections.emptySet(),
		Collections.emptySet(),
		Collections.emptyMap()
	);

	private final Set<Field> fields;
	private final Set<Field> persistableFields;
	private final Map<String, Field> fieldsByName;
	private final Field idField;
	private final Set<Method> methods;
	private final Set<Method> postLoadMethods;
	@Getter(AccessLevel.NONE)
	private final Map<Field, FieldBinding> fieldBindings;
	@Getter(AccessLevel.NONE)
	private final Map<Class<? extends Annotation>, Set<Method>> methodsByAnnotation = new ConcurrentHashMap<>();

	public static ClassMeta of(Class<?> objectClass) {
		if(objectClass == null || objectClass == Object.class) {
			return EMPTY;
		}

		ClassMeta cached = CACHE.get(objectClass);
		if(cached != null) {
			return cached;
		}

		ClassMeta built = build(objectClass);
		ClassMeta raced = CACHE.putIfAbsent(objectClass, built);
		return raced != null ? raced : built;
	}

	public Field findField(String attribute) {
		if(attribute == null) {
			return null;
		}
		return fieldsByName.get(attribute);
	}

	public static Field findField(Class<?> objectClass, String attribute) {
		if(objectClass == null || attribute == null) {
			return null;
		}
		return of(objectClass).findField(attribute);
	}

	public FieldBinding getFieldBinding(Field field) {
		if(field == null) {
			return null;
		}
		return fieldBindings.get(field);
	}

	public static FieldBinding fieldBinding(Field field) {
		if(field == null) {
			return null;
		}
		return of(field.getDeclaringClass()).getFieldBinding(field);
	}

	public Set<Method> methodsWithAnnotation(Class<? extends Annotation> annotation) {
		return methodsByAnnotation.computeIfAbsent(annotation, annotationType -> {
			Set<Method> annotated = new HashSet<>();
			for(Method method : methods) {
				if(method.isAnnotationPresent(annotationType)) {
					annotated.add(method);
				}
			}
			return Collections.unmodifiableSet(annotated);
		});
	}

	public static Set<Method> findMethodWithAnnotation(Class<?> objectClass, Class<? extends Annotation> annotation) {
		if(objectClass == null || annotation == null) {
			return Collections.emptySet();
		}
		return of(objectClass).methodsWithAnnotation(annotation);
	}

	private static ClassMeta build(Class<?> objectClass) {
		ClassMeta parent = of(objectClass.getSuperclass());

		// CachedEntity is the persistence base: keep its lifecycle methods, but do not expose its fields.
		Set<Field> fields;
		Map<Field, FieldBinding> fieldBindings = new HashMap<>();
		if(objectClass == CachedEntity.class) {
			fields = Collections.emptySet();
		}
		else {
			fields = new HashSet<>(parent.fields);
			for(Field field : objectClass.getDeclaredFields()) {
				if(field.isSynthetic()) {
					continue;
				}
				if(FieldUtils.isTransient(field) && !FieldUtils.hasProjectionAnnotation(field)) {
					continue;
				}
				if(field.getType() == org.slf4j.Logger.class) {
					continue;
				}
				field.setAccessible(true);
				fields.add(field);
				fieldBindings.put(field, FieldBinding.of(field));
			}
			fields = Collections.unmodifiableSet(fields);
		}

		LinkedHashSet<Field> persistable = new LinkedHashSet<>();
		Map<String, Field> byName = new HashMap<>();
		Field idField = null;
		for(Field field : fields) {
			byName.put(field.getName(), field);
			if(FieldUtils.isPersistable(field)) {
				persistable.add(field);
			}
			if(idField == null
					&& (field.isAnnotationPresent(javax.persistence.Id.class)
						|| field.isAnnotationPresent(jakarta.persistence.Id.class))) {
				idField = field;
			}
		}

		Set<Method> methods = new HashSet<>(parent.methods);
		for(Method method : objectClass.getDeclaredMethods()) {
			methods.add(method);
		}
		methods = Collections.unmodifiableSet(methods);

		Set<Method> postLoadMethods = new HashSet<>();
		for(Method method : methods) {
			if(method.isAnnotationPresent(javax.persistence.PostLoad.class)
					|| method.isAnnotationPresent(jakarta.persistence.PostLoad.class)) {
				postLoadMethods.add(method);
			}
		}

		return new ClassMeta(
			fields,
			Collections.unmodifiableSet(persistable),
			Collections.unmodifiableMap(byName),
			idField,
			methods,
			Collections.unmodifiableSet(postLoadMethods),
			Collections.unmodifiableMap(fieldBindings)
		);
	}

	/**
	 * Per-field write/read strategy for {@code @Convert} and {@code @Enumerated},
	 * built once with the declaring class {@link ClassMeta}.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class FieldBinding {

		public enum EnumMode {
			NONE,
			STRING,
			ORDINAL
		}

		@Getter
		private final Field field;
		private final Object converter;
		private final EnumMode enumMode;

		static FieldBinding of(Field field) {
			Class<?> converterClass = FieldUtils.getBestOption(field,
				javax.persistence.Convert.class, javax.persistence.Convert::converter,
				jakarta.persistence.Convert.class, jakarta.persistence.Convert::converter);
			Object converter = instantiateConverter(converterClass);

			EnumMode enumMode = EnumMode.NONE;
			if(converter == null && field.getType().isEnum()) {
				javax.persistence.Enumerated javaxEnumerated = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
				jakarta.persistence.Enumerated jakartaEnumerated = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if((javaxEnumerated != null && javaxEnumerated.value() == javax.persistence.EnumType.STRING)
						|| (jakartaEnumerated != null && jakartaEnumerated.value() == jakarta.persistence.EnumType.STRING)) {
					enumMode = EnumMode.STRING;
				}
				else {
					enumMode = EnumMode.ORDINAL;
				}
			}

			return new FieldBinding(field, converter, enumMode);
		}

		private static Object instantiateConverter(Class<?> converterClass) {
			if(converterClass == null) {
				return null;
			}
			try {
				Constructor<?> constructor = converterClass.getConstructor();
				constructor.setAccessible(true);
				return constructor.newInstance();
			}
			catch(Exception exception) {
				throw new IllegalStateException("Cannot instantiate AttributeConverter " + converterClass.getName(), exception);
			}
		}

		public boolean hasConverter() {
			return converter != null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Object toDatabaseColumn(Object value) {
			if(converter instanceof javax.persistence.AttributeConverter) {
				return ((javax.persistence.AttributeConverter) converter).convertToDatabaseColumn(value);
			}
			if(converter instanceof jakarta.persistence.AttributeConverter) {
				return ((jakarta.persistence.AttributeConverter) converter).convertToDatabaseColumn(value);
			}
			if(enumMode == EnumMode.NONE || value == null) {
				return value;
			}
			if(enumMode == EnumMode.STRING) {
				return value.toString();
			}
			return ((Enum<?>) value).ordinal();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Object toEntityAttribute(Object databaseValue) {
			if(converter instanceof javax.persistence.AttributeConverter) {
				return ((javax.persistence.AttributeConverter) converter).convertToEntityAttribute(databaseValue);
			}
			if(converter instanceof jakarta.persistence.AttributeConverter) {
				return ((jakarta.persistence.AttributeConverter) converter).convertToEntityAttribute(databaseValue);
			}
			return databaseValue;
		}
	}
}
