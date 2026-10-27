package com.estivate.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * Loaded only when spring-jdbc is present ({@link ConnectionAccess}).
 */
final class SpringConnectionAccessor implements ConnectionAccess.Accessor {

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		return DataSourceUtils.getConnection(dataSource);
	}

	@Override
	public void releaseConnection(Connection connection, DataSource dataSource) {
		DataSourceUtils.releaseConnection(connection, dataSource);
	}
}
