package com.estivate.inject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ObjectProvider} backed by instances registered manually.
 * Lookup is by the class passed to {@link #set(Class, Object)}.
 */
public class CacheObjectProvider implements ObjectProvider {

	private final ConcurrentMap<Class<?>, Object> objects = new ConcurrentHashMap<>();

	/**
	 * Registers {@code instance} under {@code instance.getClass()}.
	 */
	public void set(Object instance) {
		if (instance == null) {
			throw new IllegalArgumentException("Cannot cache a null instance");
		}
		objects.put(instance.getClass(), instance);
	}

	/**
	 * Registers {@code instance} so {@link #get(Class)} returns it for {@code type}.
	 */
	public <T> void set(Class<T> type, T instance) {
		if (type == null) {
			throw new IllegalArgumentException("Cannot cache an instance under a null type");
		}
		if (instance == null) {
			throw new IllegalArgumentException("Cannot cache a null instance for " + type.getName());
		}
		objects.put(type, instance);
	}

	@Override
	public Object get(Class<?> type) {
		Object instance = objects.get(type);
		if (instance == null) {
			throw new IllegalStateException("No cached instance for " + type.getName());
		}
		return instance;
	}

}
