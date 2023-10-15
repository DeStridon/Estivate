package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.query.Aggregator;
import com.estivate.query.Criterion;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.util.FieldUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Statement {

	Connection connection;
	
	StringBuilder query = new StringBuilder();
	List<Object> parameters = new ArrayList<>();
	
	PreparedStatement statement = null;
	
	public Statement(Connection connection){
		this.connection = connection;
	}
	
	public Statement appendQuery(String queryContent) {
		if(query.length() > 0) {
			query.append(" ");
		}
		query.append(queryContent);
		return this;
	}
	
	public String query() {
		return query.toString();
	}
	
	
	public Statement appendValue(Class entity, String fieldName, Object parameter) {
		parameters.add(compileObject(entity, fieldName, parameter));
		return this;
	}
	
	public Statement appendParameter(Class entity, String attribute, Object parameter) {
		if(parameter instanceof PropertyValue) {
			PropertyValue field = (PropertyValue) parameter;
			appendQuery(field.toString());
		}
		else {
			appendQuery("?");
			appendValue(entity, attribute, parameter);
		}
		return this;
	}
	
	public String appendParameterFetchQuery(Class entity, String attribute, Object parameter) {
		if(parameter instanceof PropertyValue) {
			PropertyValue field = (PropertyValue) parameter;
			return field.toString();
		}
		

		appendValue(entity, attribute, parameter);
		return "?";

	}

	public boolean execute() {
		try {
			
			statement = connection.prepareStatement(query.toString(), java.sql.Statement.RETURN_GENERATED_KEYS);
			for(int i = 0; i < parameters.size(); i++) {
				
				Object object = parameters.get(i);
				
				try {
					if(object instanceof String) {
						String s = (String) object;
						statement.setString(i+1, s);
					}
					else if(object instanceof Integer) {
						Integer n = (Integer) object;
						statement.setInt(i+1, n);
					}
					else if(object instanceof Long) {
						Long l = (Long) object;
						statement.setLong(i+1, l);
					}
					else if(object instanceof Float) {
						Float f = (Float) object;
						statement.setFloat(i+1, f);
					}
					else if(object instanceof Double) {
						Double f = (Double) object;
						statement.setDouble(i+1, f);
					}
					else if(object instanceof Boolean) {
						Boolean b = (Boolean) object;
						statement.setBoolean(i+1, b);
					}
					else if(object instanceof Date) {
						Date d = (Date) object;
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

	public static Statement toStatement(Connection connection, Query joinQuery) {
		
		Statement statement = new Statement(connection);
		
		statement.appendQuery("SELECT ");
		
		//TODO : avoid modifying joinQuery
		if(joinQuery.getSelects().isEmpty()) {
			joinQuery.select(joinQuery.getBaseClass());
		}
		
		if(joinQuery.getSelects().stream().allMatch(x -> x.contains(".")) && joinQuery.getGroupBys().isEmpty()) {
			statement.appendQuery("distinct");
		}
		
		statement.appendQuery(String.join(", ", joinQuery.getSelects())+"\n");
		statement.appendQuery("FROM "+Query.nameMapper.mapEntity(joinQuery.getBaseClass())+"\n");
		
		if(joinQuery.getIndexHint() != null && joinQuery.getIndexNames() != null && !joinQuery.getIndexNames().isEmpty()) {
			statement.appendQuery(joinQuery.getIndexHint()+ " INDEX ("+joinQuery.getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
        for(Join join : joinQuery.buildJoins()) {
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
	
	public static void attachWhere(Statement statement, EstivateNode node) {
		
		if(node instanceof Aggregator) {
			Aggregator aggregator = (Aggregator) node;
			for(int i = 0; i < aggregator.getCriterions().size(); i++) {
				if(i > 0) {
					statement.appendQuery(" "+aggregator.getGroupType().toString()+" ");
				}
				attachWhere(statement, aggregator.getCriterions().get(i));
			}
			
		}
		else if(node instanceof Criterion.Operator) {
			Criterion.Operator operator = (Criterion.Operator) node;
			statement.appendQuery(operator.entity.getName()+"."+Query.nameMapper.mapAttribute(operator.attribute));
			statement.appendQuery(operator.type.symbol);
			statement.appendParameter(operator.entity.entity, operator.attribute, operator.value);
		}
		else if(node instanceof Criterion.In) {
			Criterion.In in = (Criterion.In) node;
			statement.appendQuery(in.entity.getName()+"."+Query.nameMapper.mapAttribute(in.attribute));
			statement.appendQuery(" in (");
			statement.appendQuery(in.getValues().stream().map(x -> statement.appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			statement.appendQuery(")");
			for(Object value : in.getValues()) {
				statement.appendValue(in.entity.entity, in.attribute, value);
			}
		}
		else if(node instanceof Criterion.Between) {
			Criterion.Between between = (Criterion.Between) node;
			statement.appendQuery(between.entity.getName()+"."+Query.nameMapper.mapAttribute(between.attribute));
			statement.appendQuery(between.entity.getName()+"."+ Query.nameMapper.mapAttribute(between.attribute));
			statement.appendParameter(between.entity.entity, between.attribute, between.min);
			statement.appendQuery(" and ");
			statement.appendParameter(between.entity.entity, between.attribute, between.max);
			
		}
		else if(node instanceof Criterion.NullCheck) {
			Criterion.NullCheck nullcheck = (Criterion.NullCheck) node;
			statement.appendQuery(nullcheck.entity.getName() + "." + Query.nameMapper.mapAttribute(nullcheck.attribute)+(nullcheck.isNull ? " is null":" is not null"));
		}
		else {
			throw new RuntimeException("Node type not supported : "+node.getClass());
		}
		
	}
	
	
	Object compileObject(Class entity, String attribute, Object value) {
		try {
			Field field = FieldUtils.findField(entity, attribute);
			
			Type fieldType = field.getType();
			
			// Convert annotation takes priority
			if(field.getDeclaredAnnotation(Convert.class) != null) {
				Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(converter instanceof AttributeConverter) {
					AttributeConverter attributeConverter = (AttributeConverter) converter;
					Object convertedValue = attributeConverter.convertToDatabaseColumn(value);
					return convertedValue;
				}
			}
			// Then check if enum and with annotation
			if(fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
				if(value == null) {
					return null;
				}
				
				if(field.getDeclaredAnnotation(Enumerated.class) != null && field.getDeclaredAnnotation(Enumerated.class).value() != null && field.getDeclaredAnnotation(Enumerated.class).value() == EnumType.STRING) {
					return value.toString();
				}
				
				return ((Enum) value).ordinal();
				
			}

			return value;

		}
		catch(Exception e) {
			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute);
			e.printStackTrace();
		}
		
		log.warn("Could not determine type of field "+entity.getSimpleName()+"."+attribute);
		
		return compileGenericType(value);

	}
	
	String compileGenericType(Object value) {
		if(value instanceof String) {
			
			return "'"+((String) value)
					.replace("\\", "\\\\")
					//.replace("'", "\'\'")
					.replace("\"", "\"\"")
					.replace(":", "\\:")
					+"'";
		}
		else if(value instanceof java.util.Date) {
			return "\"" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) value) + "\""; 
		}
		else if(value instanceof Boolean) {
			return (boolean) value ? "1":"0";
		}
		else {
			return value.toString();
		}
	}
	
	String compileAttribute(Class entity, String attribute, Object value) {
		return compileGenericType(compileObject(entity, attribute, value));
	}
	

}
