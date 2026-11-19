package com.estivate.inject;

/**
 * {@link ObjectProvider} that builds a new instance with the public no-arg constructor.
 * This is the provider on {@link com.estivate.context.Context} until {@code setProvider} replaces it.
 */
public final class ConstructorObjectProvider implements ObjectProvider {

	@Override
	public Object get(Class<?> type) {
		try {
			return type.getConstructor().newInstance();
		}
		catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Impossible to construct " + type.getName() + " with a no-arg constructor", e);
		}
	}

}
