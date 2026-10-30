package com.estivate.test.query.postgresql;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.estivate.context.Context;
import com.estivate.context.PostgreSQLContext;
import com.estivate.test.TestData;

/**
 * Builds a {@link PostgreSQLContext} on a Testcontainers PostgreSQL instance.
 */
public final class PostgresqlTestDatabase {

	private PostgresqlTestDatabase() {
	}

	public static PostgreSQLContainer<?> newContainer() {
		return new PostgreSQLContainer<>("postgres:16-alpine")
				.withDatabaseName("estivate")
				.withUsername("estivate")
				.withPassword("estivate");
	}

	public static Context createContext(PostgreSQLContainer<?> postgres) {
		Context context = new PostgreSQLContext(dataSource(postgres));
		TestData.install(context);
		return context;
	}

	public static DataSource dataSource(PostgreSQLContainer<?> postgres) {
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setURL(postgres.getJdbcUrl());
		dataSource.setUser(postgres.getUsername());
		dataSource.setPassword(postgres.getPassword());
		return dataSource;
	}

}
