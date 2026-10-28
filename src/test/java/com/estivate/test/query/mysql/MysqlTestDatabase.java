package com.estivate.test.query.mysql;

import javax.sql.DataSource;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.context.MySQLContext;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.JsonDocumentEntity;
import com.mysql.cj.jdbc.MysqlDataSource;

import org.testcontainers.containers.MySQLContainer;

/**
 * Builds a {@link MySQLContext} on top of a Testcontainers MySQL instance
 * for dialect-fidelity tests (real {@code JSON_*} functions).
 */
public final class MysqlTestDatabase {

	private MysqlTestDatabase() {
	}

	public static MySQLContainer<?> newContainer() {
		return new MySQLContainer<>("mysql:8.0.36")
			.withDatabaseName("estivate")
			.withUsername("estivate")
			.withPassword("estivate");
	}

	public static Context createContext(MySQLContainer<?> mysql) {
		DataSource dataSource = dataSource(mysql);
		Context context = new MySQLContext(dataSource);
		context.nameMapper = new DatabaseGenerator.TestNameMapper();

		Estivate.Tools.createTableFullQuery(JsonDocumentEntity.class, context)
			.ifNotExists()
			.execute(context);

		context.selectInterceptor = query -> {};
		context.updateInterceptor = query -> {};
		context.deleteInterceptor = query -> {};
		context.insertInterceptor = object -> {};

		return context;
	}

	public static DataSource dataSource(MySQLContainer<?> mysql) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setURL(mysql.getJdbcUrl());
		dataSource.setUser(mysql.getUsername());
		dataSource.setPassword(mysql.getPassword());
		return dataSource;
	}
}
