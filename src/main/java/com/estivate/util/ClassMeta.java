package com.estivate.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.estivate.Entity.InsertDate;
import com.estivate.Entity.UpdateDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Cached reflection snapshot for one entity class hierarchy,
 * including insert/update field sets.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassMeta {

	private static final Map<Class<?>, ClassMeta> CACHE = new ConcurrentHashMap<>();

	static final ClassMeta EMPTY = new ClassMeta(
		Collections.emptySet(),
		Collections.emptySet(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyMap(),
		null,
		null,
		false,
		Collections.emptyList(),
		Collections.emptySet(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyList(),
		Collections.emptyMap()
	);

	private final Set<Field> fields;
	private final Set<Field> persistableFields;
	/** Persistable columns for INSERT/UPDATE (excludes {@code @Id}). */
	private final List<Field> writableFields;
	private final List<Field> insertDateFields;
	private final List<Field> updateDateFields;
	private final Map<String, Field> fieldsByName;
	private final Field idField;
	/** No-arg constructor used to instantiate mapped entities; may be {@code null}. */
	private final Constructor<?> noArgConstructor;
	/** True when any field uses {@code @Projection.*} / nested select expansion. */
	private final boolean hasProjectionSelectFields;
	/** Plain field names for {@code selectAll} when {@link #hasProjectionSelectFields} is false. */
	private final List<String> plainSelectFieldNames;
	private final Set<Method> methods;
	private final List<Method> postLoadMethods;
	private final List<Method> prePersistMethods;
	private final List<Method> postPersistMethods;
	private final List<Method> preUpdateMethods;
	private final List<Method> postUpdateMethods;
	private final List<Method> preRemoveMethods;
	private final List<Method> postRemoveMethods;
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

	public void invokePrePersist(Object entity) {
		invokeLifecycle(prePersistMethods, entity);
	}

	public void invokePostPersist(Object entity) {
		invokeLifecycle(postPersistMethods, entity);
	}

	public void invokePreUpdate(Object entity) {
		invokeLifecycle(preUpdateMethods, entity);
	}

	public void invokePostUpdate(Object entity) {
		invokeLifecycle(postUpdateMethods, entity);
	}

	public void invokePreRemove(Object entity) {
		invokeLifecycle(preRemoveMethods, entity);
	}

	public void invokePostRemove(Object entity) {
		invokeLifecycle(postRemoveMethods, entity);
	}

	public void invokePostLoad(Object entity) {
		invokeLifecycle(postLoadMethods, entity);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if(noArgConstructor == null) {
			throw new NoSuchMethodException("No no-arg constructor");
		}
		return (T) noArgConstructor.newInstance();
	}

	@SneakyThrows
	private static void invokeLifecycle(List<Method> lifecycleMethods, Object entity) {
		if(lifecycleMethods.isEmpty()) {
			return;
		}
		for(Method method : lifecycleMethods) {
			method.invoke(entity);
		}
	}

	@SneakyThrows
	private static Constructor<?> resolveNoArgConstructor(Class<?> objectClass) {
		try {
			Constructor<?> constructor = objectClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor;
		}
		catch(NoSuchMethodException ignored) {
			return null;
		}
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
		for(Field field : fields) {
			byName.put(field.getName(), field);
			if(FieldUtils.isPersistable(field)) {
				persistable.add(field);
			}
		}

		List<Field> writableFields = new ArrayList<>();
		List<Field> insertDateFields = new ArrayList<>();
		List<Field> updateDateFields = new ArrayList<>();
		Field idField = null;

		for(Field field : persistable) {
			boolean isId = field.isAnnotationPresent(javax.persistence.Id.class)
				|| field.isAnnotationPresent(jakarta.persistence.Id.class);
			if(isId) {
				if(idField != null) {
					throw new IllegalArgumentException("Multiple id fields found for entity: " + objectClass.getName());
				}
				idField = field;
				continue;
			}

			writableFields.add(field);
			if(field.isAnnotationPresent(InsertDate.class)
					&& (field.getType() == java.util.Date.class || field.getType() == java.sql.Date.class)) {
				insertDateFields.add(field);
			}
			if(field.isAnnotationPresent(UpdateDate.class)
					&& (field.getType() == java.util.Date.class || field.getType() == java.sql.Date.class)) {
				updateDateFields.add(field);
			}
		}

		Set<Method> methods = new HashSet<>(parent.methods);
		for(Method method : objectClass.getDeclaredMethods()) {
			methods.add(method);
		}
		methods = Collections.unmodifiableSet(methods);

		List<Method> postLoadMethods = new ArrayList<>();
		List<Method> prePersistMethods = new ArrayList<>();
		List<Method> postPersistMethods = new ArrayList<>();
		List<Method> preUpdateMethods = new ArrayList<>();
		List<Method> postUpdateMethods = new ArrayList<>();
		List<Method> preRemoveMethods = new ArrayList<>();
		List<Method> postRemoveMethods = new ArrayList<>();

		for(Method method : methods) {
			boolean lifecycle = false;
			if(method.isAnnotationPresent(javax.persistence.PostLoad.class)
					|| method.isAnnotationPresent(jakarta.persistence.PostLoad.class)) {
				postLoadMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PrePersist.class)
					|| method.isAnnotationPresent(jakarta.persistence.PrePersist.class)) {
				prePersistMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PostPersist.class)
					|| method.isAnnotationPresent(jakarta.persistence.PostPersist.class)) {
				postPersistMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PreUpdate.class)
					|| method.isAnnotationPresent(jakarta.persistence.PreUpdate.class)) {
				preUpdateMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PostUpdate.class)
					|| method.isAnnotationPresent(jakarta.persistence.PostUpdate.class)) {
				postUpdateMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PreRemove.class)
					|| method.isAnnotationPresent(jakarta.persistence.PreRemove.class)) {
				preRemoveMethods.add(method);
				lifecycle = true;
			}
			if(method.isAnnotationPresent(javax.persistence.PostRemove.class)
					|| method.isAnnotationPresent(jakarta.persistence.PostRemove.class)) {
				postRemoveMethods.add(method);
				lifecycle = true;
			}
			if(lifecycle) {
				method.setAccessible(true);
			}
		}

		boolean hasProjectionSelectFields = false;
		List<String> plainSelectFieldNames = new ArrayList<>();
		for(Field field : fields) {
			if(FieldUtils.hasProjectionAnnotation(field)) {
				hasProjectionSelectFields = true;
			}
			else {
				plainSelectFieldNames.add(field.getName());
			}
		}

		return new ClassMeta(
			fields,
			Collections.unmodifiableSet(persistable),
			List.copyOf(writableFields),
			List.copyOf(insertDateFields),
			List.copyOf(updateDateFields),
			Collections.unmodifiableMap(byName),
			idField,
			resolveNoArgConstructor(objectClass),
			hasProjectionSelectFields,
			List.copyOf(plainSelectFieldNames),
			methods,
			List.copyOf(postLoadMethods),
			List.copyOf(prePersistMethods),
			List.copyOf(postPersistMethods),
			List.copyOf(preUpdateMethods),
			List.copyOf(postUpdateMethods),
			List.copyOf(preRemoveMethods),
			List.copyOf(postRemoveMethods),
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
		@Getter
		private final EnumMode enumMode;
		@Getter
		private final Class<?> convertDatabaseType;

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

			Class<?> convertDatabaseType = converter != null ? FieldUtils.resolveConvertDatabaseType(field) : null;
			return new FieldBinding(field, converter, enumMode, convertDatabaseType);
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

		public boolean needsConversion() {
			return converter != null || enumMode != EnumMode.NONE;
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
