package com.estivate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.EstivateAggregator;
import com.estivate.query.EstivateCriterion;
import com.estivate.query.EstivateField;
import com.estivate.query.EstivateJoin;
import com.estivate.query.EstivateNode;
import com.estivate.query.EstivateQuery;

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

	public static EstivateStatement toStatement(Connection connection, EstivateQuery joinQuery) {
		
		EstivateStatement statement = new EstivateStatement(connection);
		
		statement.appendQuery("SELECT ");
		
		//TODO : avoid modifying joinQuery
		if(joinQuery.getSelects().isEmpty()) {
			joinQuery.select(joinQuery.getBaseClass());
		}
		
		if(joinQuery.getSelects().stream().allMatch(x -> x.contains(".")) && joinQuery.getGroupBys().isEmpty()) {
			statement.appendQuery("distinct");
		}
		
		statement.appendQuery(String.join(", ", joinQuery.getSelects())+"\n");
		statement.appendQuery("FROM "+joinQuery.nameMapper.mapEntity(joinQuery.getBaseClass())+"\n");
		
        for(EstivateJoin join : joinQuery.buildJoins()) {
        	statement.appendQuery(join.toString()+'\n');
        }
        
        if(!joinQuery.getCriterions().isEmpty()) {
        	statement.appendQuery("WHERE");
        	attachWhere(statement, joinQuery);
        }
        
		// Append group bys (if any)
		if(!joinQuery.getGroupBys().isEmpty()) {
			statement.appendQuery(joinQuery.getGroupBys().stream().collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// Append order
		if(!joinQuery.getOrders().isEmpty()) {
			statement.appendQuery(joinQuery.getOrders().stream().collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// Append limit & offset
		if(joinQuery.getLimit() != null) {
			statement.appendQuery("LIMIT "+joinQuery.getLimit()+"\n");
		}
		if(joinQuery.getOffset() != null) {
			statement.appendQuery("OFFSET "+ joinQuery.getOffset() +"\n");
		}
        
        return statement;
        
	}
	
	public static void attachWhere(EstivateStatement statement, EstivateNode node) {
		
		if(node instanceof EstivateAggregator aggregator) {
			for(int i = 0; i < aggregator.getCriterions().size(); i++) {
				if(i > 0) {
					statement.appendQuery(" "+aggregator.getGroupType().toString()+" ");
				}
				attachWhere(statement, aggregator.getCriterions().get(i));
			}
			
		}
		else if(node instanceof EstivateCriterion.Operator operator) {
			statement.appendQuery(operator.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(operator.attribute));
			statement.appendQuery(operator.type.symbol);
			statement.appendParameter(operator.entity.entity, operator.attribute, operator.value);
		}
		else if(node instanceof EstivateCriterion.In in) {
			statement.appendQuery(in.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(in.attribute));
			statement.appendQuery(" in (");
			statement.appendQuery(in.getValues().stream().map(x -> statement.appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			statement.appendQuery(")");
			for(Object value : in.getValues()) {
				statement.appendValue(in.entity.entity, in.attribute, value);
			}
		}
		else if(node instanceof EstivateCriterion.Between between) {
			statement.appendQuery(between.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(between.attribute));
			statement.appendQuery(between.entity.getName()+"."+ EstivateQuery.nameMapper.mapAttribute(between.attribute));
			statement.appendParameter(between.entity.entity, between.attribute, between.min);
			statement.appendQuery(" and ");
			statement.appendParameter(between.entity.entity, between.attribute, between.max);
			
		}
		else if(node instanceof EstivateCriterion.NullCheck nullcheck) {
			statement.appendQuery(nullcheck.entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(nullcheck.attribute)+(nullcheck.isNull ? " is null":" is not null"));
		}
		else {
			throw new RuntimeException("Node type not supported : "+node.getClass());
		}
		
	}

	
	

}
