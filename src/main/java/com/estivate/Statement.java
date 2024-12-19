package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;

import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Criterion;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Group;
import com.estivate.query.Query.Order;
import com.estivate.query.Select;
import com.estivate.query.Select.SelectMethod;
import com.estivate.util.FieldUtils;
import com.estivate.util.StackLog;
import com.estivate.util.StringPipe;

import lombok.extern.slf4j.Slf4j;


/*
 * Wrapper for java.sql.PreparedStatement
 * doesn't hold any connection, it just helps using statement
 */
@Slf4j
public class Statement {

	final Context context;
	final Connection connection;
	
	String queryName;
	
	StringBuilder query = new StringBuilder();
	List<Object> parameters = new ArrayList<>();
	
	PreparedStatement statement = null;
	
	public Statement(Context context, Connection connection){
		this.context = context;
		this.connection = connection;
	}
	
	public Statement appendQuery(String queryContent) {
		if(query.length() > 0 && !Arrays.asList(" ", "(").contains(query.substring(query.length() - 1)) && !queryContent.equals(")")) {
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
			appendQuery(context.nameMapper.mapDatabase(field.entity, field.attributeName));
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

	public boolean executeForValidation() throws SQLException{
		return execute(connection);
	}

	public ResultSet executeForGeneratedKeys() throws SQLException{
		if(statement == null) {
			execute(connection);
		}
		return statement.getGeneratedKeys();
	}
	
	public ResultSet executeForResultSet() throws SQLException {
		if(statement == null) {
			execute(connection);
		}
		return statement.getResultSet();	
	}
	
	private boolean execute(Connection connection) throws SQLException {
		query.insert(0, "-- Stack = "+StackLog.create().subList(0, 3).stream().collect(Collectors.joining(", "))+"\n"); 
		if(!StringUtils.isBlank(queryName)) {
			query.insert(0, "-- "+queryName+"\n");
		}
		
		
		statement = connection.prepareStatement(query.toString(), java.sql.Statement.RETURN_GENERATED_KEYS);
	

		for(int i = 0; i < parameters.size(); i++) {
			
			Object object = parameters.get(i);
			
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
			else if(object == null) {
				statement.setObject(i+1, null);
			}
			else {
				log.error("Cannot map object of type "+object.getClass());
			}

		}
		
		return statement.execute();
						
		
	
	}
	
	
	
	public static Statement toStatement(Context context, Connection connection, Query query) {
		
		Statement statement = new Statement(context, connection);
		statement.queryName = query.getName();
		
		statement.appendQuery("SELECT ");
		
		//TODO : avoid modifying joinQuery
		if(query.getSelects().isEmpty()) {
			query.selectAll(query.getEntity());
		}
		
//		if(joinQuery.getSelects().stream().map(x -> x.toString()).allMatch(x -> x.contains(".")) && joinQuery.getGroupBys().isEmpty()) {
//			statement.appendQuery("distinct");
//		}
		
		statement.appendQuery(String.join(", ", query.getSelects().stream().map(x -> statement.selectString(x)).collect(Collectors.toList()))+"\n");

		//statement.appendQuery("FROM "+Query.nameMapper.mapDatabaseClass(joinQuery.getEntity())+"\n");
		statement.appendQuery("FROM").appendQuery(context.nameMapper.mapDatabaseClass(query.getEntity().entity));
		if(query.getEntity().alias != null) {
			statement.appendQuery(query.getEntity().alias);
		}

		if(query.getIndexHint() != null && query.getIndexNames() != null && !query.getIndexNames().isEmpty()) {
			statement.appendQuery(query.getIndexHint()+ " INDEX ("+query.getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
        for(Join join : query.buildJoins()) {
        	statement.appendQuery(statement.joinString(join)+'\n');
        }
        
        if(!query.getCriterions().isEmpty()) {
        	statement.appendQuery("WHERE");
        	attachWhere(statement, query, true);
        }
        
		// Append group bys (if any)
        
		if(!query.getGroupBys().isEmpty()) {
			statement.appendQuery(query.getGroupBys().stream().map(x -> statement.groupString(x)).collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// Append order
		if(!query.getOrders().isEmpty()) {
			statement.appendQuery(query.getOrders().stream().map(x -> statement.orderString(x)).collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// Append limit & offset
		if(query.getLimit() != null) {
			statement.appendQuery("LIMIT "+query.getLimit()+"\n");
		}
		if(query.getOffset() != null) {
			statement.appendQuery("OFFSET "+ query.getOffset() +"\n");
		}
        
        return statement;
        
	}
	
	public String joinString(Join join) {
		
		StringPipe sb = new StringPipe().separator(" ")
				.append  (join.joinType.toString())
				.append  ("JOIN")
				.append  (context.nameMapper.mapDatabaseClass(join.rightEntity.entity))
				.appendIf(join.rightEntity.alias != null, join.rightEntity.alias);
		if(join.indexHint != null && join.indexNames != null && !join.indexNames.isEmpty()) {
			sb	.append  (join.indexHint.toString()+ " INDEX ("+join.indexNames.stream().collect(Collectors.joining(", "))+")");
		}
		sb		.append  ("ON")
				.append  (join.joins.stream().map(x -> context.nameMapper.mapDatabase(join.leftEntity, x.getLeft()) + " = " + context.nameMapper.mapDatabase(join.rightEntity, x.getRight())).collect(Collectors.joining(" and ")));
		return sb.toString();
		
	}
	
	public String orderString(Order order) {
		return context.nameMapper.mapDatabase(order.entity, order.attribute) + (order.asc ? " ASC" : " DESC");
	}
	
	public String groupString(Group group) {
		return context.nameMapper.mapDatabase(group.entity, group.attribute);
	}
	
	public String selectString(Select select) {
		if(select.method == SelectMethod.Distinct) {
			return "DISTINCT "+context.nameMapper.mapDatabase(select.entity, select.attribute)+" as `"+(select.alias != null ? select.alias : context.nameMapper.mapEntity(select.entity, select.attribute))+"`";
		}
		else if(select.method == SelectMethod.Count) {
			if (select.entity == null) {
				return "COUNT(*)"+(select.alias != null ? " as `"+select.alias+"`" : "");
			}
			return "COUNT(distinct "+context.nameMapper.mapDatabase(select.entity, select.attribute)+")"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		else if(select.method == SelectMethod.Max) {
			return "MAX("+context.nameMapper.mapDatabase(select.entity, select.attribute)+")"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		else if(select.method == SelectMethod.Min) {
			return "MIN("+context.nameMapper.mapDatabase(select.entity, select.attribute)+")"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		else if(select.method == SelectMethod.Sum) {
			return "SUM("+context.nameMapper.mapDatabase(select.entity, select.attribute)+")"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		else if(select.method == SelectMethod.GroupConcat) {
			return "GROUP_CONCAT("+context.nameMapper.mapDatabase(select.entity, select.attribute)+")"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		
		return context.nameMapper.mapDatabase(select.entity, select.attribute)+" as `"+(select.alias != null ? select.alias : context.nameMapper.mapEntity(select.entity, select.attribute))+"`";
		
	
	}
	
	public static void attachWhere(Statement statement, EstivateNode node, boolean rootNode) {
		
		if(node instanceof Aggregator) {
			Aggregator aggregator = (Aggregator) node;
			if(!rootNode && aggregator.getCriterions().size() > 1) {
				statement.appendQuery("(");
			}
			for(int i = 0; i < aggregator.getCriterions().size(); i++) {
				if(i > 0) {
					statement.appendQuery(aggregator.getGroupType().toString());
				}
				attachWhere(statement, aggregator.getCriterions().get(i), false);
			}
			if(!rootNode && aggregator.getCriterions().size() > 1) {
				statement.appendQuery(")");
			}
			
		}
		else if(node instanceof Criterion.Operator) {
			Criterion.Operator operator = (Criterion.Operator) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(operator.entity, operator.attribute));
			statement.appendQuery(operator.type.symbol);
			statement.appendParameter(operator.entity.entity, operator.attribute, operator.value);
		}
		else if(node instanceof Criterion.In) {
			Criterion.In in = (Criterion.In) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(in.entity, in.attribute));
			statement.appendQuery("in (");
			statement.appendQuery(in.getValues().stream().map(x -> statement.appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			statement.appendQuery(")");
		}
		else if(node instanceof Criterion.NotIn) {
			Criterion.NotIn in = (Criterion.NotIn) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(in.entity, in.attribute));
			statement.appendQuery("not in (");
			statement.appendQuery(in.getValues().stream().map(x -> statement.appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			statement.appendQuery(")");
		}
		else if(node instanceof Criterion.Between) {
			Criterion.Between between = (Criterion.Between) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(between.entity, between.attribute));
			statement.appendQuery("between");
			statement.appendParameter(between.entity.entity, between.attribute, between.min);
			statement.appendQuery("and");
			statement.appendParameter(between.entity.entity, between.attribute, between.max);
			
		}
		else if(node instanceof Criterion.NullCheck) {
			Criterion.NullCheck nullcheck = (Criterion.NullCheck) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(nullcheck.entity, nullcheck.attribute)+(nullcheck.isNull ? " is null":" is not null"));
		}
		else if(node instanceof Criterion.InSubQuery) {
			Criterion.InSubQuery subQuery = (Criterion.InSubQuery) node;
			statement.appendQuery(statement.context.nameMapper.mapDatabase(subQuery.entity, subQuery.attribute)+(subQuery.include ? " in ":" not in "));
			Statement subStatement = Statement.toStatement(statement.context, statement.connection, subQuery.subQuery);
			statement.appendQuery("("+subStatement.query()+")");
			statement.parameters.addAll(subStatement.parameters);
		}
		else if(node instanceof Criterion.ExistsSubQuery){
			Criterion.ExistsSubQuery subQuery = (Criterion.ExistsSubQuery) node;
			Statement subStatement = Statement.toStatement(statement.context, statement.connection, subQuery.subQuery);
			statement.appendQuery(subQuery.include ? "EXISTS": "NOT EXISTS");
			statement.appendQuery("("+subStatement.query()+")");
			statement.parameters.addAll(subStatement.parameters);
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
			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute, e);
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
