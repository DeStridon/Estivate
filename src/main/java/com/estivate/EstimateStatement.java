package com.estivate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstimateStatement {

	Connection connection;
	
	StringBuilder query = new StringBuilder();
	List<Object> parameters = new ArrayList<>();
	
	public EstimateStatement(Connection connection){
		this.connection = connection;
	}
	
	public EstimateStatement appendQuery(String queryContent) {
		query.append(queryContent);
		return this;
	}
	
//	public EstimateStatement appendParameter(Class entity, String fieldName) {
//		query.append();
//		return this;
//	}
	
	public EstimateStatement appendValue(Class entity, String fieldName, Object parameter) {
		parameters.add(EstivateUtil.compileObject(entity, fieldName, parameter));
		return this;
	}

	public boolean execute() {
		try {
			
			PreparedStatement statement = connection.prepareStatement(query.toString());
			for(int i = 0; i < parameters.size(); i++) {
				
				Object object = parameters.get(i);
				
				try {
					if(object instanceof String s) {
						statement.setString(i+1, s);
					}
					else if(object instanceof Integer n) {
						statement.setInt(i+1, n);
					}
					else if(object instanceof Long l) {
						statement.setLong(i+1, l);
					}
					else if(object instanceof Float f) {
						statement.setFloat(i+1, f);
					}
					else if(object instanceof Boolean b) {
						statement.setBoolean(i+1, b);
					}
					else if(object instanceof Date d) {
						statement.setDate(i+1, new java.sql.Date(d.getTime()));
					}
					else {
						log.error("Cannot map object of type "+object.getClass());
					}
				}
				catch(Exception e) {
					log.error("Error while creating statement ",e);
				}
			}
						
			return statement.execute();
		
		} catch (SQLException e) {
			log.error("Error executing statement", e);
		}
		return false;
	}
	
	

}
