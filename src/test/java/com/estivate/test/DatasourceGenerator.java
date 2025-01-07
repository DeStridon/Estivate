package com.estivate.test;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;



public class DatasourceGenerator {
	
//	public static DataSource datasource() throws NamingException {
//		
//	    Map<String, String> env = new HashMap<String, String>();
//	   
//	    
//		 JdbcDataSource ds = new JdbcDataSource();
//		 ds.setURL("jdbc:h2:˜/test");
//		 ds.setUser("sa");
//		 ds.setPassword("sa");
//		 Context ctx = context();
//
//		 ctx.rebind("jdbc/dsName", ds);
//	
//		 return ds;
//		
//	}
	
	public static DataSource datasource() throws NamingException {
	      System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
          System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
          InitialContext ic = new InitialContext();

          ic.createSubcontext("java:");
          ic.createSubcontext("java:/comp");
          ic.createSubcontext("java:/comp/env");
          ic.createSubcontext("java:/comp/env/jdbc");

          
          
//          JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:./data/db;FILE_LOCK=NO;DB_CLOSE_ON_EXIT=TRUE", "sa", "sasasa");
          JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:test;FILE_LOCK=NO;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE", "sa", "sasasa");
         
          // Construct DataSource
          // OracleConnectionPoolDataSource ds = new
          // OracleConnectionPoolDataSource();
          // ds.setURL("jdbc:oracle:thin:@host:port:db");
          // ds.setUser("MY_USER_NAME");
          // ds.setPassword("MY_USER_PASSWORD");

          ic.bind("java:/dsName", ds);
          
          return ds;

	}

}
