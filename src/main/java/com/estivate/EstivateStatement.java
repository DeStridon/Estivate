package com.estivate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.estivate.query.EstivateField;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EstivateStatement {

	Connection connection;
	
	StringBuilder query = new StringBuilder();
	List<Object> parameters = new ArrayList<>();
	
	PreparedStatement statement = null;
	
	public EstivateStatement(Connection connection){
		this.connection = connection;
	}
	
	public EstivateStatement appendQuery(String queryContent) {
		if(!query.isEmpty()) {
			query.append(" ");
		}
		query.append(queryContent);
		return this;
	}
	
	public String query() {
		return query.toString();
	}
	
	
	public EstivateStatement appendValue(Class entity, String fieldName, Object parameter) {
		parameters.add(EstivateUtil.compileObject(entity, fieldName, parameter));
		return this;
	}
	
	public EstivateStatement appendParameter(Class entity, String attribute, Object parameter) {
		if(parameter instanceof EstivateField field) {
			appendQuery(field.toString());
		}
		else {
			appendQuery("?");
			appendValue(entity, attribute, parameter);
		}
		return this;
	}
	
	public String appendParameterFetchQuery(Class entity, String attribute, Object parameter) {
		if(parameter instanceof EstivateField field) {
			return field.toString();
		}

		appendValue(entity, attribute, parameter);
		return "?";

	}

	public boolean execute() {
		try {
			
			statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
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

	public ResultSet getGeneratedKeys() {
		try {
			return statement.getGeneratedKeys();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet getResultSet() {
		try {
			return statement.getResultSet();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	
	

}
