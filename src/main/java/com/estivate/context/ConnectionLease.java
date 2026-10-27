package com.estivate.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.Getter;

/**
 * AutoCloseable connection handle. Nested leases inside {@link Context#withConnection}
 * do not release the shared connection.
 */
public final class ConnectionLease implements AutoCloseable {

	@Getter
	private final Connection connection;

	private final DataSource dataSource;
	private final boolean releaseOnClose;

	ConnectionLease(Connection connection, DataSource dataSource, boolean releaseOnClose) {
		this.connection = connection;
		this.dataSource = dataSource;
		this.releaseOnClose = releaseOnClose;
	}

	@Override
	public void close() throws SQLException {
		if(releaseOnClose) {
			ConnectionAccess.releaseConnection(connection, dataSource);
		}
	}
}
