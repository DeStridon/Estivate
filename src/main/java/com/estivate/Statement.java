package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.estivate.query.Attribute;
import com.estivate.query.Criterion;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Keyword;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Group;
import com.estivate.query.Query.Order;
import com.estivate.query.Select;
import com.estivate.util.FieldUtils;
import com.estivate.util.StackLog;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/*
 * Wrapper for java.sql.PreparedStatement
 * doesn't hold any connection, it just helps using statement
 */
@ToString
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

	public Statement appendAttribute(Criterion criterion){
		// TODO : nest functions
		
		String attribute = context.nameMapper.mapDatabase(criterion.entity, criterion.attribute);
		for(Attribute.Function function : criterion.functions){
			attribute = function.render(attribute);
		}
		appendQuery(attribute);
		return this;
	}
	
	public String query() {
		return query.toString();
	}

	
	public Statement appendValue(Class<?> entity, String fieldName, Object parameter) {
		parameters.add(compileObject(entity, fieldName, parameter));
		return this;
	}
	
	public Statement appendParameter(Class<?> entity, String attribute, Object parameter) {
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
	
	public String appendParameterFetchQuery(Class<?> entity, String attribute, Object parameter) {
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
				statement.setTimestamp(i+1, new Timestamp(d.getTime()));
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
		

		if(query.getSelects().isEmpty()) {
			query.selectAll(query.getEntity());
		}
		
		List<Select> selects = query.getSelects().stream().sorted(Comparator.comparing(x -> x.function == null || !x.function.equals(Estivate.Functions.distinct))).collect(Collectors.toList());
		
//		selects.addAll(query.getSelects().stream().filter(x -> x.function != null && x.function.equals(Estivate.Functions.distinct)).collect(Collectors.toList()));
//		selects.addAll(query.getSelects().stream().filter(x -> !selects.contains(x)).collect(Collectors.toList()));
		
//		statement.appendQuery(String.join(", ", query.getSelects().stream().filter(x -> x.function != null && x.function.equals(Estivate.Functions.distinct)).map(statement::selectString).collect(Collectors.toList())));
//		statement.appendQuery(String.join(", ", query.getSelects().stream().filter(x -> x.function == null || !x.function.equals(Estivate.Functions.distinct)).map(statement::selectString).collect(Collectors.toList()))+"\n");

		statement.appendQuery(String.join(", ", selects.stream().map(statement::selectString).collect(Collectors.toList()))+"\n");
		
		//statement.appendQuery("FROM "+Query.nameMapper.mapDatabaseClass(joinQuery.getEntity())+"\n");
		statement.appendQuery("FROM").appendQuery(context.nameMapper.mapDatabaseClass(query.getEntity().entity));
		if(query.getEntity().alias != null) {
			statement.appendQuery(query.getEntity().alias);
		}

		if(query.getIndexHint() != null && query.getIndexNames() != null && !query.getIndexNames().isEmpty()) {
			statement.appendQuery(query.getIndexHint()+ " INDEX ("+query.getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
        for(Join join : query.getJoins()) {
        	statement.appendJoin(join);
        	statement.appendQuery("\n");
        }
        
        if(!query.getCriterions().isEmpty()) {
        	statement.appendQuery("WHERE");
        	statement.appendNodeToStatement(query, true);
        }
        
		// Append group bys (if any)
		if(!query.getGroupBys().isEmpty()) {
			statement.appendQuery(query.getGroupBys().stream().map(x -> statement.groupString(x)).collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}

		// Append having (if any)
		if(query.getHaving() != null) {
			statement.appendQuery("HAVING");
			statement.appendNodeToStatement(query.getHaving(), true);
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
	
	public void appendJoin(Join join) {
		
		appendQuery(join.joinType.toString());
		appendQuery("JOIN");
		appendQuery(context.nameMapper.mapDatabaseClass(join.rightEntity.entity));
		if(join.rightEntity.alias != null) { appendQuery(join.rightEntity.alias);}
		if(join.indexHint != null && join.indexNames != null && !join.indexNames.isEmpty()) {
			appendQuery(join.indexHint.toString()+ " INDEX ("+join.indexNames.stream().collect(Collectors.joining(", "))+")");
		}
		appendQuery("ON");
//				.append  (join.joiningAttributes.stream().map(x -> context.nameMapper.mapDatabase(join.leftEntity, x.getLeft()) + " = " + context.nameMapper.mapDatabase(join.rightEntity, x.getRight())).collect(Collectors.joining(" and ")));
		appendNodeToStatement(join.joiningCriterion, false);
		
		
	}
	
	public String orderString(Order order) {

		StringBuilder sb = new StringBuilder();
	
		if(order.function != null) {
			sb.append(order.function.render(context.nameMapper.mapDatabase(order.entity, order.attribute)));
		}
		else {
			sb.append(context.nameMapper.mapDatabase(order.entity, order.attribute));
		}
		sb.append(order.direction != null ? " " + order.direction.toString().toUpperCase() : "");
	
		return sb.toString();
		
	}
		
	
	public String groupString(Group group) {
		return context.nameMapper.mapDatabase(group.entity, group.attribute);
	}
	
	public String selectString(Select select) {

		if(select.function != null && select.function.equals(Estivate.Functions.count) && (select.entity == null || select.entity.entity == null)) {
			return "COUNT(*)"+(select.alias != null ? " as `"+select.alias+"`" : "");
		}
		else if (select.function != null && select.function.equals(Estivate.Functions.distinct)) {
			return select.function.render(context.nameMapper.mapDatabase(select.entity, select.attribute))+" as `"+(select.alias != null ? select.alias : context.nameMapper.mapEntity(select.entity, select.attribute))+"`";
		}
		else if (select.function != null) {
			return select.function.render(context.nameMapper.mapDatabase(select.entity, select.attribute))+(select.alias != null ? " as `"+select.alias+"`" : "");
		}

		return context.nameMapper.mapDatabase(select.entity, select.attribute)+" as `"+(select.alias != null ? select.alias : context.nameMapper.mapEntity(select.entity, select.attribute))+"`";
	
	}
	
	public void appendNodeToStatement(EstivateNode node, boolean rootNode) {
		
		if(node instanceof Aggregator) {
			Aggregator aggregator = (Aggregator) node;
			if(!rootNode && aggregator.getCriterions().size() > 1) {
				appendQuery("(");
			}
			for(int i = 0; i < aggregator.getCriterions().size(); i++) {
				if(i > 0) {
					appendQuery(aggregator.getGroupType().toString());
				}
				appendNodeToStatement(aggregator.getCriterions().get(i), false);
			}
			if(!rootNode && aggregator.getCriterions().size() > 1) {
				appendQuery(")");
			}
			
		}
		else if(node instanceof Criterion.Operator) {
			Criterion.Operator operator = (Criterion.Operator) node;
			appendAttribute(operator);
			appendQuery(operator.type.symbol);
			appendParameter(operator.entity.entity, operator.attribute, operator.value);
		}
		else if(node instanceof Criterion.In) {
			Criterion.In in = (Criterion.In) node;
			appendAttribute(in);
			appendQuery("in (");
			appendQuery(in.getValues().stream().map(x -> appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			appendQuery(")");
		}
		else if(node instanceof Criterion.NotIn) {
			Criterion.NotIn in = (Criterion.NotIn) node;
			appendAttribute(in);
			appendQuery("not in (");
			appendQuery(in.getValues().stream().map(x -> appendParameterFetchQuery(in.entity.entity, in.attribute, x)).collect(Collectors.joining(", ")));
			appendQuery(")");
		}
		else if(node instanceof Criterion.Between) {
			Criterion.Between between = (Criterion.Between) node;
			appendAttribute(between);
			appendQuery("between");
			appendParameter(between.entity.entity, between.attribute, between.min);
			appendQuery("and");
			appendParameter(between.entity.entity, between.attribute, between.max);
			
		}
		else if(node instanceof Criterion.NullCheck) {
			Criterion.NullCheck nullcheck = (Criterion.NullCheck) node;
			appendAttribute(nullcheck);
			appendQuery(nullcheck.isNull ? " is null":" is not null");
		}
		else if(node instanceof Criterion.MatchAgainst) {
			
			Criterion.MatchAgainst matchAgainst = (Criterion.MatchAgainst) node;
			if(!matchAgainst.inclusive){
				appendQuery("NOT");
			}
			appendQuery("MATCH" + matchAgainst.attributes.stream().map(x -> context.nameMapper.mapDatabase(matchAgainst.entity, x)).collect(Collectors.joining(", ", "(", ")")));
			appendQuery("AGAINST(");
			appendParameter(matchAgainst.entity.entity, matchAgainst.attributes.get(0), matchAgainst.value);
			appendQuery(")");
			
		}
		else if(node instanceof Criterion.NativeCriterion) {
			Criterion.NativeCriterion nativeCriterion = (Criterion.NativeCriterion) node;
			appendAttribute(nativeCriterion);
			appendQuery(nativeCriterion.criterion);
		}
		else if(node instanceof Criterion.InSubQuery) {
			Criterion.InSubQuery subQuery = (Criterion.InSubQuery) node;
			appendAttribute(subQuery);
			appendQuery(subQuery.include ? "in " : "not in ");
			Statement subStatement = Statement.toStatement(context, connection, subQuery.subQuery);
			appendQuery("("+subStatement.query()+")");
			parameters.addAll(subStatement.parameters);
		}
		else if(node instanceof Criterion.ExistsSubQuery){
			Criterion.ExistsSubQuery subQuery = (Criterion.ExistsSubQuery) node;
			Statement subStatement = Statement.toStatement(context, connection, subQuery.subQuery);
			appendQuery(subQuery.include ? "EXISTS": "NOT EXISTS");
			appendQuery("("+subStatement.query()+")");
			parameters.addAll(subStatement.parameters);
		}
		else if(node instanceof Keyword) {
			appendQuery(((Keyword) node).getValue().toString().toLowerCase());
		}
		else {
			throw new RuntimeException("Node type not supported : "+node.getClass());
		}
		
	}
	
	
	Object compileObject(Class<?> entity, String attribute, Object value) {
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
				
				return ((Enum<?>) value).ordinal();
				
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
	
	String compileAttribute(Class<?> entity, String attribute, Object value) {
		return compileGenericType(compileObject(entity, attribute, value));
	}
	

}
