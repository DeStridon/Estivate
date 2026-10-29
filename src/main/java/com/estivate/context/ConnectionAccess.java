package com.estivate.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Acquires and releases JDBC connections, using Spring {@code DataSourceUtils}
 * when spring-jdbc is on the classpath so Estivate joins {@code @Transactional}.
 */
final class ConnectionAccess {

	private static final Accessor ACCESSOR = createAccessor();

	private ConnectionAccess() {
	}

	static Connection getConnection(DataSource dataSource) throws SQLException {
		return ACCESSOR.getConnection(dataSource);
	}

	static void releaseConnection(Connection connection, DataSource dataSource) throws SQLException {
		if(connection == null) {
			return;
		}
		ACCESSOR.releaseConnection(connection, dataSource);
	}

	/**
	 * True when {@link #releaseConnection} leaves the physical connection open
	 * (e.g. Spring {@code @Transactional}), so PreparedStatements can be reused.
	 */
	static boolean retainsConnection(DataSource dataSource) {
		return ACCESSOR.retainsConnection(dataSource);
	}

	private static Accessor createAccessor() {
		try {
			Class.forName("org.springframework.jdbc.datasource.DataSourceUtils");
			return (Accessor) Class.forName("com.estivate.context.SpringConnectionAccessor")
				.getDeclaredConstructor()
				.newInstance();
		}
		catch(Throwable ignored) {
			return new DirectAccessor();
		}
	}

	interface Accessor {
		Connection getConnection(DataSource dataSource) throws SQLException;

		void releaseConnection(Connection connection, DataSource dataSource) throws SQLException;

		boolean retainsConnection(DataSource dataSource);
	}

	private static final class DirectAccessor implements Accessor {
		@Override
		public Connection getConnection(DataSource dataSource) throws SQLException {
			return dataSource.getConnection();
		}

		@Override
		public void releaseConnection(Connection connection, DataSource dataSource) throws SQLException {
			connection.close();
		}

		@Override
		public boolean retainsConnection(DataSource dataSource) {
			return false;
		}
	}
}
