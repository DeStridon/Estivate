package com.estivate.inject;

/**
 * Supplies instances such as projection transformers.
 * The active provider is held by {@link com.estivate.context.Context} and defaults to
 * {@link ConstructorObjectProvider}. Replace it per context:
 *
 * <pre>
 * context.setProvider(type -&gt; injector.getInstance(type));
 * </pre>
 */
public interface ObjectProvider {

	/**
	 * Returns the managed instance of {@code type}.
	 * {@link #resolve(ObjectProvider, Class)} casts the result to the requested class.
	 */
	Object get(Class<?> type);

	/**
	 * Loads {@code type} from {@code provider}, or from {@link ConstructorObjectProvider} when {@code provider} is null.
	 */
	static <T> T resolve(ObjectProvider provider, Class<T> type) {
		ObjectProvider current = provider == null ? new ConstructorObjectProvider() : provider;
		Object instance = current.get(type);
		if (instance == null) {
			throw new IllegalStateException(current.getClass().getName() + " returned null for " + type.getName());
		}
		if (!type.isInstance(instance)) {
			throw new IllegalStateException(current.getClass().getName() + " returned " + instance.getClass().getName() + " for " + type.getName());
		}
		return type.cast(instance);
	}

}
