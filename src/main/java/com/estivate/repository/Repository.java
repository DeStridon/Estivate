package com.estivate.repository;

/**
 * Base type for Estivate derived-query repositories.
 *
 * <p>Subclass with a concrete entity type and declare abstract query methods
 * ({@code findAllBy…}, {@code countBy…}, etc.). Spring registration uses
 * {@code @EstivateManager}; ByteBuddy implements the methods via
 * {@link RepositoryQueryExecutor}.</p>
 *
 * @param <T> entity type
 */
public abstract class Repository<T> {
}
