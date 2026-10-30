package com.estivate.test.query.h2;

import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import com.estivate.context.Context;
import com.estivate.context.H2Context;
import com.estivate.test.TestData;

/**
 * Shared in-memory H2 context for tests. Schema is reconciled once from {@code com.estivate.test.entities}.
 */
public final class H2TestDatabase {

	private static Context context = null;

	private H2TestDatabase() {
	}

	public static Context getContext() {
		if (context == null) {
			try {
				context = new H2Context(datasource());
			} catch (NamingException e) {
				e.printStackTrace();
			}
			try {
				Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			TestData.install(context);

			System.out.println(context.showTables().stream().collect(Collectors.joining(", ")));
			System.out.println();
		}

		return context;
	}

	public static DataSource datasource() throws NamingException {
		System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(javax.naming.Context.URL_PKG_PREFIXES, "org.apache.naming");
		InitialContext ic = new InitialContext();

		ic.createSubcontext("java:");
		ic.createSubcontext("java:/comp");
		ic.createSubcontext("java:/comp/env");
		ic.createSubcontext("java:/comp/env/jdbc");

		JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:test;FILE_LOCK=NO;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE", "sa", "sasasa");

		ic.bind("java:/dsName", ds);

		return ds;
	}

}
