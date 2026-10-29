package com.estivate.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Loaded only when spring-jdbc is present ({@link ConnectionAccess}).
 * Outside a Spring transaction this uses the pool directly. {@code DataSourceUtils}
 * still joins {@code @Transactional} when synchronization is active.
 */
final class SpringConnectionAccessor implements ConnectionAccess.Accessor {

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		if(TransactionSynchronizationManager.isSynchronizationActive()) {
			return DataSourceUtils.getConnection(dataSource);
		}
		return dataSource.getConnection();
	}

	@Override
	public void releaseConnection(Connection connection, DataSource dataSource) throws SQLException {
		if(connection == null) {
			return;
		}
		if(TransactionSynchronizationManager.isSynchronizationActive() || dataSource instanceof SmartDataSource) {
			DataSourceUtils.releaseConnection(connection, dataSource);
			return;
		}
		connection.close();
	}

	@Override
	public boolean retainsConnection(DataSource dataSource) {
		return TransactionSynchronizationManager.isSynchronizationActive() || dataSource instanceof SmartDataSource;
	}
}
