package com.estivate.context;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.entity.CompositeIndex;
import com.estivate.query.Query;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MySQLContext extends Context {
	
	
	public boolean tracePerformances = false;
	
	public MySQLContext(DataSource datasource) {
		super(datasource);
	}
		
	
	
	@SneakyThrows
	public List<CompositeIndex> listIndexes(Class<?> c) {
		List<CompositeIndex> indexes = new ArrayList<>();

		try (Connection connection = datasource.getConnection()){
			Statement statement = new Statement(connection).appendQuery("SHOW INDEX FROM ").appendQuery(Query.nameMapper.mapDatabaseClass(c));
			statement.execute();
			
			ResultSet resultSet = statement.getResultSet();
			
			List<String> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	rows.add(resultSet.getString(1));        	
	        }
	        
		}
		return indexes;
    }
	
	

}
