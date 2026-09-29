package com.estivate.repository;

/**
 * How a derived repository method returns its {@link RepositoryQuery#returnEntity} values.
 */
public enum ReturnType {
    /** Single entity or projected field value. */
    Entity,
    /** List without DISTINCT. */
    List,
    /** List with DISTINCT (e.g. findDistinct…). */
    DistinctList,
    /** Aggregate count ({@link Long}); exists queries use this with {@code boolean} returnEntity. */
    Count
}
